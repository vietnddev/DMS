package com.flowiee.dms.config;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.config.entity.SystemConfig;
import com.flowiee.dms.account.repository.AccountRepository;
import com.flowiee.dms.config.repository.SystemConfigRepository;
import com.flowiee.dms.common.utils.constants.AccountStatus;
import com.flowiee.dms.common.utils.constants.ConfigCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final AccountRepository      accountRepository;
    private final SystemConfigRepository sysConfigRepository;

    private int mvMaxFailLogon = 5;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        Account account = accountRepository.findByUsername(username);
        if (account != null) {
            if (account.isLocked()) {
                return;
            }
            account.setFailLogonCount(account.getFailLogonCount() + 1);

            SystemConfig lvFailLogonCountMdl = sysConfigRepository.findByCode(ConfigCode.FailLogonCount.name());
            if (lvFailLogonCountMdl != null && lvFailLogonCountMdl.getValue() != null) {
                mvMaxFailLogon = Integer.parseInt(lvFailLogonCountMdl.getValue());
            }
            if (account.getFailLogonCount() >= mvMaxFailLogon) {
                account.setStatus(AccountStatus.L.name());
            }

            accountRepository.save(account);
        }
    }
}