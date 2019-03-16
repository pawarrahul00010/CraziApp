package com.technohertz.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technohertz.common.Constant;
import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;
import com.technohertz.repo.UserProfileRepository;
import com.technohertz.service.IMediaFileService;

@Service
public class MediaFileServiceImpl implements IMediaFileService{
	
	@Autowired
	private UserProfileRepository userprofilerepo;

	@Autowired
	public EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<LikedUsers> getUserLikesByFileId(int fileid, int userId) {

		return entityManager.createNativeQuery("SELECT * FROM liked_users WHERE file_id IN (:fileid) AND user_id=:user_id AND TYPE IN(:type1,:type2)",LikedUsers.class)
				.setParameter("fileid", fileid)
				.setParameter("type1", Constant.LIKE)
				.setParameter("type2", Constant.UNLIKED)
				.setParameter("user_id", userId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LikedUsers> getUserRatingByFileId(int userfileid, int userId) {
		
		return entityManager.createNativeQuery("SELECT * FROM liked_users WHERE file_id IN (:fileid) AND user_id=:userId AND TYPE IN(:type)",LikedUsers.class)
				.setParameter("fileid", userfileid)
				.setParameter("type", Constant.RATE)
				.setParameter("userId", userId).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<LikedUsers> getUserViewExistOrNotByFileId(int userfileid, int userId) {
		
		return entityManager.createNativeQuery("SELECT * FROM liked_users WHERE file_id IN (:fileid) AND user_id=:userId AND TYPE IN(:type)",LikedUsers.class)
				.setParameter("fileid", userfileid)
				.setParameter("type", Constant.VIEW)
				.setParameter("userId", userId).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<LikedUsers> getUserBookmarkByFileId(int userfileid, int userId) {
		
		return entityManager.createNativeQuery("SELECT * FROM liked_users WHERE file_id IN (:fileid) AND user_id=:userId AND TYPE IN(:type)",LikedUsers.class)
				.setParameter("fileid", userfileid)
				.setParameter("type", Constant.BOOKMARK)
				.setParameter("userId", userId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MediaFiles> getBookmarksByUserId(int userId) {
		return entityManager.createNativeQuery("SELECT * FROM media_files WHERE file_id IN (SELECT file_id from liked_users where user_id=:userId and TYPE=:type order by create_date desc) ",MediaFiles.class)
				.setParameter("type", Constant.BOOKMARK)
				.setParameter("userId", userId).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public List<MediaFiles> getAllMediaByUserId(int userId) {
		return entityManager.createNativeQuery("SELECT * FROM media_files WHERE usr_det_id=:userId ",MediaFiles.class)
				.setParameter("userId", userId).getResultList();
	}

	@Transactional
	@Override
	public Number  getAllProfileCountById(Integer userId) {
		
		return (Number ) entityManager.createNativeQuery("SELECT COUNT(*) FROM media_files  WHERE usr_det_id=:userId AND File_Type=:PROFILE ORDER BY file_id  DESC")
				.setParameter("userId", userId)	.setParameter("PROFILE", "PROFILE")
				.getSingleResult();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LikedUsers> getRatingByFileId(int userfileid) {
		
		return entityManager.createNativeQuery("SELECT * FROM liked_users WHERE file_id IN (:fileid) AND TYPE IN(:type)",LikedUsers.class)
				.setParameter("fileid", userfileid)
				.setParameter("type", Constant.RATE)
				.getResultList();
	}

}
