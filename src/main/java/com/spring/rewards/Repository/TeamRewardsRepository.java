package com.spring.rewards.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring.rewards.entity.TeamRewards;

@Repository
public interface TeamRewardsRepository extends JpaRepository<TeamRewards,String> {


	List<TeamRewards> findByEmpId(Long empId);

}
