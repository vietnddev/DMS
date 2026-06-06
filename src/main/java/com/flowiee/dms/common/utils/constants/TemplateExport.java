package com.flowiee.dms.common.utils.constants;

import com.flowiee.dms.account.model.MODULE;
import lombok.Getter;

@Getter
public enum TemplateExport {
    EX_LIST_OF_DOCUMENTS("Template_E_Document.xlsx", "E", MODULE.STORAGE, MasterObject.Document.name());

    private final String templateName;
    private final String type;
    private final MODULE module;
    private final String entity;

    TemplateExport(String templateName, String type, MODULE module, String entity) {
        this.templateName = templateName;
        this.type = type;
        this.module = module;
        this.entity = entity;
    }
}