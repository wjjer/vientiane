package vip.ablog.vientiane.ui.film;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.ext.titles.ScaleTransitionPagerTitleView;
import vip.ablog.vientiane.listener.BannerChangeListener;
import vip.ablog.vientiane.ui.home.HomeFragment;
import vip.ablog.vientiane.ui.home.adapter.MyPageAdapter;
import vip.ablog.vientiane.ui.ContentFragment;

public class FilmFragment extends Fragment implements BannerChangeListener {

    private ViewPager mViewPager;
    private List<String> mTitlesList = new ArrayList<>();
    private List<Fragment> mDataList = new ArrayList<>();
    private List<String> mUrlsList = new ArrayList<>();
    private MyPageAdapter myPageAdapter;
    private View root;
    private MagicIndicator magicIndicator;
    private BannerChangeListener bannerChangeListener;
    private Map<String, String> dataMap;

    public FilmFragment(BannerChangeListener bannerChangeListener) {
        this.bannerChangeListener = bannerChangeListener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public static FilmFragment newInstance(int type, BannerChangeListener bannerChangeListener) {

        Bundle args = new Bundle();
        FilmFragment fragment = new FilmFragment(bannerChangeListener);
        args.putInt(Constant.RESOURCE_TYPR, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){

        }else{

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        int type = arguments.getInt(Constant.RESOURCE_TYPR);
        if (type == 0) {
            dataMap = Constant.tabTVMap;
        } else if (type == 3) {
            dataMap = Constant.tabDVMap;
        } else if (type == 1) {
            dataMap = Constant.tabMVMap;
        } else {
            dataMap = Constant.tabZVMap;
        }


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        magicIndicator = root.findViewById(R.id.magic_indicator);
        mViewPager = root.findViewById(R.id.view_pager);
        initMagicIndicator();
        myPageAdapter = new MyPageAdapter(getChildFragmentManager(), mDataList, mTitlesList);
        mViewPager.setAdapter(myPageAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String url = mUrlsList.get(position);
                bannerChangeListener.change(url);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return root;
    }


    private void initMagicIndicator() {
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            mTitlesList.add(entry.getKey());
            mUrlsList.add(Constant.HOST + entry.getValue());
            mDataList.add(ContentFragment.newInstance(Constant.HOST + entry.getValue()));
        }

        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mTitlesList.get(index));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(Color.GRAY);
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(v -> mViewPager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
                indicator.setColors(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4"), Color.parseColor("#76b0ff"), Color.parseColor("#c683fe"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }


    @Override
    public void change(String url) {

    }
}