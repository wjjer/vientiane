package vip.ablog.vientiane.ui.home.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.module.UpFetchModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;


import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.entity.VideoData;
import vip.ablog.vientiane.utils.GlideImgManager;
import vip.ablog.vientiane.utils.MobileInfoUtil;


/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class HomeAdapter extends BaseQuickAdapter<VideoData, BaseViewHolder> implements LoadMoreModule, UpFetchModule {
    Context context;

    public HomeAdapter(List<VideoData> data, Context context) {
        super(R.layout.item_home, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoData item) {
        helper.setText(R.id.tv_item_home_title, item.getTitle().replace("在线观看",""));
        helper.setText(R.id.tv_item_home_director, item.getActor());
        helper.setText(R.id.tv_item_home_update, item.getUpdate());
        GlideImgManager.normalImageLoader(context, item.getCover(),
                helper.getView(R.id.iv_item_home_img));
    }


    //helper.setImageUrl(R.id.iv, item.getContent());

//            case FilmBean.TYPE:
//                helper.setText(R.id.tv_item_home_type,item.getType());
//                break;


    /**
     * 设置某个View的margin
     *
     * @param view   需要设置的view
     * @param left   左边距
     * @param right  右边距
     * @param top    上边距
     * @param bottom 下边距
     * @return
     * @para 需要设置的数值是否为DP
     */
    public static ViewGroup.LayoutParams setViewMargin(Context context, View view, int left, int right, int top, int bottom) {
        if (view == null) {
            return null;
        }

        int leftPx = left;
        int rightPx = right;
        int topPx = top;
        int bottomPx = bottom;
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams marginParams = null;
        //获取view的margin设置参数
        if (params instanceof ViewGroup.MarginLayoutParams) {
            marginParams = (ViewGroup.MarginLayoutParams) params;
        } else {
            //不存在时创建一个新的参数
            marginParams = new ViewGroup.MarginLayoutParams(params);
        }
        params.height = MobileInfoUtil.getAndroiodScreenProperty(context) / 3;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setTranslationZ(3.0f);
        view.setLayoutParams(params);
        //根据DP与PX转换计算值
//        if (isDp) {
//            leftPx = getPxFromDpi(left);
//            rightPx = getPxFromDpi(right);
//            topPx = getPxFromDpi(top);
//            bottomPx = getPxFromDpi(bottom);
//        }
        //设置margin
        marginParams.setMargins(leftPx, topPx, rightPx, bottomPx);
        view.setLayoutParams(marginParams);
        return marginParams;
    }

}

