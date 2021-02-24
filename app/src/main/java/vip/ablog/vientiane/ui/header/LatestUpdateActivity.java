package vip.ablog.vientiane.ui.header;

import android.content.Intent;
import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.varunest.loader.TheGlowingLoader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.adapter.LatestUpdateAdapter;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.LatestUpdateData;
import vip.ablog.vientiane.ui.TvInfoActivity;
import vip.ablog.vientiane.utils.JsoupUtil;

public class LatestUpdateActivity extends AppCompatActivity {

    private List<LatestUpdateData> updateDataList = new ArrayList<>();
    RecyclerView rv_latest_update;
    private TheGlowingLoader loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latest_update);
        rv_latest_update = findViewById(R.id.rv_latest_update);
        loading = findViewById(R.id.loading);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
    }

    private void initData() {
        new Thread(() -> {
            Document document = JsoupUtil.jsoupParse(Constant.LATEST_UPDATE_URL);
            Element box = document.getElementsByClass("box").get(0);
            Elements li = box.getElementsByTag("li");
            for (Element item : li) {
                String date = item.getElementsByTag("b").get(0).text();
                String title = item.getElementsByTag("a").get(0).text();
                String url = item.getElementsByTag("a").get(0).attr("href");
                LatestUpdateData updateData = new LatestUpdateData();
                updateData.setUpdateDate(date);
                updateData.setUpdateTitle(title);
                updateData.setUrl(url);
                updateDataList.add(updateData);
            }
            runOnUiThread(() -> {
                rv_latest_update.setLayoutManager(new LinearLayoutManager(this));
                LatestUpdateAdapter adapter = new LatestUpdateAdapter(updateDataList);
                adapter.setOnItemClickListener((adapter1, view, position) -> {
                    String url = Constant.HOST + updateDataList.get(position).getUrl();
                    Intent intent = new Intent(this, TvInfoActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title", updateDataList.get(position).getUpdateTitle());
                    //intent.putExtra("img",  hotTopDataList.get(position).get());
                    startActivity(intent);
                });
                rv_latest_update.setAdapter(adapter);
                loading.setVisibility(View.GONE);
            });
        }).start();
    }
}