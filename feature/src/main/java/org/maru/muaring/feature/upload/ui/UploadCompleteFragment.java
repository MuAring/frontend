package org.maru.muaring.feature.upload.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import org.maru.muaring.feature.R;

public class UploadCompleteFragment extends Fragment {

    public UploadCompleteFragment() {
        super(R.layout.fragment_upload_complete);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_complete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivAlbum = view.findViewById(R.id.ivAlbum);
        Button btnGoHome = view.findViewById(R.id.goToHome);

        if (getArguments() != null) {
            String albumImgUrl = getArguments().getString("albumImgUrl");

            Glide.with(this)
                    .load(albumImgUrl)
                    .placeholder(R.drawable.album_image)
                    .into(ivAlbum);
        }

        btnGoHome.setOnClickListener(v -> {
            NavHostFragment.findNavController(UploadCompleteFragment.this)
                    .navigate(R.id.homeFragment);
        });
    }
}
