package com.flowiee.dms.account.repository;

import com.flowiee.dms.account.entity.GroupAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupAccountRepository extends JpaRepository<GroupAccount, Long> {
}