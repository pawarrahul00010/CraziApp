package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.technohertz.model.UserRegister;

@EnableJpaRepositories("com.technohertz.repo")
public interface UserRegisterRepository extends JpaRepository<UserRegister, Integer>,JpaSpecificationExecutor<UserRegister>{
	
	
	@Query(value="SELECT  r.userName, r.password from UserRegister r WHERE r.userName=?1 AND r.password=?2")
	List<UserRegister> findByUserNameAndPassword(String userName,String Password);
	@Query(value="SELECT  r from UserRegister r WHERE r.userName=?1")
	List<UserRegister> findByUserName(String userName);
	
	

	@Query(value="SELECT  r from UserRegister r WHERE r.userId=?1")
	List<UserRegister> getById(Integer userId);
	
	@Query(value="SELECT  r from UserRegister r WHERE r.mobilNumber=?1")
	List<UserRegister> findByMobileNumber(String user);
}
