package org.maru.muaring.data.api.dto;

import com.google.gson.annotations.SerializedName;

public class GroupCategoryResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("displayName")
    private String displayName;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}