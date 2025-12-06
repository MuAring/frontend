package org.maru.muaring.feature.history.ui.calendar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.maru.muaring.feature.R;

public class MusicHistoryCalendarViewHolder extends RecyclerView.ViewHolder {

    public TextView txtDay;
    public ImageView img;

    public MusicHistoryCalendarViewHolder(@NonNull View itemView) {
        super(itemView);
        txtDay = itemView.findViewById(R.id.txtDay);
        img = itemView.findViewById(R.id.dayImage);
    }
}
