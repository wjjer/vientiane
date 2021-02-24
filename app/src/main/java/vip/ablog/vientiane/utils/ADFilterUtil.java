package vip.ablog.vientiane.utils;

import android.content.Context;
import android.content.res.Resources;

import vip.ablog.vientiane.R;


/**
 * Created by allan on 18-2-22.
 */

public class ADFilterUtil {
    public static boolean hasAd(Context context, String url){
        Resources res= context.getResources();
        String[] adUrls =res.getStringArray(R.array.adBlockUrl);
        for(String adUrl:adUrls){
            if(url.contains(adUrl)){
                return true;
            }
        }
        return false;
    }
}
