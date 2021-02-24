package vip.ablog.vientiane.constant;

import android.os.Environment;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import vip.ablog.vientiane.utils.ACache;

/**
 * Created by allan on 17-9-24.
 */

public class Constant {

    /*Bmob Configuration*/
    public static final String BMOB_SDK_ID = "XXXX";

    public static final String SHOW_SOURCE_JS_NAME = "java_show";
    public static final String HOST = "https://www.101yingyuan.net";
    public static final String LATEST_UPDATE_URL = HOST + "/top/new.html";
    public static final String RESOURCE_TYPR = "RESOURCE_TYPR";
    public static final String TOOLBAR_TITLE = "TOOLBAR_TITLE";
    public static final String CACHE_TAB_ITEMS = "CACHE_TAB_ITEMS";
    public static final String APP_ACTIVE_KEY = "APP_ACTIVE_KEY";
    public static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A403 Safari/8536.25";
    public static final int FIRST_PAGE = 1;
    public static final int PAGE_CACHE_COUNT = 1024;
    public static final long PAGE_CACHE_SIZE = 10240;
    public static final Object PAGE_DATA_CACHE = "Cache";
    public static final String PAGE_SUFFIX = ".html";
    public static final int CACHE_TIME = 3600 * 72;
    public static final String SEARCH_HIS = "SEARCH_HIS";
    public static final String PLAY_VIDEO_AD = "PLAY_VIDEO_AD";
    public static final String APP_PERMISSION = "APP_PERMISSION";
    public static String APP_CODE = "";
    public static Map<String, String> tabTVMap = new HashMap<String, String>();
    public static Map<String, String> tabMVMap = new HashMap<String, String>(10);
    public static Map<String, String> tabDVMap = new HashMap<String, String>(10);
    public static Map<String, String> tabZVMap = new HashMap<String, String>(10);
    public static Gson gson = new Gson();
    public static ACache APP_CACHE =  ACache.get(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), Constant.PAGE_CACHE_SIZE, Constant.PAGE_CACHE_COUNT);

}
