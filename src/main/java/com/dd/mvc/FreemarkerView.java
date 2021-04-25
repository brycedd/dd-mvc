package com.dd.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author durui
 * @Date 2020/10/13
 */
public class FreemarkerView {

    private String ftlPath;
    private Map<String,Object> models = new HashMap<String, Object>();

    public FreemarkerView(String ftlPath) {
        this.ftlPath = ftlPath;
    }

    public FreemarkerView(String ftlPath, Map<String, Object> models) {
        this.ftlPath = ftlPath;
        this.models = models;
    }

    public String getFtlPath() {
        return ftlPath;
    }

    public Map<String, Object> getModels() {
        return models;
    }

    public void setFtlPath(String ftlPath) {
        this.ftlPath = ftlPath;
    }

    public void setModels(Map<String, Object> models) {
        this.models = models;
    }
}
