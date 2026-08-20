package com.mysociety.identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "visitor_approvals", schema = "mysociety")
public class VisitorApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "approval_code", nullable = false, length = 40)
    private String approvalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @Column(name = "visitor_name", nullable = false, length = 150)
    private String visitorName;

    @Column(name = "visitor_mobile", length = 30)
    private String visitorMobile;

    @Column(name = "visitor_type", nullable = false, length = 30)
    private String visitorType;

    @Column(name = "expected_from", nullable = false)
    private OffsetDateTime expectedFrom;

    @Column(name = "valid_until", nullable = false)
    private OffsetDateTime validUntil;

    @ColumnDefault("1")
    @Column(name = "number_of_visitors", nullable = false)
    private Integer numberOfVisitors;

    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @Column(name = "purpose", length = 300)
    private String purpose;

    @Column(name = "instructions", length = 500)
    private String instructions;

    @ColumnDefault("false")
    @Column(name = "multiple_entry", nullable = false)
    private Boolean multipleEntry;

    @ColumnDefault("1")
    @Column(name = "max_entry_count", nullable = false)
    private Integer maxEntryCount;

    @ColumnDefault("0")
    @Column(name = "used_entry_count", nullable = false)
    private Integer usedEntryCount;

    @ColumnDefault("'APPROVED'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

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