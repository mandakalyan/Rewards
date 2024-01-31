package com.spring.rewards.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.rewards.entity.Employee;
import com.spring.rewards.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	boolean existsByEmail(String email);

	void save(Employee employee);

	
	
	  
	



}
