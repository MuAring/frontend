//package org.maru.muaring.feature.home.ui.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.maru.muaring.feature.R;
//
//import java.util.ArrayList;
//
//public class RecommendMusicAdapter extends RecyclerView.Adapter<RecommendMusicAdapter.ViewHolder> {
//
//    private List<MusicItem> items = new ArrayList<>();
//
//    public void setItems(List<MusicItem> newItems) {
//        items = newItems;
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_recommend_music, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        MusicItem item = items.get(position);
//
//        holder.txtTitle.setText(item.getTitle());
//        holder.txtArtist.setText(item.getArtist());
//
//        Glide.with(holder.itemView.getContext())
//                .load(item.getAlbumImageUrl())
//                .into(holder.imgAlbum);
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgAlbum;
//        TextView txtTitle, txtArtist;
//
//        ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgAlbum = itemView.findViewById(R.id.img_album);
//            txtTitle = itemView.findViewById(R.id.txt_title);
//            txtArtist = itemView.findViewById(R.id.txt_artist);
//        }
//    }
//}
