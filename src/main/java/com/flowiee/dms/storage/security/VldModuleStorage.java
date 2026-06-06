package com.flowiee.dms.storage.security;

import com.flowiee.dms.common.service.BaseAuthorize;
import com.flowiee.dms.account.model.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleStorage extends BaseAuthorize implements IVldModuleStorage {
    @Override
    public boolean dashboard(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DASHBOARD.name(), throwException);
    }

    @Override
    public boolean readDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_READ.name(), throwException);
    }

    @Override
    public boolean insertDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_CREATE.name(), throwException);
    }

    @Override
    public boolean updateDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_UPDATE.name(), throwException);
    }

    @Override
    public boolean deleteDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_DELETE.name(), throwException);
    }

    @Override
    public boolean moveDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_MOVE.name(), throwException);
    }

    @Override
    public boolean copyDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_COPY.name(), throwException);
    }

    @Override
    public boolean downloadDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_DOWNLOAD.name(), throwException);
    }

    @Override
    public boolean shareDoc(boolean throwException) {
        return super.isAuthorized(ACTION.STG_DOC_SHARE.name(), throwException);
    }
}