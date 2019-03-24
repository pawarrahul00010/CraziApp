package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.AdminProfile;
import com.technohertz.model.PollLikes;
import com.technohertz.model.PollOption;
import com.technohertz.model.SecretConversation;

@Repository
public interface SecretConversationRepo extends JpaRepository<SecretConversation, Integer>{


}

