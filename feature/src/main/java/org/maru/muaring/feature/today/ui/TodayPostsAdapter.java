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
        void onLikeClicked(MusicPostFeedResponse post);
        void onCommentClicked(MusicPostFeedResponse post);
        void onLibraryClick(MusicPostFeedResponse post);
    }

    private final List<MusicPostFeedResponse> items = new ArrayList<>();
    private final Listener listener;

    public TodayPostsAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<MusicPostFeedResponse> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        android.util.Log.d("TodayAdapter", "submitList size=" + items.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_today_post, parent, false);
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

            ivAlbum = itemView.findViewById(R.id.ivAlbum);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            ivWave = itemView.findViewById(R.id.ivWave);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivAddMusic = itemView.findViewById(R.id.btnAddMusic);
            ivWriterProfile = itemView.findViewById(R.id.ivWriterProfile);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvWriter = itemView.findViewById(R.id.tvWriter);

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

            // 좋아요/댓글 카운트 (null 안전 처리)
            int likeCount = post.getLikeCount() != null ? post.getLikeCount() : 0;
            int commentCount = post.getCommentCount() != null ? post.getCommentCount() : 0;
            tvLikeCount.setText(String.valueOf(likeCount));
            tvCommentCount.setText(String.valueOf(commentCount));

            // 좋아요 상태에 따른 하트 아이콘
            if (post.getIsLiked()) {
                ivLike.setImageResource(org.maru.muaring.design.R.drawable.ic_heart_filled);          // 꽉 찬 하트
            } else {
                ivLike.setImageResource(org.maru.muaring.design.R.drawable.ic_heart_outline);  // 빈 하트
            }

            // 보관함 여부에 따른 아이콘 변경
            if (post.isInLibrary()) {
                ivAddMusic.setImageResource(org.maru.muaring.design.R.drawable.ic_is_in_library);
            } else {
                ivAddMusic.setImageResource(org.maru.muaring.design.R.drawable.ic_is_not_in_library);
            }

            // ===========================================
            // =============== 클릭 이벤트들 ===============
            // ===========================================

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPostClicked(post);
            });

            btnPlay.setOnClickListener(v -> {
                if (listener != null) listener.onPlayClicked(post);
            });

            // 좋아요 토글
            ivLike.setOnClickListener(v -> {
                boolean currentLiked = post.getIsLiked();
                int currentCount = post.getLikeCount() != null ? post.getLikeCount() : 0;

                boolean newLiked = !currentLiked;
                int newCount = newLiked ? currentCount + 1 : Math.max(0, currentCount - 1);

                post.setIsLiked(newLiked);
                post.setLikeCount(newCount);
                tvLikeCount.setText(String.valueOf(newCount));

                int newIcon = newLiked
                        ? org.maru.muaring.design.R.drawable.ic_heart_filled
                        : org.maru.muaring.design.R.drawable.ic_heart_outline;
                animateIconChange(ivLike, newIcon);

                if (listener != null) listener.onLikeClicked(post);
            });
            
            // TODO: 댓글 클릭 -> 상세보기의 댓글창 활성화
            ivComment.setOnClickListener(v -> {
                if (listener != null) listener.onCommentClicked(post);
            });

            // 보관함 토글
            ivAddMusic.setOnClickListener(v -> {
                boolean newInLibrary = !post.isInLibrary();
                post.setInLibrary(newInLibrary);

                int newIcon = newInLibrary
                        ? org.maru.muaring.design.R.drawable.ic_is_in_library
                        : org.maru.muaring.design.R.drawable.ic_is_not_in_library;
                animateIconChange(ivAddMusic, newIcon);

                if (listener != null) listener.onLibraryClick(post);
            });
        }

        // 아이콘이 바뀔 때 살짝 페이드 되는 느낌
        private void animateIconChange(ImageView view, int newResId) {
            view.animate()
                    .alpha(0f)
                    .setDuration(120)
                    .withEndAction(() -> {
                        view.setImageResource(newResId);
                        view.setAlpha(0f);
                        view.animate()
                                .alpha(1f)
                                .setDuration(120)
                                .start();
                    })
                    .start();
        }
    }
}
