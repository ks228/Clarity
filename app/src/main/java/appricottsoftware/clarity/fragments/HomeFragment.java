package appricottsoftware.clarity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import appricottsoftware.clarity.R;
import appricottsoftware.clarity.adapters.TabPagerAdapter;
import appricottsoftware.clarity.models.Channel;
import appricottsoftware.clarity.models.Episode;
import appricottsoftware.clarity.models.PlayerInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.loopj.android.http.AsyncHttpClient.log;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    @BindView(R.id.vp_tabs) ViewPager vpTabs;
    @BindView(R.id.tl_tabs) TabLayout tlTabs;

    private TabPagerAdapter tpaAdapter;

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
        tpaAdapter = new TabPagerAdapter(getChildFragmentManager());
        vpTabs.setAdapter(tpaAdapter);

        // Plug view pager into the tab layout
        tlTabs.setupWithViewPager(vpTabs);
    }

    public void showChannelFragment() {
        // Switch to the channel fragment if it is not showing
        if(vpTabs.getCurrentItem() != 0) {
            vpTabs.setCurrentItem(0);
        }
    }

    public void sendDataToBrowseFragment(List<Channel> channels) {
        tpaAdapter.sendDataToBrowseFragment(channels);
    }

    public void requestDataFromChannelFragment() {
        tpaAdapter.requestDataFromChannelFragment();
    }

    public void addChannelToChannelFragment(Channel channel) {
        tpaAdapter.addChannelToChannelFragment(channel);
    }
}
