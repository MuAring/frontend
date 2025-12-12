package org.maru.muaring.data.repository;

import androidx.lifecycle.LiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.dto.LikeResponseDTO;

public interface LikeRepository {
    LiveData<Resource<LikeResponseDTO>> toggleLike(Long postId);
}
