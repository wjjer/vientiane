package vip.ablog.vientiane.ui.header;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.varunest.loader.TheGlowingLoader;
import com.youth.banner.util.LogUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.adapter.HotTopAdapter;
import vip.ablog.vientiane.adapter.LatestUpdateAdapter;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.HotTopData;
import vip.ablog.vientiane.entity.LatestUpdateData;
import vip.ablog.vientiane.ui.TvInfoActivity;
import vip.ablog.vientiane.utils.JsoupUtil;

public class HotTopActivity extends AppCompatActivity {

    private List<HotTopData> hotTopDataList = new ArrayList<>();
    RecyclerView rv_hot_top;
    private TheGlowingLoader loading;
    private int type = 0;
    private String barTitle = "热门剧排行榜";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_top);
        rv_hot_top = findViewById(R.id.rv_hot_top);
        loading = findViewById(R.id.loading);
        Toolbar toolbar = findViewById(R.id.toolbar);
        type = getIntent().getIntExtra(Constant.RESOURCE_TYPR, 0);
        barTitle = getIntent().getStringExtra(Constant.TOOLBAR_TITLE);
        toolbar.setTitle(barTitle);
        setSupportActionBar(toolbar);
        System.out.println("barTitle === " + barTitle);
        initData();
    }

    private synchronized void initData() {
        new Thread(() -> {
            Document document = JsoupUtil.jsoupParse(Constant.HOST);
            Element box = document.getElementsByClass("index_right").get(0);
            Elements li = box.getElementsByClass("top" + (type + 1)).get(0).getElementsByTag("li");
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
            runOnUiThread(() -> {
                rv_hot_top.setLayoutManager(new LinearLayoutManager(this));
                HotTopAdapter adapter = new HotTopAdapter(R.layout.item_hot_top,hotTopDataList);
                rv_hot_top.setAdapter(adapter);
                adapter.setOnItemClickListener((adapter1, view, position) -> {

                    String url = Constant.HOST + hotTopDataList.get(position).getUrl();
                    Intent intent = new Intent(this, TvInfoActivity.class);
                    Log.d("InfoPage","详情页："+url);
                    intent.putExtra("url", url);
                    intent.putExtra("title", hotTopDataList.get(position).getTitle());
                    //intent.putExtra("img",  hotTopDataList.get(position).get());
                    startActivity(intent);

                });
                loading.setVisibility(View.GONE);
            });
        }).start();
    }
}