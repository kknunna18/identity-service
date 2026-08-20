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
@Table(name = "visitor_entries", schema = "mysociety")
public class VisitorEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "visitor_approval_id")
    private VisitorApproval visitorApproval;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(name = "visitor_name", nullable = false, length = 150)
    private String visitorName;

    @Column(name = "visitor_mobile", length = 30)
    private String visitorMobile;

    @Column(name = "vehicle_number", length = 30)
    private String vehicleNumber;

    @ColumnDefault("'APPROVED'")
    @Column(name = "entry_type", nullable = false, length = 20)
    private String entryType;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "check_in_at", nullable = false)
    private OffsetDateTime checkInAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "checked_in_by")
    private AppUser checkedInBy;

    @Column(name = "check_out_at")
    private OffsetDateTime checkOutAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "checked_out_by")
    private AppUser checkedOutBy;

    @Column(name = "exception_reason", length = 1000)
    private String exceptionReason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;


}