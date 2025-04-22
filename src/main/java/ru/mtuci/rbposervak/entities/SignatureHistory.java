package ru.mtuci.rbposervak.entities;

import jakarta.persistence.*;
import lombok.*;
import ru.mtuci.rbposervak.entities.ENUMS.signature.STATUS;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "signatures_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignatureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_id", referencedColumnName = "id")
    private Signature signature;

    @Column(name = "version_created_at")
    private LocalDateTime versionCreatedAt;

    @Column(name = "threat_name", columnDefinition = "TEXT")
    private String threatName;

    @Column(name = "first_bytes", columnDefinition = "BINARY(8)")
    private byte[] firstBytes;

    private String remainderHash;

    private int remainderLength;

    private String fileType;

    private int offsetStart;

    private int offsetEnd;

    private String digitalSignature;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private STATUS status;
}
