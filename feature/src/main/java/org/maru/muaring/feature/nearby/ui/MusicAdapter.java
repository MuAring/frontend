package org.maru.muaring.feature.nearby.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.follow.ui.FollowAdapter;
import org.maru.muaring.feature.nearby.ui.model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private List<Music> musicList;
    private Context context;
    private OnProfileClickListener profileClickListener;

    public MusicAdapter(Context context, List<Music> musicList, OnProfileClickListener profileClickListener) {
        this.context = context;
        this.musicList = musicList;
        this.profileClickListener = profileClickListener;
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAlbum;
        TextView tvTitle;
        TextView tvArtist;

        ImageView ivProfile;
        ImageView btnAdd;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nearby_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Music item = musicList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvArtist.setText(item.getArtist());

        Glide.with(context)
                .load(item.getAlbumImageUrl())
                .into(holder.ivAlbum);

        if (item.getProfileImageUrl() == null || item.getProfileImageUrl().isEmpty()) {
            holder.ivProfile.setImageResource(R.drawable.profile_default); // 기본 이미지
        } else {
            Glide.with(context)
                    .load(item.getProfileImageUrl())
                    .into(holder.ivProfile);
        }

        holder.ivProfile.setOnClickListener(v -> {
            if (profileClickListener != null) {
                profileClickListener.onProfileClick(item.getMemberId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public interface OnProfileClickListener {
        void onProfileClick(long memberId);
    }

}
