//package org.maru.muaring.feature.home.ui;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import org.maru.muaring.feature.R;
//import org.maru.muaring.feature.home.ui.adapter.RecommendMusicAdapter;
//
//import java.util.Arrays;
//
//public class RecommendMusicFragment extends Fragment {
//
//    private RecommendMusicAdapter adapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_recommend_music, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        RecyclerView recycler = view.findViewById(R.id.recycler_recommend_music);
//
//        adapter = new RecommendMusicAdapter();
//        recycler.setAdapter(adapter);
//        recycler.setLayoutManager(
//                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
//        );
//
//        loadMockData(); // 테스트용
//    }
//
//    private void loadMockData() {
//        List<MusicItem> list = Arrays.asList(
//                new MusicItem("Watermelon Sugar", "Harry Styles", "https://..."),
//                new MusicItem("As It Was", "Harry Styles", "https://..."),
//                new MusicItem("Espresso", "Sabrina Carpenter", "https://...")
//        );
//
//        adapter.setItems(list);
//    }
//}
