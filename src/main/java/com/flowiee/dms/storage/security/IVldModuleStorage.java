package com.flowiee.dms.storage.security;

public interface IVldModuleStorage {
    boolean dashboard(boolean throwException);

    boolean readDoc(boolean throwException);

    boolean insertDoc(boolean throwException);

    boolean updateDoc(boolean throwException);

    boolean deleteDoc(boolean throwException);

    boolean moveDoc(boolean throwException);

    boolean copyDoc(boolean throwException);

    boolean downloadDoc(boolean throwException);

    boolean shareDoc(boolean throwException);
}