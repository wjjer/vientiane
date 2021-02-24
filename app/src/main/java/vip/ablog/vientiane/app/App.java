package vip.ablog.vientiane.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

import cn.bmob.v3.Bmob;
import vip.ablog.vientiane.constant.Constant;

public class App extends Application {
    private static Context mContext;


    @Override
    public void onCreate() {
        super.onCreate();
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        initXWebSdk();
        initAdSdk();
        initBombSdk();
        mContext = getApplicationContext();
    }

    private void initBombSdk() {
        Bmob.initialize(this, Constant.BMOB_SDK_ID);
    }

    private void initAdSdk() {

    }

    private void initXWebSdk() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", "Tencent WebX5 内核加载状态： " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                HashMap map = new HashMap();
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
                QbSdk.initTbsSettings(map);
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    public static Context getContext() {
        return mContext;
    }


}
