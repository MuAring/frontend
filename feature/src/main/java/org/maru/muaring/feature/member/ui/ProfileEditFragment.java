package org.maru.muaring.feature.member.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;

import org.maru.muaring.core.ui.CommonToolbarView;
import org.maru.muaring.core.ui.ImagePickerView;
import org.maru.muaring.core.ui.ToggleView;
import org.maru.muaring.data.helper.ProfileSetupState;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.common.navigation.CommonNavigator;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileEditFragment extends Fragment {

    private CommonNavigator commonNavigator;
    private ProfileEditViewModel viewModel;

    private Uri selectedImageUri;
    private ImagePickerView imagePicker;
    private EditText editNickname;
    private TextView txtNicknameHint;
    private Button btnCheckNickname;
    private ToggleView toggleIsAccountPublic;
    private ToggleView toggleIsDiscoveryEnable;
    private Button btnEdit;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(ProfileEditViewModel.class);

        imagePicker = view.findViewById(R.id.imagePickerForEdit);
        editNickname = view.findViewById(R.id.editNickname);
        txtNicknameHint = view.findViewById(R.id.txtNicknameHint);
        btnCheckNickname = view.findViewById(R.id.btnCheckNickname);
        toggleIsAccountPublic = view.findViewById(R.id.toggleIsAccountPublic);
        toggleIsDiscoveryEnable = view.findViewById(R.id.toggleIsDiscoveryEnable);
        btnEdit = view.findViewById(R.id.btnEdit);

        CommonToolbarView toolbar = view.findViewById(R.id.toolbarInEditProfile);
        toolbar.setTitle("프로필 수정");

        // 회원 기존 설정 정보 불러와서 UI 업데이트
        bindLiveData();

        // 이미지 선택
        imagePicker.setOnClickListener(v -> openGallery());

        btnCheckNickname.setOnClickListener(v -> {
            String nickname = editNickname.getText().toString().trim();
            viewModel.checkNickname(nickname);
        });

        btnEdit.setOnClickListener(v -> {
            viewModel.setNickname(editNickname.getText().toString().trim());
            viewModel.editProfile();
        });

        toggleIsAccountPublic.setOnToggleChangeListener(isOn -> {
            viewModel.setIsAccountPublic(isOn);
        });

        toggleIsDiscoveryEnable.setOnToggleChangeListener(isOn -> {
            viewModel.setIsDiscoveryEnabled(isOn);
        });

        btnEdit.setOnClickListener(v -> {
            viewModel.editProfile();
        });

        // 화면 진입하면 초기값 로드
        viewModel.loadProfile();

        observeState();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CommonNavigator) {
            commonNavigator = (CommonNavigator) context;
        }
    }

    private void bindLiveData() {
        viewModel.getImageUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                Glide.with(imagePicker.getContext())
                        .load(url)
                        .centerCrop()
                        .into(imagePicker.getImageView());
            }
        });

        viewModel.getNickname().observe(getViewLifecycleOwner(), editNickname::setText);

        viewModel.getIsAccountPublic().observe(getViewLifecycleOwner(), toggleIsAccountPublic::setToggle);

        viewModel.getIsDiscoveryEnabled().observe(getViewLifecycleOwner(), toggleIsDiscoveryEnable::setToggle);
    }

    // ⚪ 갤러리 앱 실행을 요청하는 메서드
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // 이미지만 선택하도록 제한 (동영상 X)
        galleryLauncher.launch(intent);
    }

    // ⚪ 선택한 이미지 결과를 받는 객체
    ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            selectedImageUri = result.getData().getData();
                            viewModel.uploadProfileImage(selectedImageUri, requireContext());
                        }
                    }
            );

    private void observeState() {
        viewModel.getState().observe(getViewLifecycleOwner(), state -> {

            if (state instanceof ProfileSetupState.Loading) {
                // 로딩 표시
            }
            else if (state instanceof ProfileSetupState.NicknameAvailable available) {
                txtNicknameHint.setText("사용 가능한 닉네임입니다.");
                txtNicknameHint.setTextColor(ContextCompat.getColor(requireContext(), org.maru.muaring.design.R.color.green));
            }
            else if (state instanceof ProfileSetupState.NicknameUnAvailable) {
                txtNicknameHint.setText("사용할 수 없는 닉네임입니다.");
                txtNicknameHint.setTextColor(ContextCompat.getColor(requireContext(), org.maru.muaring.design.R.color.error));
            }
            else if (state instanceof ProfileSetupState.ImageUploaded uploaded) {
                // ui에 선택한 프로필 사진 보이게
                imagePicker.setImage(selectedImageUri);
                imagePicker.getUploadButton().setBackgroundResource(R.drawable.photo_uploaded);

                viewModel.setUploadedImageInfo(uploaded.s3Key, uploaded.fileName, uploaded.fileType, uploaded.fileSize);
            }
            else if (state instanceof ProfileSetupState.ProfileCreated edited) {
                Toast.makeText(requireContext(), "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this)
                        .popBackStack();   // 이전 프로필 조회 화면으로 복귀
            }
            else if (state instanceof ProfileSetupState.Error) {
                Toast.makeText(requireContext(),
                        ((ProfileSetupState.Error) state).message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}