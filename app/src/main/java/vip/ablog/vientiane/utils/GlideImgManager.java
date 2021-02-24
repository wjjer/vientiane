package vip.ablog.vientiane.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by QLY on 2016/6/22.
 */
public class GlideImgManager {

    /**
     * load normal  for img
     *
     * @param url
     * @param erroImg
     * @param emptyImg
     * @param iv
     */
    public static void glideLoader(Context context, String url, int erroImg, int emptyImg, ImageView iv) {
        //原生 API
        //Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).into(iv);
        Glide.with(context).load(url).into(iv);
    }

    public static void normalImageLoader(Context context, String url, ImageView iv) {
        //原生 API
        Glide.with(context).load(url).into(iv);
    }
    /**
     * load normal  for  circle or round img
     *
     * @param url
     * @param erroImg
     * @param emptyImg
     * @param iv
     * @param tag
     */
    public static void glideLoader(Context context, String url, int erroImg, int emptyImg, ImageView iv, int tag) {

        Glide.with(context).load(url).into(iv);
        /*  if (0 == tag) {
            Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).transform(new GlideCircleTransform(context)).into(iv);
        }
        if (1 == tag) {
            Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).transform(new RoundImage(context,20)).into(iv);
        }
        if (2 == tag) {
            Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).transform(new RoundImage(context,5)).into(iv);
        }*/
    }


}