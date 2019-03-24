package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.AdminProfile;
import com.technohertz.model.PollLikes;
import com.technohertz.model.PollOption;

@Repository
public interface PolloptionRepository extends JpaRepository<PollOption, Integer>{


}

