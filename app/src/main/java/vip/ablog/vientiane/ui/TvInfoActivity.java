package vip.ablog.vientiane.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import com.varunest.loader.TheGlowingLoader;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.PlaySourceData;
import vip.ablog.vientiane.entity.SeriesData;
import vip.ablog.vientiane.ext.SerializableMap;
import vip.ablog.vientiane.ui.home.adapter.HomeSeriesAdapter;
import vip.ablog.vientiane.utils.FastBlurUtil;
import vip.ablog.vientiane.utils.GlideImgManager;
import vip.ablog.vientiane.utils.JsoupUtil;
import vip.ablog.vientiane.utils.ToastUtil;
import vip.ablog.vientiane.web.BaseTencenWebactivity;


public class TvInfoActivity extends AppCompatActivity {
    private TextView tv_home_tv_info, tv_zk, tv_tv_title, tv_year, tv_area, tv_director, tv_actor;
    private String url;
    private ImageView iv_tv_info_cover;
    private static Handler handler;
    private String info;
    private Map<String, String> playUrl = new LinkedHashMap<>();
    private LinearLayout fm_juji;
    private ViewGroup fl_ad_banner;
    private RecyclerView rv_juji;
    private List<SeriesData> juJiList;
    private HomeSeriesAdapter adapter;
    //private ObjectAnimator icon_anim;
    private AppBarLayout app_bar;
    private String title;
    private String hisImg;
    private String cover;
    private String type;
    private String year;
    private String area;
    private String actor;
    private String director;
    private ImageView ll_info_bg, ll_bg_juji;
    private Bitmap bg;
    LinearLayout ll_tv_info_banner_ad;
    ImageView iv_ad_image;
    TextView tv_ad_text;
    TheGlowingLoader loading, info_loading;
    private boolean isParse = false;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_info);
        loading = findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        handler = new MyHandler();
        juJiList = new ArrayList<>();
        url = getIntent().getStringExtra("url");
        initView();
        initAd();
        initData();
    }


    private void initAd() {

    }

    private void initData() {

        new Thread(() -> {
            try {
                //设置背景模糊图片
                if (!StringUtil.isBlank(hisImg)) {
                    bg = FastBlurUtil.GetUrlBitmap(hisImg, 10);
                }
                Document jsoupParse = JsoupUtil.jsoupParse(url);
                Element contain = jsoupParse.getElementsByClass("wrap").get(0);
                Element seriesInfo = contain.getElementsByClass("box").get(0).getElementsByClass("vod_l").get(0);
                type = seriesInfo.getElementsByTag("p").get(2).text();
                year = seriesInfo.getElementsByTag("p").get(4).text();
                area = seriesInfo.getElementsByTag("p").get(3).text();
                actor = seriesInfo.getElementsByTag("p").get(1).text();
                director = seriesInfo.getElementsByTag("p").get(2).text();
                info = jsoupParse.getElementsByClass("vod_content").get(0).text();
                Elements juji = jsoupParse.getElementsByClass("playlist wbox").get(0).getElementsByTag("a");
                for (Element e : juji) {
                    String num = e.text();
                    String url = e.attr("href");
                    playUrl.put(num, url);
                    SeriesData juJi = new SeriesData();
                    if (!num.equals("")) {
                        juJi.setName(num);
                        juJi.setUrl(Constant.HOST + url);
                        juJiList.add(juJi);
                    }
                }

                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ll_tv_info_banner_ad = findViewById(R.id.ll_tv_info_banner_ad);
        ll_info_bg = (ImageView) findViewById(R.id.ll_info_bg);
        hisImg = getIntent().getStringExtra("img");
        title = getIntent().getStringExtra("title");
        iv_tv_info_cover = (ImageView) findViewById(R.id.iv_tv_info_cover);
        tv_home_tv_info = findViewById(R.id.tv_home_tv_info);
        tv_tv_title = (TextView) findViewById(R.id.tv_tv_title);
        tv_year = (TextView) findViewById(R.id.tv_year);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_director = (TextView) findViewById(R.id.tv_director);
        tv_actor = (TextView) findViewById(R.id.tv_actor);
        fm_juji = (LinearLayout) findViewById(R.id.fm_juji);
        ll_bg_juji = (ImageView) findViewById(R.id.ll_bg_juji);
        info_loading = findViewById(R.id.info_loading);
        fm_juji.setVisibility(View.GONE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        rv_juji = (RecyclerView) findViewById(R.id.rv_juji);
        tv_zk = (TextView) findViewById(R.id.tv_zk);
        tv_tv_title.setText(title);
        tv_zk.setOnClickListener(view -> {
            if (tv_home_tv_info.getVisibility() == View.GONE) {
                tv_zk.setText("关闭");
                tv_home_tv_info.setVisibility(View.VISIBLE);
            } else {
                tv_zk.setText("展开");
                tv_home_tv_info.setVisibility(View.GONE);
            }
        });
        GlideImgManager.normalImageLoader(TvInfoActivity.this, hisImg, iv_tv_info_cover);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setTitle(title);
        fab.setOnClickListener(view -> app_bar.setExpanded(false));
    }
    class MyHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ll_info_bg.setImageBitmap(bg);
                    ll_bg_juji.setImageBitmap(bg);
                    //info = StringUtils.substringBefore(info,"收起");
                    if (!StringUtil.isBlank(info)) {
                        info = info.substring(0, info.indexOf("通过"));
                        tv_home_tv_info.setText(info);
                    }
                    tv_actor.setText(actor);
                    tv_area.setText(area);
                    tv_director.setText(director);
                    tv_actor.setText(actor);
                    tv_year.setText(year);
                    adapter = new HomeSeriesAdapter(juJiList, TvInfoActivity.this);
                    rv_juji.setLayoutManager(new GridLayoutManager(TvInfoActivity.this, 4));
                    rv_juji.setAdapter(adapter);
                    adapter.setOnItemClickListener((adapter, view, position) -> {
                        if (!isParse)
                            parsePlayUrl(juJiList.get(position).getUrl(), position);
                    });
                    info_loading.setVisibility(View.GONE);
                    break;
                case 1:
                    ToastUtil.showShortToast(TvInfoActivity.this, "出现错误，请联系开发者！");
                    break;
                case 4:

                    break;

            }
        }
    }

    private void parsePlayUrl(String url, int position) {
        loading.setVisibility(View.VISIBLE);
        isParse = true;
        new Thread(() -> {
            System.out.println("播放链接：" + url);
            Document document = JsoupUtil.jsoupParse(url);
            String text = document.getElementsByClass("player").get(0).getElementsByTag("script").get(0).toString();
            String replace = text.replace("<script language=\"javascript\">var ff_urls='", "").replace("';</script>", "").replace("\\", "");
            PlaySourceData playSourceData = Constant.gson.fromJson("{" + convert(replace), PlaySourceData.class);
            List<List<String>> playurls = null;
            for (PlaySourceData.Source source : playSourceData.getData()) {
                if ("zuidam3u8".equals(source.getPlayname())) {
                    playurls = source.getPlayurls();
                    break;
                }
            }
            if (playurls == null) {
                playurls = playSourceData.getData().get(0).getPlayurls();
            }

            String startUrl = playurls.get(position).get(1);
            Log.d("startUrl", "当前播放视频链接： " + startUrl);
            runOnUiThread(() -> {
                loading.setVisibility(View.GONE);
                isParse = false;
                Intent intent =
                        new Intent(TvInfoActivity.this, PlayActivity.class);
                intent.putExtra("url", startUrl);
                intent.putExtra("hisImg", hisImg);
                intent.putExtra("hisUrl", url);
                intent.putExtra("hisTitle", title);
                intent.putExtra("hisSeries", juJiList.get(position).getName());
                intent.putExtra("info", info);
                intent.putExtra("title", title);
                SerializableMap serializableMap = new SerializableMap();
                serializableMap.setMap(playUrl);
                Bundle bundle = new Bundle();
                bundle.putSerializable("playUrls", serializableMap);
                intent.putExtras(bundle);
                //View viewByPosition = adapter.getViewByPosition(rv_juji, position,R.id.tv_juji_button);
                //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(TvInfoActivity.this, position, "shareNames").toBundle());
                startActivity(intent);
            });

        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public static String convert(String utfString) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        int iint = 0;
        while ((i = utfString.indexOf("\\u", pos)) != -1) {
            String sd = utfString.substring(pos, i);
            sb.append(sd);
            iint = i + 5;

            if (iint < utfString.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
            }
        }
        String endStr = utfString.substring(iint + 1, utfString.length());
        return sb + "" + endStr;
    }
}
