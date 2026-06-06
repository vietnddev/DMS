package com.flowiee.dms.common.utils;

import com.flowiee.dms.category.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeLog {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    final Map<String, Object[]> logChanges;
    final String oldValues;
    final String newValues;

    public ChangeLog(Object oldObject, Object newObject) {
        Map<String, Object[]> changes = new HashMap<>();
        StringBuilder oldValueBuilder = new StringBuilder("Fields: ");
        StringBuilder newValueBuilder = new StringBuilder("Fields: ");
        for (Field field : oldObject.getClass().getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObject);
                Object newValue = field.get(newObject);

                if (oldValue instanceof Category) {
                    oldValue = ((Category) oldValue).getName();
                    newValue = ((Category) newValue).getName();
                }

                if (!Objects.equals(oldValue, newValue)) {
                    changes.put(field.getName(), new Object[] {oldValue, newValue});

                    oldValueBuilder.append(field.getName()).append(" (").append(ObjectUtils.isNotEmpty(oldValue) ? oldValue.toString() : " ").append("); ");
                    newValueBuilder.append(field.getName()).append(" (").append(ObjectUtils.isNotEmpty(newValue) ? newValue.toString() : " ").append("); ");
                }
            } catch (IllegalAccessException e) {
                logger.error("Error when checking entity's fields changes {}", oldObject.getClass().getName(), e);
            }
        }

        if (oldValueBuilder.toString().equals("Fields: "))
            oldValueBuilder = new StringBuilder("Nothing change");
        if (newValueBuilder.toString().equals("Fields: "))
            newValueBuilder = new StringBuilder("Nothing change");

        this.oldValues = formatValueChange(oldValueBuilder.toString());
        this.newValues = formatValueChange(newValueBuilder.toString());
        this.logChanges = changes;
    }

    private String formatValueChange(String valueChange) {
        if (valueChange != null && valueChange.endsWith("; ")) {
            valueChange = valueChange.substring(0, valueChange.length() - 2);
        }
        return valueChange;
    }
}