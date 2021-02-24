package vip.ablog.vientiane;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vip.ablog.vientiane.constant.Constant;
import vip.ablog.vientiane.utils.SPUtil;

public class UserCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        TextView textView = findViewById(R.id.tv_active_status);
        CardView download = findViewById(R.id.cd_download);
        CardView cd_join_group = findViewById(R.id.cd_join_group);
        cd_join_group.setOnClickListener(v->{
            String key = "5EpYW8SN4LhIMVmBQXviEhwlKKLCioGA";
            joinQQGroup(key);
        });
        download.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setData(Uri.parse("https://ablog.lanzous.com/b015u3fuf"));//Url 就是你要打开的网址
            intent.setAction(Intent.ACTION_VIEW);
            this.startActivity(intent); //启动浏览器
        });
        boolean b = SPUtil.getInstance().getBoolean(this, Constant.APP_ACTIVE_KEY);
        if (b) {
            textView.setText("已激活");
            textView.setTextColor(Color.GREEN);
        }
    }
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(this, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}