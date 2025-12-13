package org.maru.muaring.feature.post.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import org.maru.muaring.feature.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostDetailFragment extends Fragment {

    private PostDetailReadViewModel viewModel;
    private View toolBar;
    private ImageButton toolBarBtnBack;
    private TextView toolbarTitle;
    private ImageView ivPosterProfile;
    private TextView tvNickname;
    private TextView tvCreatedAt;
    private TextView tvPostContent;
    private TextView tvLikeCount;
    private TextView tvCommentCount;
    private Long postId;

    // 음악 카드
    private View musicCardView;

    private ImageView ivAlbum;
    private TextView tvMusicTitle;
    private TextView tvArtist;
    private ImageButton btnAddLibrary;
    private boolean isAlreadyInLibrary;

    // 좋아요 영역
    private LinearLayout likeSection;
    private ImageView ivLike;
    private boolean isLiked;
    private int likeCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PostDetailReadViewModel.class);

        if (getArguments() != null) {
            postId = getArguments().getLong("postId");
        }

        bindViews(view);
        initArgs();
        observePostDetail(postId);
    }

    private void initArgs() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("postId")) {
            postId = args.getLong("postId");
        } else {
            throw new IllegalArgumentException("postId를 받지 못했습니다.");
        }
    }

    private void bindViews(View v) {
        toolBar =  v.findViewById(R.id.include_toolbar);
        toolBarBtnBack = toolBar.findViewById(R.id.btn_back);
        toolbarTitle = toolBar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("게시물");

        ivPosterProfile = v.findViewById(R.id.iv_poster_profile);
        tvNickname = v.findViewById(R.id.tv_nickname);
        tvCreatedAt = v.findViewById(R.id.tv_created_at);
        tvPostContent = v.findViewById(R.id.tv_post_content);
        tvLikeCount = v.findViewById(R.id.tv_like_count);
        tvCommentCount = v.findViewById(R.id.tv_comment_count);

        // 음악카드
        musicCardView = v.findViewById(R.id.include_music);
        ivAlbum = musicCardView.findViewById(R.id.iv_album);
        tvMusicTitle = musicCardView.findViewById(R.id.tv_music_title);
        tvArtist = musicCardView.findViewById(R.id.tv_artist);
        btnAddLibrary = musicCardView.findViewById(R.id.btn_add_library);

        // 좋아요 영역
        likeSection = v.findViewById(R.id.like_section);
        ivLike = v.findViewById(R.id.iv_like);
    }

    private void bindPostDetail(PostDetailReadResponse response) {
        tvNickname.setText(response.getAuthor().getNickname());
        tvCreatedAt.setText(response.getCreatedAt());
        tvPostContent.setText(response.getContent());
        tvLikeCount.setText(String.valueOf(response.getLikeCount()));
        tvCommentCount.setText(String.valueOf(response.getCommentCount()));

        Glide.with(this)
                .load(response.getAuthor().getProfileImageUrl())
                .circleCrop()
                .into(ivPosterProfile);

        // 음악카드
        Glide.with(this)
                .load(response.getMusic().getAlbumImgUrl())
                .into(ivAlbum);
        tvMusicTitle.setText(response.getMusic().getName());
        tvArtist.setText(response.getMusic().getArtistName());
        isAlreadyInLibrary = response.getMusic().getIsAlreadyInLibrary();
        updateLibraryIcon();
        setupLibraryClick(response.getMusic().getMusicId());

        // 좋아요
        isLiked = response.isLiked();
        likeCount = response.getLikeCount() != null
                ? response.getLikeCount()
                : 0;
        ivLike.setImageResource(
                isLiked
                        ? org.maru.muaring.design.R.drawable.ic_heart_filled
                        : R.drawable.ic_heart_outline
        );

        likeSection.setOnClickListener(v -> {
            boolean prevLiked = isLiked;
            int prevCount = likeCount;

            // UI 먼저 변경 (Optimistic)
            isLiked = !isLiked;
            likeCount = isLiked ? likeCount + 1 : Math.max(0, likeCount - 1);
            updateLikeUI();

            viewModel.toggleLike(postId)
                    .observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;
                        switch (resource.status) {
                            case LOADING:
                                // 필요하면 로딩 표시
                                break;

                            case SUCCESS:
                                if (resource.data != null) {
                                    isLiked = resource.data.isLiked();
                                    likeCount = resource.data.getNumOfLikes();
                                    updateLikeUI();
                                }
                                break;

                            case ERROR:
                                // 실패 시 롤백
                                isLiked = prevLiked;
                                likeCount = prevCount;
                                updateLikeUI();

                                Toast.makeText(
                                        requireContext(),
                                        "좋아요 처리에 실패했어요 😢",
                                        Toast.LENGTH_SHORT
                                ).show();
                                break;
                        }
                    });
        });
    }

    private void updateLikeUI() {
        ivLike.setImageResource(
                isLiked
                        ? org.maru.muaring.design.R.drawable.ic_heart_filled
                        : R.drawable.ic_heart_outline
        );
        tvLikeCount.setText(String.valueOf(likeCount));
    }

    private void updateLibraryIcon() {
        btnAddLibrary.setImageResource(
                isAlreadyInLibrary
                        ? org.maru.muaring.design.R.drawable.ic_is_in_library
                        : org.maru.muaring.design.R.drawable.ic_is_not_in_library
        );
    }

    private void setupLibraryClick(Long musicId) {

        btnAddLibrary.setOnClickListener(v -> {
            if (isAlreadyInLibrary) {
                btnAddLibrary.setEnabled(!isAlreadyInLibrary);
                return;
            }

            viewModel.addMusicToLibrary(musicId, null)
                    .observe(getViewLifecycleOwner(), resource -> {
                        if (resource == null) return;
                        switch (resource.status) {
                            case LOADING:
                                // 필요하면 로딩 처리
                                break;

                            case SUCCESS:
                                isAlreadyInLibrary = true;
                                updateLibraryIcon();

                                Toast.makeText(
                                        requireContext(),
                                        "보관함에 음악을 추가했어요 🎵",
                                        Toast.LENGTH_SHORT
                                ).show();
                                break;

                            case ERROR:
                                Toast.makeText(
                                        requireContext(),
                                        "보관함 음악 추가에 실패했어요 😢",
                                        Toast.LENGTH_SHORT
                                ).show();
                                break;
                        }
                    });
        });
    }

    private void observePostDetail(Long postId) {
        viewModel.getPostDetail(postId)
                .observe(getViewLifecycleOwner(), resource -> {
                    if (resource == null) return;
                    switch (resource.status) {
                        case LOADING:
                            // TODO 로딩 UI 표시
                            return;

                        case ERROR:
                            // TODO 에러 토스트 / 에러 UI
                            return;

                        case SUCCESS:
                            if (resource.data == null) return;
                            bindPostDetail(resource.data);
                            return;
                    }
                });
    }
}