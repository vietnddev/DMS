package com.flowiee.dms.storage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
@Setter
public class SummaryQuota {
    private String totalMemoryUsed;
    private List<DocumentQuota> documents;
    @JsonIgnore
    private Page<Object[]> documentQuotaPage;

    @Builder
    @Getter
    @Setter
    public static class DocumentQuota {
        private int id;
        private String icon;
        private String name;
        private String memoryUsed;
    }
}