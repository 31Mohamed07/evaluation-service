
package com.quamtech.evaluation.dto;

import com.quamtech.evaluation.enums.EvaluationStatus;
import com.quamtech.evaluation.enums.EvaluationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EvaluationResponse(
        UUID evaluationId,
        UUID schoolId,
        UUID academicYearId,
        UUID termId,
        UUID classId,
        UUID subjectId,
        UUID teacherId,
        EvaluationType type,
        String title,
        LocalDate evalDate,
        BigDecimal maxScore,
        BigDecimal weight,
        EvaluationStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
