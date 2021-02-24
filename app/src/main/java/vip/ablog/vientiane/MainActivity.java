package vip.ablog.vientiane;

import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.transformer.MZScaleInTransformer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;
import vip.ablog.vientiane.app.App;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.BannerData;
import vip.ablog.vientiane.entity.UserAnalyse;
import vip.ablog.vientiane.ext.dialog.PopupDialog;
import vip.ablog.vientiane.listener.BannerChangeListener;
import vip.ablog.vientiane.ui.anime.AnimeFragment;
import vip.ablog.vientiane.ui.banner.BannerExtAdapter;
import vip.ablog.vientiane.ui.film.FilmFragment;
import vip.ablog.vientiane.ui.header.HotTopActivity;
import vip.ablog.vientiane.ui.header.LatestUpdateActivity;
import vip.ablog.vientiane.ui.home.HomeFragment;
import vip.ablog.vientiane.ui.variety.VarietyFragment;
import vip.ablog.vientiane.utils.DeviceIdUtils;
import vip.ablog.vientiane.utils.JsoupUtil;
import vip.ablog.vientiane.utils.SPUtil;
import vip.ablog.vientiane.utils.ShareUtil;
import vip.ablog.vientiane.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, BannerChangeListener, View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private int lastSelectedPosition;
    private FragmentTransaction mTransaction;
    private FragmentManager mManager;
    private BottomNavigationBar mBottomNavigationBar;
    private HomeFragment homeFragment;
    private AnimeFragment animeFragment;
    private VarietyFragment varietyFragment;
    private FilmFragment filmFragment;
    private TextBadgeItem mTextBadgeItem;
    private Banner banner_home;
    private List<BannerData> bannerDataList = new ArrayList<>();
    private FrameLayout sv_type;
    private Button btn_search_his;
    private Intent intent;
    private int position = 0;
    private String title = "热门剧场排行";
    private TextView tv_top_hot;
    private LinearLayout ll_home_update, ll_home_zone, ll_home_top, ll_home_random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarTransparent();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initUserAnalyse();
        initView();
        initBanner();
        setDefaultFragment();
    }

    private void initUserAnalyse() {
        String deviceId = DeviceIdUtils.getDeviceId(this);
        BmobQuery<UserAnalyse> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("deviceId", deviceId);
        bmobQuery.setLimit(1).order("-lastUseTime").findObjects(new FindListener<UserAnalyse>() {
            @Override
            public void done(List<UserAnalyse> object, BmobException e) {
                if (e == null) {
                    //查询成功
                    updateUserAnalyse(object.get(0));
                } else {
                    saveUserAnalyse(deviceId);
                }
            }
        });
    }

    private void updateUserAnalyse(UserAnalyse analyse) {
        analyse.setUseTimes(analyse.getUseTimes() + 1);
        analyse.setLastUseTime(new Date());
        analyse.setActiveApp(SPUtil.getInstance().getBoolean(this, Constant.APP_ACTIVE_KEY));
        analyse.update(analyse.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    ToastUtil.showShortToast("初始化成功");
                } else {
                    ToastUtil.showShortToast("初始化失败");
                }
            }

        });
    }

    private void saveUserAnalyse(String deviceId) {
        UserAnalyse analyse = new UserAnalyse();
        analyse.setDeviceId(deviceId);
        analyse.setLastUseTime(new Date());
        analyse.setActiveApp(SPUtil.getInstance().getBoolean(this, Constant.APP_ACTIVE_KEY));
        analyse.setUseTimes(1);
        analyse.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    ToastUtil.showShortToast("初始化成功");
                } else {
                    ToastUtil.showShortToast("初始化失败");
                }
            }
        });
    }


    /**
     * 初始化界面，初始化底部导航
     */
    private void initView() {
        mBottomNavigationBar = findViewById(R.id.nav_view);
        banner_home = findViewById(R.id.banner_home);
        sv_type = findViewById(R.id.sv_type);
        btn_search_his = findViewById(R.id.btn_search_his);
        ll_home_random = findViewById(R.id.ll_home_random);
        ll_home_top = findViewById(R.id.ll_home_top);
        ll_home_update = findViewById(R.id.ll_home_update);
        ll_home_zone = findViewById(R.id.ll_home_zone);
        tv_top_hot = findViewById(R.id.tv_top_hot);
        mBottomNavigationBar.setTabSelectedListener(this);
        ll_home_zone.setOnClickListener(this);
        ll_home_update.setOnClickListener(this);
        ll_home_top.setOnClickListener(this);
        ll_home_random.setOnClickListener(this);
        mBottomNavigationBar.setAutoHideEnabled(false);
        btn_search_his.setOnClickListener(view -> {
            //intent = new Intent(MainActivity.this, HistoryActivity.class);
            //startActivity(intent);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_qrcode);//自己本地的图片可以是drawabe/mipmap
            Uri imageUri =
                    Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "分享AB影院", getResources().getString(R.string.app_desc)));
            ShareUtil.shareImage(this, imageUri, "分享AB影院");
        });
        sv_type.setOnClickListener(view -> {
            intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_RIPPLE);
        mBottomNavigationBar.setBarBackgroundColor(R.color.background_gray_color);
        mBottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.tab_home_pressed, "电视剧").setActiveColorResource(R.color.md_blue_grey_300_color_code).setInactiveIconResource(R.mipmap.tab_home_normal).setInActiveColorResource(R.color.white))
                .addItem(new BottomNavigationItem(R.mipmap.tab_nearby, "电影").setActiveColorResource(R.color.md_brown_300_color_code).setInactiveIconResource(R.mipmap.tab_nearby_off).setInActiveColorResource(R.color.white))
                .addItem(new BottomNavigationItem(R.mipmap.tab_benefits_check, "动漫").setActiveColorResource(R.color.md_teal_300_color_code).setInactiveIconResource(R.mipmap.tab_benefits_check_no).setInActiveColorResource(R.color.white))
                .addItem(new BottomNavigationItem(R.mipmap.tab_mine_off, "综艺").setActiveColorResource(R.color.md_indigo_300_color_code).setInactiveIconResource(R.mipmap.tab_mine).setInActiveColorResource(R.color.white))
                .setFirstSelectedPosition(lastSelectedPosition)
                .initialise();
        mBottomNavigationBar.setTabSelectedListener(this);
        PopupDialog.create(this, "公告", getResources().getString(R.string.app_notify),
                "复制公众号", view -> {
                    ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText("AB小站"); // 复制
                    ToastUtil.showLongToast("公众号已复制，请前去粘贴搜索");
                }, "取消", view -> {

                }, false, false, false).show();

    }

    private void initBanner() {

        banner_home.setAdapter(new BannerExtAdapter(bannerDataList))
                .addBannerLifecycleObserver(this)//添加生命周期观察者
                .setIndicator(new CircleIndicator(App.getContext()));

        banner_home.setPageTransformer(new MZScaleInTransformer());
        banner_home.setIndicator(new RectangleIndicator(this));
        change("https://www.101yingyuan.net/vod/list11-1.html");
    }


    private void initBannerData(String url) {
        Document document = JsoupUtil.jsoupParse(url);
        Elements items = document.getElementsByClass("imgh4 index_stars").get(0).getElementsByTag("li");
        bannerDataList.clear();
        for (Element item : items) {
            String cover = item.getElementsByTag("img").get(0).attr("src");
            String intoUrl = item.getElementsByTag("a").get(0).attr("href");
            String title = item.getElementsByTag("a").get(0).attr("title");
            BannerData bannerData = new BannerData();
            bannerData.setCover(cover);
            bannerData.setTitle(title);
            bannerData.setUrl(intoUrl);
            bannerDataList.add(bannerData);
        }

        runOnUiThread(() -> {
            banner_home.setDatas(bannerDataList);
        });

    }


    private void setDefaultFragment() {
        homeFragment = HomeFragment.newInstance(0, this);
        mManager = getSupportFragmentManager();
        mTransaction = mManager.beginTransaction();
        mTransaction.add(R.id.ll_content, homeFragment);
        mTransaction.commit();
    }

    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    @Override
    public void onTabSelected(int position) {
        lastSelectedPosition = position;
        //开启事务
        mTransaction = mManager.beginTransaction();
        hideFragment(mTransaction);

        /**
         * fragment 用 add + show + hide 方式
         * 只有第一次切换会创建fragment，再次切换不创建
         *
         * fragment 用 replace 方式
         * 每次切换都会重新创建
         *
         */
        switch (position) {
            case 0:   // 首页
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance(position, this);
                    mTransaction.add(R.id.ll_content, homeFragment);
                } else {
                    mTransaction.show(homeFragment);
                }
                this.title = "热门剧场排行";
                break;
            case 3:    // 热映
                if (varietyFragment == null) {
                    varietyFragment = VarietyFragment.newInstance(position, this);
                    mTransaction.add(R.id.ll_content,
                            varietyFragment);
                } else {
                    mTransaction.show(varietyFragment);
                }

                this.title = "热门综艺排行";
                break;

            case 1:  // 资讯
                if (filmFragment == null) {
                    filmFragment = FilmFragment.newInstance(position, this);
                    mTransaction.add(R.id.ll_content,
                            filmFragment);
                } else {
                    mTransaction.show(filmFragment);
                }
                this.title = "热门电影排行";
                break;
            case 2:  // 我的
                if (animeFragment == null) {
                    animeFragment = AnimeFragment.newInstance(position, this);
                    mTransaction.add(R.id.ll_content,
                            animeFragment);
                } else {
                    mTransaction.show(animeFragment);
                }
                this.title = "热门动漫排行";
                break;

        }
        // 事务提交
        mTransaction.commit();
        this.position = position;
        tv_top_hot.setText(title);
        //mTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * 隐藏当前fragment
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (filmFragment != null) {
            transaction.hide(filmFragment);
        }
        if (varietyFragment != null) {
            transaction.hide(varietyFragment);
        }
        if (animeFragment != null) {
            transaction.hide(animeFragment);
        }
    }

    @Override
    public void change(String url) {
        new Thread(() -> {
            try {
                initBannerData(url);
            } catch (Exception e) {

            }
        }).start();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_home_random:
                ToastUtil.showLongToast(App.getContext(), "敬请期待");
                break;
            case R.id.ll_home_top:
                intent = new Intent(this, HotTopActivity.class);
                intent.putExtra(Constant.RESOURCE_TYPR, position);
                intent.putExtra(Constant.TOOLBAR_TITLE, this.title);
                startActivity(intent);
                break;
            case R.id.ll_home_update:
                intent = new Intent(this, LatestUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_home_zone:
                intent = new Intent(this, UserCenterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}

