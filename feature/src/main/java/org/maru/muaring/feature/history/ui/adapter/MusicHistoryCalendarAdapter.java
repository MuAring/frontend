package org.maru.muaring.feature.history.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarDayItem;
import org.maru.muaring.feature.history.ui.calendar.MusicHistoryCalendarViewHolder;
import java.util.ArrayList;
import java.util.List;

public class MusicHistoryCalendarAdapter extends RecyclerView.Adapter<MusicHistoryCalendarViewHolder> {

    private List<MusicHistoryCalendarDayItem> days = new ArrayList<>();
    private Context context;

    public MusicHistoryCalendarAdapter(Context context) {
        this.context = context;
    }

    public void setDays(List<MusicHistoryCalendarDayItem> newDays) {
        days.clear();
        days.addAll(newDays);
        notifyDataSetChanged();
    }

    public List<MusicHistoryCalendarDayItem> getDays() {
        return days;
    }

    @NonNull
    @Override
    public MusicHistoryCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_calendar_day, parent, false);
        return new MusicHistoryCalendarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHistoryCalendarViewHolder holder, int position) {
        MusicHistoryCalendarDayItem item = days.get(position);

        // 빈칸
        if (item.day == 0) {
            holder.txtDay.setText("");
            holder.img.setVisibility(View.GONE);
            return;
        }

        holder.txtDay.setText(String.valueOf(item.day));

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(holder.img);
        } else {
            holder.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return days.size();
    }
}