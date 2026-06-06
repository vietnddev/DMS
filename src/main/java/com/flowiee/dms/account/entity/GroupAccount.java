package com.flowiee.dms.account.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "group_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupAccount extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "group_code", length = 20)
    String groupCode;

    @Column(name = "group_name", nullable = false)
    String groupName;

    @Column(name = "note", length = 500)
    String note;

    public GroupAccount(Long id, String groupCode, String groupName) {
        this.id = id;
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "groupAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Account> listAccount;
}