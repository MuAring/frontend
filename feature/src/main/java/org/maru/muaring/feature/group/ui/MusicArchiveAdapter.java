package org.maru.muaring.feature.group.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;
import org.maru.muaring.data.api.dto.MusicArchiveDto;

import java.util.ArrayList;
import java.util.List;

public class MusicArchiveAdapter extends RecyclerView.Adapter<MusicArchiveAdapter.ViewHolder> {

    private List<MusicArchiveDto> musicList = new ArrayList<>();
    private OnMusicClickListener listener;

    public interface OnMusicClickListener {
        void onMusicClick(MusicArchiveDto music);
    }

//    public void setOnMusicClickListener(OnMusicClickListener listener) {
//        this.listener = listener;
//    }

    public void submitList(List<MusicArchiveDto> newList) {
        this.musicList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItems(List<MusicArchiveDto> newItems) {
        if (newItems != null && !newItems.isEmpty()) {
            int startPosition = musicList.size();
            musicList.addAll(newItems);
            notifyItemRangeInserted(startPosition, newItems.size());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicArchiveDto music = musicList.get(position);
        holder.bind(music);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAlbum;
        private final TextView tvTitle;
        private final TextView tvArtist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMusicClick(musicList.get(position));
                }
            });
        }

        public void bind(MusicArchiveDto music) {
            tvTitle.setText(music.getTitle());
            tvArtist.setText(music.getArtist());

            // Glide로 앨범 이미지 로드
            Glide.with(itemView.getContext())
                    .load(music.getAlbumImage())
                    .placeholder(R.drawable.album_image)
                    .error(R.drawable.album_image)
                    .into(ivAlbum);
        }
    }
}