package org.maru.muaring.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

    // ✅ 선택 배경 뷰
    private View bgGroupSelected;
    private View bgUserSelected;

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
        setOrientation(VERTICAL); // 혹시라도 레이아웃 깨지는 것 방지용
        LayoutInflater.from(context).inflate(R.layout.view_segmented_toggle, this, true);

        btnGroup = findViewById(R.id.btn_group);
        btnUser = findViewById(R.id.btn_user);

        bgGroupSelected = findViewById(R.id.bg_group_selected);
        bgUserSelected = findViewById(R.id.bg_user_selected);

        btnGroup.setOnClickListener(v -> selectGroup());
        btnUser.setOnClickListener(v -> selectUser());

        // 초기: 그룹 선택
        isGroupSelected = true;
        applyState();
    }

    private void applyState() {
        if (isGroupSelected) {
            // ✅ 그룹 선택
            bgGroupSelected.setVisibility(View.VISIBLE);
            bgUserSelected.setVisibility(View.INVISIBLE);

            btnGroup.setTextColor(
                    getResources().getColor(org.maru.muaring.design.R.color.surface) // white
            );
            btnUser.setTextColor(
                    getResources().getColor(org.maru.muaring.design.R.color.black) // black
            );
        } else {
            // ✅ 사용자 선택
            bgGroupSelected.setVisibility(View.INVISIBLE);
            bgUserSelected.setVisibility(View.VISIBLE);

            btnGroup.setTextColor(
                    getResources().getColor(org.maru.muaring.design.R.color.black)
            );
            btnUser.setTextColor(
                    getResources().getColor(org.maru.muaring.design.R.color.surface)
            );
        }

        // 위치는 고정 / z-index는 이미 XML에서 배경 -> 버튼 순서로 해결됨
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
