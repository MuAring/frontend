package org.maru.muaring.feature.group.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.maru.muaring.core.ui.CommonToolbarView;
import org.maru.muaring.data.api.dto.ApiResponse;
import org.maru.muaring.data.api.dto.GroupCreateRequest;
import org.maru.muaring.data.api.dto.GroupCreateResponse;
import org.maru.muaring.data.repository.GroupRepository;
import org.maru.muaring.feature.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CreateGroupActivity";

    @Inject
    GroupRepository repository;

    private EditText etGroupName;
    private EditText etGroupDescription;
    private TextView tvGroupCapacity;
    private ImageButton btnDecreaseCapacity;
    private ImageButton btnIncreaseCapacity;
    private LinearLayout layoutPublicGroup;
    private LinearLayout layoutPrivateGroup;
    private Button btnCreateGroup;
    private View toolbar;

    // CategoryFragment 가져옴
    private CategoryFragment categoryFragment;

    // 그룹 정원 초기 세팅 수
    private int currentCapacity = 2;
    private boolean isPublicGroup = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initViews();
        setupCategoryFragment();
        setupListeners();
    }

    private void initViews() {
        etGroupName = findViewById(R.id.et_group_name);
        etGroupDescription = findViewById(R.id.et_group_description);
        tvGroupCapacity = findViewById(R.id.tv_group_capacity);
        btnDecreaseCapacity = findViewById(R.id.btn_decrease_capacity);
        btnIncreaseCapacity = findViewById(R.id.btn_increase_capacity);
        layoutPublicGroup = findViewById(R.id.layout_public_group);
        layoutPrivateGroup = findViewById(R.id.layout_private_group);
        btnCreateGroup = findViewById(R.id.btn_create_group);
        toolbar = findViewById(R.id.toolbar);

        // 툴바 제목 설정
        CommonToolbarView toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("그룹 생성");

        // 초기 선택 상태 설정
        layoutPublicGroup.setSelected(true);
        layoutPrivateGroup.setSelected(false);
    }

    private void setupCategoryFragment() {
        categoryFragment = (CategoryFragment) getSupportFragmentManager()
                .findFragmentById(R.id.category_section);

        if (categoryFragment == null) {
            categoryFragment = new CategoryFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_section, categoryFragment)
                    .commit();
        }
    }

    private void setupListeners() {
        // 정원 감소
        btnDecreaseCapacity.setOnClickListener(v -> {
            if (currentCapacity > 2) {
                currentCapacity--;
                tvGroupCapacity.setText(String.valueOf(currentCapacity));
            } else {
                Toast.makeText(this, "최소 인원은 2명입니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 정원 증가
        btnIncreaseCapacity.setOnClickListener(v -> {
            if (currentCapacity < 100) {
                currentCapacity++;
                tvGroupCapacity.setText(String.valueOf(currentCapacity));
            } else {
                Toast.makeText(this, "최대 인원은 100명입니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 공개 그룹 선택
        layoutPublicGroup.setOnClickListener(v -> {
            isPublicGroup = true;
            layoutPublicGroup.setSelected(true);
            layoutPrivateGroup.setSelected(false);
        });

        // 비공개 그룹 선택
        layoutPrivateGroup.setOnClickListener(v -> {
            isPublicGroup = false;
            layoutPublicGroup.setSelected(false);
            layoutPrivateGroup.setSelected(true);
        });

        // 그룹 생성 버튼
        btnCreateGroup.setOnClickListener(v -> createGroup());
    }

    private void createGroup() {
        // 입력값 검증
        String name = etGroupName.getText().toString().trim();
        String description = etGroupDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "그룹명을 입력해주세요", Toast.LENGTH_SHORT).show();
            etGroupName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "그룹 설명을 입력해주세요", Toast.LENGTH_SHORT).show();
            etGroupDescription.requestFocus();
            return;
        }

        // ===== 디버깅 로그 추가 =====
        Log.d(TAG, "categoryFragment null? " + (categoryFragment == null));

        if (categoryFragment != null) {
            Set<Long> selectedCategoryIds = categoryFragment.getSelectedCategoryIds();
            Log.d(TAG, "selectedCategoryIds size: " + selectedCategoryIds.size());
            Log.d(TAG, "selectedCategoryIds: " + selectedCategoryIds);

            Set<String> selectedNames = categoryFragment.getSelectedCategoryNames();
            Log.d(TAG, "selectedCategoryNames: " + selectedNames);
        }
        // ===========================

        // 선택된 카테고리 가져오기
        Set<Long> selectedCategoryIds = categoryFragment.getSelectedCategoryIds();
        if (selectedCategoryIds.isEmpty()) {
            Toast.makeText(this, "카테고리를 최소 1개 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // Request DTO 생성
        List<Long> categoryList = new ArrayList<>(selectedCategoryIds);
        GroupCreateRequest request = new GroupCreateRequest(
                categoryList,
                name,
                description,
                currentCapacity,
                isPublicGroup
        );

        // API 호출
        btnCreateGroup.setEnabled(false);
        btnCreateGroup.setText("생성 중...");

        repository.createGroup(request).enqueue(new Callback<ApiResponse<GroupCreateResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<GroupCreateResponse>> call,
                                   Response<ApiResponse<GroupCreateResponse>> response) {
                btnCreateGroup.setEnabled(true);
                btnCreateGroup.setText("그룹 생성");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<GroupCreateResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        GroupCreateResponse data = apiResponse.getData();
                        Log.d(TAG, "그룹 생성 성공 - ID: " + data.getGroupId());

                        Toast.makeText(CreateGroupActivity.this,
                                "그룹이 생성되었습니다!", Toast.LENGTH_SHORT).show();

                        // TODO: 생성된 그룹 상세 화면으로 이동
                        finish();
                    } else {
                        showError("그룹 생성에 실패했습니다");
                    }
                } else {
                    handleErrorResponse(response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GroupCreateResponse>> call, Throwable t) {
                btnCreateGroup.setEnabled(true);
                btnCreateGroup.setText("그룹 생성");

                Log.e(TAG, "네트워크 오류", t);
                showError("네트워크 오류가 발생했습니다");
            }
        });
    }

    private void handleErrorResponse(int code) {
        String message;
        switch (code) {
            case 401:
                message = "로그인이 필요합니다";
                break;
            case 400:
                message = "입력 정보를 확인해주세요";
                break;
            default:
                message = "그룹 생성에 실패했습니다 (코드: " + code + ")";
        }
        showError(message);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }
}