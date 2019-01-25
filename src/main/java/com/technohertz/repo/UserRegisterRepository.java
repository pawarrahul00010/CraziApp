package com.technohertz.repo;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.technohertz.model.UserRegister;

@EnableJpaRepositories("com.technohertz.repo")
public interface UserRegisterRepository extends JpaRepository<UserRegister, Integer>,JpaSpecificationExecutor<UserRegister>{
	
	
	@Query(value="SELECT  r.userName, r.password from UserRegister r WHERE r.userName=?1 AND r.password=?2")
	List<UserRegister> findByUserNameAndPassword(String userName,String Password);
	@Query(value="SELECT  r.userName from UserRegister r WHERE r.userName=?1")
	List<UserRegister> findByUserName(String userName);
	
}
