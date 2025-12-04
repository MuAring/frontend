package org.maru.muaring.core.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import org.maru.muaring.core.R;

public class ToggleView extends RelativeLayout {

    private boolean isOn = false;
    private View thumb;
    private RelativeLayout container;

    private OnToggleChangeListener listener;

    public interface OnToggleChangeListener {
        void onChanged(boolean isOn);
    }

    public void setOnToggleChangeListener(OnToggleChangeListener l) {
        this.listener = l;
    }

    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToggleView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_toggle, this, true);
        container = findViewById(R.id.toggle_container);
        thumb = findViewById(R.id.toggle_thumb);

        setOnClickListener(v -> toggle());
    }

    private void toggle() {
        setToggle(!isOn);

        if (listener != null) {
            listener.onChanged(isOn);
        }
    }

    public void setToggle(boolean on) {
        isOn = on;

        // 배경 변경
        container.setBackgroundResource(
                on ? R.drawable.toggle_background_on : R.drawable.toggle_background_off
        );

        // thumb 이동
        float moveX = on
                ? container.getWidth() - thumb.getWidth() - 4
                : 0;

        thumb.animate()
                .x(moveX)
                .setDuration(150)
                .start();
    }

    public boolean isOn() {
        return isOn;
    }
}
