package vip.ablog.vientiane;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


import vip.ablog.vientiane.adapter.HistoryAdapter;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.HistoryData;
import vip.ablog.vientiane.ui.PlayActivity;
import vip.ablog.vientiane.ui.TvInfoActivity;
import vip.ablog.vientiane.utils.ToastUtil;


public class HistoryActivity extends AppCompatActivity {


    private RecyclerView rv_his;
    private Handler handler;
    private FrameLayout progress, frame_tv;
    private HistoryAdapter adapter;
    private String TAG = "TypeActivity";
    private List<HistoryData> hisBeanList;
    private Toolbar tb_his;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rv_his = (RecyclerView) findViewById(R.id.rv_his);
        progress = (FrameLayout) findViewById(R.id.loading_progress);
        tb_his = (Toolbar) findViewById(R.id.tb_his);
        tb_his.setTitle("历史记录");
        setSupportActionBar(tb_his);
        tb_his.inflateMenu(R.menu.his_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tb_his.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));
        handler = new MyHandler();
        hisBeanList = new ArrayList<>();


        handler.sendEmptyMessage(0);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cl_his:
                ToastUtil.showShortToast(HistoryActivity.this, "所有历史记录已删除!");
                handler.sendEmptyMessage(2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.his_menu, menu);
        return true;

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    progress.setVisibility(View.GONE);

                    adapter = new HistoryAdapter(hisBeanList);
                    rv_his.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                    adapter.setOnItemClickListener((adapter, view, position) -> {
                        Intent intent = null;
                        String url = hisBeanList.get(position).getUrl();
                        intent = new Intent(HistoryActivity.this, TvInfoActivity.class);
                        intent.putExtra("title", hisBeanList.get(position).getTitle());
                        intent.putExtra("hisImg", hisBeanList.get(position).getCover());
                        intent.putExtra("hisUrl", hisBeanList.get(position).getUrl());
                        startActivity(intent);
                    });
                    rv_his.setAdapter(adapter);
                    break;
                case 2:

                    rv_his.setVisibility(View.GONE);
                    break;


            }
        }
    }

}
