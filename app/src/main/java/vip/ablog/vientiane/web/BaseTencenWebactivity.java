package vip.ablog.vientiane.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.utils.ToastUtil;

public abstract class BaseTencenWebactivity extends AppCompatActivity {
    protected RelativeLayout rlToolbar;
    protected Button ibRight2,ibLeft1;
    protected WebView webView;
    protected TextView tvTitle;
    protected ProgressBar progressBar;

    protected static final String HYBRID = "HYBRID";    //js调用android时候，android注入的对象名称

    public static final String FROM_WHERE = "H5";   //跳转来源H5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tencen_web);
        init();
    }


    public void init() {
        rlToolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);
        ibLeft1 = findViewById(R.id.ibLeft);
        ibRight2 =  findViewById(R.id.ibRight);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setBlockNetworkLoads(false);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1.1; zh-cn;) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/6.3 Mobile Safari/537.36");
        onWebViewSettingBuild();
        configPlaySetting();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            injectJavaScript();
        }
        if (isNoWebCache()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("https") || url.startsWith("http") ) {
                    view.loadUrl(url);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    BaseTencenWebactivity.this.shouldOverrideUrlLoading(view, url);
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
                BaseTencenWebactivity.this.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                BaseTencenWebactivity.this.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (enabledHTTPS()) {
                    //当启用https,遇到证书错误的时候，直接调用handler.proceed实际上是存在安全因素的。
                    //实际上可以根据不同的错误类型，来做调整。比如遇到一个不可信的证书。 SslError.SSL_UNTRUSTED
                    //可以像其他浏览器那样，弹出对话框提示，提示用户证书不可信等。
                    handler.proceed();
                } else {
                    super.onReceivedSslError(view, handler, error);
                }

            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            private View myView;
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                BaseTencenWebactivity.this.onProgressChanged(view, progress);
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
                tvTitle.setText(s);
            }

            //自定义视频播放  如果需要启用这个，需要设置x5,自己实现全屏播放。目前的使用的x5的视频播放
            //如果是点击h5 vedio标签的播放，需要自己实现全屏播放
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
                super.onShowCustomView(view, customViewCallback);
                ViewGroup parent = (ViewGroup) webView.getParent();
                parent.removeView(webView);

                // 设置背景色为黑色
                view.setBackgroundColor(BaseTencenWebactivity.this.getResources().getColor(R.color.black));
                parent.addView(view);
                myView = view;
                rlToolbar.setVisibility(View.GONE);
                setFullScreen();
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (myView != null) {
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(webView);
                    myView = null;
                    rlToolbar.setVisibility(View.VISIBLE);
                    quitFullScreen();
                }
            }

        });
        tvTitle.setText(getWebViewTitle());
        String url = getUrl();
        if (!TextUtils.isEmpty(url)) {
            loadUrl(url);
        }

    }

    /**
     * 播放设置    已经开启x5全屏  小窗播放  页内播放等。
     */
    protected void configPlaySetting(){
        Bundle data = new Bundle();
//true表示标准全屏，false表示X5全屏；不设置默认false，
        data.putBoolean("standardFullScreen", false);
//false：关闭小窗；true：开启小窗；不设置默认true，
        data.putBoolean("supportLiteWnd", true);
//1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
        data.putInt("DefaultVideoScreen", 1);
        webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
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
     *设置全屏
     */
    private void setFullScreen(){
        BaseTencenWebactivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = BaseTencenWebactivity.this.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BaseTencenWebactivity.this.getWindow().setAttributes(attrs);
        BaseTencenWebactivity.this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 播放视频  传入视频的url地址
     * @return
     */
    protected boolean playVideoByTbs(String videoUrl){
        if(TbsVideo.canUseTbsPlayer(BaseTencenWebactivity.this)){
            //播放器是否可以使用
            Bundle xtraData = new Bundle();
            xtraData.putInt("screenMode", 102);//全屏设置 和控制栏设置
            TbsVideo.openVideo(BaseTencenWebactivity.this,videoUrl, xtraData);
            return true;
        }else{
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
     * 注入javaScript对象
     */
    @SuppressLint("AddJavascriptInterface")
    private void injectJavaScript() {
        webView.addJavascriptInterface(getJavascriptInterface(), HYBRID);
    }

    /**
     * 返回 注入javaScript对象,必须继承 {@link BaseHYBRID}
     *
     * @return T extend BaseHYBRID
     */
    protected BaseHYBRID getJavascriptInterface() {
        return new BaseHYBRID();
    }

    protected boolean enabledHTTPS() {
        return  true;
    }
    
    

    /**
     * 调用javascript方法
     *
     * @param js
     */
    protected void loadJavaScript(String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //sdk>19才有
            webView.evaluateJavascript(js, responseJson -> {
                //这里获取的参数就是JS函数的返回值
            });
        } else {
            //SDK <= 19
            webView.loadUrl(js);
        }
    }

    protected void onWebViewSettingBuild() {

    }

    protected void loadUrl(String url) {
        webView.loadUrl(url);
    }

    /**
     * 不用缓存,true:无缓存,false:默认
     *
     * @return
     */
    protected boolean isNoWebCache() {
        return false;
    }

    protected String getWebViewTitle() {
        return getIntent().getStringExtra("title");
    }

    public String getUrl() {
        return getIntent().getStringExtra("url");
    }
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
//                back();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void shouldOverrideUrlLoading(WebView view, String url) {
    }

    protected void onPageFinished(WebView view, String url) {
    }

    protected void onPageStarted(WebView view, String url, Bitmap favicon) {
    }

    protected void onProgressChanged(WebView view, int progress) {
    }

    /**
     * 公共的JS行为基类,封装通用的操作给第三方
     */
    public class BaseHYBRID {

        /**
         * 退出当前页面
         */
        @JavascriptInterface
        public void exitPage() {
        }

        /**
         * 设置页面标题
         *
         * @param title
         */
        @JavascriptInterface
        public void setTitle(String title) {
            setTitleOnUiThread(title);
        }

        public void setTitleOnUiThread(final String title) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTitle.setText(title);
                }
            });
        }
    }



    @Override
    protected void onDestroy() {
        webView.stopLoading();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
        super.onDestroy();
    }

}