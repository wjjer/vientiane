package vip.ablog.vientiane.web;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.ArrayList;
import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.constant.Constant;


public class VipPagePlayActivity extends AppCompatActivity implements View.OnClickListener {
    WebView mwebview;
    String url = "";
    private String title = "";
    private String TAG = "PlayActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_play_page);
        initView();
        mwebview = (WebView) findViewById(R.id.wb_vip_page);
        // 对于刘海屏机器如果webview被遮挡会自动padding
        mwebview.getSettingsExtension().setDisplayCutoutEnable(true);

        WebSettings webSettings = mwebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setPluginsEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        mwebview.loadUrl(url);
    }


    private void initView() {
  /*      getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                int size = outView.size();
                if (outView != null && outView.size() > 0) {
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });*/
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
    }


    @Override
    public void onClick(View view) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除cookie
        QbSdk.clearAllWebViewCache(this, false);
        Log.e(TAG, "数据已清理！！！！");
    }
}