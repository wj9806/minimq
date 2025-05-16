package io.github.wj9806.minimq.broker.model;

public class QueueModel {

    private Integer id;

    private Long minOffset;

    private Long currentOffset;

    private Long maxOffset;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMinOffset() {
        return minOffset;
    }

    public void setMinOffset(Long minOffset) {
        this.minOffset = minOffset;
    }

    public Long getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(Long currentOffset) {
        this.currentOffset = currentOffset;
    }

    public Long getMaxOffset() {
        return maxOffset;
    }

    public void setMaxOffset(Long maxOffset) {
        this.maxOffset = maxOffset;
    }
}
