package org.maru.muaring.feature.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.feature.R;

public class SearchBarFragment extends Fragment {

    private static final String ARG_HINT = "arg_hint";

    private EditText etSearch;
    private ImageView ivSearch;
    private View rootLayout;

    public interface OnSearchClickListener {
        void onSearchClick(String query);
    }

    private OnSearchClickListener listener;

    public SearchBarFragment() {
        // 빈 생성자 필수
    }

    public static SearchBarFragment newInstance(String hint) {
        SearchBarFragment fragment = new SearchBarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HINT, hint);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_search_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        ivSearch = view.findViewById(R.id.ivSearch);
        rootLayout = view.findViewById(R.id.rootSearchBar);

        // 포커스 변화에 따라 배경 변경
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                rootLayout.setBackgroundResource(
                        org.maru.muaring.design.R.drawable.bg_search_bar_active
                );
            } else {
                rootLayout.setBackgroundResource(
                        org.maru.muaring.design.R.drawable.bg_search_bar
                );
            }
        });

        // 루트 뷰 눌렀을 때 포커스 해제 → 색도 자동 원복
        view.setOnTouchListener((v, event) -> {
            if (etSearch.hasFocus()) etSearch.clearFocus();
            return false;
        });

        // 힌트 적용
        if (getArguments() != null) {
            String hint = getArguments().getString(ARG_HINT);
            if (!TextUtils.isEmpty(hint)) {
                etSearch.setHint(hint);
            }
        }

        // 아이콘 클릭 시 콜백
        ivSearch.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSearchClick(etSearch.getText().toString());
            }
        });

        // 키보드의 검색 버튼 눌렀을 때도 동일 동작
        etSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (listener != null) {
                listener.onSearchClick(etSearch.getText().toString());
            }
            return false;
        });
    }

    // 부모 Fragment/Activity에서 리스너 설정
    public void setOnSearchClickListener(OnSearchClickListener listener) {
        this.listener = listener;
    }

    // 필요하면 외부에서 힌트 바꾸기
    public void setHint(String hint) {
        if (etSearch != null) {
            etSearch.setHint(hint);
        }
    }

    public String getQuery() {
        return etSearch != null ? etSearch.getText().toString() : "";
    }

    // 검색어 비우기
    public void clearQuery() {
        if (etSearch != null) {
            etSearch.setText("");
        }
    }

}
