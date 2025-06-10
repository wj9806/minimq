package io.github.wj9806.minimq.broker.core.data;

import io.github.wj9806.minimq.broker.utils.ByteUtils;

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

    public byte[] toBytes() {
        byte[] commitLogIndexByte = ByteUtils.intToBytes(this.getCommitLogIndex());
        byte[] msgIndexByte = ByteUtils.intToBytes(this.getMsgIndex());
        byte[] msgLengthByte = ByteUtils.intToBytes(this.getMsgLength());

        byte[] consumerQueue = new byte[commitLogIndexByte.length + msgIndexByte.length + msgLengthByte.length];
        System.arraycopy(commitLogIndexByte, 0, consumerQueue, 0, commitLogIndexByte.length);
        System.arraycopy(msgIndexByte, 0, consumerQueue, commitLogIndexByte.length, msgIndexByte.length);
        System.arraycopy(msgLengthByte, 0, consumerQueue, commitLogIndexByte.length + msgIndexByte.length,
                msgLengthByte.length);

        return consumerQueue;
    }
}
