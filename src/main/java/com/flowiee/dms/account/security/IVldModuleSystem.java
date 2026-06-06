package com.flowiee.dms.account.security;

public interface IVldModuleSystem {
    boolean readPermission(boolean throwException);

    boolean readAccount(boolean throwException);

    boolean insertAccount(boolean throwException);

    boolean updateAccount(boolean throwException);

    boolean deleteAccount(boolean throwException);

    boolean readLog(boolean throwException);

    boolean setupConfig(boolean throwException);

    boolean readGroupAccount(boolean throwException);

    boolean insertGroupAccount(boolean throwException);

    boolean updateGroupAccount(boolean throwException);

    boolean deleteGroupAccount(boolean throwException);
}