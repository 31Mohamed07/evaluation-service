
package com.quamtech.evaluation.dto;

import com.quamtech.evaluation.enums.EvaluationType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateEvaluationRequest(

        @NotNull
        UUID academicYearId,

        @NotNull
        UUID termId,

        @NotNull
        UUID classId,

        @NotNull
        UUID subjectId,

        @NotNull
        UUID teacherId,

        @NotNull
        EvaluationType type,

        @NotBlank
        @Size(max = 150)
        String title,

        @NotNull
        LocalDate evalDate,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 4, fraction = 2)
        BigDecimal maxScore,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 4, fraction = 2)
        BigDecimal weight

) {}
