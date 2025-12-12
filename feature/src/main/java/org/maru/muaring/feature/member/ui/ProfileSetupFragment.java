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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import org.maru.muaring.core.ui.ImagePickerView;
import org.maru.muaring.data.helper.ProfileSetupState;
import org.maru.muaring.feature.R;
import org.maru.muaring.feature.common.navigation.CommonNavigator;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileSetupFragment extends Fragment {

    private CommonNavigator commonNavigator;
    private ProfileSetupViewModel viewModel;
    private TextView txtNicknameHint;

    private LinearLayout imageSection;
    private TextView tvProfileImage;
    private ImagePickerView imagePickerView;
    private Uri selectedImageUri;
    private Button btnCompleteProfile;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_profile_setup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileSetupViewModel.class);

        EditText editNickname = view.findViewById(R.id.editNickname);
        Button btnCheckNickname = view.findViewById(R.id.btnCheckNickname);
        txtNicknameHint = view.findViewById(R.id.txtNicknameHint);
        imageSection = view.findViewById(R.id.layoutImageSection);
        tvProfileImage = view.findViewById(R.id.tvProfileImage);
        imagePickerView = view.findViewById(R.id.imagePicker);
        btnCompleteProfile = view.findViewById(R.id.btnCompleteProfile);

        btnCheckNickname.setOnClickListener(v ->
                viewModel.checkNickname(editNickname.getText().toString())
        );

        imagePickerView.setOnClickListener(v -> openGallery());

        btnCompleteProfile.setOnClickListener(v -> {
            viewModel.createProfile(editNickname.getText().toString());
        });

        observeState();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CommonNavigator) {
            commonNavigator = (CommonNavigator) context;
        }
    }

    private void observeState() {
        viewModel.getState().observe(getViewLifecycleOwner(), state -> {

            if (state instanceof ProfileSetupState.Loading) {
                // 로딩 표시
            }
            else if (state instanceof ProfileSetupState.NicknameAvailable available) {
                txtNicknameHint.setText("사용 가능한 닉네임입니다.");
                txtNicknameHint.setTextColor(ContextCompat.getColor(requireContext(), org.maru.muaring.design.R.color.green));
                imageSection.setVisibility(View.VISIBLE);
                tvProfileImage.setText(available.nickname + "님을 나타낼\n사진을 골라볼까요?");
            }
            else if (state instanceof ProfileSetupState.NicknameUnAvailable) {
                txtNicknameHint.setText("사용할 수 없는 닉네임입니다.");
                txtNicknameHint.setTextColor(ContextCompat.getColor(requireContext(), org.maru.muaring.design.R.color.error));
                imageSection.setVisibility(View.GONE);
            }
            else if (state instanceof ProfileSetupState.ImageUploaded uploaded) {
                // ui에 선택한 프로필 사진 보이게
                imagePickerView.setImage(selectedImageUri);
                imagePickerView.getUploadButton().setBackgroundResource(R.drawable.photo_uploaded);

                viewModel.setUploadedImageInfo(uploaded.s3Key, uploaded.fileName, uploaded.fileType, uploaded.fileSize);
            }
            else if (state instanceof ProfileSetupState.ProfileCreated created) {
                commonNavigator.navigateToMain(created.nickname);
            }
            else if (state instanceof ProfileSetupState.Error) {
                Toast.makeText(requireContext(),
                        ((ProfileSetupState.Error) state).message,
                        Toast.LENGTH_SHORT).show();
            }
        });
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
}