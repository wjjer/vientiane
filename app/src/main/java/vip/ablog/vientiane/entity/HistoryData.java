package vip.ablog.vientiane.entity;


public class HistoryData extends VideoData {
    private String playTime;   //播放时间
    private String playDate;   //播放日期
    private String playSeries;   //播放续集

    public String getPlaySeries() {
        return playSeries;
    }

    public void setPlaySeries(String playSeries) {
        this.playSeries = playSeries;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }
}
