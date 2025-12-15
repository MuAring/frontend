package org.maru.muaring.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.maru.muaring.core.util.Resource;
import org.maru.muaring.data.api.GroupApi;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.api.dto.GroupInviteResponse;
import org.maru.muaring.data.api.dto.GroupListResponse;
import org.maru.muaring.data.api.dto.GroupMemberResponse;
import org.maru.muaring.data.api.dto.GroupProfileResponse;
import org.maru.muaring.data.api.dto.GroupSummary;
import org.maru.muaring.data.api.dto.InvitePreviewResponse;
import org.maru.muaring.data.api.dto.MusicArchiveDto;
import org.maru.muaring.data.api.dto.MyGroupListResponse;
import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.data.api.dto.PageResponse;
import org.maru.muaring.data.api.dto.TodayMusicPostResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupRepositoryImpl implements GroupRepository {

    @Override
    public Call<ApiResponse<GroupCreateResponse>> createGroup(GroupCreateRequest request) {
        return groupApi.createGroup(request);
    }

    private static final String TAG = "GroupRepository";
    private final GroupApi groupApi;

    @Inject
    public GroupRepositoryImpl(GroupApi groupApi) {
        this.groupApi = groupApi;
    }

    
    @Override
    public LiveData<Resource<GroupInviteResponse>> createInviteLink(Long groupId) {
        MutableLiveData<Resource<GroupInviteResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "초대 링크 생성 시작 - groupId: " + groupId);

        groupApi.createInviteLink(groupId).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<GroupInviteResponse>> call,
                    Response<ApiResponse<GroupInviteResponse>> response
            ) {
                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GroupInviteResponse> apiResponse = response.body();
                    GroupInviteResponse data = apiResponse.getData();

                    if (data != null) {
                        Log.d(TAG, "초대 링크 생성 성공: " + data.getInviteUrl());
                        result.setValue(Resource.success(data));
                    } else {
                        Log.e(TAG, "데이터가 null입니다");
                        result.setValue(Resource.error("데이터를 받아오지 못했습니다", null));
                    }
                } else {
                    Log.e(TAG, "응답 실패: " + response.message());

                    String errorMessage;
                    switch (response.code()) {
                        case 401:
                            errorMessage = "로그인이 필요합니다";
                            break;
                        case 404:
                            errorMessage = "그룹을 찾을 수 없습니다";
                            break;
                        default:
                            errorMessage = "초대 링크 생성 실패 (코드: " + response.code() + ")";
                    }
                    result.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<GroupInviteResponse>> call,
                    Throwable t
            ) {
                Log.e(TAG, "네트워크 오류", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }
    @Override
    public LiveData<Resource<List<MyGroupSummary>>> getMemberGroupsWithSearch(Long memberId, String searchName) {
        MutableLiveData<Resource<List<MyGroupSummary>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "멤버 그룹 조회 시작 - memberId: " + memberId + ", search: " + searchName);

        groupApi.getMemberGroups(memberId, searchName)
                .enqueue(new Callback<ApiResponse<MyGroupListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<MyGroupListResponse>> call,
                                           Response<ApiResponse<MyGroupListResponse>> response) {

                        Log.d(TAG, "응답 코드: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<MyGroupListResponse> apiResponse = response.body();

                            if (apiResponse.getData() != null) {
                                List<MyGroupSummary> groups = apiResponse.getData().getGroups();
                                Log.d(TAG, "멤버 그룹 조회 성공 - 그룹 수: " + (groups != null ? groups.size() : 0));
                                result.setValue(Resource.success(groups));
                            } else {
                                Log.e(TAG, "데이터가 null입니다");
                                result.setValue(Resource.error("데이터를 불러올 수 없습니다.", null));
                            }
                        } else {
                            Log.e(TAG, "응답 실패: " + response.message());
                            result.setValue(Resource.error("서버 응답 오류: " + response.code(), null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<MyGroupListResponse>> call, Throwable t) {
                        Log.e(TAG, "네트워크 오류", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }
    
    @Override
    public LiveData<Resource<InvitePreviewResponse>> getInvitePreview(String inviteToken) {
        MutableLiveData<Resource<InvitePreviewResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "초대 정보 조회 시작 - token: " + inviteToken);

        groupApi.getInvitePreview(inviteToken).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<InvitePreviewResponse>> call,
                    Response<ApiResponse<InvitePreviewResponse>> response
            ) {
                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<InvitePreviewResponse> apiResponse = response.body();
                    InvitePreviewResponse data = apiResponse.getData();

                    if (data != null) {
                        Log.d(TAG, "초대 정보 조회 성공: " + data.getGroupName());
                        result.setValue(Resource.success(data));
                    } else {
                        Log.e(TAG, "데이터가 null입니다");
                        result.setValue(Resource.error("초대 정보를 찾을 수 없습니다", null));
                    }
                } else {
                    Log.e(TAG, "응답 실패: " + response.message());

                    String errorMessage;
                    switch (response.code()) {
                        case 404:
                            errorMessage = "초대 링크를 찾을 수 없습니다";
                            break;
                        case 410:
                            errorMessage = "만료된 초대 링크입니다";
                            break;
                        default:
                            errorMessage = "초대 정보 조회 실패 (코드: " + response.code() + ")";
                    }
                    result.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<InvitePreviewResponse>> call,
                    Throwable t
            ) {
                Log.e(TAG, "네트워크 오류", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }

    
    @Override
    public LiveData<Resource<Void>> joinByInviteToken(String inviteToken) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "그룹 참여 시작 - token: " + inviteToken);

        groupApi.joinByInviteToken(inviteToken).enqueue(new Callback<>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<Void>> call,
                    Response<ApiResponse<Void>> response
            ) {
                Log.d(TAG, "응답 코드: " + response.code());

                if (response.isSuccessful()) {
                    Log.d(TAG, "그룹 참여 성공");
                    result.setValue(Resource.success(null));
                } else {
                    Log.e(TAG, "응답 실패: " + response.message());

                    String errorMessage;
                    switch (response.code()) {
                        case 400:
                            errorMessage = "이미 참여한 그룹입니다";
                            break;
                        case 404:
                            errorMessage = "초대 링크를 찾을 수 없습니다";
                            break;
                        case 409:
                            errorMessage = "그룹이 가득 찼습니다";
                            break;
                        case 410:
                            errorMessage = "만료된 초대 링크입니다";
                            break;
                        default:
                            errorMessage = "그룹 참여 실패 (코드: " + response.code() + ")";
                    }
                    result.setValue(Resource.error(errorMessage, null));
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Void>> call,
                    Throwable t
            ) {
                Log.e(TAG, "네트워크 오류", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }
    
    
    // 그룹 검색
    @Override
    public void searchGroups(String name, int page, int size, SearchGroupsCallback callback) {
        Call<ApiResponse<GroupListResponse>> call =
                groupApi.searchGroups(name, true, null, page, size);

        call.enqueue(new Callback<ApiResponse<GroupListResponse>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ApiResponse<GroupListResponse>> call,
                    @NonNull Response<ApiResponse<GroupListResponse>> response
            ) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<GroupListResponse> body = response.body();
                    GroupListResponse data = body.getData();

                    List<GroupSummary> groups =
                            (data != null && data.getGroups() != null)
                                    ? data.getGroups()
                                    : Collections.emptyList();

                    callback.onSuccess(groups);

                } else {
                    callback.onError(new RuntimeException("검색 실패: " + response.code()));
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<ApiResponse<GroupListResponse>> call,
                    @NonNull Throwable t
            ) {
                callback.onError(t);
            }
        });
    }
    
    
    // 그룹 카테고리 조회
    @Override
    public Call<ApiResponse<List<GroupCategoryResponse>>> getGroupCategories() {
        return groupApi.getGroupCategories();
    }


    // 홈 화면용 내 그룹 조회
    @Override
    public LiveData<Resource<List<MyGroupSummary>>> getMyGroups() {
        MutableLiveData<Resource<List<MyGroupSummary>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        groupApi.getMyGroups(null).enqueue(new retrofit2.Callback<ApiResponse<MyGroupListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyGroupListResponse>> call,
                                   Response<ApiResponse<MyGroupListResponse>> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<MyGroupSummary> groups = response.body().getData().getGroups();
                    result.setValue(Resource.success(groups));
                } else {
                    result.setValue(Resource.error("그룹 정보를 불러오지 못했어요.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyGroupListResponse>> call, Throwable t) {
                t.printStackTrace(); // 또는 Log.e(TAG, "getMyGroups 실패", t);
                result.setValue(Resource.error("네트워크 오류가 발생했어요.", null));
            }
        });

        return result;
    }


    // 그룹 프로필 조회
    @Override
    public LiveData<Resource<GroupProfileResponse>> getGroupProfile(Long groupId) {
        MutableLiveData<Resource<GroupProfileResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "getGroupProfile() 호출 - groupId: " + groupId);

        groupApi.getGroupProfile(groupId).enqueue(new Callback<ApiResponse<GroupProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupProfileResponse>> call,
                                   Response<ApiResponse<GroupProfileResponse>> response) {
                Log.d(TAG, "getGroupProfile onResponse - code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GroupProfileResponse> body = response.body();
                    Log.d(TAG, "body.code=" + body.getCode()
                            + ", message=" + body.getMessage()
                            + ", data=" + body.getData());

                    if (body.getData() != null) {
                        result.setValue(Resource.success(body.getData()));
                    } else {
                        result.setValue(Resource.error("data가 null입니다.", null));
                    }
                } else {
                    String msg = "response 실패 - code: " + response.code();
                    Log.e(TAG, msg);
                    result.setValue(Resource.error(msg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupProfileResponse>> call, Throwable t) {
                Log.e(TAG, "getGroupProfile onFailure", t);
                result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
            }
        });

        return result;
    }

    
    // 공개 그룹 가입
    @Override
    public void joinPublicGroup(Long groupId, JoinGroupCallback callback) {
        groupApi.joinPublicGroup(groupId)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call,
                                           Response<ApiResponse<Void>> response) {

                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(new Exception("가입 실패 code=" + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        callback.onError(t);
                    }
                });
    }

    // 그룹 멤버 조회 및 검색
    @Override
    public void getGroupMembers(Long groupId, String search, GetGroupMembersCallback callback) {
        Log.d(TAG, "그룹 멤버 조회 시작 - groupId: " + groupId + ", search: " + search);

        groupApi.getGroupMembers(groupId, search)
                .enqueue(new Callback<ApiResponse<List<GroupMemberResponse>>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<GroupMemberResponse>>> call,
                            @NonNull Response<ApiResponse<List<GroupMemberResponse>>> response
                    ) {
                        Log.d(TAG, "응답 코드: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<List<GroupMemberResponse>> apiResponse = response.body();
                            List<GroupMemberResponse> members = apiResponse.getData();

                            if (members != null) {
                                Log.d(TAG, "멤버 조회 성공 - 멤버 수: " + members.size());
                                callback.onSuccess(members);
                            } else {
                                Log.e(TAG, "멤버 데이터가 null입니다");
                                callback.onError(new Exception("멤버 데이터를 받아오지 못했습니다"));
                            }
                        } else {
                            Log.e(TAG, "응답 실패: " + response.message());
                            String errorMessage = "멤버 조회 실패 (코드: " + response.code() + ")";
                            callback.onError(new Exception(errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<GroupMemberResponse>>> call,
                            @NonNull Throwable t
                    ) {
                        Log.e(TAG, "네트워크 오류", t);
                        callback.onError(t);
                    }
                });
    }

    // 검색 메서드
    @Override
    public LiveData<Resource<List<MyGroupSummary>>> getMyGroupsWithSearch(String searchName) {
        MutableLiveData<Resource<List<MyGroupSummary>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        groupApi.getMyGroups(searchName).enqueue(new retrofit2.Callback<ApiResponse<MyGroupListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyGroupListResponse>> call,
                                   Response<ApiResponse<MyGroupListResponse>> response) {

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<MyGroupSummary> groups = response.body().getData().getGroups();
                    result.setValue(Resource.success(groups));
                } else {
                    result.setValue(Resource.error("그룹 정보를 불러오지 못했어요.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyGroupListResponse>> call, Throwable t) {
                t.printStackTrace();
                result.setValue(Resource.error("네트워크 오류가 발생했어요.", null));
            }
        });

        return result;
    }

    // 그룹 오늘 공유한 음악 조회 메서드
    @Override
    public LiveData<Resource<TodayMusicPostResponse>> getTodayGroupFeed(Long groupId) {
        MutableLiveData<Resource<TodayMusicPostResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "오늘의 피드 조회 시작 - groupId: " + groupId);

        // page=0, size=1 (최신 1개만)
        groupApi.getTodayGroupFeed(groupId, 0, 1)
                .enqueue(new Callback<ApiResponse<PageResponse<TodayMusicPostResponse>>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<PageResponse<TodayMusicPostResponse>>> call,
                            @NonNull Response<ApiResponse<PageResponse<TodayMusicPostResponse>>> response
                    ) {
                        Log.d(TAG, "오늘의 피드 응답 코드: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PageResponse<TodayMusicPostResponse>> apiResponse = response.body();
                            PageResponse<TodayMusicPostResponse> pageData = apiResponse.getData();

                            if (pageData != null && pageData.isNotEmpty()) {
                                // 첫 번째 항목만 추출
                                TodayMusicPostResponse todayPost = pageData.getContent().get(0);
                                Log.d(TAG, "오늘의 피드 조회 성공: " + todayPost.getMusicName());
                                result.setValue(Resource.success(todayPost));
                            } else {
                                Log.d(TAG, "오늘 공유된 음악이 없습니다");
                                result.setValue(Resource.success(null)); // 데이터 없음
                            }
                        } else {
                            Log.e(TAG, "오늘의 피드 조회 실패: " + response.message());
                            String errorMessage = "오늘의 피드 조회 실패 (코드: " + response.code() + ")";
                            result.setValue(Resource.error(errorMessage, null));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<PageResponse<TodayMusicPostResponse>>> call,
                            @NonNull Throwable t
                    ) {
                        Log.e(TAG, "오늘의 피드 네트워크 오류", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

    // 그룹 보관함 조회
    @Override
    public LiveData<Resource<List<MusicArchiveDto>>> getGroupMusicArchive(Long groupId, int page) {
        MutableLiveData<Resource<List<MusicArchiveDto>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));

        Log.d(TAG, "그룹 보관함 조회 시작 - groupId: " + groupId + ", page: " + page);

        groupApi.getGroupMusicArchive(groupId, page, 20)
                .enqueue(new Callback<ApiResponse<PageResponse<MusicArchiveDto>>>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<PageResponse<MusicArchiveDto>>> call,
                            @NonNull Response<ApiResponse<PageResponse<MusicArchiveDto>>> response
                    ) {
                        Log.d(TAG, "보관함 응답 코드: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<PageResponse<MusicArchiveDto>> apiResponse = response.body();
                            PageResponse<MusicArchiveDto> pageData = apiResponse.getData();

                            if (pageData != null && pageData.getContent() != null) {
                                List<MusicArchiveDto> musicList = pageData.getContent();
                                Log.d(TAG, "보관함 조회 성공 - 음악 수: " + musicList.size());
                                result.setValue(Resource.success(musicList));
                            } else {
                                Log.d(TAG, "보관함이 비어있습니다");
                                result.setValue(Resource.success(Collections.emptyList()));
                            }
                        } else {
                            Log.e(TAG, "보관함 조회 실패: " + response.message());
                            String errorMessage = "보관함 조회 실패 (코드: " + response.code() + ")";
                            result.setValue(Resource.error(errorMessage, null));
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<PageResponse<MusicArchiveDto>>> call,
                            @NonNull Throwable t
                    ) {
                        Log.e(TAG, "보관함 네트워크 오류", t);
                        result.setValue(Resource.error("네트워크 오류: " + t.getMessage(), null));
                    }
                });

        return result;
    }

}