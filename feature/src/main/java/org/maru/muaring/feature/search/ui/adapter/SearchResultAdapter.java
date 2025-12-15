package org.maru.muaring.feature.search.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.search.ui.model.SearchResultItem;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {

    public interface OnItemActionClickListener {
        void onItemClick(SearchResultItem item);   // 카드 전체 클릭
        void onActionClick(SearchResultItem item); // 버튼 클릭
    }

    private final List<SearchResultItem> items = new ArrayList<>();
    private final OnItemActionClickListener listener;

    public SearchResultAdapter(OnItemActionClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<SearchResultItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result_card, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder {

        ImageView ivThumbnail;
        TextView tvTitle;
        FrameLayout containerExtra;
        Button btnAction;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            containerExtra = itemView.findViewById(R.id.containerExtra);
            btnAction = itemView.findViewById(R.id.btnAction);
        }

        void bind(SearchResultItem item) {
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            ivThumbnail.setImageResource(org.maru.muaring.design.R.drawable.bg_group_circle);

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(org.maru.muaring.design.R.drawable.bg_group_circle)
                        .error(org.maru.muaring.design.R.drawable.bg_group_circle)
                        .circleCrop()
                        .into(ivThumbnail);
            }

            tvTitle.setText(item.getTitle());
            btnAction.setText(item.getActionText());

            containerExtra.removeAllViews();

            if (item.getType() == SearchResultItem.Type.GROUP) {
                // 그룹용 extra
                View extra = inflater.inflate(
                        R.layout.view_search_group_extra,
                        containerExtra,
                        false
                );
                LinearLayout tagContainer = extra.findViewById(R.id.layoutTagContainer);
                tagContainer.removeAllViews();

                List<String> tags = item.getCategoryNames();
                if (tags != null) {
                    for (String name : tags) {
                        TextView chip = (TextView) inflater.inflate(
                                R.layout.view_category_chip,
                                tagContainer,
                                false
                        );
                        chip.setText(name);

                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                        params.setMarginEnd(5);
                        chip.setLayoutParams(params);

                        tagContainer.addView(chip);
                    }
                }
                containerExtra.addView(extra);

                boolean joined = Boolean.TRUE.equals(item.getIsJoined());

                if (joined) {
                    btnAction.setText("가입 중");
//                    btnAction.setEnabled(false);
                    btnAction.setBackgroundResource(
                            org.maru.muaring.design.R.drawable.bg_button_group_joined
                    );
                    btnAction.setTextColor(
                            itemView.getResources().getColor(
                                    org.maru.muaring.design.R.color.black)
                    );
                } else {
                    btnAction.setText("가입");
                    btnAction.setEnabled(true);
                    btnAction.setBackgroundResource(
                            org.maru.muaring.design.R.drawable.bg_button_group_join
                    );
                    btnAction.setTextColor(
                            itemView.getResources().getColor(android.R.color.white)
                    );
                }

            } else { // 멤버 검색 결과
                View extra = inflater.inflate(
                        R.layout.view_search_user_extra,
                        containerExtra,
                        false
                );
                ImageView ivMusicIcon = extra.findViewById(R.id.ivMusicIcon);
                TextView tvMusicInfo = extra.findViewById(R.id.tvMusicInfo);

                String today = item.getTodayMusicText();
                if (today != null && !today.isEmpty()) {
                    ivMusicIcon.setImageResource(
                            org.maru.muaring.design.R.drawable.ic_today_music_active
                    );
                    tvMusicInfo.setText(today);
                    tvMusicInfo.setTextColor(
                            itemView.getResources().getColor(
                                    org.maru.muaring.design.R.color.green
                            )
                    );
                } else {
                    ivMusicIcon.setImageResource(
                            org.maru.muaring.design.R.drawable.ic_today_music_inactive
                    );
                    tvMusicInfo.setText("오늘의 음악이 없습니다.");
                    tvMusicInfo.setTextColor(
                            itemView.getResources().getColor(
                                    org.maru.muaring.design.R.color.text_light_gray
                            )
                    );
                }

                containerExtra.addView(extra);

                // ==== 멤버용 버튼: isFollowing 에 따라 스타일 분기 ====
                btnAction.setBackgroundTintList(null);
                boolean following = Boolean.TRUE.equals(item.getIsFollowing());

                if (following) {
                    // 팔로잉 상태 → 흰 배경
                    btnAction.setText("팔로잉");
                    btnAction.setEnabled(true); // 언팔 가능해야 하니까 활성화 유지
                    btnAction.setBackgroundResource(
                            org.maru.muaring.design.R.drawable.bg_button_group_joined
                    );
                    btnAction.setTextColor(
                            itemView.getResources().getColor(
                                    org.maru.muaring.design.R.color.black
                            )
                    );
                } else {
                    // 팔로우 전 상태 → 초록 버튼
                    btnAction.setText("팔로우");
                    btnAction.setEnabled(true);
                    btnAction.setBackgroundResource(
                            org.maru.muaring.design.R.drawable.bg_button_group_join
                    );
                    btnAction.setTextColor(
                            itemView.getResources().getColor(android.R.color.white)
                    );
                }
            }

            // 카드 전체 클릭 → onItemClick
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            // 버튼 클릭 → onActionClick
            btnAction.setOnClickListener(v -> {
                // 그룹인 경우 참여 중이면 막기
                if (item.getType() == SearchResultItem.Type.GROUP &&
                        Boolean.TRUE.equals(item.getIsJoined())) {
                    return;
                }
                if (listener != null) listener.onActionClick(item);
            });
        }
    }

    public void refreshItem(SearchResultItem item) {
        int index = items.indexOf(item);
        if (index != -1) {
            notifyItemChanged(index);
        }
    }
}
