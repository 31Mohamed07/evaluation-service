
package com.quamtech.evaluation.controller;

import com.quamtech.evaluation.service.EvaluationService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.quamtech.evaluation.dto.CreateEvaluationRequest;
import com.quamtech.evaluation.entity.Evaluation;
import com.quamtech.evaluation.enums.EvaluationStatus;
import com.quamtech.evaluation.enums.EvaluationType;
import com.quamtech.evaluation.dto.UpdateEvaluationRequest;
import java.time.Instant;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EvaluationController.class)
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationService evaluationService;

    @Test
    void shouldReturnBadRequestWhenSchoolHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/evaluations"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldReturnEmptyListWhenSchoolHasNoEvaluations() throws Exception {
        UUID schoolId = UUID.randomUUID();

        when(evaluationService.getAllEvaluations(schoolId))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/evaluations")
                        .header("X-School-Id", schoolId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldCreateEvaluationSuccessfully() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UUID academicYearId = UUID.randomUUID();
        UUID termId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID teacherId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = Evaluation.builder()
                .evaluationId(evaluationId)
                .schoolId(schoolId)
                .academicYearId(academicYearId)
                .termId(termId)
                .classId(classId)
                .subjectId(subjectId)
                .teacherId(teacherId)
                .type(EvaluationType.DEVOIR)
                .title("Devoir de mathématiques")
                .evalDate(LocalDate.of(2026, 11, 20))
                .maxScore(new BigDecimal("20.00"))
                .weight(new BigDecimal("2.00"))
                .status(EvaluationStatus.DRAFT)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(userId)
                .build();

        when(evaluationService.createEvaluation(any(Evaluation.class)))
                .thenReturn(evaluation);

        String requestBody = """
            {
                "academicYearId": "%s",
                "termId": "%s",
                "classId": "%s",
                "subjectId": "%s",
                "teacherId": "%s",
                "type": "DEVOIR",
                "title": "Devoir de mathématiques",
                "evalDate": "2026-11-20",
                "maxScore": 20.00,
                "weight": 2.00
            }
            """.formatted(
                academicYearId,
                termId,
                classId,
                subjectId,
                teacherId
        );

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("X-School-Id", schoolId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evaluationId").value(evaluationId.toString()))
                .andExpect(jsonPath("$.title").value("Devoir de mathématiques"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }
    @Test
    void shouldRejectEvaluationWithInvalidData() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String requestBody = """
            {
                "academicYearId": null,
                "termId": null,
                "classId": null,
                "subjectId": null,
                "teacherId": null,
                "type": null,
                "title": "",
                "evalDate": null,
                "maxScore": 0,
                "weight": 0
            }
            """;

        mockMvc.perform(post("/api/v1/evaluations")
                        .header("X-School-Id", schoolId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(evaluationService, never()).createEvaluation(any(Evaluation.class));
    }
    @Test
    void shouldReturnEvaluationById() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = Evaluation.builder()
                .evaluationId(evaluationId)
                .schoolId(schoolId)
                .academicYearId(UUID.randomUUID())
                .termId(UUID.randomUUID())
                .classId(UUID.randomUUID())
                .subjectId(UUID.randomUUID())
                .teacherId(UUID.randomUUID())
                .type(EvaluationType.COMPOSITION)
                .title("Composition de mathématiques")
                .evalDate(LocalDate.of(2026, 11, 20))
                .maxScore(new BigDecimal("20.00"))
                .weight(new BigDecimal("3.00"))
                .status(EvaluationStatus.PUBLISHED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(UUID.randomUUID())
                .build();

        when(evaluationService.getEvaluationById(evaluationId, schoolId))
                .thenReturn(evaluation);

        mockMvc.perform(get("/api/v1/evaluations/{evaluationId}", evaluationId)
                        .header("X-School-Id", schoolId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evaluationId").value(evaluationId.toString()))
                .andExpect(jsonPath("$.title").value("Composition de mathématiques"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }
    private Evaluation createEvaluation(
            UUID evaluationId,
            UUID schoolId,
            EvaluationStatus status,
            String title
    ) {
        return Evaluation.builder()
                .evaluationId(evaluationId)
                .schoolId(schoolId)
                .academicYearId(UUID.randomUUID())
                .termId(UUID.randomUUID())
                .classId(UUID.randomUUID())
                .subjectId(UUID.randomUUID())
                .teacherId(UUID.randomUUID())
                .type(EvaluationType.DEVOIR)
                .title(title)
                .evalDate(LocalDate.of(2026, 11, 20))
                .maxScore(new BigDecimal("20.00"))
                .weight(new BigDecimal("2.00"))
                .status(status)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .createdBy(UUID.randomUUID())
                .build();
    }
    @Test
    void shouldUpdateEvaluationSuccessfully() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = createEvaluation(
                evaluationId,
                schoolId,
                EvaluationStatus.DRAFT,
                "Devoir modifié"
        );

        when(evaluationService.updateEvaluation(
                eq(evaluationId),
                eq(schoolId),
                any(Evaluation.class)
        )).thenReturn(evaluation);

        String requestBody = """
            {
                "academicYearId": "%s",
                "termId": "%s",
                "classId": "%s",
                "subjectId": "%s",
                "teacherId": "%s",
                "type": "DEVOIR",
                "title": "Devoir modifié",
                "evalDate": "2026-11-20",
                "maxScore": 20.00,
                "weight": 2.00
            }
            """.formatted(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        mockMvc.perform(put("/api/v1/evaluations/{evaluationId}", evaluationId)
                        .header("X-School-Id", schoolId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Devoir modifié"));
    }
    @Test
    void shouldDeleteEvaluationSuccessfully() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        mockMvc.perform(delete(
                        "/api/v1/evaluations/{evaluationId}", evaluationId)
                        .header("X-School-Id", schoolId))
                .andExpect(status().isNoContent());

        verify(evaluationService).deleteEvaluation(evaluationId, schoolId);
    }
    @Test
    void shouldPublishEvaluationSuccessfully() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = createEvaluation(
                evaluationId,
                schoolId,
                EvaluationStatus.PUBLISHED,
                "Devoir publié"
        );

        when(evaluationService.publishEvaluation(evaluationId, schoolId))
                .thenReturn(evaluation);

        mockMvc.perform(patch(
                        "/api/v1/evaluations/{evaluationId}/publish",
                        evaluationId)
                        .header("X-School-Id", schoolId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }
    @Test
    void shouldCloseEvaluationSuccessfully() throws Exception {

        UUID schoolId = UUID.randomUUID();
        UUID evaluationId = UUID.randomUUID();

        Evaluation evaluation = createEvaluation(
                evaluationId,
                schoolId,
                EvaluationStatus.CLOSED,
                "Devoir clôturé"
        );

        when(evaluationService.closeEvaluation(evaluationId, schoolId))
                .thenReturn(evaluation);

        mockMvc.perform(patch(
                        "/api/v1/evaluations/{evaluationId}/close",
                        evaluationId)
                        .header("X-School-Id", schoolId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}
