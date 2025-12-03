package org.maru.muaring.feature.group.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import org.maru.muaring.core.util.Resource;
import org.maru.muaring.feature.R;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@AndroidEntryPoint
public class GroupProfileSetupActivity extends AppCompatActivity {

    private static final String TAG = "GroupProfileSetup";
    private static final String EXTRA_GROUP_ID = "group_id";

    private ImageView ivGroupProfile;
    private ImageButton btnEditProfile;
    private Button btnSkip;

    private GroupProfileViewModel viewModel;
    private Long groupId;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public static Intent newIntent(Context context, Long groupId) {
        Intent intent = new Intent(context, GroupProfileSetupActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile_setup);

        // groupId 받기
        groupId = getIntent().getLongExtra(EXTRA_GROUP_ID, -1L);
        if (groupId == -1L) {
            Toast.makeText(this, "그룹 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(GroupProfileViewModel.class);

        initViews();
        setupImagePicker();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        ivGroupProfile = findViewById(R.id.iv_group_profile);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSkip = findViewById(R.id.btn_skip);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleSelectedImage(imageUri);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> openImagePicker());
        btnSkip.setOnClickListener(v -> skipProfileSetup());
    }

    private void observeViewModel() {
        viewModel.getUploadStatus().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    Toast.makeText(this, "업로드 중...", Toast.LENGTH_SHORT).show();
                    break;

                case SUCCESS:
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
                    finishSetup();
                    break;

                case ERROR:
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void handleSelectedImage(Uri imageUri) {
        try {
            // 이미지를 byte array로 변환
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 이미지 압축
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            long fileSize = imageBytes.length;

            // UI 업데이트
            ivGroupProfile.setImageBitmap(bitmap);
            ivGroupProfile.setPadding(0, 0, 0, 0);

            // 파일명과 타입 설정
            String fileName = "group_profile_" + System.currentTimeMillis() + ".jpg";
            String fileType = "image/jpeg";

            // ViewModel을 통해 업로드 시작
            viewModel.uploadGroupProfileImage(groupId, fileName, fileType, imageBytes, fileSize);

        } catch (Exception e) {
            Log.e(TAG, "이미지 처리 중 오류 발생", e);
            Toast.makeText(this, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void skipProfileSetup() {
        finishSetup();
    }

    private void finishSetup() {
        // TODO: 다음 화면으로 이동 또는 그룹 상세 화면으로 이동
        finish();
    }
}