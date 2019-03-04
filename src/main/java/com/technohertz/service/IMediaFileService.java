package com.technohertz.service;

import java.util.List;

import com.technohertz.model.LikedUsers;
import com.technohertz.model.MediaFiles;

public interface IMediaFileService {
	
	
	List<LikedUsers> getUserLikesByFileId(int fileid, int userId);

	List<LikedUsers> getUserRatingByFileId(int userfileid, int userId);

	List<LikedUsers> getUserBookmarkByFileId(int userfileid, int userId);

	List<MediaFiles> getBookmarksByUserId(int userId);

	List<LikedUsers> getUserViewExistOrNotByFileId(int userfileid, int userId);

	List<MediaFiles> getAllMediaByUserId(int userId);
	
}
