package com.flowiee.dms.account.security;

import com.flowiee.dms.common.service.BaseAuthorize;
import com.flowiee.dms.account.model.ACTION;
import org.springframework.stereotype.Component;

@Component
public class VldModuleSystem extends BaseAuthorize implements IVldModuleSystem {
    @Override
    public boolean readPermission(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ROLE_READ.name(), throwException);
    }

    @Override
    public boolean readAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACCOUNT_READ.name(), throwException);
    }

    @Override
    public boolean insertAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACCOUNT_CREATE.name(), throwException);
    }

    @Override
    public boolean updateAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACCOUNT_UPDATE.name(), throwException);
    }

    @Override
    public boolean deleteAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_ACCOUNT_DELETE.name(), throwException);
    }

    @Override
    public boolean readLog(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_LOG_READ.name(), throwException);
    }

    @Override
    public boolean setupConfig(boolean throwException) {
        return super.isAuthorized("CONFIG", throwException);
    }

    @Override
    public boolean readGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_R.name(), throwException);
    }

    @Override
    public boolean insertGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_C.name(), throwException);
    }

    @Override
    public boolean updateGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_U.name(), throwException);
    }

    @Override
    public boolean deleteGroupAccount(boolean throwException) {
        return super.isAuthorized(ACTION.SYS_GR_ACC_D.name(), throwException);
    }
}