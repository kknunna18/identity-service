package com.mysociety.identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "payment_attempts", schema = "mysociety")
public class PaymentAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "initiated_by")
    private AppUser initiatedBy;

    @Column(name = "payment_reference", nullable = false, length = 100)
    private String paymentReference;

    @Column(name = "idempotency_key", nullable = false, length = 150)
    private String idempotencyKey;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_order_id", length = 150)
    private String providerOrderId;

    @Column(name = "provider_payment_id", length = 150)
    private String providerPaymentId;

    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    @ColumnDefault("'INR'")
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "requested_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedAmount;

    @ColumnDefault("0")
    @Column(name = "confirmed_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal confirmedAmount;

    @ColumnDefault("0")
    @Column(name = "refunded_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal refundedAmount;

    @ColumnDefault("'CREATED'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "failure_code", length = 100)
    private String failureCode;

    @Column(name = "failure_message", length = 500)
    private String failureMessage;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ColumnDefault("0")
    @Column(name = "version", nullable = false)
    private Long version;


}