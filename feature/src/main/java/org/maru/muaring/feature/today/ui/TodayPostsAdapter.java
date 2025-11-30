package org.maru.muaring.feature.today.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.data.api.dto.MusicPostFeedResponse;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;

public class TodayPostsAdapter extends RecyclerView.Adapter<TodayPostsAdapter.PostViewHolder> {

    public interface Listener {
        void onPostClicked(MusicPostFeedResponse post);
        void onPlayClicked(MusicPostFeedResponse post);
        void onAddMusicClicked(MusicPostFeedResponse post);
        void onLikeClicked(MusicPostFeedResponse post);
        void onCommentClicked(MusicPostFeedResponse post);
    }

    private final List<MusicPostFeedResponse> items = new ArrayList<>();
    private final Listener listener;

    public TodayPostsAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<MusicPostFeedResponse> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(org.maru.muaring.design.R.layout.item_today_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAlbum;
        TextView tvTitle, tvArtist, tvDescription;

        ImageButton btnPlay;
        ImageView ivWave;

        // 하단 영역
        ImageView ivLike, ivComment, ivAddMusic, ivWriterProfile;
        TextView tvLikeCount, tvCommentCount, tvWriter;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAlbum = itemView.findViewById(org.maru.muaring.design.R.id.ivAlbum);
            tvTitle = itemView.findViewById(org.maru.muaring.design.R.id.tvTitle);
            tvArtist = itemView.findViewById(org.maru.muaring.design.R.id.tvArtist);
            tvDescription = itemView.findViewById(org.maru.muaring.design.R.id.tvDescription);

            btnPlay = itemView.findViewById(org.maru.muaring.design.R.id.btnPlay);
            ivWave = itemView.findViewById(org.maru.muaring.design.R.id.ivWave);

            ivLike = itemView.findViewById(org.maru.muaring.design.R.id.ivLike);
            ivComment = itemView.findViewById(org.maru.muaring.design.R.id.ivComment);
            ivAddMusic = itemView.findViewById(org.maru.muaring.design.R.id.btnAddMusic);
            ivWriterProfile = itemView.findViewById(org.maru.muaring.design.R.id.ivWriterProfile);

            tvLikeCount = itemView.findViewById(org.maru.muaring.design.R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(org.maru.muaring.design.R.id.tvCommentCount);
            tvWriter = itemView.findViewById(org.maru.muaring.design.R.id.tvWriter);
        }

        void bind(MusicPostFeedResponse post) {

            // 텍스트
            tvTitle.setText(post.getMusicName());
            tvArtist.setText(post.getArtistName());
            tvDescription.setText(post.getContent());

            // 앨범 이미지
            Glide.with(ivAlbum.getContext())
                    .load(post.getAlbumImgUrl())
                    .into(ivAlbum);

            // 작성자
            tvWriter.setText(post.getMemberNickname());
            Glide.with(ivWriterProfile.getContext())
                    .load(post.getMemberProfileImageUrl())
                    .circleCrop()
                    .into(ivWriterProfile);

            // 좋아요/댓글 카운트
            tvLikeCount.setText(String.valueOf(post.getLikeCount()));
            tvCommentCount.setText(String.valueOf(post.getCommentCount()));

            // 클릭 이벤트들
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPostClicked(post);
            });

            btnPlay.setOnClickListener(v -> {
                if (listener != null) listener.onPlayClicked(post);
            });

            ivAddMusic.setOnClickListener(v -> {
                if (listener != null) listener.onAddMusicClicked(post);
            });

            ivLike.setOnClickListener(v -> {
                if (listener != null) listener.onLikeClicked(post);
            });

            ivComment.setOnClickListener(v -> {
                if (listener != null) listener.onCommentClicked(post);
            });
        }
    }
}
