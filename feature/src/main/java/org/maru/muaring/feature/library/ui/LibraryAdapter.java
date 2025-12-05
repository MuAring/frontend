package org.maru.muaring.feature.library.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.library.ui.Music;

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MusicViewHolder> {

    private List<Music> musicList;

    private List<Long> selectedIds = new ArrayList<>();

    public interface OnSelectionChanged {
        void onSelectionChanged(int count);
    }

    private OnSelectionChanged callback;

    public LibraryAdapter(List<Music> musicList, OnSelectionChanged callback) {
        this.musicList = musicList;
        this.callback = callback;
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
        Long libraryId = music.getLibraryId();

        holder.tvTitle.setText(music.getTitle());
        holder.tvArtist.setText(music.getArtist());
        Glide.with(holder.itemView.getContext())
                .load(music.getAlbumImage())
                .into(holder.ivAlbum);

        if (selectedIds.contains(libraryId)) {
            holder.itemView.setBackgroundResource(R.drawable.bg_library_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.frame_library_item);
        }


        holder.itemView.setOnClickListener(v -> {

            if (selectedIds.contains(libraryId)) {
                selectedIds.remove(libraryId);
            } else {
                selectedIds.add(libraryId);
            }

            callback.onSelectionChanged(selectedIds.size());
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public int getSelectedCount() { return selectedIds.size(); }

    public List<Long> getSelectedLibraryIds() {
        return new ArrayList<>(selectedIds);
    }
    public void selectAll() {
        selectedIds.clear();
        for (Music music : musicList) {
            selectedIds.add(music.getLibraryId());
        }
        callback.onSelectionChanged(selectedIds.size());
        notifyDataSetChanged();
    }
    public void clearSelection() {
        selectedIds.clear();
        callback.onSelectionChanged(0);
        notifyDataSetChanged();
    }
    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAlbum;
        TextView tvTitle, tvArtist;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
        }
    }
}
