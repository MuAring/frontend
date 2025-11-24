package org.maru.muaring.feature.search.ui.model;

import java.util.List;

public class SearchResultItem {

    public enum Type {
        GROUP,
        USER
    }

    private final Type type;
    private final Long id;
    private final String title;
    private final List<String> categoryNames; // 그룹용
    private final String todayMusicText;      // 사용자용
    private final String actionText;          // 버튼 텍스트
    private final Boolean isJoined;

    public SearchResultItem(Type type,
                            Long id,
                            String title,
                            List<String> categoryNames,
                            String todayMusicText,
                            String actionText,
                            Boolean isJoined) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.categoryNames = categoryNames;
        this.todayMusicText = todayMusicText;
        this.actionText = actionText;
        this.isJoined = isJoined;
    }

    public Type getType() { return type; }
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getCategoryNames() { return categoryNames; }
    public String getTodayMusicText() { return todayMusicText; }
    public String getActionText() { return actionText; }
    public Boolean getIsJoined() { return isJoined; }
}
