package vip.ablog.vientiane.ui.banner;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BannerExtHolder extends RecyclerView.ViewHolder {
    public RelativeLayout bannerView;

    public BannerExtHolder(@NonNull View view) {
        super(view);
        this.bannerView = (RelativeLayout) view;

    }
}