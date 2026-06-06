package com.flowiee.dms.account.service;

import com.flowiee.dms.common.service.BaseCurdService;
import com.flowiee.dms.account.entity.Account;

import java.util.List;

public interface AccountService extends BaseCurdService<Account> {
    List<Account> findAll();

    Account findByUsername(String username);

    Account findCurrentAccount();

    Account getUserByResetTokens(String token);

    void updateTokenForResetPassword(String email, String resetToken);

    void resetPassword(Account account);
}