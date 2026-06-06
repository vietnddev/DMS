package com.flowiee.dms.storage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.common.entity.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.account.entity.Account;
import com.flowiee.dms.common.utils.constants.DocRight;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "doc_share")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocShare extends BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    Account account;

    @Column(name = "role", nullable = false, length = 20)
    String role;

    public DocShare(Long documentId, Long accountId, String role) {
        this.document = new Document(documentId);
        this.account = new Account(accountId);
        this.role = role;
    }

    public DocShare(Long documentId, Long accountId, DocRight role) {
        this(documentId, accountId, role.getValue());
    }

	@Override
	public String toString() {
		return "DocShare [id=" + super.id + ", document=" + document + ", account=" + account + "]";
	}
}