package com.ingcase.digitalwallet.model.entity;

import com.ingcase.digitalwallet.model.enums.Status;
import com.ingcase.digitalwallet.model.enums.Type;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Type type;
    private String oppositePartyType;
    private String oppositeParty;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Timestamp createDate;

    @PrePersist
    public void setCreateDate() {
        this.createDate = Timestamp.from(Instant.now());
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id",referencedColumnName = "id")
    private Wallet wallet;

    @Version
    private int version;
}
