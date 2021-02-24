package vip.ablog.vientiane.entity;

import java.util.List;

public class SearchThinkData {
    private int status;
    private String info;
    private List<ThinkTitle> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<ThinkTitle> getData() {
        return data;
    }

    public void setData(List<ThinkTitle> data) {
        this.data = data;
    }

    public class ThinkTitle{
        private String vod_name;

        public String getVod_name() {
            return vod_name;
        }

        public void setVod_name(String vod_name) {
            this.vod_name = vod_name;
        }
    }
}
