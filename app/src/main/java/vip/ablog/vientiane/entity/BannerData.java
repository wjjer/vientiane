package vip.ablog.vientiane.entity;

public class BannerData {
    private String title;
    private String cover;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "BannerData{" +
                "title='" + title + '\'' +
                ", cover='" + cover + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
