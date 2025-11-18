package org.maru.muaring.feature.library.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MusicViewHolder> {

    private List<Music> musicList;

    public LibraryAdapter(List<Music> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music music = musicList.get(position);

        holder.tvTitle.setText(music.getTitle());
        holder.tvArtist.setText(music.getArtist());
        holder.ivAlbum.setImageResource(music.getAlbumImageRes());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAlbum;
        TextView tvTitle, tvArtist, btnSelect;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            btnSelect = itemView.findViewById(R.id.btnSelect);
        }
    }
}
