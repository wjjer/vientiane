package vip.ablog.vientiane.web;

import android.content.Context;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.utils.ADFilterUtil;


/**
 * Created by allan on 18-2-22.
 */

public class NoAdWebViewClient extends WebViewClient {
    private String baseurl;
    private Context context;

    private static final String TAG = "MWebViewClient";
    private static final String GET_FRAME_CONTENT_STR =
            "document.getElementsByTagName('iframe')[0].getAttribute('src')";

    public NoAdWebViewClient(Context context, String baseurl){
        this.context= context;
        this.baseurl= baseurl;
    }
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url){
        url= url.toLowerCase();
        if(url!=null && !url.contains(baseurl)){
            if(!ADFilterUtil.hasAd(context,url)){
                return super.shouldInterceptRequest(view,url);
            }else{
                return new WebResourceResponse(null,null,null);
            }
        }else{
            return super.shouldInterceptRequest(view,url);
        }


    }
    @Override
    public void onPageFinished(final WebView view, String url) {
        view.loadUrl("javascript:window."
                + Constant.SHOW_SOURCE_JS_NAME
                + ".showSource("
                + GET_FRAME_CONTENT_STR
                + ");");

        super.onPageFinished(view, url);
    }


    //如果只是为了获取网页源代码的话，可以重写onPageFinished方法，在onPageFinished方法里执行相应的逻辑就好。但是当框架里显示的内容发生变化时，onPageFinished方法不会再掉用，只会调用onLoadResource方法，所以此处需要重写此方法。
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }




}
