package vip.ablog.vientiane.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.youth.banner.Banner;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;


import javax.xml.transform.Transformer;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.entity.VideoData;
import vip.ablog.vientiane.utils.GlideImgManager;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class SearchResultAdapter extends BaseQuickAdapter<VideoData, BaseViewHolder> implements LoadMoreModule {
    Context context;

    public SearchResultAdapter(List data, Context context) {
        super(R.layout.item_search_result, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoData item) {
        helper.setText(R.id.tv_item_hot_title, item.getTitle());
        helper.setText(R.id.tv_item_diqu, item.getArea());
        helper.setText(R.id.tv_item_lx, item.getType());
        helper.setText(R.id.tv_item_act, item.getActor());
        helper.setText(R.id.tv_item_direct, item.getInfo());
        GlideImgManager.normalImageLoader(helper.itemView.getContext(), item.getCover(),
                helper.getView(R.id.iv_hot_img));

    }


}

