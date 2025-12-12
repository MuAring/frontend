package org.maru.muaring.feature.home.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.data.api.dto.MyGroupSummary;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;

public class GroupSelectorAdapter extends RecyclerView.Adapter<GroupSelectorAdapter.AvatarViewHolder> {

    private static final int VIEW_TYPE_ME = 0;
    private static final int VIEW_TYPE_GROUP_TOGGLE = 1;
    private static final int VIEW_TYPE_GROUP_ITEM = 2;
    private static final int VIEW_TYPE_ADD = 3;

    public interface Listener {
        void onMeClicked();
        void onGroupToggleClicked();
        void onGroupItemClicked(MyGroupSummary group);
        void onAddGroupClicked();
    }

    private final Listener listener;
    private final List<MyGroupSummary> groups = new ArrayList<>();
    private boolean isExpanded = false;
    private String myProfileImageUrl;
    private Long selectedGroupId = null; // null이면 "나" 선택

    public GroupSelectorAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setGroups(List<MyGroupSummary> newGroups) {
        groups.clear();
        if (newGroups != null) {
            groups.addAll(newGroups);
        }
        notifyDataSetChanged();
    }

    public void setExpanded(boolean expanded) {
        if (this.isExpanded == expanded) return;

        // 그룹 아이템들이 시작하는 position = 2 ("나", "그룹 토글" 다음)
        int startPosition = 2;
        int groupCount = groups.size();

        if (expanded) {
            // 펼치는 경우: 아이템이 생기는 것처럼
            this.isExpanded = true;
            notifyItemRangeInserted(startPosition, groupCount);
        } else {
            // 접는 경우: 아이템이 사라지는 것처럼
            notifyItemRangeRemoved(startPosition, groupCount);
            this.isExpanded = false;
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setMyProfileImage(String url) {
        this.myProfileImageUrl = url;
        notifyItemChanged(0); // 0번 포지션이 '나'
    }

    public void setSelectedGroup(Long groupId) {
        this.selectedGroupId = groupId;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = 2 + 1; // me + groupToggle + add
        if (isExpanded) {
            count += groups.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_ME;
        if (position == 1) return VIEW_TYPE_GROUP_TOGGLE;

        int lastIndex = getItemCount() - 1;
        if (position == lastIndex) return VIEW_TYPE_ADD;

        return VIEW_TYPE_GROUP_ITEM;
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(org.maru.muaring.design.R.layout.item_group_avatar, parent, false);
        return new AvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_TYPE_ME:
                bindMe(holder);
                break;
            case VIEW_TYPE_GROUP_TOGGLE:
                bindGroupToggle(holder);
                break;
            case VIEW_TYPE_GROUP_ITEM:
                bindGroupItem(holder, position);
                break;
            case VIEW_TYPE_ADD:
                bindAdd(holder);
                break;
        }
    }

    private void bindMe(AvatarViewHolder holder) {
        resetAvatarStyle(holder);

        holder.tvLabel.setText("나");

        boolean isSelected = (selectedGroupId == null);

        // 내 프사 가져오기
        if (myProfileImageUrl != null && !myProfileImageUrl.isEmpty()) {
            Glide.with(holder.ivAvatar.getContext())
                    .load(myProfileImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_me)
                    .error(R.drawable.ic_profile_me)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_profile_me);
        }

        // 선택 여부에 따라 stroke 적용
        if (isSelected) {
            holder.avatarContainer.setBackgroundResource(
                    org.maru.muaring.design.R.drawable.bg_avatar_selected
            );
        } else {
            holder.avatarContainer.setBackgroundResource(
                    org.maru.muaring.design.R.drawable.bg_avatar_unselected
            );
        }

//        if (isSelected) {
//            holder.ivAvatar.setBackgroundResource(
//                    org.maru.muaring.design.R.drawable.bg_avatar_selected);
//        } else {
//            holder.ivAvatar.setBackgroundResource(
//                    org.maru.muaring.design.R.drawable.bg_circle_gray);
//        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMeClicked();
        });
    }

    private void bindGroupToggle(AvatarViewHolder holder) {
        holder.tvLabel.setText("그룹");
//        holder.ivAvatar.setImageResource(R.drawable.ic_profile_group);
//        holder.itemView.setSelected(isExpanded);

        // 토글은 '아이콘'이니까 crop 말고 inside + padding
        int padding = dpToPx(holder.itemView.getContext(), 12); // 12~16dp
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.ivAvatar.setPadding(padding, padding, padding, padding);

        holder.ivAvatar.setImageResource(R.drawable.ic_profile_group);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGroupToggleClicked();
        });
    }

    private void bindGroupItem(AvatarViewHolder holder, int position) {
        int groupIndex = position - 2;
        MyGroupSummary group = groups.get(groupIndex);

        // 항상 초기화 (재사용 버그 방지)
        holder.ivAvatar.setPadding(0, 0, 0, 0);
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);

        holder.tvLabel.setText(group.getName());
        String imageUrl = group.getImageUrl();

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            // 프사 없으면: 아이콘 모드 (원 안에만)
            int padding = dpToPx(holder.itemView.getContext(), 12); // 12~16dp
            holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.ivAvatar.setPadding(padding, padding, padding, padding);
            holder.ivAvatar.setImageResource(R.drawable.ic_profile_group);
        } else {
            // 프사 있으면: 프사 모드 (꽉 차게)
            Glide.with(holder.ivAvatar.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(org.maru.muaring.design.R.drawable.bg_avatar_unselected)
                    .error(org.maru.muaring.design.R.drawable.bg_avatar_unselected)
                    .into(holder.ivAvatar);
        }

        boolean isSelected = (selectedGroupId != null && selectedGroupId.equals(group.getGroupId()));

        if (isSelected) {
            holder.avatarContainer.setBackgroundResource(
                    org.maru.muaring.design.R.drawable.bg_avatar_selected
            );
        } else {
            holder.avatarContainer.setBackgroundResource(
                    org.maru.muaring.design.R.drawable.bg_avatar_unselected
            );
        }

//        if (isSelected) {
//            holder.ivAvatar.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_avatar_selected);
//        } else {
//            holder.ivAvatar.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_circle_gray);
//        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGroupItemClicked(group);
        });
    }

    private void bindAdd(AvatarViewHolder holder) {
        holder.tvLabel.setText("");
//        holder.ivAvatar.setImageResource(R.drawable.ic_add);

        int padding = dpToPx(holder.itemView.getContext(), 16);
        holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.ivAvatar.setPadding(padding, padding, padding, padding);
        holder.ivAvatar.setImageResource(R.drawable.ic_add);
        holder.ivAvatar.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_group_add);
//        int padding = dpToPx(holder.itemView.getContext(), 16);  // 원하는 값으로 조절
//        holder.ivAvatar.setPadding(padding, padding, padding, padding);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAddGroupClicked();
        });
    }

    private int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        View avatarContainer;
        ImageView ivAvatar;
        TextView tvLabel;

        AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarContainer = itemView.findViewById(org.maru.muaring.design.R.id.avatarContainer);
            ivAvatar = itemView.findViewById(org.maru.muaring.design.R.id.ivAvatar);
            tvLabel = itemView.findViewById(org.maru.muaring.design.R.id.tvLabel);
        }
    }

    private void resetAvatarStyle(AvatarViewHolder holder) {
        holder.ivAvatar.setPadding(0, 0, 0, 0);
    }

    public int getGroupCount() {
        return groups.size();
    }

}
