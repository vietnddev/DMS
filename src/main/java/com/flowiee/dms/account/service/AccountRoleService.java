package com.flowiee.dms.account.service;

import com.flowiee.dms.account.entity.AccountRole;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.role.ActionModel;
import com.flowiee.dms.account.role.RoleModel;

import java.util.List;
import java.util.Optional;

public interface AccountRoleService {
    List<RoleModel> findAllRoleByAccountId(Long accountId);

    List<RoleModel> findAllRoleByGroupId(Long groupId);

    List<ActionModel> findAllAction();

    Optional<AccountRole> findById(Long id);

    List<AccountRole> findByAccountId(Long accountId);

    List<AccountRole> findByGroupId(Long accountId);

    String updatePermission(String moduleKey, String actionKey, Long accountId);

    boolean isAuthorized(long accountId, String module, String action);

    String deleteAllRole(Long groupId, Long accountId);

    List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId);

    List<AccountRole> findByAction(ACTION action);
}