package org.maru.muaring.feature.group.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;
import org.maru.muaring.data.api.dto.MyGroupSummary;

import java.util.ArrayList;
import java.util.List;

public class MyGroupsAdapter extends RecyclerView.Adapter<MyGroupsAdapter.ViewHolder> {

    private List<MyGroupSummary> groups = new ArrayList<>();
    private OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(MyGroupSummary group);
    }

    public void setOnGroupClickListener(OnGroupClickListener listener) {
        this.listener = listener;
    }

    public void setGroups(List<MyGroupSummary> groups) {
        this.groups = groups != null ? groups : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProfile;
        private final TextView textName;
        private final ViewGroup chipContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_profile);
            textName = itemView.findViewById(R.id.text_name);
            chipContainer = itemView.findViewById(R.id.chip_container);
        }

        void bind(MyGroupSummary group) {
            // 그룹명 설정
            textName.setText(group.getName());

            // 프로필 이미지 로드
            if (group.getImageUrl() != null && !group.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(group.getImageUrl())
                        .placeholder(org.maru.muaring.design.R.drawable.ic_person)
                        .error(org.maru.muaring.design.R.drawable.ic_person)
                        .circleCrop()
                        .into(imgProfile);
            } else {
                imgProfile.setImageResource(org.maru.muaring.design.R.drawable.ic_person);
            }

            // 카테고리 칩 설정
            chipContainer.removeAllViews();
            if (group.getCategoryNames() != null) {
                for (String categoryName : group.getCategoryNames()) {
                    TextView chip = (TextView) LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.view_category_chip, chipContainer, false);
                    chip.setText(categoryName);
                    chipContainer.addView(chip);
                }
            }

            // 아이템 클릭
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onGroupClick(group);
                }
            });
        }
    }
}