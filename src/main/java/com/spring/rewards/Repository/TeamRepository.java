package com.spring.rewards.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.rewards.entity.Team;

public interface TeamRepository extends JpaRepository<Team, String> {

}
