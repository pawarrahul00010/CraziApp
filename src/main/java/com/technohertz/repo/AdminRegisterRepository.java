package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.technohertz.model.AdminRegister;

@EnableJpaRepositories("com.technohertz.repo")
public interface AdminRegisterRepository extends JpaRepository<AdminRegister, Integer>,JpaSpecificationExecutor<AdminRegister>{
	
		
	@Query(value="SELECT  r from AdminRegister r WHERE r.adminId=?1")
	List<AdminRegister> getById(Integer adminId);
	
	@Query(value="SELECT  r from AdminRegister r WHERE r.mailId=?1")
	List<AdminRegister> findByMailId(String mailId);
	
	@Query(value="SELECT  r from AdminRegister r WHERE r.Token=?1")
	AdminRegister findByToken(Long token);
}
