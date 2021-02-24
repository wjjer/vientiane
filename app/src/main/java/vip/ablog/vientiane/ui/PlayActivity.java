package vip.ablog.vientiane.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.app.App;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.SeriesData;
import vip.ablog.vientiane.ext.SerializableMap;
import vip.ablog.vientiane.ext.dialog.PopupDialog;
import vip.ablog.vientiane.ui.home.adapter.HomeSeriesAdapter;
import vip.ablog.vientiane.utils.MobileInfoUtil;
import vip.ablog.vientiane.utils.ToastUtil;


public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private String info;
    private Toolbar tb_play;
    private DrawerLayout draw_play;
    private WebView mwebview;
    public String finalUrl = "";
    public String url = "";
    private String title = "";
    private Button btn_tp, btn_juji;
    private MyHandler handler = new MyHandler();
    private String videoId = "";
    private String TAG = "PlayActivity";
    private ProgressBar pb_loading;
    private TextView tv_loading, tv_xl_tag;
    private int pageFinish = 0;
    private String tv;
    private List<String> mDatasTitle;
    private List<String> mDatasUrl;
    private boolean isFilm;
    private RecyclerView rv_juji;
    private ArrayList<SeriesData> juJiList;
    private Map<String, String> urlMap;
    private long playTime;
    private String series;
    private String hisImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        //检查内核加载状况
        if (MobileInfoUtil.checkX5(mwebview) == null) {
            ToastUtil.showLongToast("播放器内核加载失败，请重启软件再试,或者加群反馈。");
        }else{
            mwebview.getSettingsExtension().setDayOrNight(true);
            mwebview.getSettingsExtension().setShouldTrackVisitedLinks(true);
            mwebview.getSettingsExtension().setDisplayCutoutEnable(true);
            configPlaySetting();
        }
        //checkAD();
        initData();
        WebSettings webSettings = mwebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setBlockNetworkLoads(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setUserAgentString(Constant.USER_AGENT);
        mwebview.setVideoFullScreen(this, false);
        mwebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https") || url.startsWith("http")) {
                    view.loadUrl(url);
                } else {
                    ToastUtil.showShortToast("视频资源已失效！");
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                //更新进度条
                pageFinish++;
                if (pageFinish == 1) {
                    pb_loading.setProgress(149);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pb_loading.setVisibility(View.GONE);
                        }
                    }, 1600);
                }
            }
        });

        mwebview.setWebChromeClient(new WebChromeClient() {

            private View myView;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                pb_loading.setProgress(progress);
                tv_loading.setText("加载中:" + progress + "%");
                if (progress == 100) {
                    handler.postDelayed(() -> handler.sendEmptyMessage(4), 2000);

                }
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                return handleJsPrompt(view, url, message, defaultValue, result);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return handleJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return handleJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {

            }

            //自定义视频播放  如果需要启用这个，需要设置x5,自己实现全屏播放。目前的使用的x5的视频播放
            //如果是点击h5 vedio标签的播放，需要自己实现全屏播放
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                super.onShowCustomView(view, customViewCallback);
                ViewGroup parent = (ViewGroup) mwebview.getParent();
                parent.removeView(mwebview);

                // 设置背景色为黑色
                view.setBackgroundColor(getResources().getColor(R.color.black));
                parent.addView(view);
                myView = view;
                tb_play.setVisibility(View.GONE);
                setFullScreen();
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (myView != null) {
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(mwebview);
                    myView = null;
                    tb_play.setVisibility(View.VISIBLE);
                    quitFullScreen();
                }
            }

        });

        mwebview.setDayOrNight(true);
        mwebview.loadUrl(url);
    }

    private void checkAD() {
        String flag = Constant.APP_CACHE.getAsString(Constant.PLAY_VIDEO_AD);
        if (!"1".equals(flag)) {
            PopupDialog.create(this, "提示", getResources().getString(R.string.ad_notify),
                    "好的", view -> {
                        //Constant.APP_CACHE.put(Constant.PLAY_VIDEO_AD, "1", 60);
                        initAD();
                    }, "咦~", view -> {
                        ToastUtil.showShortToast("呜呜呜~");
                    }, false, false, false).show();
        }
    }

    private void initAD() {


    }

    private void initView() {
        tv = getIntent().getStringExtra("tv");
        hisImg = getIntent().getStringExtra("hisImg");
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        series = getIntent().getStringExtra("hisSeries");
        videoId = getIntent().getStringExtra("id");
        info = getIntent().getStringExtra("info");
        playTime = System.currentTimeMillis();
        btn_juji = findViewById(R.id.btn_juji);
        rv_juji = (RecyclerView) findViewById(R.id.rv_juji);
        tb_play = findViewById(R.id.tb_play);
        draw_play = findViewById(R.id.draw_play);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        btn_tp = findViewById(R.id.btn_tp);
        pb_loading.setMax(150);
        mwebview = findViewById(R.id.mwebview);
        setSupportActionBar(tb_play);
        setTitle(title + "_" + series);
        tb_play.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_tp.setOnClickListener(this);
        btn_juji.setOnClickListener(view -> {
            //打开关闭抽屉
            if (draw_play.isDrawerOpen(GravityCompat.START)) {
                draw_play.closeDrawer(GravityCompat.START);
            } else {
                draw_play.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initData() {
        //获取剧集信息
        SerializableMap playUrls = (SerializableMap) getIntent().getExtras().getSerializable("playUrls");
        if (playUrls != null) {
            urlMap = playUrls.getMap();
            juJiList = new ArrayList<>();
            for (Map.Entry<String, String> map : urlMap.entrySet()) {
                SeriesData juJi = new SeriesData();
                juJi.setName(map.getKey());
                juJi.setUrl(map.getValue());
                juJiList.add(juJi);
            }
        }
        mDatasTitle = new ArrayList<>();
        mDatasUrl = new ArrayList<>();
        HomeSeriesAdapter adapter = new HomeSeriesAdapter(juJiList, PlayActivity.this);
        rv_juji.setLayoutManager(new GridLayoutManager(PlayActivity.this, 3));
        rv_juji.setAdapter(adapter);
        final Intent intent = new Intent(PlayActivity.this, PlayActivity.class);
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            intent.putExtra("url", juJiList.get(position).getUrl());
            intent.putExtra("hisUrl", url);
            intent.putExtra("hisTitle", title);
            intent.putExtra("hisImg", hisImg);
            intent.putExtra("hisSeries", juJiList.get(position).getName());
            intent.putExtra("info", info);
            intent.putExtra("title", title);
            SerializableMap serializableMap = new SerializableMap();
            serializableMap.setMap(urlMap);
            Bundle bundle = new Bundle();
            bundle.putSerializable("playUrls", serializableMap);
            intent.putExtras(bundle);
            startActivity(intent);
            PlayActivity.this.finish();
        });

    }

    /**
     * 播放设置    已经开启x5全屏  小窗播放  页内播放等。
     */
    protected void configPlaySetting() {
        Bundle data = new Bundle();
//true表示标准全屏，false表示X5全屏；不设置默认false，
        data.putBoolean("standardFullScreen", true);
//false：关闭小窗；true：开启小窗；不设置默认true，
        data.putBoolean("supportLiteWnd", false);
        data.putString("title", title);
//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        data.putInt("DefaultVideoScreen", 2);
        mwebview.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
//        standardFullScreen 全屏设置
//
//        设置为true时，我们会回调WebChromeClient的onShowCustomView方法，由开发者自己实现全屏展示；
//
//        设置为false时，由我们实现全屏展示，我们实现全屏展示需要满足下面两个条件：
//
//        a. 我们 Webview初始化的Context必须是Activity类型的Context
//
//        b. 我们 Webview 所在的Activity要声明这个属性
//
//        android:configChanges="orientation|screenSize|keyboardHidden"
//        如果不满足这两个条件，standardFullScreen 自动置为 true
//        supportLiteWnd 小窗播放设置
//
//        前提standardFullScreen=false，这个条件才生效
//
//        设置为 true， 开启小窗功能
//
//        设置为 false，不使用小窗功能
//
//        DefaultVideoScreen 初始播放形态设置
//
//        a、以页面内形态开始播放
//
//        b、以全屏形态开始播放

    }

    /**
     * 播放视频  传入视频的url地址
     *
     * @return
     */
    protected boolean playVideoByTbs(String videoUrl) {
        if (TbsVideo.canUseTbsPlayer(this)) {
            //播放器是否可以使用
            Bundle xtraData = new Bundle();
            xtraData.putInt("screenMode", 102);//全屏设置 和控制栏设置
            TbsVideo.openVideo(this, videoUrl, xtraData);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否在android中处理Js的Prompt弹出框
     * false不处理，true可拦截处理
     */
    protected boolean handleJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return false;
    }

    /**
     * 是否在android中处理Js的Alert弹出框
     * false不处理，true可拦截处理
     */
    protected boolean handleJsAlert(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * 是否在android中处理Js的Confirm弹出框
     * false不处理，true可拦截处理
     */
    protected boolean handleJsConfirm(WebView view, String url, String message, JsResult result) {
        return false;
    }

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setAttributes(attrs);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 4:
                    tv_loading.setVisibility(View.GONE);
                    break;
                case 5:
                    pb_loading.setVisibility(View.GONE);
                    break;
                case 6:
                    ToastUtil.showLongToast(App.getContext(), "请先去首页投屏菜单打开投屏!");
                    break;
                case 7:
                    ToastUtil.showShortToast(App.getContext(), "此视频不支持投屏！");
                    break;

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除cookie
        QbSdk.clearAllWebViewCache(this,true);
        mwebview.clearCache(true);
        mwebview.clearFormData();
        mwebview.clearHistory();
        mwebview.stopLoading();
        mwebview.destroyDrawingCache();
        handler.removeCallbacksAndMessages(null);
        Log.i(TAG, "数据已清理！！！！");
    }


    @Override
    public void onClick(View view) {


        TbsVideo.openVideo(getApplicationContext(), "https://www.jqaaa.com/jq3/?url=https://v.qq.com/x/cover/ldl8jqkvepk3t6a.html?ptag=360kan.movie.free");
        //ToastUtils.showShortToast(PlayActivity.this,Constant.jiexiUrl+url);
        //投屏解析
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    String datas = HttpUtils.getData("http://192.168.0.114:8190/video/system/parseVideo?url="+Constant.jiexiUrl+url);
//                    http://y2.xsdd.org:91/ifr?url=hel%2fmDvrhQnevHwsCFB0GQck%2bWPQvJNCuGgDSP0%2fT%2f9%2bCiySDWWSFH3QTbUtz%2bh%2f%2fARg2isVUtk1DdWV4ff5cg%3d%3d&type=m3u8 = datas;
                finalUrl = "http://dy.video.ums.uc.cn/w/1522810665/video/wemedia/4544cfc0c34a4f71ae7b39859216a76b/28510688d76f01f2ff190884c5a27263-811694039-6-0-1.mp4?auth_key=1524490734-2c43ebc6f277a96be50259ac1dffca8a-0-cf122eda81159c6882b46386c80841a6&count=5&tll=1505";
                handler.sendEmptyMessage(6);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    handler.sendEmptyMessage(7);
//                }

            }
        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
