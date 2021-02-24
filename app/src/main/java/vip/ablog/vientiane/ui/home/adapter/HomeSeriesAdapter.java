package vip.ablog.vientiane.ui.home.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;


import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.entity.SeriesData;

/**
 * Created by Administrator on 2017/8/26 0026.
 */

public class HomeSeriesAdapter extends BaseQuickAdapter<SeriesData, BaseViewHolder> {
    Context context;

    public HomeSeriesAdapter(List data, Context context) {
        super(R.layout.item_series,data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, SeriesData item) {
                helper.setText(R.id.tv_item_juji_title,item.getName());

        }

}

