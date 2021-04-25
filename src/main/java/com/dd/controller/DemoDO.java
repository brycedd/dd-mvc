package com.dd.controller;

import java.io.Serializable;
import java.util.Date;

/**
 * @author durui
 * @Date 2020/10/17
 */
public class DemoDO implements Serializable {

    private String title;
    private String author;
    private String content;
    protected Date createTime;

    public DemoDO() {
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
