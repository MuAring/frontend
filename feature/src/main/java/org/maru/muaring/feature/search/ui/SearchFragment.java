package org.maru.muaring.feature.search.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import org.maru.muaring.feature.R;

public class SearchFragment extends Fragment {
    public SearchFragment() { super(R.layout.fragment_search); }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        ViewPager2 pager = v.findViewById(R.id.pager);
        pager.setAdapter(new org.maru.muaring.feature.search.ui.adapter.SearchPagerAdapter(this));

        MaterialButtonToggleGroup toggle = v.findViewById(R.id.toggleGroup);
        toggle.check(R.id.btnGroup);
        toggle.addOnButtonCheckedListener((g, id, checked) -> {
            if (checked) pager.setCurrentItem(id == R.id.btnGroup ? 0 : 1, true);
        });
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int pos) { toggle.check(pos==0?R.id.btnGroup:R.id.btnUser); }
        });

        TextInputEditText et = v.findViewById(R.id.etSearch);
        et.setOnEditorActionListener((tv, actionId, event) -> {
            boolean enter = event!=null && event.getKeyCode()== KeyEvent.KEYCODE_ENTER;
            if (actionId== EditorInfo.IME_ACTION_SEARCH || enter) { /* TODO: call VM */ return true; }
            return false;
        });
    }
}