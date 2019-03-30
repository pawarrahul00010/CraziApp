package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.GroupPoll;

@Repository
public interface GroupPollRepository extends JpaRepository<GroupPoll, Integer>{

	@Query("SELECT p from GroupPoll p where p.pollId=?1")
	List<GroupPoll> findAllByPollId(Integer pollId);

	@Query("SELECT p from GroupPoll p where p.createdBy=?1")
	List<GroupPoll> findAllBycreatedBy(Integer createdBy);


}

