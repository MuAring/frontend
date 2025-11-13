package org.maru.muaring.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.maru.muaring.core.R;

public class SegmentedToggleView extends LinearLayout {

    public interface OnSegmentSelectedListener {
        void onGroupSelected();
        void onUserSelected();
    }

    private Button btnGroup;
    private Button btnUser;

    private OnSegmentSelectedListener listener;
    private boolean isGroupSelected = true;

    public SegmentedToggleView(Context context) {
        super(context);
        init(context);
    }

    public SegmentedToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SegmentedToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_segmented_toggle, this, true);

        btnGroup = findViewById(R.id.btn_group);
        btnUser = findViewById(R.id.btn_user);

        btnGroup.setOnClickListener(v -> selectGroup());
        btnUser.setOnClickListener(v -> selectUser());

        // 초기: 그룹 선택
        applyState();
    }
    private void applyState() {
        if (isGroupSelected) {
            btnGroup.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_segment_selected);
            btnGroup.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.surface));

            btnUser.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_segment_unselected);
            btnUser.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.muaring_primary_2));

            // 선택된 애를 위로 올리기
            btnGroup.bringToFront();
        } else {
            btnGroup.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_segment_unselected);
            btnGroup.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.muaring_primary_2));

            btnUser.setBackgroundResource(org.maru.muaring.design.R.drawable.bg_segment_selected);
            btnUser.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.surface));

            // 선택된 애를 위로 올리기
            btnUser.bringToFront();
        }

        // 부모 레이아웃 다시 그리기
        invalidate();
        requestLayout();
    }

    public void setOnSegmentSelectedListener(OnSegmentSelectedListener listener) {
        this.listener = listener;
    }

    public void selectGroup() {
        if (!isGroupSelected) {
            isGroupSelected = true;
            applyState();
            if (listener != null) {
                listener.onGroupSelected();
            }
        }
    }

    public void selectUser() {
        if (isGroupSelected) {
            isGroupSelected = false;
            applyState();
            if (listener != null) {
                listener.onUserSelected();
            }
        }
    }

    public boolean isGroupSelected() {
        return isGroupSelected;
    }
}