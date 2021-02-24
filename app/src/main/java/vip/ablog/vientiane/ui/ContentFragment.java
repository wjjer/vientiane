package vip.ablog.vientiane.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.varunest.loader.TheGlowingLoader;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.app.App;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.VideoData;
import vip.ablog.vientiane.ext.fragment.LazyFragment;
import vip.ablog.vientiane.ext.textview.AlwaysMarqueeTextView;
import vip.ablog.vientiane.ui.home.adapter.HomeAdapter;
import vip.ablog.vientiane.utils.ACache;
import vip.ablog.vientiane.utils.JsoupUtil;

public class ContentFragment extends LazyFragment {
    private final String TAG = this.getClass().getName();
    private RecyclerView rv_home;
    private HomeAdapter adapter;
    private SwipeRefreshLayout refresh_home;
    private String url;
    private AlwaysMarqueeTextView tv_item_home_title;
    private MyHandler handler;
    private int currentPageNo = 1;


    List<VideoData> videoDataList = new ArrayList<>();
    private List<VideoData> dataFromCache;
    private TheGlowingLoader loading;


    public static ContentFragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString("url", url);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String url = arguments.getString("url");
            url = url.substring(0, url.indexOf('-') + 1);
            this.url = url;
        }
    }


    @Override
    protected void initView(View rootView) {
        rv_home = rootView.findViewById(R.id.rv_home);
        refresh_home = rootView.findViewById(R.id.refresh_home);
        loading = rootView.findViewById(R.id.loading);
        refresh_home.setOnRefreshListener(this::upFetch);
        WeakReference<MyHandler> myHandlerWeakReference = new WeakReference<>(new MyHandler());
        handler = myHandlerWeakReference.get();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_notifications;
    }

    @Override
    public void onFragmentLoad() {
        initData(false);
    }

    public void initData(boolean b) {
        String pageUrl = this.url + currentPageNo + Constant.PAGE_SUFFIX;
        if (b) {
            ACache.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), Constant.PAGE_CACHE_SIZE, Constant.PAGE_CACHE_COUNT).remove(pageUrl);
        }
        new Thread(() -> {
            Log.d(TAG, "=============开始获取数据=========");
            List<VideoData> dataFromCache = null;
            if (currentPageNo == Constant.FIRST_PAGE) {
                dataFromCache = getDataFromCache(pageUrl);
                if (dataFromCache.size() > 0) {
                    videoDataList = dataFromCache;
                    Log.d(TAG, "存在缓存，优先读取缓存");
                    handler.sendEmptyMessage(0);
                    return;
                }
            }
            Document document = JsoupUtil.jsoupParse(pageUrl);
            Elements items = document.getElementsByClass("vodlist_l box").get(0).getElementsByTag("ul");
            for (Element item : items) {
                String title = item.getElementsByTag("a").get(0).attr("title");
                String cover = item.getElementsByTag("img").get(0).attr("data-original");
                String actor = item.getElementsByClass("space").get(0).text();
                String update = item.getElementsByTag("p").get(1).text();
                String intoUrl = item.getElementsByTag("a").get(0).attr("href");
                String info = item.getElementsByClass("content").get(0).text();
                VideoData videoData = new VideoData();
                videoData.setCover(cover);
                videoData.setTitle(title);
                videoData.setInfo(info);
                videoData.setActor(actor);
                videoData.setUpdate(update);
                videoData.setUrl(Constant.HOST + intoUrl);
                videoDataList.add(videoData);

            }

            if (currentPageNo == 1) {
                if (dataFromCache == null || dataFromCache.size() == 0) {
                    Log.d(TAG, "不存在缓存，开始存储缓存");
                    saveDataToCache(pageUrl, videoDataList);
                }
                handler.sendEmptyMessage(0);
            } else {
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    /*将数据储存到缓存中*/
    private void saveDataToCache(String pageUrl, List<VideoData> videoDataList) {
        //Environment.getDataDirectory().getAbsoluteFile(), Constant.PAGE_CACHE_SIZE, Constant.PAGE_CACHE_COUNT
        String jsonData = JSON.toJSONString(videoDataList);
        ACache cache = ACache.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), Constant.PAGE_CACHE_SIZE, Constant.PAGE_CACHE_COUNT);
        cache.put(pageUrl, jsonData, Constant.CACHE_TIME);
    }

    /*从缓存中获取数据*/
    private List<VideoData> getDataFromCache(String pageUrl) {
        ACache cache = ACache.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), Constant.PAGE_CACHE_SIZE, Constant.PAGE_CACHE_COUNT);
        String jsonData = cache.getAsString(pageUrl);
        TypeReference<List<VideoData>> typeReference = new TypeReference<List<VideoData>>() {
        };
        List<VideoData> videoData = JSON.parseObject(jsonData, typeReference);
        if (videoData == null) {
            return new ArrayList<>();
        }
        return videoData;
    }


    public synchronized void loadMore() {
        currentPageNo++;
        initData(false);
    }

    private synchronized void upFetch() {
        this.currentPageNo = Constant.FIRST_PAGE;
        videoDataList.clear();
        System.out.println("=====下拉刷新====");
        initData(true);
    }


    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    loading.setVisibility(View.GONE);
                    if (refresh_home.isRefreshing()) {
                        refresh_home.setRefreshing(false);
                    }
                    adapter = new HomeAdapter(videoDataList, getActivity());
                    rv_home.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                    rv_home.setAdapter(adapter);
                    adapter.getLoadMoreModule();
                    adapter.getLoadMoreModule().setOnLoadMoreListener(() -> loadMore());
                    adapter.setOnItemClickListener((adapter, view, position) -> {
                        Intent intent = new Intent(getActivity(), TvInfoActivity.class);
                        intent.putExtra("url", videoDataList.get(position).getUrl());
                        intent.putExtra("title", videoDataList.get(position).getTitle());
                        intent.putExtra("img", videoDataList.get(position).getCover());
                        View v = adapter.getViewByPosition(position, R.id.iv_item_home_img);
                        startActivity(intent,
                                ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                        v, "shareNames").toBundle());

                    });
                    break;
                case 1:
                    adapter.setNewInstance(videoDataList);
                    adapter.notifyDataSetChanged();
                    adapter.getLoadMoreModule().loadMoreComplete();
                    break;
                default:
                    break;
            }
        }
    }

}