
package com.quamtech.evaluation.service;

import com.quamtech.evaluation.entity.Evaluation;
import com.quamtech.evaluation.enums.EvaluationStatus;
import com.quamtech.evaluation.repository.EvaluationRepository;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;

    // Créer une évaluation
    public Evaluation createEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    // Rechercher une évaluation par son identifiant
    public Evaluation getEvaluationById(
            UUID evaluationId,
            UUID schoolId
    ) {
        return evaluationRepository
                .findByEvaluationIdAndSchoolId(evaluationId, schoolId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Évaluation introuvable : " + evaluationId
                        )
                );
    }

    // Lister les évaluations d'un établissement
    @Transactional(readOnly = true)
    public List<Evaluation> getAllEvaluations(UUID schoolId) {
        return evaluationRepository.findAllBySchoolId(schoolId);
    }

    // Modifier une évaluation existante
    public Evaluation updateEvaluation(
            UUID evaluationId,
            UUID schoolId,
            Evaluation updatedEvaluation
    ) {
        Evaluation existingEvaluation =
                getEvaluationById(evaluationId, schoolId);
        if (existingEvaluation.getStatus() != EvaluationStatus.DRAFT) {
            throw new IllegalStateException(
                    "Seules les évaluations en brouillon peuvent être modifiées."
            );
        }
        existingEvaluation.setAcademicYearId(
                updatedEvaluation.getAcademicYearId()
        );

        existingEvaluation.setTermId(
                updatedEvaluation.getTermId()
        );

        existingEvaluation.setClassId(
                updatedEvaluation.getClassId()
        );

        existingEvaluation.setSubjectId(
                updatedEvaluation.getSubjectId()
        );

        existingEvaluation.setTeacherId(
                updatedEvaluation.getTeacherId()
        );

        existingEvaluation.setType(updatedEvaluation.getType());
        existingEvaluation.setTitle(updatedEvaluation.getTitle());
        existingEvaluation.setEvalDate(updatedEvaluation.getEvalDate());
        existingEvaluation.setMaxScore(updatedEvaluation.getMaxScore());
        existingEvaluation.setWeight(updatedEvaluation.getWeight());

        return evaluationRepository.save(existingEvaluation);
    }


    // Supprimer une évaluation
    public void deleteEvaluation(
            UUID evaluationId,
            UUID schoolId
    ) {
        Evaluation evaluation =
                getEvaluationById(evaluationId, schoolId);

        if (evaluation.getStatus() != EvaluationStatus.DRAFT) {
            throw new IllegalStateException(
                    "Seules les évaluations en brouillon peuvent être supprimées."
            );
        }

        evaluationRepository.delete(evaluation);
    }


    // Publier une évaluation
    public Evaluation publishEvaluation(
            UUID evaluationId,
            UUID schoolId
    ) {
        Evaluation evaluation =
                getEvaluationById(evaluationId, schoolId);

        if (evaluation.getStatus() != EvaluationStatus.DRAFT) {
            throw new IllegalStateException(
                    "Seules les évaluations en brouillon peuvent être publiées."
            );
        }

        evaluation.setStatus(EvaluationStatus.PUBLISHED);

        return evaluationRepository.save(evaluation);
    }

    // Clôturer une évaluation
    public Evaluation closeEvaluation(
            UUID evaluationId,
            UUID schoolId
    ) {
        Evaluation evaluation =
                getEvaluationById(evaluationId, schoolId);

        if (evaluation.getStatus() != EvaluationStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Seules les évaluations publiées peuvent être clôturées."
            );
        }

        evaluation.setStatus(EvaluationStatus.CLOSED);

        return evaluationRepository.save(evaluation);
    }


}
