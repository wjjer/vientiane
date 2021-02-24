package vip.ablog.vientiane.ui.home.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allan on 17-9-26.
 */

public class MyPageAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fmList;
    private List<String> tabIndicators;
    public MyPageAdapter(FragmentManager fm, List<Fragment> list, List<String> tabIndicators) {
        super(fm);
        this.fmList = list;
        this.tabIndicators = tabIndicators;
    }

    @Override
    public Fragment getItem(int position) {
        return fmList.get(position);
    }

    @Override
    public int getCount() {
        return fmList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabIndicators.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
