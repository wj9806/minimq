package io.github.wj9806.minimq.broker.model;

import java.util.List;

public class TopicModel {

    private String topic;

    private CommitLogModel lastedCommitLog;

    private List<QueueModel> queueList;

    private Long createTime;

    private Long updateTime;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<QueueModel> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<QueueModel> queueList) {
        this.queueList = queueList;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public CommitLogModel getLastedCommitLog() {
        return lastedCommitLog;
    }

    public void setLastedCommitLog(CommitLogModel lastedCommitLog) {
        this.lastedCommitLog = lastedCommitLog;
    }
}
