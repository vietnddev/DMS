package com.flowiee.dms.account.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.account.entity.GroupAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupAccountService extends BaseCurdService<GroupAccount> {
    Page<GroupAccount> findAll(int pageSize, int pageNum);

    List<GroupAccount> findAll();
}