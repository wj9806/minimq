package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.core.data.CommitLog;
import io.github.wj9806.minimq.broker.core.data.ConsumerQueue;
import io.github.wj9806.minimq.broker.core.data.Message;
import io.github.wj9806.minimq.broker.core.data.Topic;
import io.github.wj9806.minimq.broker.utils.PutMessageLock;
import io.github.wj9806.minimq.broker.utils.UnfairReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.wj9806.minimq.broker.constants.BrokerConstants.COMMIT_LOG_DEFAULT_SIZE;
import static io.github.wj9806.minimq.broker.utils.CommitLogFileNameUtils.buildCommitLogPath;
import static io.github.wj9806.minimq.broker.utils.CommitLogFileNameUtils.incrCommitLogFileName;

public class MMapFile {

    private String topicName;
    private File file;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;
    private PutMessageLock putMessageLock;
    private static final Logger LOGGER = LoggerFactory.getLogger(MMapFile.class);

    /**
     * mmap访问文件
     */
    public void loadFileInMMap(String topicName, int startOffset, int size) throws IOException {
        this.topicName = topicName;
        String filePath = getLastedCommitLog(topicName);
        doMMap(filePath, startOffset, size);
        putMessageLock = new UnfairReentrantLock();
    }

    private void doMMap(String filePath, int startOffset, int size) throws IOException {
        this.file = new File(filePath);
        if (!file.exists()) throw new FileNotFoundException(filePath + " is invalid");


        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, size);
    }

    /**
     * 获取最新的CommitLog
     */
    private String getLastedCommitLog(String topicName) {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        if (topic == null)
            throw new NullPointerException(topicName + " is invalid");

        CommitLog commitLog = topic.getLastedCommitLog();
        long diff = commitLog.countDiff();

        String fileName = null;
        if (Objects.equals(diff, 0)) {
            //写满
            CommitLogFilePath commitLogFilePath = createNewCommitLog(topicName, commitLog);
            fileName = commitLogFilePath.getFilePath();
        } else if (diff > 0) {
            fileName = commitLog.getFileName();
        } else if (diff < 0) {
            throw new IllegalStateException(topicName + "'s commitlog offset state is Illegal: " +
                    commitLog.getOffsetLimit() + " < " + commitLog.getOffset());
        }

        return buildCommitLogPath(topicName, fileName);
    }

    /**
     * @return 返回新的CommitLog文件绝对路径
     */
    private CommitLogFilePath createNewCommitLog(String topicName, CommitLog commitLog) {
        String newFileName = incrCommitLogFileName(commitLog.getFileName());
        String path = buildCommitLogPath(topicName, newFileName);
        File newCommitLog = new File(path);
        try {
            if (!newCommitLog.createNewFile()) {
                throw new RuntimeException("create new file failed: " + path);
            }
            LOGGER.info("create new CommitLog, topic: {}, file: {}", topicName, newFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new CommitLogFilePath(newFileName, path);
    }

    /**
     * 读取文件
     */
    public byte[] read(int readOffset, int size) {
        mappedByteBuffer.position(readOffset);

        byte[] content = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            byte b = mappedByteBuffer.get(readOffset + i);
            content[j++] = b;
        }

        return content;
    }

    /**
     * 写入文件
     * @param message 要写入的内容
     * @param force 是否强制刷盘
     */
    public void write(Message message, boolean force) throws IOException {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        if (topic == null) throw new NullPointerException(topicName + " is invalid");

        CommitLog commitLog = topic.getLastedCommitLog();
        if (commitLog == null) throw new NullPointerException(topicName + "'s commitlog is invalid");

        putMessageLock.lock();
        try {
            ensureCapacity(message);

            byte[] content = message.toBytes();
            mappedByteBuffer.put(content);

            int offset = commitLog.getOffset().get();
            dispatch(content, offset);

            commitLog.getOffset().addAndGet(content.length);
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            putMessageLock.unlock();
        }
    }

    /**
     * 将ConsumerQueue文件写入
     * @param content
     */
    private void dispatch(byte[] content, int msgIndex) {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        if (topic == null) {
            throw new NullPointerException(topicName + " is invalid");
        }
        ConsumerQueue queue = new ConsumerQueue();
        queue.setCommitLogIndex(Integer.parseInt(topic.getLastedCommitLog().getFileName()));
        queue.setMsgIndex(msgIndex);
        queue.setMsgLength(content.length);


    }

    private void ensureCapacity(Message message) throws IOException {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        CommitLog commitLog = topic.getLastedCommitLog();
        long offsetNum = commitLog.countDiff();
        if (offsetNum < message.toBytes().length) {
            CommitLogFilePath commitLogFilePath = createNewCommitLog(topicName, commitLog);
            commitLog.setOffset(new AtomicInteger(0));
            commitLog.setOffsetLimit(new Long(COMMIT_LOG_DEFAULT_SIZE));
            commitLog.setFileName(commitLogFilePath.getFileName());
            doMMap(commitLogFilePath.getFilePath(), 0, COMMIT_LOG_DEFAULT_SIZE);
        }
    }

    public void write(Message message) throws IOException {
        write(message, false);
    }

    /**
     * 释放MMap内存占用
     */
    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0)
            return;
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null)
            return buffer;
        else
            return viewed(viewedBuffer);
    }

    class CommitLogFilePath {
        private String fileName;
        private String filePath;

        public CommitLogFilePath(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
