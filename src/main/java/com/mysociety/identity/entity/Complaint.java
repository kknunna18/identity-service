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
@Table(name = "complaints", schema = "mysociety")
public class Complaint {
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
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(name = "complaint_number", nullable = false, length = 80)
    private String complaintNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "reported_by", nullable = false)
    private AppUser reportedBy;

    @Column(name = "category", nullable = false, length = 60)
    private String category;

    @Column(name = "subcategory", length = 80)
    private String subcategory;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "location_details", length = 300)
    private String locationDetails;

    @ColumnDefault("'MEDIUM'")
    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @ColumnDefault("'OPEN'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "assigned_user_id")
    private AppUser assignedUser;

    @Column(name = "sla_due_at")
    private OffsetDateTime slaDueAt;

    @Column(name = "preferred_visit_at")
    private OffsetDateTime preferredVisitAt;

    @ColumnDefault("false")
    @Column(name = "entry_permission", nullable = false)
    private Boolean entryPermission;

    @Column(name = "resolution_summary", length = Integer.MAX_VALUE)
    private String resolutionSummary;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "resident_rating")
    private Short residentRating;

    @Column(name = "resident_feedback", length = 1000)
    private String residentFeedback;

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