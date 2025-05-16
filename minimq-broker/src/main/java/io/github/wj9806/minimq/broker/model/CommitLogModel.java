package io.github.wj9806.minimq.broker.model;

public class CommitLogModel {

    private String fileName;

    //文件写入的最大上限
    private Long offsetLimit;

    private Long offset;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getOffsetLimit() {
        return offsetLimit;
    }

    public void setOffsetLimit(Long offsetLimit) {
        this.offsetLimit = offsetLimit;
    }
}
