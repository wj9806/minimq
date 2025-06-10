package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.core.data.Queue;
import io.github.wj9806.minimq.broker.core.data.Topic;
import io.github.wj9806.minimq.broker.utils.PutMessageLock;
import io.github.wj9806.minimq.broker.utils.UnfairReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Objects;

import static io.github.wj9806.minimq.broker.utils.LogFileNameUtils.*;

public class QueueMMapFile {

    private String topicName;
    private Integer queueId;
    private File file;
    private FileChannel fileChannel;
    private MappedByteBuffer mappedByteBuffer;
    private PutMessageLock putMessageLock;
    private String queueFileName;
    private static final Logger LOGGER = LoggerFactory.getLogger(MMapFile.class);

    /**
     * mmap访问文件
     */
    public void loadFileInMMap(String topicName, Integer queueId, String queueFileName, int startOffset, int size) throws IOException {
        this.topicName = topicName;
        this.queueId = queueId;
        this.queueFileName = queueFileName;

        String filePath = getLatestCommitLog();
        this.doMMap(filePath, startOffset, size);

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
    private String getLatestCommitLog() {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        if (topic == null)
            throw new NullPointerException(topicName + " is invalid");

        List<Queue> queueList = topic.getQueueList();
        Queue queue = queueList.get(queueId);
        if (queue == null) {
            throw new NullPointerException(queueId + " is invalid");
        }

        long diff = queue.countDiff();

        String fileName = null;
        if (Objects.equals(diff, 0)) {
            //写满
            fileName = createNewQueueFile(queue.getFileName());
            queueId++;
        } else if (diff > 0) {
            fileName = queue.getFileName();
        } else if (diff < 0) {
            throw new IllegalStateException(topicName + "'s queue offset state is Illegal: " +
                    queue.getOffsetLimit() + " < " + queue.getLatestOffset());
        }

        return buildQueuePath(topicName, queueId, fileName);
    }

    private String createNewQueueFile(String queueFileName) {
        String newFileName = incrFileName(queueFileName);
        String path = buildQueuePath(topicName, queueId, newFileName);
        File queueFile = new File(path);
        try {
            if (!queueFile.createNewFile()) {
                throw new RuntimeException("create new file failed: " + path);
            }
            LOGGER.info("create new queue file, topic: {}, file: {}", topicName, newFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFileName;
    }
}
