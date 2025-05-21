package io.github.wj9806.minimq.broker.core.data;

import java.util.concurrent.atomic.AtomicInteger;

public class CommitLog {

    private String fileName;

    //文件写入的最大上限
    private Long offsetLimit;

    private AtomicInteger offset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public AtomicInteger getOffset() {
        return offset;
    }

    public void setOffset(AtomicInteger offset) {
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
