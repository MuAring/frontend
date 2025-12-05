package org.maru.muaring.data.api;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.MemberProfileCreateRequest;
import org.maru.muaring.data.api.dto.MemberProfileCreateResponse;
import org.maru.muaring.data.api.dto.MemberSettingsResponse;
import org.maru.muaring.data.api.dto.NicknameCheckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MemberApi {

    @GET("/members/check-nickname")
    Call<ApiResponse<NicknameCheckResponse>> checkNicknameDuplicated(@Query("nickname") String nickname);

    @POST("/members")
    Call<ApiResponse<MemberProfileCreateResponse>> createProfile(@Body MemberProfileCreateRequest request);

    @GET("/me/settings")
    Call<ApiResponse<MemberSettingsResponse>> getMySettings();

}
