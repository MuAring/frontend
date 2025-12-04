package org.maru.muaring.feature.history.ui.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

import java.util.List;

public class MusicHistoryCalendarView extends FrameLayout {

    public interface OnDayClickListener {
        void onDayClick(int year, int month, int day,
                        List<MusicHistoryItem> itemsOfThatDay);
    }

    private OnDayClickListener onDayClickListener;

    public MusicHistoryCalendarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MusicHistoryCalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicHistoryCalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_music_history_calendar, this, true);
        // TODO: 여기서 캘린더 내부 뷰 초기화 (다른 사람이 구현)
    }

    /**
     * 히스토리 리스트를 통째로 넣어주면 내부에서 날짜별로 배치해서 그림.
     */
    public void setItems(List<MusicHistoryItem> items) {
        // TODO: items를 날짜별로 그룹핑해서 캘린더에 적용
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.onDayClickListener = listener;
    }

    /* 아래 메서드는 캘린더 셀 클릭 시 다른 사람이 호출해주면 됨 */
    void dispatchDayClick(int year, int month, int day, List<MusicHistoryItem> dayItems) {
        if (onDayClickListener != null) {
            onDayClickListener.onDayClick(year, month, day, dayItems);
        }
    }
}
