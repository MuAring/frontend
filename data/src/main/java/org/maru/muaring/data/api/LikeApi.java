package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.LikeResponseDTO;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikeApi {

    // 좋아요 토글
    @POST("/posts/{postId}/likes")
    Call<ApiResponse<LikeResponseDTO>> toggleLike(@Path("postId") Long postId);
}
