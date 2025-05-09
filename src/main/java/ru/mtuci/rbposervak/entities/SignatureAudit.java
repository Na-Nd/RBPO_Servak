package ru.mtuci.rbposervak.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import ru.mtuci.rbposervak.entities.ENUMS.signature.ChangeType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signatures_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"signature"})
public class SignatureAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_id", referencedColumnName = "id")
    private Signature signature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", referencedColumnName = "id")
    private User changedBy;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @Column(name = "change_type")
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    @Column(name = "fields_changed", columnDefinition = "TEXT")
    private String fieldsChanged;
}
