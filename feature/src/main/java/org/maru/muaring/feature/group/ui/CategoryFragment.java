package org.maru.muaring.feature.group.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.maru.muaring.feature.R;

import java.util.HashSet;
import java.util.Set;

public class CategoryFragment extends Fragment {

    private Set<TextView> selectedCategories = new HashSet<>();
    private final int MAX_SELECTION = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_category_selection, container, false);

        setupCategoryButtons(view);

        return view;
    }

    private void setupCategoryButtons(View view) {
        int[] buttonIds = {
                R.id.btn_pop, R.id.btn_kpop, R.id.btn_hiphop, R.id.btn_rnb,
                R.id.btn_rock, R.id.btn_indie, R.id.btn_edm, R.id.btn_ballad,
                R.id.btn_jazz, R.id.btn_metal, R.id.btn_latin, R.id.btn_classic,
                R.id.btn_ost, R.id.btn_national, R.id.btn_jpop, R.id.btn_guitar
        };

        View.OnClickListener categoryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCategorySelection((TextView) v);
            }
        };

        for (int id : buttonIds) {
            TextView button = view.findViewById(id);
            button.setOnClickListener(categoryClickListener);
        }
    }

    private void toggleCategorySelection(TextView button) {
        if (button.isSelected()) {
            // 이미 선택된 버튼을 클릭하면 선택 해제
            button.setSelected(false);
            button.setBackgroundResource(R.drawable.category_button_outline);
            button.setTextColor(Color.parseColor("#666666"));
            selectedCategories.remove(button);
        } else {
            // 새로운 버튼 선택
            if (selectedCategories.size() < MAX_SELECTION) {
                button.setSelected(true);
                button.setBackgroundResource(R.drawable.category_button_outline_selected);
                button.setTextColor(Color.parseColor("#2E7D32"));
                selectedCategories.add(button);
            } else {
                Toast.makeText(getContext(), "최대 3개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 선택된 카테고리 가져오기
    public Set<String> getSelectedCategories() {
        Set<String> categories = new HashSet<>();
        for (TextView button : selectedCategories) {
            categories.add(button.getText().toString());
        }
        return categories;
    }
}