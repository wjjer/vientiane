package vip.ablog.vientiane.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    //提供一个静态方法，当别的地方需要发起网络请求时，简单的调用这个方法即可
    //请求实例
    //OKHttp请求
    //callback是okhttp自带的回调接口，这里写的是使用GET方式获取服务器数据
    public static void sendOkHttpRequest(final String address, final Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        //enqueue方法内部已经帮助我们开启好了线程，最终的结果会回调到callback中
        client.newCall(request).enqueue(callback);
    }

    //使用POST方式向服务器提交数据并获取返回提示数据
    public static void sendOkHttpResponse(final String address,
                                          final RequestBody requestBody, final Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //JSONObject这里是要提交的数据部分
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //使用DELETE方式向服务器提交数据并获取返回提示数据
    public static void sendOkHttpDelete(final String address
            , final Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //JSONObject这里是要提交的数据部分
        Request request = new Request.Builder()
                .url(address)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    //使用PUT方式向服务器提交数据并获取返回提示数据
    public static void sendOkHttpPUT(final String address,
                                     final RequestBody requestBody, final Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //JSONObject这里是要提交的数据部分
        Request request = new Request.Builder()
                .url(address)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


}