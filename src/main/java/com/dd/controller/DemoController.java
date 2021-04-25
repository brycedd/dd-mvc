package com.dd.controller;

import com.dd.mvc.MvcMapping;
import org.springframework.stereotype.Controller;

/**
 * @author durui
 * @Date 2020/10/14
 */
@Controller
public class DemoController {

    @MvcMapping("/demo.do")
    public DemoDO demo(String content) {
        DemoDO demoDO = new DemoDO();
        demoDO.setTitle("tt");
        demoDO.setContent(content);
        return demoDO;
//        FreemarkerView freemarkerView = new FreemarkerView("demo.ftl");
//        freemarkView.setModel("name",name);
//        return freemarkerView;
    }

    /*@MvcMapping("/hello.do")
    public FreemarkerView open(String name, DemoDO demoDO, HttpServletRequest req, HttpServletResponse resp) {
        FreemarkerView freemarkerView = new FreemarkerView("demo.ftl");
        return freemarkerView;
    }*/
}
