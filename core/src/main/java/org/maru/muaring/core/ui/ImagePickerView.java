package org.maru.muaring.core.ui;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.maru.muaring.core.databinding.ViewImagePickerBinding;

public class ImagePickerView extends FrameLayout {
    private ViewImagePickerBinding binding;

    public ImagePickerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ImagePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImagePickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = ViewImagePickerBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
        );
    }

    // ⚪ 선택한 이미지를 보여준다
    public void setImage(Uri uri) {
        binding.imgProfile.setImageURI(uri);
    }

    public ImageButton getUploadButton() {
        return binding.btnUploadImage;
    }

    public ImageView getImageView() {
        return binding.imgProfile;
    }
}
