package io.github.wj9806.minimq.broker.core.data;

public class ConsumerQueue {

    private int commitLogIndex;

    private int msgIndex;

    private int msgLength;

    public int getCommitLogIndex() {
        return commitLogIndex;
    }

    public void setCommitLogIndex(int commitLogIndex) {
        this.commitLogIndex = commitLogIndex;
    }

    public int getMsgIndex() {
        return msgIndex;
    }

    public void setMsgIndex(int msgIndex) {
        this.msgIndex = msgIndex;
    }

    public int getMsgLength() {
        return msgLength;
    }

    public void setMsgLength(int msgLength) {
        this.msgLength = msgLength;
    }
}
