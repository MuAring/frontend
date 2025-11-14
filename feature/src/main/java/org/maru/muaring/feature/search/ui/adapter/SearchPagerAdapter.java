package org.maru.muaring.feature.search.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.maru.muaring.feature.search.ui.group.GroupSearchFragment;
import org.maru.muaring.feature.search.ui.member.MemberSearchFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;

public class SearchPagerAdapter extends FragmentStateAdapter {
    public SearchPagerAdapter(@NonNull Fragment parent) { super(parent); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new GroupSearchFragment() : new MemberSearchFragment();
    }

    private List<SearchItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchItem item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<SearchItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }

//    @NonNull
//    @Override
//    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_search_result, parent, false);
//        return new SearchViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
//        SearchItem item = items.get(position);
//        holder.bind(item);
//
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onItemClick(item);
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imgProfile;
        private final TextView textName;
        private final TextView textInfo;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.img_profile);
            textName = itemView.findViewById(R.id.text_name);
            textInfo = itemView.findViewById(R.id.text_info);
        }

        public void bind(SearchItem item) {
            textName.setText(item.getName());
            textInfo.setText(item.getInfo());

            // TODO: 이미지 로딩 (Glide, Coil 등 사용)
            // Glide.with(imgProfile.getContext())
            //     .load(item.getProfileImageUrl())
            //     .placeholder(R.drawable.bg_circle_placeholder)
            //     .into(imgProfile);
        }
    }

    // 검색 결과 데이터 모델
    public static class SearchItem {
        private String id;
        private String name;
        private String info;
        private String profileImageUrl;

        public SearchItem(String id, String name, String info, String profileImageUrl) {
            this.id = id;
            this.name = name;
            this.info = info;
            this.profileImageUrl = profileImageUrl;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }
    }
}
