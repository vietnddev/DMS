package com.flowiee.dms.account.repository;

import com.flowiee.dms.account.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    List<AccountRole> findByAccountId(Long accountId);

    List<AccountRole> findByGroupId(Long accountId);

    @Query("from AccountRole " +
            "where 1=1 " +
            "and (:groupId is null or groupId=:groupId) " +
            "and (:accountId is null or accountId=:accountId) " +
            "and module=:module " +
            "and action=:action")
    AccountRole isAuthorized(@Param("groupId") Long groupId,
                             @Param("accountId") Long accountId,
                             @Param("module") String module,
                             @Param("action") String action);

    @Modifying
    void deleteByAccountId(Long accountId);

    @Query("from AccountRole r " +
            "where 1=1 " +
            "and (:module is null or r.module=:module) " +
            "and (:action is null or r.action=:action)")
    List<AccountRole> findByModuleAndAction(@Param("module") String module, @Param("action") String action);
}