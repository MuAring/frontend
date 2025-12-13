package org.maru.muaring.data.api.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class FollowListResponse implements Parcelable {

    private Long memberId;

    private String name;

    private String profileImage;

    private Boolean isPublic;

    private String followStatus;
    private String musicTitle;
    private String musicArtist;

    public Long getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public String getFollowStatus() {
        return followStatus;
    }

    public String getMusicTitle() { return musicTitle; }
    public String getMusicArtist() { return musicArtist; }

    // Parcelable 구현
    protected FollowListResponse(Parcel in) {
        if (in.readByte() == 0) {
            memberId = null;
        } else {
            memberId = in.readLong();
        }
        name = in.readString();
        profileImage = in.readString();
        byte tmpIsPublic = in.readByte();
        isPublic = tmpIsPublic == 0 ? null : tmpIsPublic == 1;
        followStatus = in.readString();
    }

    public static final Creator<FollowListResponse> CREATOR = new Creator<FollowListResponse>() {
        @Override
        public FollowListResponse createFromParcel(Parcel in) {
            return new FollowListResponse(in);
        }

        @Override
        public FollowListResponse[] newArray(int size) {
            return new FollowListResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (memberId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(memberId);
        }

        dest.writeString(name);
        dest.writeString(profileImage);

        if (isPublic == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) (isPublic ? 1 : 2));
        }

        dest.writeString(followStatus);
    }
}
