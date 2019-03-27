package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.AdminProfile;

@Repository
public interface AdminProfileRepository extends JpaRepository<AdminProfile, Integer>{

	Object findBydisplayName(String displayName);
	
	
	List<AdminProfile> getByDisplayName(String userName);
	
	
	@Query(value="SELECT  r from AdminProfile r WHERE r.profileId=?1")
	List<AdminProfile> findByProfileId(int id);

}
