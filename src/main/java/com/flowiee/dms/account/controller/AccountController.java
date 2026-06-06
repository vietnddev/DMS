package com.flowiee.dms.account.controller;

import com.flowiee.dms.common.web.BaseController;
import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.common.exception.AppException;
import com.flowiee.dms.common.exception.BadRequestException;
import com.flowiee.dms.common.exception.DataExistsException;
import com.flowiee.dms.common.exception.ResourceNotFoundException;
import com.flowiee.dms.common.model.ApiResponse;
import com.flowiee.dms.account.role.RoleModel;
import com.flowiee.dms.account.service.AccountService;
import com.flowiee.dms.account.service.AccountRoleService;
import com.flowiee.dms.common.utils.constants.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${app.api.prefix}/system/account")
@Tag(name = "Account system API", description = "Quản lý tài khoản hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AccountController extends BaseController {
    AccountRoleService roleService;
    AccountService accountService;

    @Operation(summary = "Find all accounts")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public ApiResponse<List<Account>> findAllAccounts() {
        try {
            return ApiResponse.ok(accountService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "account"), ex);
        }
    }

    @Operation(summary = "Find detail account")
    @GetMapping("/{accountId}")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public ApiResponse<Account> findDetailAccount(@PathVariable("accountId") Long accountId) {
        Optional<Account> account = accountService.findById(accountId);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Account not found!", false);
        }
        return ApiResponse.ok(account.get());
    }

    @Operation(summary = "Create account")
    @PostMapping(value = "/create")
    @PreAuthorize("@vldModuleSystem.insertAccount(true)")
    public ApiResponse<Account> save(@RequestBody Account account) {
        try {
            if (account == null) {
                throw new BadRequestException();
            }
            if (accountService.findByUsername(account.getUsername()) != null) {
                throw new DataExistsException("Username exists!");
            }
            return ApiResponse.ok(accountService.save(account));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR.getDescription(), "account"), ex);
        }
    }

    @Operation(summary = "Update account")
    @PutMapping(value = "/update/{accountId}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public ApiResponse<Account> update(@RequestBody Account account, @PathVariable("accountId") Long accountId) {
        try {
            if (accountId <= 0 || accountService.findById(accountId).isEmpty()) {
                throw new BadRequestException();
            }
            return ApiResponse.ok(accountService.update(account, accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR.getDescription(), "account"), ex);
        }
    }

    @PutMapping("/update-permission/{accountId}")
    @PreAuthorize("@vldModuleSystem.updateAccount(true)")
    public ApiResponse<List<RoleModel>> updatePermission(@RequestBody String[] actions, @PathVariable("accountId") Long accountId) {
        try {
            if (accountId <= 0 || accountService.findById(accountId).isEmpty()) {
                throw new BadRequestException();
            }
//            roleService.deleteAllRole(accountId);
//            List<ActionOfModule> listAction = roleService.findAllAction();
//            for (ActionOfModule sysAction : listAction) {
//                String clientActionKey = request.getParameter(sysAction.getActionKey());
//                if (clientActionKey != null) {
//                    boolean isAuthorSelected = clientActionKey.equals("on");
//                    if (isAuthorSelected) {
//                        roleService.updatePermission(sysAction.getModuleKey(), sysAction.getActionKey(), accountId);
//                    }
//                }
//            }
            return ApiResponse.ok(null);
        } catch (RuntimeException ex) {
            throw new AppException();
        }
    }

    @Operation(summary = "Delete account")
    @DeleteMapping(value = "/delete/{accountId}")
    @PreAuthorize("@vldModuleSystem.deleteAccount(true)")
    public ApiResponse<String> deleteAccount(@PathVariable("accountId") Long accountId) {
        try {
            if (accountId <= 0 ||accountService.findById(accountId).isEmpty()) {
                throw new ResourceNotFoundException("Account not found!", false);
            }
            return ApiResponse.ok(accountService.delete(accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.DELETE_ERROR.getDescription(), "account"), ex);
        }
    }

    @Operation(summary = "Find roles of account")
    @GetMapping("/{accountId}/role")
    @PreAuthorize("@vldModuleSystem.readAccount(true)")
    public ApiResponse<List<RoleModel>> findRolesOfAccount(@PathVariable("accountId") Long accountId) {
        try {
            return ApiResponse.ok(roleService.findAllRoleByAccountId(accountId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "role"), ex);
        }
    }
}