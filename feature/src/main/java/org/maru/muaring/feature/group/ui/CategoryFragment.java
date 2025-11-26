package org.maru.muaring.feature.group.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCategoryResponse;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.feature.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";
    private Set<TextView> selectedCategories = new HashSet<>();
    private final int MAX_SELECTION = 3;
    private LinearLayout categoryContainer;

    @Inject
    GroupRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_category_selection, container, false);

        categoryContainer = view.findViewById(R.id.category_container);

        loadCategories();

        return view;
    }

    private void loadCategories() {
        repository.getGroupCategories().enqueue(new Callback<ApiResponse<List<GroupCategoryResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<GroupCategoryResponse>>> call,
                                   Response<ApiResponse<List<GroupCategoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<GroupCategoryResponse>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<GroupCategoryResponse> categories = apiResponse.getData();
                        createCategoryButtons(categories);
                    } else {
                        showError("카테고리 데이터를 불러올 수 없습니다.");
                    }
                } else {
                    showError("서버 응답 오류: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<GroupCategoryResponse>>> call, Throwable t) {
                Log.e(TAG, "API 호출 실패", t);
                showError("네트워크 오류가 발생했습니다.");
            }
        });
    }

    private void createCategoryButtons(List<GroupCategoryResponse> categories) {
        if (categories == null || categories.isEmpty()) {
            showError("카테고리가 없습니다.");
            return;
        }

        int buttonsPerRow = 4;
        LinearLayout currentRow = null;

        for (int i = 0; i < categories.size(); i++) {
            // 4개마다 새로운 행 생성
            if (i % buttonsPerRow == 0) {
                currentRow = new LinearLayout(getContext());
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                categoryContainer.addView(currentRow);
            }

            TextView button = createCategoryButton(categories.get(i));
            currentRow.addView(button);
        }
    }

    private TextView createCategoryButton(GroupCategoryResponse category) {
        TextView button = new TextView(getContext());

        // 레이아웃 파라미터 설정 (weight=1로 균등 분배)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                dpToPx(38),
                1.0f
        );
        params.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        button.setLayoutParams(params);

        // 텍스트 설정 (displayName 사용)
        button.setText(category.getDisplayName());
        button.setTextSize(14);
        button.setTextColor(Color.parseColor("#666666"));
        button.setGravity(android.view.Gravity.CENTER);

        // 배경 설정
        button.setBackgroundResource(R.drawable.category_button_outline);

        // 클릭 가능 설정
        button.setClickable(true);
        button.setFocusable(true);

        // 카테고리 ID를 태그로 저장 (나중에 서버로 전송할 때 사용)
        button.setTag(category.getId());

        // 클릭 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategorySelection((TextView) v);
            }
        });

        return button;
    }

    private void toggleCategorySelection(TextView button) {
        if (button.isSelected()) {
            // 선택 해제
            button.setSelected(false);
            button.setBackgroundResource(R.drawable.category_button_outline);
            button.setTextColor(Color.parseColor("#666666"));
            selectedCategories.remove(button);
        } else {
            // 선택
            if (selectedCategories.size() < MAX_SELECTION) {
                button.setSelected(true);
                button.setBackgroundResource(R.drawable.category_button_outline_selected);
                button.setTextColor(Color.parseColor("#2E7D32"));
                selectedCategories.add(button);
            } else {
                Toast.makeText(getContext(), "최대 3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, message);
    }

    // dp를 px로 변환
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // 선택된 카테고리 ID 목록 가져오기
    public Set<Long> getSelectedCategoryIds() {
        Set<Long> categoryIds = new HashSet<>();
        for (TextView button : selectedCategories) {
            Long id = (Long) button.getTag();
            if (id != null) {
                categoryIds.add(id);
            }
        }
        return categoryIds;
    }

    // 선택된 카테고리 이름 목록 가져오기 (디버깅용)
    public Set<String> getSelectedCategoryNames() {
        Set<String> categories = new HashSet<>();
        for (TextView button : selectedCategories) {
            categories.add(button.getText().toString());
        }
        return categories;
    }
}