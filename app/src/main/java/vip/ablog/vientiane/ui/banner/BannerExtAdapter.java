package vip.ablog.vientiane.ui.banner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.LogUtils;

import java.util.List;

import vip.ablog.vientiane.R;
import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.entity.BannerData;
import vip.ablog.vientiane.ui.TvInfoActivity;
import vip.ablog.vientiane.utils.ToastUtil;

/**
 * 自定义布局，下面是常见的图片样式，更多实现可以看demo，可以自己随意发挥
 */
public class BannerExtAdapter extends BannerAdapter<BannerData, BannerExtHolder> {


    private List dataList;

    public BannerExtAdapter(List<BannerData> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
        this.dataList = mDatas;
    }

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @SuppressLint("ResourceType")
    @Override
    public BannerExtHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setId(9889);
        TextView textView = new TextView(parent.getContext());
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        //textView.setBackground(parent.getContext().getDrawable(R.drawable.translate2_bg));
        textView.setTag("title");
        textView.setTextSize(18f);

        textView.setGravity(Gravity.BOTTOM | Gravity.CENTER);
        RelativeLayout relativeLayout = new RelativeLayout(parent.getContext());
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(imageView);
        relativeLayout.addView(textView);
        return new BannerExtHolder(relativeLayout);
    }

    @Override
    public void onBindView(BannerExtHolder holder, BannerData data, int position, int size) {
        @SuppressLint("ResourceType") ImageView img = holder.bannerView.findViewById(9889);
        TextView title = holder.bannerView.findViewWithTag("title");
        Glide.with(holder.itemView)
                .load(data.getCover())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                .into(img);

        title.setText(data.getTitle());
        img.setOnClickListener(view -> {
            String url = Constant.HOST + data.getUrl();
            Intent intent = new Intent(holder.itemView.getContext(), TvInfoActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", data.getTitle());
            intent.putExtra("img", data.getCover());
            holder.itemView.getContext().startActivity(intent);
        });
    }
}