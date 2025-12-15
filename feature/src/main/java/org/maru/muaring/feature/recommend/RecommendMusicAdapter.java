package org.maru.muaring.feature.recommend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.maru.muaring.data.api.dto.DailyTopMusicResponse;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendMusicAdapter extends RecyclerView.Adapter<RecommendMusicAdapter.VH> {

    private final List<DailyTopMusicResponse.Item> items = new ArrayList<>();

    public void submitList(List<DailyTopMusicResponse.Item> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend_music, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DailyTopMusicResponse.Item item = items.get(position);

        holder.txtTitle.setText(item.getTitle());
        holder.txtArtist.setText(item.getArtistName());

        Glide.with(holder.imgAlbum.getContext())
                .load(item.getAlbumImgUrl())
                .placeholder(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                .error(org.maru.muaring.design.R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(holder.imgAlbum);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgAlbum;
        TextView txtTitle;
        TextView txtArtist;

        VH(@NonNull View itemView) {
            super(itemView);
            imgAlbum = itemView.findViewById(R.id.img_album);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtArtist = itemView.findViewById(R.id.txt_artist);
        }
    }
}
