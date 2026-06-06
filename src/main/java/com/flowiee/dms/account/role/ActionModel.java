package com.flowiee.dms.account.role;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActionModel {
    String actionKey;
    String actionLabel;
    String moduleKey;

    public ActionModel() {}
    
    public ActionModel(String actionKey, String actionLabel, String moduleKey) {
    	this.actionKey = actionKey;
    	this.actionLabel = actionLabel;
    	this.moduleKey = moduleKey;
    }
}