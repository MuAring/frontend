package org.maru.muaring.feature.history.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MusicHistoryAdapter extends RecyclerView.Adapter<MusicHistoryAdapter.HistoryViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MusicHistoryItem item);
    }

    private final List<MusicHistoryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public MusicHistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<MusicHistoryItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    public MusicHistoryItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_music, parent, false);
        return new HistoryViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        MusicHistoryItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView textBadgeDay;
        private final ImageView imageAlbum;
        private final TextView textTitle;
        private final TextView textArtist;

        private MusicHistoryItem currentItem;

        HistoryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textBadgeDay = itemView.findViewById(R.id.text_badge_day);
            imageAlbum = itemView.findViewById(R.id.image_album);
            textTitle = itemView.findViewById(R.id.text_title);
            textArtist = itemView.findViewById(R.id.text_artist);

            itemView.setOnClickListener(v -> {
                if (listener != null && currentItem != null) {
                    listener.onItemClick(currentItem);
                }
            });
        }

        void bind(MusicHistoryItem item) {
            currentItem = item;

            textBadgeDay.setText(String.valueOf(item.getDayNumber()));
            textTitle.setText(item.getTitle());
            textArtist.setText(item.getArtist());

            // 앨범 이미지는 URL 기준 (없으면 placeholder)
            if (item.getAlbumImageUrl() != null && !item.getAlbumImageUrl().isEmpty()) {
                Glide.with(imageAlbum.getContext())
                        .load(item.getAlbumImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.launcher_background)
                        .into(imageAlbum);
            } else {
                imageAlbum.setImageResource(R.drawable.launcher_background);
            }
        }
    }
}
