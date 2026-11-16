
package com.quamtech.evaluation.controller;

import com.quamtech.evaluation.dto.CreateEvaluationRequest;
import com.quamtech.evaluation.dto.UpdateEvaluationRequest;
import com.quamtech.evaluation.dto.EvaluationResponse;
import com.quamtech.evaluation.entity.Evaluation;
import com.quamtech.evaluation.service.EvaluationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    // Conversion d'une entité en DTO de réponse
    private EvaluationResponse toResponse(Evaluation evaluation) {
        return new EvaluationResponse(
                evaluation.getEvaluationId(),
                evaluation.getSchoolId(),
                evaluation.getAcademicYearId(),
                evaluation.getTermId(),
                evaluation.getClassId(),
                evaluation.getSubjectId(),
                evaluation.getTeacherId(),
                evaluation.getType(),
                evaluation.getTitle(),
                evaluation.getEvalDate(),
                evaluation.getMaxScore(),
                evaluation.getWeight(),
                evaluation.getStatus(),
                evaluation.getCreatedAt(),
                evaluation.getUpdatedAt()
        );
    }

    // Créer une évaluation
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationResponse createEvaluation(
            @Valid @RequestBody CreateEvaluationRequest request,
            @RequestHeader("X-School-Id") UUID schoolId,
            @RequestHeader("X-User-Id") UUID userId
    ) {

        Evaluation evaluation = Evaluation.builder()
                .schoolId(schoolId)
                .createdBy(userId)
                .academicYearId(request.academicYearId())
                .termId(request.termId())
                .classId(request.classId())
                .subjectId(request.subjectId())
                .teacherId(request.teacherId())
                .type(request.type())
                .title(request.title())
                .evalDate(request.evalDate())
                .maxScore(request.maxScore())
                .weight(request.weight())
                .build();

        return toResponse(
                evaluationService.createEvaluation(evaluation)
        );
    }

    // Récupérer toutes les évaluations d'un établissement
    @GetMapping
    public List<EvaluationResponse> getAllEvaluations(
            @RequestHeader("X-School-Id") UUID schoolId
    ) {
        return evaluationService.getAllEvaluations(schoolId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Récupérer une évaluation par son identifiant
    @GetMapping("/{evaluationId}")
    public EvaluationResponse getEvaluationById(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-School-Id") UUID schoolId
    ) {
        return toResponse(
                evaluationService.getEvaluationById(
                        evaluationId,
                        schoolId
                )
        );
    }

    // Modifier une évaluation existante
    @PutMapping("/{evaluationId}")
    public EvaluationResponse updateEvaluation(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-School-Id") UUID schoolId,
            @Valid @RequestBody UpdateEvaluationRequest request
    ) {

        Evaluation updatedEvaluation = Evaluation.builder()
                .academicYearId(request.academicYearId())
                .termId(request.termId())
                .classId(request.classId())
                .subjectId(request.subjectId())
                .teacherId(request.teacherId())
                .type(request.type())
                .title(request.title())
                .evalDate(request.evalDate())
                .maxScore(request.maxScore())
                .weight(request.weight())
                .build();

        return toResponse(
                evaluationService.updateEvaluation(
                        evaluationId,
                        schoolId,
                        updatedEvaluation
                )
        );
    }

    // Supprimer une évaluation
    @DeleteMapping("/{evaluationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvaluation(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-School-Id") UUID schoolId
    ) {
        evaluationService.deleteEvaluation(
                evaluationId,
                schoolId
        );
    }

    // Publier une évaluation
    @PatchMapping("/{evaluationId}/publish")
    public EvaluationResponse publishEvaluation(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-School-Id") UUID schoolId
    ) {
        return toResponse(
                evaluationService.publishEvaluation(
                        evaluationId,
                        schoolId
                )
        );
    }

    // Clôturer une évaluation
    @PatchMapping("/{evaluationId}/close")
    public EvaluationResponse closeEvaluation(
            @PathVariable UUID evaluationId,
            @RequestHeader("X-School-Id") UUID schoolId
    ) {
        return toResponse(
                evaluationService.closeEvaluation(
                        evaluationId,
                        schoolId
                )
        );
    }
}
