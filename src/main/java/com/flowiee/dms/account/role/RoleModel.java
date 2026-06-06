package com.flowiee.dms.account.role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleModel {
    Long accountId;
    Long groupId;
    ModuleModel module;
    ActionModel action;
    Boolean isAuthor;
}