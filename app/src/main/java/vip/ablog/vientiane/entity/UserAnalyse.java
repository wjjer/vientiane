package vip.ablog.vientiane.entity;

import java.security.Timestamp;
import java.util.Date;

import cn.bmob.v3.BmobObject;

public class UserAnalyse extends BmobObject {

    /*设备唯一id信息*/
    private String deviceId;
    /*app使用总次数*/
    private Integer useTimes;
    /*用户id信息*/
    private String userId;
    /*最近使用时间*/
    private Date lastUseTime;
    /*是否激活软件*/
    private boolean activeApp;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(Integer useTimes) {
        this.useTimes = useTimes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(Date lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public boolean isActiveApp() {
        return activeApp;
    }

    public void setActiveApp(boolean activeApp) {
        this.activeApp = activeApp;
    }
}
