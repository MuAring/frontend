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
        isExpanded = expanded;
        notifyDataSetChanged();
    }

    public boolean isExpanded() {
        return isExpanded;
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
        holder.tvLabel.setText("나");
        holder.ivAvatar.setImageResource(R.drawable.ic_profile_me);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMeClicked();
        });
    }

    private void bindGroupToggle(AvatarViewHolder holder) {
        holder.tvLabel.setText("그룹");
        holder.ivAvatar.setImageResource(R.drawable.ic_profile_group);
        holder.itemView.setSelected(isExpanded);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGroupToggleClicked();
        });
    }

    private void bindGroupItem(AvatarViewHolder holder, int position) {
        int groupIndex = position - 2;
        MyGroupSummary group = groups.get(groupIndex);

        holder.tvLabel.setText(group.getName());

        if (group.getImageUrl() != null) {
            Glide.with(holder.ivAvatar.getContext())
                    .load(group.getImageUrl())
                    .circleCrop()
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_profile_group);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGroupItemClicked(group);
        });
    }

    private void bindAdd(AvatarViewHolder holder) {
        holder.tvLabel.setText("");
        holder.ivAvatar.setImageResource(R.drawable.ic_add);

        holder.ivAvatar.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_group_add);
        int padding = dpToPx(holder.itemView.getContext());  // 원하는 값으로 조절
        holder.ivAvatar.setPadding(padding, padding, padding, padding);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAddGroupClicked();
        });
    }

    private int dpToPx(Context context) {
        return Math.round(16 * context.getResources().getDisplayMetrics().density);
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvLabel;

        AvatarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(org.maru.muaring.design.R.id.ivAvatar);
            tvLabel = itemView.findViewById(org.maru.muaring.design.R.id.tvLabel);
        }
    }
}
