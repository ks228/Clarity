package appricottsoftware.clarity.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import appricottsoftware.clarity.R;
import appricottsoftware.clarity.adapters.TabPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karen on 1/19/18.
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.vp_tabs) ViewPager vpTabs;
    @BindView(R.id.tl_tabs) TabLayout tlTabs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Initialize view lookups, listeners

        // Get the view pager to display tabs
        TabPagerAdapter tpaAdapter = new TabPagerAdapter(getChildFragmentManager());
        vpTabs.setAdapter(tpaAdapter);
        // Plug view pager into the tab layout
        tlTabs.setupWithViewPager(vpTabs);
    }
}