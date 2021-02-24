package vip.ablog.vientiane.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.entity.HistoryData;
import vip.ablog.vientiane.utils.GlideImgManager;
import vip.ablog.vientiane.utils.MobileInfoUtil;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class HistoryAdapter extends BaseQuickAdapter<HistoryData, BaseViewHolder> {


    public HistoryAdapter(List data) {
        super(R.layout.item_history, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryData item) {
        //CardView cardView = helper.getView(R.id.cd_item_home);
        //setViewMargin(context,cardView,5,5,5,5);
        helper.setText(R.id.tv_play_date, item.getPlayDate());
        String data = item.getPlaySeries();
        helper.setText(R.id.tv_play_series, "上次看到：" + data);
        helper.setText(R.id.tv_play_time, "播放时间：" + item.getPlayTime());
        helper.setText(R.id.tv_his_title, item.getTitle());
        helper.setText(R.id.tv_his_info, item.getInfo());
        helper.setText(R.id.tv_his_juji, item.getUpdate());
        GlideImgManager.normalImageLoader(helper.itemView.getContext(), item.getCover(),
                (ImageView) helper.getView(R.id.iv_his_cover));

    }


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

