package vip.ablog.vientiane;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yanzhenjie.permission.AndPermission;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateStatus;
import vip.ablog.vientiane.app.App;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.AppTabData;
import vip.ablog.vientiane.ext.dialog.PopupDialog;
import vip.ablog.vientiane.utils.JsoupUtil;
import vip.ablog.vientiane.utils.MobileInfoUtil;
import vip.ablog.vientiane.utils.SPUtil;
import vip.ablog.vientiane.utils.ToastUtil;

import static java.lang.Thread.sleep;


public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private String TAG = "SplashActivity";
    private String path = "https://ablog.lanzous.com/b015szfid";
    FrameLayout frameLayout;
    private boolean hasPermission = false;
    private List<AppTabData> tabDataList = new ArrayList<>();
    private boolean hasNewVersion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        frameLayout = findViewById(R.id.ad);
        handler = new MyHandler();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        if (!MobileInfoUtil.isNetworkAvailable(SplashActivity.this)) {
            ToastUtil.showLongToast(SplashActivity.this, "需要网络才能打开APP，请先检查您的网络");
            return;
        }
        hasPermission = SPUtil.getInstance().getBoolean(this, Constant.APP_PERMISSION);
        new Thread(() -> {
            if (!hasPermission) {
                runOnUiThread(this::checkPermission);
            }
            try {
                initAppUpdate();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!hasPermission || hasNewVersion) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            initData();
            handler.postDelayed(() -> {
                handler.sendEmptyMessage(1);
            }, 1900);
        }).start();
    }

    private void initAd() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    private void initData() {
        String tabString = SPUtil.getInstance().getString(this, Constant.CACHE_TAB_ITEMS);
        Constant.APP_CODE = App.getContext().getString(R.string.app_key);
        if (StringUtil.isBlank(tabString)) {
            Document document = JsoupUtil.jsoupParse(Constant.HOST);
            Elements items = document.getElementsByClass("nav").get(0).getElementsByTag("a");
            Elements items1 = document.getElementsByClass("query").get(0).getElementsByTag("a");
            items.addAll(items1);
            int index = 0;
            for (Element item : items) {
                index++;
                String url = item.attr("href");
                String title = item.text();
                if (!"/".equals(url)) {
                    AppTabData tabData = new AppTabData();
                    tabData.setTitle(title);
                    tabData.setUrl(url);
                    tabDataList.add(tabData);
                    if (index <= 9) {
                        Constant.tabTVMap.put(title, url);
                    } else if (index == 10) {
                        Constant.tabDVMap.put(title, url);
                    } else if (index <= 19) {
                        Constant.tabMVMap.put(title, url);
                    } else {
                        Constant.tabZVMap.put(title, url);
                    }
                }
            }
            SPUtil.getInstance().putString(this, Constant.CACHE_TAB_ITEMS, JSON.toJSONString(tabDataList));
        } else {
            List<AppTabData> appTabDataList = JSON.parseObject(tabString, new TypeReference<List<AppTabData>>() {
            });
            int index = 1;
            for (AppTabData tab : appTabDataList) {
                index++;
                String title = tab.getTitle();
                String url = tab.getUrl();
                if (index <= 9) {
                    Constant.tabTVMap.put(title, url);
                } else if (index == 10) {
                    Constant.tabDVMap.put(title, url);
                } else if (index <= 19) {
                    Constant.tabMVMap.put(title, url);
                } else {
                    Constant.tabZVMap.put(title, url);
                }
            }

        }
    }

    private void initAppUpdate() {
        Log.d("bmob","开始检查更新APP。。。。。");
        BmobUpdateAgent.setUpdateListener((updateStatus, updateInfo) -> {
            if (updateStatus == UpdateStatus.Yes) {//版本有更新
                path = updateInfo.path;
                Log.d("bmob","发现新版本。。。。。");
                hasNewVersion = true;
            }
        });
        //设置对对话框按钮的点击事件的监听
        BmobUpdateAgent.setDialogListener(status -> {
            switch (status) {
                case UpdateStatus.Update:
                    Uri uri = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                default:break;
            }
        });
        //发起自动更新
        BmobUpdateAgent.update(this);
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void checkPermission() {
        PopupDialog.create(this, "提示", getResources().getString(R.string.check_permission),
                "好的", view -> {
                    getPermission();
                }, "拒绝", view -> {
                    ToastUtil.showLongToast(App.getContext(), "因为需要缓存部分必要数据，没有储存权限，无法生成缓存，请先授予存储权限！");
                    finish();
                }, false, false, false).show();
    }

    private void getPermission() {
        String[] permissionArr = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        AndPermission.with(this)
                .permission(permissionArr)
                // 准备方法，和 okhttp 的拦截器一样，在请求权限之前先运行改方法，已经拥有权限不会触发该方法
                .rationale((context, permissions, executor) -> {
                    // 此处可以选择显示提示弹窗
                    executor.execute();
                })
                // 用户给权限了
                .onGranted(permissions -> {
                    hasPermission = true;
                    SPUtil.getInstance().putBoolean(this, Constant.APP_PERMISSION, true);
                })
                // 用户拒绝权限，包括不再显示权限弹窗也在此列
                .onDenied(permissions -> {
                    // 判断用户是不是不再显示权限弹窗了，若不再显示的话进入权限设置页
                    if (AndPermission.hasAlwaysDeniedPermission(SplashActivity.this, permissions)) {
                        // 打开权限设置页
                        AndPermission.permissionSetting(SplashActivity.this).execute();
                        return;
                    }
                    ToastUtil.showLongToast(App.getContext(), "因为需要缓存部分必要数据，没有储存权限，无法生成缓存，请先授予存储权限！");
                }).start();

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    initAd();
                    break;
                default:
                    break;

            }
        }

    }


}
