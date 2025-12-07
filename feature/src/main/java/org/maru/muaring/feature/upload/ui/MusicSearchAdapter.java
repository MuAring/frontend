package org.maru.muaring.feature.upload.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.data.api.dto.SpotifyTrackResponse;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicSearchAdapter extends RecyclerView.Adapter<MusicSearchAdapter.ViewHolder> {

    private List<SpotifyTrackResponse> list = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(SpotifyTrackResponse item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<SpotifyTrackResponse> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_music, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        SpotifyTrackResponse item = list.get(pos);

        holder.title.setText(item.getName());
        holder.artist.setText(item.getArtistName());

        Glide.with(holder.itemView)
                .load(item.getAlbumImgUrl())
                .into(holder.album);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, artist;
        ImageView album;

        public ViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            artist = v.findViewById(R.id.tvArtist);
            album = v.findViewById(R.id.ivAlbum);
        }
    }
}

