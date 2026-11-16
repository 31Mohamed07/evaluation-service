
package com.quamtech.evaluation.entity;

import com.quamtech.evaluation.enums.EvaluationStatus;
import com.quamtech.evaluation.enums.EvaluationType;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "evaluation_id", nullable = false, updatable = false)
    private UUID evaluationId;

    @NotNull
    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @NotNull
    @Column(name = "academic_year_id", nullable = false)
    private UUID academicYearId;

    @NotNull
    @Column(name = "term_id", nullable = false)
    private UUID termId;

    @NotNull
    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @NotNull
    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

    @NotNull
    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private EvaluationType type;

    @NotNull
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotNull
    @Column(name = "eval_date", nullable = false)
    private LocalDate evalDate;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "max_score", nullable = false, precision = 6, scale = 2)
    private BigDecimal maxScore;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "weight", nullable = false, precision = 6, scale = 2)
    private BigDecimal weight;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private EvaluationStatus status = EvaluationStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
