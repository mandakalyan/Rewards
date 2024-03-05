package com.spring.rewards.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.rewards.entity.Reconciliation;

@Repository
public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {

	List<Reconciliation> findByEmployeeNumber(Long empId);

}
