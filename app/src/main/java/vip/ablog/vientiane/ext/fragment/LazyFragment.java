package vip.ablog.vientiane.ext.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class LazyFragment extends Fragment {
    private final String TAG = this.getClass().getName();
    private View rootView;
    private boolean isViewCreated = false;  //View是否创建了
    private boolean isCurrentVisibleState = false;  //当前是否可见

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutRes(), container, false);
        }
        initView(rootView);
        isViewCreated = true;

        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
        return rootView;
    }

    protected abstract void initView(View rootView);


    protected abstract int getLayoutRes();

    //判断Fragment是否可见
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isViewCreated) {
            if (!isCurrentVisibleState && isVisibleToUser) {
                dispatchUserVisibleHint(true);
            } else if (isCurrentVisibleState && !isVisibleToUser) {
                dispatchUserVisibleHint(false);
            }
        }
    }

    private void dispatchUserVisibleHint(boolean visibleState) {
        isCurrentVisibleState = visibleState;
        if (visibleState) {
            onFragmentLoad();
        } else {
            onFragmentLoadStop();
        }
    }

    //停止网络数据请求
    public void onFragmentLoadStop() {
        Log.d(TAG, "onFragmentLoadStop: ");
    }

    //加载网络数据请求
    public abstract void onFragmentLoad();

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !isCurrentVisibleState) {
            dispatchUserVisibleHint(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint() && isCurrentVisibleState) {
            dispatchUserVisibleHint(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        isCurrentVisibleState = false;
    }
}