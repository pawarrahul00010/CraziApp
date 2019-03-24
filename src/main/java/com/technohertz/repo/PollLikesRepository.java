package com.technohertz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technohertz.model.PollLikes;

@Repository
public interface PollLikesRepository extends JpaRepository<PollLikes, Integer>{

	

}
