package io.github.wj9806.minimq.broker.core.data;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class QueueOffset {

    private OffsetTable offsetTable;

    private static class OffsetTable {
        @JsonIgnore  // 不直接序列化该字段，而是动态处理
        private Map<String, GroupDetail> groupDetailMap = new HashMap<>();

        // 动态解析 JSON 中的 topic -> group -> partition 结构
        @JsonAnySetter
        public void addGroupDetail(String topic, Map<String, Map<String, String>> groupOffsets) {
            if (groupDetailMap == null) {
                groupDetailMap = new HashMap<>();
            }
            GroupDetail groupDetail = new GroupDetail();
            groupDetail.setGroupMap(groupOffsets);
            groupDetailMap.put(topic, groupDetail);
        }

        // 动态生成 JSON 时使用
        @JsonAnyGetter
        public Map<String, Map<String, Map<String, String>>> getGroupDetails() {
            Map<String, Map<String, Map<String, String>>> result = new HashMap<>();
            if (groupDetailMap != null) {
                for (Map.Entry<String, GroupDetail> entry : groupDetailMap.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().getGroupMap());
                }
            }
            return result;
        }

        public Map<String, GroupDetail> getGroupDetailMap() {
            return groupDetailMap;
        }

        public void setGroupDetailMap(Map<String, GroupDetail> groupDetailMap) {
            this.groupDetailMap = groupDetailMap;
        }
    }

    private static class GroupDetail {
        private Map<String, Map<String, String>> groupMap;

        public Map<String, Map<String, String>> getGroupMap() {
            return groupMap;
        }

        public void setGroupMap(Map<String, Map<String, String>> groupMap) {
            this.groupMap = groupMap;
        }
    }

    public OffsetTable getOffsetTable() {
        return offsetTable;
    }

    public void setOffsetTable(OffsetTable offsetTable) {
        this.offsetTable = offsetTable;
    }
}
