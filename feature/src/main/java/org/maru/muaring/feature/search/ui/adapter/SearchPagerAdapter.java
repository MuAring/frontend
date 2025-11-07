package org.maru.muaring.feature.search.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.maru.muaring.feature.search.ui.group.GroupSearchFragment;
import org.maru.muaring.feature.search.ui.member.MemberSearchFragment;

public class SearchPagerAdapter extends FragmentStateAdapter {
    public SearchPagerAdapter(@NonNull Fragment parent) { super(parent); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new GroupSearchFragment() : new MemberSearchFragment();
    }

    @Override
    public int getItemCount() { return 2; }
}
