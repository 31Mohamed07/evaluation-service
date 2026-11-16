
package com.quamtech.evaluation;

import com.quamtech.evaluation.entity.Evaluation;
import com.quamtech.evaluation.enums.EvaluationStatus;
import com.quamtech.evaluation.repository.EvaluationRepository;
import com.quamtech.evaluation.service.EvaluationService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    private final UUID evaluationId = UUID.randomUUID();
    private final UUID schoolId = UUID.randomUUID();

    // Création d'une évaluation
    @Test
    void shouldCreateEvaluation() {
        Evaluation evaluation = new Evaluation();

        when(evaluationRepository.save(evaluation))
                .thenReturn(evaluation);

        Evaluation result = evaluationService.createEvaluation(evaluation);

        assertNotNull(result);
        assertSame(evaluation, result);
        verify(evaluationRepository).save(evaluation);
    }

    // Récupération d'une évaluation existante
    @Test
    void shouldGetEvaluationById() {
        Evaluation evaluation = new Evaluation();

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        Evaluation result = evaluationService.getEvaluationById(
                evaluationId, schoolId);

        assertNotNull(result);
        assertSame(evaluation, result);
    }

    // Refus si l'évaluation n'existe pas dans l'établissement
    @Test
    void shouldThrowExceptionWhenEvaluationNotFound() {
        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                evaluationService.getEvaluationById(evaluationId, schoolId));
    }

    // Publication d'une évaluation en brouillon
    @Test
    void shouldPublishDraftEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.DRAFT);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        when(evaluationRepository.save(evaluation))
                .thenReturn(evaluation);

        Evaluation result = evaluationService.publishEvaluation(
                evaluationId, schoolId);

        assertEquals(EvaluationStatus.PUBLISHED, result.getStatus());
        verify(evaluationRepository).save(evaluation);
    }

    // Clôture d'une évaluation publiée
    @Test
    void shouldClosePublishedEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.PUBLISHED);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        when(evaluationRepository.save(evaluation))
                .thenReturn(evaluation);

        Evaluation result = evaluationService.closeEvaluation(
                evaluationId, schoolId);

        assertEquals(EvaluationStatus.CLOSED, result.getStatus());
        verify(evaluationRepository).save(evaluation);
    }

    // Refus de clôturer une évaluation en brouillon
    @Test
    void shouldNotCloseDraftEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.DRAFT);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        assertThrows(IllegalStateException.class, () ->
                evaluationService.closeEvaluation(evaluationId, schoolId));

        verify(evaluationRepository, never()).save(any());
    }

    // Refus de modifier une évaluation déjà publiée
    @Test
    void shouldNotUpdatePublishedEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.PUBLISHED);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        assertThrows(IllegalStateException.class, () ->
                evaluationService.updateEvaluation(
                        evaluationId, schoolId, new Evaluation()));

        verify(evaluationRepository, never()).save(any());
    }

    // Refus de supprimer une évaluation déjà publiée
    @Test
    void shouldNotDeletePublishedEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.PUBLISHED);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        assertThrows(IllegalStateException.class, () ->
                evaluationService.deleteEvaluation(evaluationId, schoolId));

        verify(evaluationRepository, never()).delete(any());
    }

    // Modification réussie d'une évaluation en brouillon
    @Test
    void shouldUpdateDraftEvaluation() {
        Evaluation existing = new Evaluation();
        existing.setStatus(EvaluationStatus.DRAFT);
        existing.setTitle("Ancien titre");

        Evaluation updated = new Evaluation();
        updated.setTitle("Nouveau titre");

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(existing));

        when(evaluationRepository.save(existing))
                .thenReturn(existing);

        Evaluation result = evaluationService.updateEvaluation(
                evaluationId, schoolId, updated);

        assertEquals("Nouveau titre", result.getTitle());
        verify(evaluationRepository).save(existing);
    }

    // Suppression réussie d'une évaluation en brouillon
    @Test
    void shouldDeleteDraftEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.DRAFT);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        evaluationService.deleteEvaluation(evaluationId, schoolId);

        verify(evaluationRepository).delete(evaluation);
    }

    // Refus de publier une évaluation déjà publiée
    @Test
    void shouldNotPublishAlreadyPublishedEvaluation() {
        Evaluation evaluation = new Evaluation();
        evaluation.setStatus(EvaluationStatus.PUBLISHED);

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, schoolId))
                .thenReturn(Optional.of(evaluation));

        assertThrows(IllegalStateException.class, () ->
                evaluationService.publishEvaluation(evaluationId, schoolId));

        verify(evaluationRepository, never()).save(any());
    }

    // Récupération de toutes les évaluations d'un établissement
    @Test
    void shouldGetAllEvaluationsBySchool() {
        Evaluation evaluation = new Evaluation();

        when(evaluationRepository.findAllBySchoolId(schoolId))
                .thenReturn(java.util.List.of(evaluation));

        var result = evaluationService.getAllEvaluations(schoolId);

        assertEquals(1, result.size());
        assertSame(evaluation, result.get(0));

        verify(evaluationRepository).findAllBySchoolId(schoolId);
    }

    // Un établissement ne peut pas consulter l'évaluation d'un autre
    @Test
    void shouldNotAccessEvaluationFromAnotherSchool() {
        UUID anotherSchoolId = UUID.randomUUID();

        when(evaluationRepository.findByEvaluationIdAndSchoolId(
                evaluationId, anotherSchoolId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                evaluationService.getEvaluationById(
                        evaluationId, anotherSchoolId));

        verify(evaluationRepository).findByEvaluationIdAndSchoolId(
                evaluationId, anotherSchoolId);
    }

}
