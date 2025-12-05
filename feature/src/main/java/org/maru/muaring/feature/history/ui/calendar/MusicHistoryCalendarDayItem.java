package org.maru.muaring.feature.history.ui.calendar;

public class MusicHistoryCalendarDayItem {
    public int day;    // 1~31 / 0 (빈칸)
    public String imageUrl;

    public MusicHistoryCalendarDayItem(int day, String imageUrl) {
        this.day = day;
        this.imageUrl = imageUrl;
    }
}