package com.flowiee.dms.account.role;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModuleModel {
    String moduleKey;
    String moduleLabel;
}