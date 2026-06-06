package com.flowiee.dms.account.service.impl;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.account.entity.AccountRole;
import com.flowiee.dms.account.entity.GroupAccount;
import com.flowiee.dms.account.model.ACTION;
import com.flowiee.dms.account.role.ActionModel;
import com.flowiee.dms.account.role.RoleModel;
import com.flowiee.dms.account.role.ModuleModel;
import com.flowiee.dms.account.repository.AccountRepository;
import com.flowiee.dms.account.repository.AccountRoleRepository;
import com.flowiee.dms.account.service.AccountRoleService;
import com.flowiee.dms.account.service.GroupAccountService;
import com.flowiee.dms.common.service.BaseService;
import com.flowiee.dms.common.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountRoleServiceImpl extends BaseService implements AccountRoleService {
    AccountRepository accountRepository;
    GroupAccountService groupAccountService;
    AccountRoleRepository accountRoleRepository;

    @Override
    public List<RoleModel> findAllRoleByAccountId(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            return new ArrayList<>();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRole(null, accountId, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<RoleModel> findAllRoleByGroupId(Long groupId) {
        Optional<GroupAccount> groupAcc = groupAccountService.findById(groupId);
        if (groupAcc.isEmpty()) {
            return List.of();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRole(groupId, null, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<ActionModel> findAllAction() {
        List<ActionModel> listAction = new ArrayList<>();
        for (ACTION sysAction : ACTION.values()) {
            listAction.add(new ActionModel(sysAction.getActionKey(), sysAction.getActionLabel(), sysAction.getModuleKey()));
        }
        return listAction;
    }

    @Override
    public Optional<AccountRole> findById(Long id) {
        return accountRoleRepository.findById(id);
    }

    @Override
    public List<AccountRole> findByAccountId(Long accountId) {
        return accountRoleRepository.findByAccountId(accountId);
    }

    @Override
    public List<AccountRole> findByGroupId(Long accountId) {
        return accountRoleRepository.findByGroupId(accountId);
    }

    @Override
    public String updatePermission(String moduleKey, String actionKey, Long accountId) {
        accountRoleRepository.save(new AccountRole(moduleKey, actionKey, accountId, null));
        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Override
    public boolean isAuthorized(long accountId, String module, String action) {
        return accountRoleRepository.isAuthorized(null, accountId, module, action) != null;
    }

    @Override
    public String deleteAllRole(Long groupId, Long accountId) {
        if (groupId == null && accountId == null) {
            throw new IllegalArgumentException("groupId and accountId cannot be null");
        }
        accountRoleRepository.deleteByAccountId(accountId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId) {
        this.deleteAllRole(groupId, null);
        List<RoleModel> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(rights)) {
            return list;
        }
        for (RoleModel role : rights) {
            if (ObjectUtils.isEmpty(role)) {
                return list;
            }
            if (role.getIsAuthor() == null || !role.getIsAuthor()) {
                continue;
            }
            String moduleKey = role.getModule().getModuleKey();
            String actionKey = role.getAction().getActionKey();
            accountRoleRepository.save(new AccountRole(moduleKey, actionKey, null, groupId));
            list.add(role);
        }
        return list;
    }

    @Override
    public List<AccountRole> findByAction(ACTION action) {
        return accountRoleRepository.findByModuleAndAction(action.getModuleKey(), action.getActionKey());
    }

    private RoleModel initRole(Long pGroupId, Long pAccountId, String pModuleKey, String pModuleLabel, String pActionKey, String pActionLabel) {
        RoleModel roleModel = new RoleModel();

        ModuleModel module = new ModuleModel();
        module.setModuleKey(pModuleKey);
        module.setModuleLabel(pModuleLabel);
        roleModel.setModule(module);

        ActionModel action = new ActionModel();
        action.setActionKey(pActionKey);
        action.setActionLabel(pActionLabel);
        action.setModuleKey(pModuleKey);
        roleModel.setAction(action);

        AccountRole isAuthor = accountRoleRepository.isAuthorized(pGroupId, pAccountId, pModuleKey, pActionKey);
        roleModel.setIsAuthor(isAuthor != null);

        roleModel.setGroupId(pGroupId);
        roleModel.setAccountId(pAccountId);

        return roleModel;
    }
}