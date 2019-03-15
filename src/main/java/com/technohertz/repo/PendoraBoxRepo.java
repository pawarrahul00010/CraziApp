package com.technohertz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.technohertz.model.PendoraBox;

@EnableJpaRepositories("com.technohertz.repo")
public interface PendoraBoxRepo extends JpaRepository<PendoraBox, Integer>,JpaSpecificationExecutor<PendoraBox>{
	
		

}
