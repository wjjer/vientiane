package vip.ablog.vientiane;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.varunest.loader.TheGlowingLoader;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.util.V;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import vip.ablog.vientiane.adapter.HotTopAdapter;
import vip.ablog.vientiane.adapter.SearchResultAdapter;
import vip.ablog.vientiane.adapter.SimpleLineAdapter;
import vip.ablog.vientiane.adapter.StringDataAdapter;
import vip.ablog.vientiane.app.App;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.HotTopData;
import vip.ablog.vientiane.entity.SearchThinkData;
import vip.ablog.vientiane.entity.VideoData;
import vip.ablog.vientiane.ext.dialog.PopupDialog;
import vip.ablog.vientiane.ui.TvInfoActivity;
import vip.ablog.vientiane.utils.HttpUtil;
import vip.ablog.vientiane.utils.JsoupUtil;
import vip.ablog.vientiane.utils.SPUtil;
import vip.ablog.vientiane.utils.ToastUtil;

public class SearchActivity extends AppCompatActivity  {

    private SearchView sv_input;
    private String movUrl = "";
    private Map<String, String> tvMap;
    private String kw = "";
    private int page = 1;
    long lastSearchTimes = 0;
    private FrameLayout fm_no_resource;
    private RecyclerView rv_search_result, rv_search_hot, rv_search_his;
    private Handler handler;
    private SearchResultAdapter searchResultAdapter;
    List<VideoData> videoDataList = new ArrayList<>();
    List<SearchThinkData.ThinkTitle> thinkTitleList = new ArrayList<>();
    private TheGlowingLoader progressBar;
    private SimpleLineAdapter simpleLineAdapter;
    private List<HotTopData> hotTopDataList = new ArrayList<>();
    private LinearLayout ll_search_banner_ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initAd();
        initPageData();
    }
    private void initAd() {

    }
    private void initPageData() {
        new Thread(() -> {
            Document document = JsoupUtil.jsoupParse(Constant.HOST);
            Element box = document.getElementsByClass("index_right").get(0);
            Elements li = box.getElementsByClass("top" + (1)).get(0).getElementsByTag("li");
            for (Element item : li) {
                String title = item.getElementsByTag("a").get(0).text();
                String url = item.getElementsByTag("a").get(0).attr("href");
                String num = item.getElementsByTag("em").get(0).text();
                HotTopData hotTopData = new HotTopData();
                hotTopData.setTitle(title);
                hotTopData.setNumber(num);
                hotTopData.setUrl(url);
                hotTopDataList.add(hotTopData);
            }
            //His
            String searchHis = SPUtil.getInstance().getString(this, Constant.SEARCH_HIS);

            runOnUiThread(() -> {
                if (!StringUtil.isBlank(searchHis)) {
                    String[] hisArr = searchHis.split(",");
                    List<String> hisList = Arrays.asList(hisArr);
                    rv_search_his.setLayoutManager(new GridLayoutManager(this, 3));
                    StringDataAdapter hisAdapter = new StringDataAdapter(R.layout.item_search_his, hisList);
                    rv_search_his.setAdapter(hisAdapter);
                    hisAdapter.setOnItemClickListener((adapter, view, position) -> {
                        sv_input.setQuery(hisList.get(position), true);
                    });
                    hisAdapter.setOnItemLongClickListener((adapter, view, position) -> {
                        String[] objects = (String[]) hisList.toArray();
                        String saveData = "";
                        List<String> newHisData = new ArrayList<>();
                        for (String item : objects) {
                            if (hisList.get(position).equals(item)) {
                                continue;
                            }
                            saveData += item + ",";
                            newHisData.add(item);
                        }
                        saveData = saveData.substring(0, saveData.lastIndexOf(','));
                        SPUtil.getInstance().putString(this, Constant.SEARCH_HIS, saveData);
                        hisAdapter.setNewInstance(newHisData);
                        hisAdapter.notifyDataSetChanged();
                        return false;
                    });
                }

                rv_search_hot.setLayoutManager(new GridLayoutManager(this, 2));
                HotTopAdapter hotTopAdapter = new HotTopAdapter(R.layout.item_search_top, hotTopDataList);
                rv_search_hot.setAdapter(hotTopAdapter);
                hotTopAdapter.setOnItemClickListener((adapter1, view, position) -> {
                    String url = Constant.HOST + hotTopDataList.get(position).getUrl();
                    Intent intent = new Intent(this, TvInfoActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", hotTopDataList.get(position).getTitle());
                    startActivity(intent);
                });
            });
        }).start();

    }


    private void initView() {
        rv_search_result = findViewById(R.id.rv_search_result);
        rv_search_his = findViewById(R.id.rv_search_his);
        rv_search_hot = findViewById(R.id.rv_search_hot);
        progressBar = findViewById(R.id.loading);
        progressBar.setVisibility(View.GONE);
        ll_search_banner_ad = findViewById(R.id.ll_search_banner_ad);
        fm_no_resource = findViewById(R.id.fm_no_resource);
        handler = new MyHandler();
        tvMap = new HashMap<>();
        sv_input = (SearchView) findViewById(R.id.sv_input);
        sv_input.setIconifiedByDefault(true);//设置展开后图标的样式,这里只有两种,一种图标在搜索框外,一种在搜索框内
        sv_input.onActionViewExpanded();// 写上此句后searchView初始是可以点击输入的状态，如果不写，那么就需要点击下放大镜，才能出现输入框,也就是设置为ToolBar的ActionView，默认展开
        sv_input.requestFocus();//输入焦点
//        sv_input.setSubmitButtonEnabled(true);//添加提交按钮，监听在OnQueryTextListener的onQueryTextSubmit响应
        sv_input.setFocusable(true);//将控件设置成可获取焦点状态,默认是无法获取焦点的,只有设置成true,才能获取控件的点击事件
        sv_input.setIconified(false);//输入框内icon不显示
        sv_input.requestFocusFromTouch();//模拟焦点点击事件
        sv_input.setFocusable(true);//禁止弹出输入法，在某些情况下有需要
        sv_input.findFocus();//弹出输入法，在某些情况下有需要
        sv_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //提交按钮的点击事件,防止恶意刷新,做判断
                boolean active = SPUtil.getInstance().getBoolean(App.getContext(), Constant.APP_ACTIVE_KEY);
                if (!active) {
                    showEditDialog();
                    return false;
                }
                int l = (int) ((System.currentTimeMillis() - lastSearchTimes) / 1000L);
                if (l < 4) {
                    ToastUtil.showLongToast(SearchActivity.this, "请" + (6 - l) + "秒后再搜索");
                    return false;
                }
                kw = query;
                page = 1;
                progressBar.setVisibility(View.VISIBLE);
                videoDataList.clear();
                initData();
                saveSearchHis(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                int length = newText.trim().length();
                if ( length < 2) {
                    if (length==0){
                        fm_no_resource.setVisibility(View.VISIBLE);
                    }
                    return false;
                } else {
                    startThinkUrl(newText);
                    return false;
                }
            }
        });
    }

    private void saveSearchHis(String query) {
        String saveData = "";
        String searchHis = SPUtil.getInstance().getString(this, Constant.SEARCH_HIS);
        if (StringUtil.isBlank(searchHis)) {
            saveData = query;
        } else {
            if (searchHis.contains(query))return;
            saveData = searchHis + "," + query;
        }
        SPUtil.getInstance().putString(this, Constant.SEARCH_HIS, saveData);
    }

    private void startThinkUrl(String word) {
        String url = Constant.HOST + "/index.php?s=plus-search-vod&q=" + word + "&limit=10&timestamp=" + System.currentTimeMillis();
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    SearchThinkData searchThinkData = Constant.gson.fromJson(body.string(), SearchThinkData.class);
                    if (searchThinkData.getStatus() == 1) {
                        thinkTitleList = searchThinkData.getData();
                        handler.sendEmptyMessage(3);
                    }
                }

            }
        });
    }


    private void showEditDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View inflate = layoutInflater.inflate(R.layout.popup_dialog_input, null);
        EditText et = inflate.findViewById(R.id.dialog_input_et);
        TextView tv = inflate.findViewById(R.id.dialog_message_tv);
        tv.setTextColor(this.getResources().getColor(R.color.md_blue_grey_700_color_code));
        tv.setTextSize(15F);
        tv.setText(getResources().getText(R.string.app_active));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入激活码").setView(inflate)
                .setNegativeButton("复制公众号", (dialog, which) -> {
                    ClipboardManager clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText("AB小站"); // 复制
                    ToastUtil.showLongToast("公众号已复制，请前去粘贴搜索");
                });
        builder.setPositiveButton("开始激活", (dialog, which) -> {
            String code = et.getText().toString().trim();
            String appCode = Constant.APP_CODE.substring(2, Constant.APP_CODE.lastIndexOf(getClass().getSimpleName().substring(0, 1))).toUpperCase();
            if (appCode.equals(code)) {
                ToastUtil.showLongToast("激活成功！");
                SPUtil.getInstance().putBoolean(this, Constant.APP_ACTIVE_KEY, true);
                dialog.dismiss();
            } else {
                PopupDialog.create(this,
                        "提示", getResources().getString(R.string.active_error),
                        "确定",
                        null,
                        null,
                        null,
                        true,
                        true, false).show();
            }

        });
        builder.setCancelable(false);

        builder.show();
    }

    private void initData() {

        lastSearchTimes = System.currentTimeMillis();
        new Thread(() -> {
            try {
                Document document = JsoupUtil.jsoupParse("https://www.101yingyuan.net/search/" + kw + "-" + page + ".html");
                Elements items = document.getElementsByClass("vodlist_l box").get(0).getElementsByTag("ul");
                for (Element item : items) {
                    String title = item.getElementsByTag("a").get(0).attr("title");
                    String cover = item.getElementsByTag("img").get(0).attr("data-original");
                    String intoUrl = item.getElementsByTag("a").get(0).attr("href");
                    String actor = item.getElementsByTag("p").get(0).text();
                    String area = item.getElementsByTag("p").get(1).text();
                    String info = item.getElementsByTag("p").get(2).text();
                    VideoData videoData = new VideoData();
                    videoData.setCover(cover);
                    videoData.setTitle(title);
                    videoData.setActor(actor);
                    videoData.setArea(area);
                    videoData.setInfo(info);
                    videoData.setUrl(Constant.HOST + intoUrl);
                    videoDataList.add(videoData);

                }
                if (page == 1) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(2);
            }
        }).start();
    }

    private void loadMore() {
        page++;
        initData();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (videoDataList == null || videoDataList.size() == 0) {
                        fm_no_resource.setVisibility(View.VISIBLE);
                    } else {
                        fm_no_resource.setVisibility(View.GONE);
                    }
                    thinkTitleList.clear();
                    progressBar.setVisibility(View.GONE);
                    searchResultAdapter = new SearchResultAdapter(videoDataList, App.getContext());
                    searchResultAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> loadMore());
                    rv_search_result.setAdapter(searchResultAdapter);
                    rv_search_result.setLayoutManager(new GridLayoutManager(SearchActivity.this, 2));
                    searchResultAdapter.setOnItemClickListener((adapter, view, position) -> {
                        Intent intent;
                        intent = new Intent(SearchActivity.this, TvInfoActivity.class);
                        intent.putExtra("url", videoDataList.get(position).getUrl().trim());
                        intent.putExtra("title", videoDataList.get(position).getTitle());
                        intent.putExtra("img", videoDataList.get(position).getCover());
                        startActivity(intent);
                    });

                    break;

                case 1:
                    progressBar.setVisibility(View.GONE);
                    searchResultAdapter.setNewInstance(videoDataList);
                    searchResultAdapter.notifyDataSetChanged();
                    searchResultAdapter.getLoadMoreModule().loadMoreComplete();
                    break;
                case 2:
                    progressBar.setVisibility(View.GONE);
                    ToastUtil.showShortToast(App.getContext(), "没有更多数据");
                    if (searchResultAdapter != null) {
                        searchResultAdapter.getLoadMoreModule().loadMoreEnd();
                    }
                    break;
                case 3:
                    fm_no_resource.setVisibility(View.GONE);
                    simpleLineAdapter = new SimpleLineAdapter(thinkTitleList);
                    rv_search_result.setLayoutManager(new LinearLayoutManager(App.getContext()));
                    rv_search_result.setAdapter(simpleLineAdapter);
                    rv_search_result.addItemDecoration(new DividerItemDecoration(SearchActivity.this, DividerItemDecoration.VERTICAL));
                    simpleLineAdapter.setOnItemClickListener((adapter1, view, position) -> {
                        sv_input.setQuery(thinkTitleList.get(position).getVod_name(), true);
                    });
                    break;
                default:
                    break;
            }
        }

    }

}
