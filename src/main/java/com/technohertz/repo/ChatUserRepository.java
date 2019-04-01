package com.technohertz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.technohertz.model.ChatUser;

@EnableJpaRepositories("com.technohertz.repo")
public interface ChatUserRepository extends JpaRepository<ChatUser, String>,JpaSpecificationExecutor<ChatUser>{
	

}
