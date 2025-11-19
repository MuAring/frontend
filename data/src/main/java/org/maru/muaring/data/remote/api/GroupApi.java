package org.maru.muaring.data.remote.api;

import org.maru.muaring.data.model.ApiResponse;
import org.maru.muaring.data.model.GroupInviteResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupApi {

    // 초대 링크 생성
    @POST("groups/{groupId}/invites")
    Call<ApiResponse<GroupInviteResponse>> createInviteLink(
            @Path("groupId") Long groupId
    );
}