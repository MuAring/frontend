package org.maru.muaring.feature.member.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.maru.muaring.feature.R;
import org.maru.muaring.feature.history.ui.adapter.MusicHistoryAdapter;
import org.maru.muaring.feature.history.ui.model.MusicHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class MemberProfileFragment extends Fragment {

    private MusicHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHistoryRecycler(view);

        // TODO: ViewModel에서 실제 히스토리 데이터 받아오면 여기서 observe 해서 setItems 호출
        historyAdapter.setItems(createDummyHistory());
    }

    private void setupHistoryRecycler(View root) {
        RecyclerView recyclerView = root.findViewById(R.id.recycler_history);

        historyAdapter = new MusicHistoryAdapter(item -> {
            // 클릭 시 postId, musicId 갖고 상세 화면으로 이동할 때 쓰기
            Long postId = item.getPostId();
            Long musicId = item.getMusicId();
            // TODO: NavController로 이동
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(historyAdapter);
    }

    private List<MusicHistoryItem> createDummyHistory() {
        List<MusicHistoryItem> list = new ArrayList<>();

        list.add(new MusicHistoryItem(
                2,
                1L,
                101L,
                "영원은 그렇듯",
                "리도어 (Redoor)",
                "https://example.com/image1.jpg"
        ));

        list.add(new MusicHistoryItem(
                3,
                2L,
                102L,
                "네모네모",
                "최예나",
                "https://example.com/image2.jpg"
        ));

        list.add(new MusicHistoryItem(
                11,
                3L,
                103L,
                "IRIS OUT",
                "Kenshi Yonezu",
                "https://example.com/image3.jpg"
        ));

        return list;
    }
}
