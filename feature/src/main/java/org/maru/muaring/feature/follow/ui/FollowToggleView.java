package org.maru.muaring.feature.follow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.maru.muaring.feature.R;

public class FollowToggleView extends LinearLayout {

    public interface OnSegmentSelectedListener {
        void onFollowerSelected();
        void onFollowingSelected();
    }

    private Button btnFollower;
    private Button btnFollowing;

    private View bgFollowerSelected;
    private View bgFollowingSelected;

    private OnSegmentSelectedListener listener;
    private boolean isFollowerSelected = true;

    public FollowToggleView(Context context) {
        super(context);
        init(context);
    }

    public FollowToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FollowToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_follow_toggle, this, true);

        btnFollower = findViewById(R.id.btn_follower);
        btnFollowing = findViewById(R.id.btn_following);

        bgFollowerSelected = findViewById(R.id.bg_group_selected);
        bgFollowingSelected = findViewById(R.id.bg_user_selected);

        btnFollower.setOnClickListener(v -> selectFollower());
        btnFollowing.setOnClickListener(v -> selectFollowing());

        isFollowerSelected = true;
        applyState();
    }

    private void applyState() {
        if (isFollowerSelected) {
            bgFollowerSelected.setVisibility(View.VISIBLE);
            bgFollowingSelected.setVisibility(View.INVISIBLE);

            btnFollower.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.surface));
            btnFollowing.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.black));
        } else {
            bgFollowerSelected.setVisibility(View.INVISIBLE);
            bgFollowingSelected.setVisibility(View.VISIBLE);

            btnFollower.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.black));
            btnFollowing.setTextColor(getResources().getColor(org.maru.muaring.design.R.color.surface));
        }
    }

    public void selectFollower() {
        if (!isFollowerSelected) {
            isFollowerSelected = true;
            applyState();
            if (listener != null) listener.onFollowerSelected();
        }
    }

    public void selectFollowing() {
        if (isFollowerSelected) {
            isFollowerSelected = false;
            applyState();
            if (listener != null) listener.onFollowingSelected();
        }
    }

    public void setOnSegmentSelectedListener(OnSegmentSelectedListener listener) {
        this.listener = listener;
    }

    public boolean isFollowerSelected() {
        return isFollowerSelected;
    }

}

