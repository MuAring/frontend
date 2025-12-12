package org.maru.muaring.feature.follow.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private List<FollowUser> userList;

    public FollowAdapter(List<FollowUser> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public FollowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_list, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowViewHolder holder, int position) {
        FollowUser user = userList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvMusic.setText(user.getMusicInfo());

        String status = user.getFollowStatus();
        Log.d("FollowAdapter", "status = " + user.getFollowStatus());

        if ("FOLLOWING".equals(status)) {
            holder.btnFollowing.setVisibility(View.VISIBLE);
            holder.btnFollow.setVisibility(View.INVISIBLE);

        } else {
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.btnFollowing.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<FollowUser> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class FollowViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvMusic;
        TextView btnFollow, btnFollowing;
        public FollowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvMusic = itemView.findViewById(R.id.tvMusicInfo);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            btnFollowing = itemView.findViewById(R.id.btnFollowing);
        }
    }
}

