package com.flowiee.dms.audit.model;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.common.utils.AppConstants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserPrincipal extends Account implements UserDetails {
    Long id;
    String username;
    String password;
    String ip;
    boolean isAccountNonExpired;
    boolean isAccountNonLocked;
    boolean isCredentialsNonExpired;
    boolean isEnabled;
    List<GrantedAuthority> grantedAuthorities;

    public UserPrincipal(Account account) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.isAccountNonExpired = true;
        this.isAccountNonLocked = true;
        this.isCredentialsNonExpired = true;
        this.isEnabled = true;
        this.setEmail(account.getEmail());
    }

    public UserPrincipal(Long id, String username, String ip) {
        this.id = id;
        this.username = username;
        this.ip = ip;
    }

    public void setAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean isAdmin() {
        return AppConstants.ADMINISTRATOR.equals(this.getUsername());
    }

    public Account toAccountEntity() {
        return new Account(this.id);
    }

    public static UserPrincipal anonymousUser() {
        return new UserPrincipal(0l, "anonymous", "unknown");
    }
}