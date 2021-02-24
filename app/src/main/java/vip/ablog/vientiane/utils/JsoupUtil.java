package vip.ablog.vientiane.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class JsoupUtil {

    private static String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13";


    public static Document jsoupParse(String url) {
        Document document = null;
        try {
            document = Jsoup.connect(url).timeout(15000).userAgent(userAgent).get();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (document != null) {
            return document;
        } else {
            new Exception("Jsoup�쳣");
        }
        return document;

    }

    public static Document httpPost(String url, Map<String, String> map, String cookie) throws IOException {
        //获取请求连接
        Connection con = Jsoup.connect(url);
        //遍历生成参数
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //添加参数
                con.data(entry.getKey(), entry.getValue());
            }
        }
        //插入cookie（头文件形式）
        con.header("Cookie", cookie);
        Document doc = con.post();
        return doc;
    }

}
