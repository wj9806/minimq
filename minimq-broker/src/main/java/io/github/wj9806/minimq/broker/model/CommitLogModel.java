package io.github.wj9806.minimq.broker.model;

import java.util.concurrent.atomic.AtomicLong;

public class CommitLogModel {

    private String fileName;

    //文件写入的最大上限
    private Long offsetLimit;

    private AtomicLong offset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AtomicLong getOffset() {
        return offset;
    }

    public void setOffset(AtomicLong offset) {
        this.offset = offset;
    }

    public Long getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }

    public Long countDiff() {
        return this.offsetLimit - this.offset.get();
    }
}
