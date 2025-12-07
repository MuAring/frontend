package org.maru.muaring.data.api.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class SpotifyTrackResponse implements Parcelable {
    private String spotifyId;
    private String name;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private int popularity;

    protected SpotifyTrackResponse(Parcel in) {
        spotifyId = in.readString();
        name = in.readString();
        artistName = in.readString();
        albumName = in.readString();
        albumImgUrl = in.readString();
        popularity = in.readInt();
    }

    public static final Creator<SpotifyTrackResponse> CREATOR = new Creator<>() {
        @Override
        public SpotifyTrackResponse createFromParcel(Parcel in) {
            return new SpotifyTrackResponse(in);
        }

        @Override
        public SpotifyTrackResponse[] newArray(int size) {
            return new SpotifyTrackResponse[size];
        }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(spotifyId);
        dest.writeString(name);
        dest.writeString(artistName);
        dest.writeString(albumName);
        dest.writeString(albumImgUrl);
        dest.writeInt(popularity);
    }

    public String getSpotifyId(){ return spotifyId; }

    public String getName() { return name; }
    public String getArtistName() { return artistName; }
    public String getAlbumImgUrl() { return albumImgUrl; }
}
