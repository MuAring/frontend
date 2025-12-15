package org.maru.muaring.feature.post.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.maru.muaring.core.common.CommentInputController;
import org.maru.muaring.data.api.dto.PostDetailReadResponse;
import org.maru.muaring.feature.R;
import java.util.List;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PostDetailFragment extends Fragment implements CommentAdapter.CommentListener {

    private CommentInputController commentInputController;
    private PostDetailReadViewModel viewModel;
    private CommentAdapter commentAdapter;
    private RecyclerView recyclerComment;

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

    // 댓글
    private int commentCount;

    // 댓글 입력창
    private EditText etAddComment;
    private ImageButton btnAddComment;

    @Nullable
    private Long replyTargetCommentId = null; // null이면 댓글, 아니면 답글

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CommentInputController) {
            commentInputController = (CommentInputController) context;
        }
    }

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

        RecyclerView recyclerView = view.findViewById(R.id.recycler_comment);

        // 댓글 입력창(50dp) + BottomNav(90dp) + 여유
        int bottomPaddingDp = 50 + 90 + 16;
        int bottomPaddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                bottomPaddingDp,
                getResources().getDisplayMetrics()
        );

        recyclerView.setPadding(
                recyclerView.getPaddingLeft(),
                recyclerView.getPaddingTop(),
                recyclerView.getPaddingRight(),
                bottomPaddingPx
        );

        recyclerView.setClipToPadding(false);

        if (commentInputController != null) {
            commentInputController.setCommentInputVisible(true);
        }

        viewModel = new ViewModelProvider(this).get(PostDetailReadViewModel.class);

        if (getArguments() != null) {
            postId = getArguments().getLong("postId");
        }

        bindViews(view);
        initArgs();
        observePostDetail(postId);
        observeComments(postId);
        observeComment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (commentInputController != null) {
            commentInputController.setCommentInputVisible(false);
        }
    }

    @Override
    public void onReplyClick(CommentItem comment) {
        replyTargetCommentId = comment.getId();

        etAddComment.setHint("답글을 입력하세요");
        etAddComment.requestFocus();

        InputMethodManager imm =
                (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etAddComment, InputMethodManager.SHOW_IMPLICIT);
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

        // 댓글
        recyclerComment = v.findViewById(R.id.recycler_comment);
        recyclerComment.setLayoutManager(new LinearLayoutManager(requireContext()));
        commentAdapter = new CommentAdapter(this);
        recyclerComment.setAdapter(commentAdapter);

        // 댓글 입력창
        etAddComment = requireActivity().findViewById(R.id.et_add_comment);
        btnAddComment = requireActivity().findViewById(R.id.btn_add_comment);
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

        // 댓글
        commentCount = response.getCommentCount() != null
                ? response.getCommentCount()
                : 0;

        // 좋아요 업데이트
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

        // 댓글 작성
        btnAddComment.setOnClickListener(v -> {
            String content = etAddComment.getText().toString().trim();
            if (content.isEmpty()) return;

            commentCount++;
            if (replyTargetCommentId == null) {
                viewModel.addComment(postId, content);
            } else {
                viewModel.addReply(replyTargetCommentId, content, postId);
            }
            tvCommentCount.setText(String.valueOf(commentCount));
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

    private void observeComments(Long postId) {
        viewModel.getComments(postId)
                .observe(getViewLifecycleOwner(), resource -> {

                    if (resource == null) return;

                    switch (resource.status) {
                        case LOADING:
                            break;

                        case ERROR:
                            Toast.makeText(
                                    requireContext(),
                                    "댓글을 불러오지 못했어요 😢",
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;

                        case SUCCESS:
                            if (resource.data == null) return;

                            List<CommentItem> items =
                                    CommentMapper.toCommentItems(resource.data);

                            commentAdapter.submitList(items);
                            break;
                    }
                });
    }

    private void observeComment() {
        viewModel.getCommentResult()
                .observe(getViewLifecycleOwner(), resource -> {

                    if (resource == null) return;

                    switch (resource.status) {

                        case LOADING:
                            btnAddComment.setEnabled(false);
                            break;

                        case SUCCESS:
                            btnAddComment.setEnabled(true);

                            // 입력창 초기화
                            etAddComment.setText("");
                            etAddComment.setHint("댓글을 입력하세요");
                            replyTargetCommentId = null;

                            // 키보드 내리기
                            InputMethodManager imm =
                                    (InputMethodManager) requireContext()
                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(
                                    etAddComment.getWindowToken(), 0
                            );
                            break;

                        case ERROR:
                            btnAddComment.setEnabled(true);
                            Toast.makeText(
                                    requireContext(),
                                    resource.message,
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                });
    }
}