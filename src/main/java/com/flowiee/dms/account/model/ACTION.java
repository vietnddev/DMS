package com.flowiee.dms.account.model;

public enum ACTION {
    CTG_READ("Xem danh mục hệ thống", "CATEGORY"),
    CTG_CREATE("Thêm mới danh mục", "CATEGORY"),
    CTG_UPDATE("Cập nhật danh mục", "CATEGORY"),
    CTG_DELETE("Xóa danh mục", "CATEGORY"),
    CTG_IMPORT("Import danh mục", "CATEGORY"),
    CTG_EXPORT("Export danh mục", "CATEGORY"),

    STG_DASHBOARD("Xem dashboard STG", "STORAGE"),
    STG_DOC_READ("Xem danh sách tài liệu", "STORAGE"),
    STG_DOC_CREATE("Thêm mới tài liệu", "STORAGE"),
    STG_DOC_UPDATE("Cập nhật tài liệu", "STORAGE"),
    STG_DOC_DELETE("Xóa tài liệu", "STORAGE"),
    STG_DOC_MOVE("Di chuyển tài liệu", "STORAGE"),
    STG_DOC_COPY("Copy tài liệu", "STORAGE"),
    STG_DOC_DOWNLOAD("Download tài liệu", "STORAGE"),
    STG_DOC_SHARE("Chia sẽ tài liệu", "STORAGE"),
    STG_DOC_DOCTYPE_CONFIG("Cấu hình loại tài liệu", "STORAGE"),
    STG_MATERIAL_READ("Xem danh sách nguyên vật liệu", "STORAGE"),
    STG_MATERIAL_CREATE("Thêm mới nguyên vật liệu", "STORAGE"),
    STG_MATERIAL_UPDATE("Cập nhật nguyên vật liệu", "STORAGE"),
    STG_MATERIAL_DELETE("Xóa nguyên vật liệu", "STORAGE"),
    STG_TICKET_IMPORT_GOODS("Nhập hàng", "STORAGE"),
    STG_TICKET_EXPORT_GOODS("Xuất hàng", "STORAGE"),
    STG_DOC_ARCHIVE("Lưu trữ phiên bản", "STORAGE"),

    SYS_ROLE_READ("Xem quyền hệ thống", "SYSTEM"),
    SYS_LOG_READ("Xem nhật ký hệ thống", "SYSTEM"),
    SYS_LOGIN("Đăng nhập", "SYSTEM"),
    SYS_LOGOUT("Đăng xuất", "SYSTEM"),
    SYS_RESET_PASSWORD("Đổi mật khẩu", "SYSTEM"),
    SYS_ACCOUNT_READ("Xem danh sách tài khoản", "SYSTEM"),
    SYS_ACCOUNT_CREATE("Thêm mới tài khoản", "SYSTEM"),
    SYS_ACCOUNT_UPDATE("Cập nhật tài khoản", "SYSTEM"),
    SYS_ACCOUNT_DELETE("Xóa tài khoản", "SYSTEM"),
    SYS_ACCOUNT_RESET_PASSWORD("Reset mật khẩu tài khoản", "SYSTEM"),
    SYS_ACCOUNT_SHARE_ROLE("Phân quyền tài khoản", "SYSTEM"),
    SYS_GR_ACC_R("View list of account groups", "SYSTEM"),
    SYS_GR_ACC_C("Create account group", "SYSTEM"),
    SYS_GR_ACC_U("Update account group", "SYSTEM"),
    SYS_GR_ACC_D("Delete account group", "SYSTEM");

    private final String label;
    private final String module;

    ACTION(String label, String module) {
        this.label = label;
        this.module = module;
    }

    public String getActionKey() {
        return this.name();
    }

    public String getActionLabel() {
        return label;
    }

    public String getModuleKey() {
        return module;
    }

    public String getModuleLabel() {
        if (MODULE.CATEGORY.name().equals(module)) return MODULE.CATEGORY.getLabel();
        if (MODULE.STORAGE.name().equals(module)) return MODULE.STORAGE.getLabel();
        if (MODULE.SYSTEM.name().equals(module)) return MODULE.SYSTEM.getLabel();
        return null;
    }
}