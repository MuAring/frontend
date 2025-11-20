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

import java.util.ArrayList;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MusicViewHolder> {

    private List<Music> musicList;

    private List<Integer> selectedList = new ArrayList<>();

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

        holder.tvTitle.setText(music.getTitle());
        holder.tvArtist.setText(music.getArtist());
        holder.ivAlbum.setImageResource(music.getAlbumImageRes());

        if (selectedList.contains(position)) {
            holder.itemView.setBackgroundResource(R.drawable.bg_library_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.frame_library_item);
        }


        holder.itemView.setOnClickListener(v -> {
            if (selectedList.contains(position)) {
                selectedList.remove(Integer.valueOf(position));
            } else {
                selectedList.add(position);
            }

            callback.onSelectionChanged(selectedList.size());

            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public int getSelectedCount() { return selectedList.size(); }

    public void selectAll() {
        selectedList.clear();
        for (int i = 0; i < musicList.size(); i++) {
            selectedList.add(i);
        }
        callback.onSelectionChanged(selectedList.size());
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedList.clear();
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
