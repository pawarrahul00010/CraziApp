package com.technohertz.util;

import java.util.List;
import java.util.Set;

import com.technohertz.model.GroupAdmin;

public  class GroupFirebase {

	  public Integer groupId;
	  public String groupName;
	  public String createdBy;
	  public List<String> groupMember;
	  public String profile;
	  public List<String> adminList;
	//  public long createDate;
	  
	  
	  
	public GroupFirebase(Integer groupId, String groupName, String createdBy, String profile) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.createdBy = createdBy;
		
		this.profile = profile;
		
	}
	  

	  

	


	

	}

