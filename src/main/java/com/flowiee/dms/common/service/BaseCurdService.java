package com.flowiee.dms.common.service;

import java.util.Optional;

public interface BaseCurdService<T> {
    Optional<T> findById(Long entityId);

    T save(T entity);

    T update(T entity, Long entityId);

    String delete(Long entityId);
}