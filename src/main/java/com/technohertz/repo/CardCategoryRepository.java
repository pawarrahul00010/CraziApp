package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.CardCategory;

@Repository
public interface CardCategoryRepository extends JpaRepository<CardCategory, Integer>{

	
	@Query(value="SELECT  r from CardCategory r WHERE r.categoryId=?1")
	List<CardCategory> getById(Integer catId);
	
}
