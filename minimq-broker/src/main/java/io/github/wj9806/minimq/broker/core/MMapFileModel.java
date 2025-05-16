package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;
import io.github.wj9806.minimq.broker.model.CommitLogModel;
import io.github.wj9806.minimq.broker.model.MessageModel;
import io.github.wj9806.minimq.broker.model.TopicModel;
import io.github.wj9806.minimq.broker.utils.CommitLogFileNameUtils;

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

public class MMapFileModel {

    private File file;
    private int mappedSize;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;

    /**
     * mmap访问文件
     */
    public void loadFileInMMap(String topicName, int startOffset, int endOffset) throws IOException {

        String filePath = getLastedCommitLog(topicName);

        this.file = new File(filePath);
        if (!file.exists()) throw new FileNotFoundException(filePath + " is invalid");

        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedSize = endOffset - startOffset;
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, endOffset);
    }

    /**
     * 获取最新的CommitLog
     */
    private String getLastedCommitLog(String topicName) {
        TopicModel topicModel = CommonCache.getTopicModelMap().get(topicName);
        if (topicModel == null)
            throw new NullPointerException(topicName + " is invalid");

        CommitLogModel commitLog = topicModel.getLastedCommitLog();
        long diff = commitLog.getOffsetLimit() - commitLog.getOffset();

        String fileName = null;
        if (Objects.equals(diff, 0)) {
            //写满
            fileName = createNewCommitLog(topicName, commitLog);
        } else if (diff > 0) {
            fileName = commitLog.getFileName();
        } else if (diff < 0) {
            throw new IllegalStateException(topicName + "'s commitlog offset state is Illegal: " +
                    commitLog.getOffsetLimit() + " < " + commitLog.getOffset());
        }

        return CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstants.BASE_STORE_PATH + topicName + "/" + fileName;
    }

    private String createNewCommitLog(String topicName, CommitLogModel commitLog) {
        String newFileName = CommitLogFileNameUtils.incrCommitLogFileName(commitLog.getFileName());
        String path = CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstants.BASE_STORE_PATH + topicName + "/" + newFileName;
        File newCommitLog = new File(path);
        try {
            if (!newCommitLog.createNewFile()) {
                throw new RuntimeException("create new file failed: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFileName;
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
     * @param content 要写入的内容
     * @param force 是否强制刷盘
     */
    public void write(MessageModel messageModel, boolean force) {
        mappedByteBuffer.put(messageModel.toBytes());
        if (force) {
            mappedByteBuffer.force();
        }
    }

    public void write(MessageModel messageModel) {
        write(messageModel, false);
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


}
