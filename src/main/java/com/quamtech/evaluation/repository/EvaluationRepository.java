package com.quamtech.evaluation.repository;

import com.quamtech.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    Optional<Evaluation> findByEvaluationIdAndSchoolId(
            UUID evaluationId,
            UUID schoolId
    );

    List<Evaluation> findAllBySchoolId(UUID schoolId);

}
