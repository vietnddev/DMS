package com.flowiee.dms.common.service;

import com.flowiee.dms.storage.model.EximModel;
import com.flowiee.dms.common.utils.constants.TemplateExport;

public interface ExportService {
    EximModel exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}