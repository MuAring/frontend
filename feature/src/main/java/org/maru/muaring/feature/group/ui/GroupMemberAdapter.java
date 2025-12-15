package org.maru.muaring.feature.group.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.data.api.dto.GroupMemberResponse;
import org.maru.muaring.feature.R;
import java.util.ArrayList;
import java.util.List;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.MemberViewHolder> {

    private List<GroupMemberResponse> members = new ArrayList<>();
    private OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(GroupMemberResponse member);
    }

    public void setOnMemberClickListener(OnMemberClickListener listener) {
        this.listener = listener;
    }

    public void setMembers(List<GroupMemberResponse> members) {
        this.members = members != null ? members : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        GroupMemberResponse member = members.get(position);
        holder.bind(member, listener);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProfile;
        private final TextView textName;
        private final TextView textInfo;
        private final ImageView imgCrown;
        private final ImageView imgMusicIcon;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_profile);
            textName = itemView.findViewById(R.id.text_name);
            textInfo = itemView.findViewById(R.id.text_info);

            // 왕관 아이콘 추가 (레이아웃에 없으면 null 체크)
            imgCrown = itemView.findViewById(R.id.img_crown);
            imgMusicIcon = itemView.findViewById(R.id.img_music_icon);
        }

        public void bind(GroupMemberResponse member, OnMemberClickListener listener) {
            // 닉네임 설정
            textName.setText(member.getNickname());

            // 프로필 이미지 설정
            if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(member.getProfileImageUrl())
                        .circleCrop()
                        .placeholder(org.maru.muaring.design.R.drawable.ic_person)
                        .error(org.maru.muaring.design.R.drawable.ic_person)
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(org.maru.muaring.design.R.drawable.ic_person);
            }

            // 최근 음악 정보 설정
            if (member.getRecentMusic() != null) {
                String displayText = member.getRecentMusic().getDisplayText();
                textInfo.setText(displayText);
                textInfo.setVisibility(View.VISIBLE);

                // 음악이 있을 때 CD 아이콘 표시
                if (imgMusicIcon != null) {
                    imgMusicIcon.setImageResource(R.drawable.ic_cd);
                    imgMusicIcon.setVisibility(View.VISIBLE);
                }
            } else {
                textInfo.setText("오늘의 음악이 없습니다.");
                textInfo.setVisibility(View.VISIBLE);

                // 음악이 없을 때 NO_CD 아이콘 표시
                if (imgMusicIcon != null) {
                    imgMusicIcon.setImageResource(R.drawable.ic_no_cd);
                    imgMusicIcon.setVisibility(View.VISIBLE);
                }
            }

            // 관리자 표시 (왕관 아이콘)
            if (imgCrown != null) {
                if (member.isAdmin()) {
                    imgCrown.setVisibility(View.VISIBLE);
                } else {
                    imgCrown.setVisibility(View.GONE);
                }
            }

            // 클릭 이벤트
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMemberClick(member);
                }
            });
        }
    }
}