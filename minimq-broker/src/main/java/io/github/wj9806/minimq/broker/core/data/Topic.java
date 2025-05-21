package io.github.wj9806.minimq.broker.core.data;

import java.util.List;

public class Topic {

    private String topic;

    private CommitLog lastedCommitLog;

    private List<Queue> queueList;

    private Long createTime;

    private Long updateTime;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Queue> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<Queue> queueList) {
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

    public CommitLog getLastedCommitLog() {
        return lastedCommitLog;
    }

    public void setLastedCommitLog(CommitLog lastedCommitLog) {
        this.lastedCommitLog = lastedCommitLog;
    }
}
