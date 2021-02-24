package vip.ablog.vientiane.entity;

import java.util.List;

public class PlaySourceData {
    private List<String> Vod;
    private List<Source> Data;

    public List<String> getVod() {
        return Vod;
    }

    public void setVod(List<String> vod) {
        Vod = vod;
    }

    public List<Source> getData() {
        return Data;
    }

    public void setData(List<Source> data) {
        Data = data;
    }

    public class Source {
        private String servername;
        private String playname;
        private List<List<String>> playurls;

        public String getServername() {
            return servername;
        }

        public void setServername(String servername) {
            this.servername = servername;
        }

        public String getPlayname() {
            return playname;
        }

        public void setPlayname(String playname) {
            this.playname = playname;
        }

        public List<List<String>> getPlayurls() {
            return playurls;
        }

        public void setPlayurls(List<List<String>> playurls) {
            this.playurls = playurls;
        }
    }
}


