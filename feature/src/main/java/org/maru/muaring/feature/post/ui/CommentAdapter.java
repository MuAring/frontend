package org.maru.muaring.feature.post.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.maru.muaring.feature.R;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_COMMENT = 0;
    private static final int VIEW_TYPE_REPLY = 1;

    private final List<CommentItem> items = new ArrayList<>();
    private final CommentListener listener;

    public interface CommentListener {
        void onReplyClick(CommentItem comment);
    }

    public CommentAdapter(CommentListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CommentItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isReply()
                ? VIEW_TYPE_REPLY
                : VIEW_TYPE_COMMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_COMMENT) {
            View view = inflater.inflate(
                    R.layout.item_comment_post_detail,
                    parent,
                    false
            );
            return new CommentViewHolder(view);
        } else {
            View view = inflater.inflate(
                    R.layout.item_reply_post_detail,
                    parent,
                    false
            );
            return new ReplyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder,
            int position
    ) {
        CommentItem item = items.get(position);

        if (holder instanceof CommentViewHolder cvh) {
            cvh.bind(item, listener);
        } else if (holder instanceof ReplyViewHolder rvh) {
            rvh.bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvNickname;
        TextView tvContent;
        TextView btnChangeToReplyMode;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvContent = itemView.findViewById(R.id.tv_content);
            btnChangeToReplyMode = itemView.findViewById(R.id.btn_change_to_reply_mode);
        }

        void bind(CommentItem item, CommentListener listener) {
            tvNickname.setText(item.getNickname());
            tvContent.setText(item.getContent());

            Glide.with(itemView)
                    .load(item.getProfileImageUrl())
                    .circleCrop()
                    .into(ivProfile);

            btnChangeToReplyMode.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReplyClick(item);
                }
            });
        }
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfile;
        TextView tvNickname;
        TextView tvContent;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.iv_profile);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvContent = itemView.findViewById(R.id.tv_content);
        }

        void bind(CommentItem item) {
            tvNickname.setText(item.getNickname());
            tvContent.setText(item.getContent());

            Glide.with(itemView)
                    .load(item.getProfileImageUrl())
                    .circleCrop()
                    .into(ivProfile);
        }
    }
}