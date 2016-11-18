package com.qf.teawork.entyties;

/**
 * Created by my on 2016/11/15.
 */

public class Content {


    /**
     * id : 1
     * title : 让中国人喝得起中国茶
     * source : 福州晚报
     * wap_content : null
     * create_time : 06月13日10:42
     * author : 爱丽丝品茶记
     * weiboUrl : http://sns.maimaicha.com/news/detail/1
     */

    private String id;
    private String title;
    private String source;
    private String wap_content;
    private String create_time;
    private String author;
    private String weiboUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWap_content() {
        return wap_content;
    }

    public void setWap_content(String wap_content) {
        this.wap_content = wap_content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getWeiboUrl() {
        return weiboUrl;
    }

    public void setWeiboUrl(String weiboUrl) {
        this.weiboUrl = weiboUrl;
    }


    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", wap_content=" + wap_content +
                ", create_time='" + create_time + '\'' +
                ", author='" + author + '\'' +
                ", weiboUrl='" + weiboUrl + '\'' +
                '}';
    }
}
