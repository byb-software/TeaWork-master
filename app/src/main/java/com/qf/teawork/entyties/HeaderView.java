package com.qf.teawork.entyties;

/**
 * Created by my on 2016/11/14.
 */

public class HeaderView {


    /**
     * id : 5613
     * title : 茶百科androidV1.2新功能简介
     * name : 茶百科androidV1.2新功能简介
     * link : http://sns.maimaicha.com/news/detail/5613
     * content : 1、	更新页面布局，增加首次登陆提示页。
     2、	增加栏目导航，按照分类列表展示。
     * image : http://s1.sns.maimaicha.com/images/2013/04/18/b0a9fed6aaef278fb5061b194c63d088.jpg
     * image_s : http://s1.sns.maimaicha.com/images/2013/04/18/9866411d3f679484822a286d58975956.jpg
     */

    private String id;
    private String title;
    private String name;
    private String link;
    private String content;
    private String image;
    private String image_s;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_s() {
        return image_s;
    }

    public void setImage_s(String image_s) {
        this.image_s = image_s;
    }


    @Override
    public String toString() {
        return "HeaderView{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", image_s='" + image_s + '\'' +
                '}';
    }
}
