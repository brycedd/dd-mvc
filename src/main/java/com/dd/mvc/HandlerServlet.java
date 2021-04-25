package com.dd.mvc;

import com.alibaba.fastjson.JSON;
import com.dd.mvc.MvcBeanFactory.MvcBean;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author durui
 * @Date 2020/10/13
 */
public class HandlerServlet extends HttpServlet {

    private WebApplicationContext context;
    private MvcBeanFactory beanFactory;
    final ParameterNameDiscoverer parameterUtil = new LocalVariableTableParameterNameDiscoverer();
    private Configuration freemarkeConfig;

    @Override
    public void init() throws ServletException {
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        beanFactory = new MvcBeanFactory(context);
        Configuration freemarkeConfig = null;
        try {
            freemarkeConfig = context.getBean(Configuration.class);

        }catch (NoSuchBeanDefinitionException e) {
            //
        }
        if(freemarkeConfig == null) {
            freemarkeConfig = new Configuration();
            freemarkeConfig.setDefaultEncoding("UTF-8");
            freemarkeConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            try {
                freemarkeConfig.setDirectoryForTemplateLoading(new File(getServletContext().getRealPath("")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.freemarkeConfig = freemarkeConfig;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doHandler(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doHandler(req,resp);
    }

    public void doHandler(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getServletPath(); //此处不包含参数及端口
        //TODO 处理静态文件
        if(uri.equals("/favicon.ico")) {
            return;
        }

        MvcBeanFactory.MvcBean mvcBean = beanFactory.getMvcBean(uri);

        if(mvcBean == null) {
            throw new IllegalArgumentException(String.format("not found %s mapping",uri));
        }

        Object[] args = buildParams(mvcBean,req,resp);

        try {
            Object result = mvcBean.run(args);
            processResult(result,resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void processResult(Object result,HttpServletResponse resp) throws IOException, TemplateException {
        if(result instanceof FreemarkerView) {
            FreemarkerView freemarkerView = (FreemarkerView) result;
            Template template = freemarkeConfig.getTemplate(freemarkerView.getFtlPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resp.setContentType("text/html; charset=utf-8");
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(200);
            template.process(freemarkerView,resp.getWriter());
        }else {
            //JSON VIEW
            String jsonString = JSON.toJSONString(result);
            resp.setContentType("text/html; charset=utf-8");
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(200);
            PrintWriter writer = resp.getWriter();
            writer.println(jsonString);

        }



    }

    private Object[] buildParams(MvcBean mvcBean,HttpServletRequest req, HttpServletResponse resp) {
        Method method = mvcBean.getTargetMethod();
        List<String> paramNames = Arrays.asList(parameterUtil.getParameterNames(method));
        //通过反射获得参数
        Class<?>[] parameterTypes = method.getParameterTypes();

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < paramNames.size(); i++) {
            if(parameterTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = req;
            }else if (parameterTypes[i].isAssignableFrom(HttpServletResponse.class)) {
                args[i] = resp;
            }else {
                String parameter = req.getParameter(paramNames.get(i));
                if(parameter == null) {
                    args[i] = null;
                }else {
                    args[i] = convert(parameter,parameterTypes[i]);
                }
            }

        }
        return args;
    }

    public <T> T convert(String val, Class<T> targetClass) {
        Object result = null;
        if (val == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            result = Integer.parseInt(val.toString());
        } else if (Long.class.equals(targetClass)) {
            result = Long.parseLong(val.toString());
        } else if (String.class.equals(targetClass)) {
            result = val;
        } else if (Date.class.equals(targetClass)) {
            try {
                result = new SimpleDateFormat("").parse(val);
            } catch (ParseException parseException) {
                parseException.printStackTrace();
            }
        }else {
            //复杂参数封装
        }

        return (T) result;

    }
}
