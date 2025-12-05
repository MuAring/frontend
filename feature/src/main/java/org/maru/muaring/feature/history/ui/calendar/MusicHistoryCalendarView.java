package org.maru.muaring.feature.history.ui.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryCalendarAdapter;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHistoryCalendarView extends FrameLayout {

    public interface OnDayClickListener {
        void onDayClick(int year, int month, int day,
                        List<MusicHistoryItem> itemsOfThatDay);
    }

    private OnDayClickListener onDayClickListener;
    private RecyclerView recyclerView;
    private MusicHistoryCalendarAdapter adapter;

    private int currentYear;
    private int currentMonth;

    // 날짜별 음악 히스토리 저장
    private Map<Integer, List<MusicHistoryItem>> groupedByDay = new HashMap<>();


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
        recyclerView = findViewById(R.id.calendarRecycler);

        GridLayoutManager gm = new GridLayoutManager(context, 7);
        recyclerView.setLayoutManager(gm);

        adapter = new MusicHistoryCalendarAdapter(context);
        recyclerView.setAdapter(adapter);

        // 기본 값: today
        Calendar cal = Calendar.getInstance();
        loadMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
    }

    /**
     * 히스토리 리스트를 통째로 넣어주면 내부에서 날짜별로 배치해서 그림.
     */
    public void setItems(List<MusicHistoryItem> items) {
        // TODO: items를 날짜별로 그룹핑해서 캘린더에 적용
        groupedByDay.clear();

        for (MusicHistoryItem item : items) {
            int day = item.getDayNumber();

            // 그 달의 범위 내에 있는 날짜만 처리
            if (day >= 1 && day <= 31) {
                if (!groupedByDay.containsKey(day)) {
                    groupedByDay.put(day, new ArrayList<>());
                }
                groupedByDay.get(day).add(item);
            }
        }

        applyItemsToCalendar();
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        this.onDayClickListener = listener;
    }

    /* 아래 메서드는 캘린더 셀 클릭 시 다른 사람이 호출해주면 됨 */
    void dispatchDayClick(int year, int month, int day) {
        List<MusicHistoryItem> dayItems =
                groupedByDay.getOrDefault(day, new ArrayList<>());

        if (onDayClickListener != null) {
            onDayClickListener.onDayClick(year, month, day, dayItems);
        }
    }

    private void applyItemsToCalendar() {
        List<MusicHistoryCalendarDayItem> list = adapter.getDays();

        for (MusicHistoryCalendarDayItem dayItem : list) {
            if (dayItem.day > 0) {
                if (groupedByDay.containsKey(dayItem.day)) {
                    List<MusicHistoryItem> dayItems = groupedByDay.get(dayItem.day);
                    if (!dayItems.isEmpty()) {
                        dayItem.imageUrl = dayItems.get(0).getAlbumImageUrl();
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    // 다른 달 표시
    public void loadMonth(int year, int month) {
        this.currentYear = year;
        this.currentMonth = month;

        List<MusicHistoryCalendarDayItem> results = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1=일
        int offset = firstDayOfWeek - 1;

        for (int i = 0; i < offset; i++) {
            results.add(new MusicHistoryCalendarDayItem(0, null));
        }

        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int d = 1; d <= lastDay; d++) {
            results.add(new MusicHistoryCalendarDayItem(d, null));
        }

        adapter.setDays(results);
    }
}
