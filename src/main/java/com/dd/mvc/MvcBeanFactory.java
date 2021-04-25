package com.dd.mvc;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author durui
 * @Date 2020/10/13
 */
public class MvcBeanFactory {

    private ApplicationContext applicationContext;

    public MvcBeanFactory(ApplicationContext applicationContext) {
        Assert.notNull(applicationContext,"argument 'applicationContext' must not be null");
        this.applicationContext = applicationContext;
        loadApiFromSpringBeans();
    }

    //接口存储
    private HashMap<String,MvcBean> mvcBeanMap = new HashMap<String, MvcBean>();

    private void loadApiFromSpringBeans() {
        mvcBeanMap.clear();
        //ioc所有bean
        //spring ioc 扫描
        String[] names = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for(String name : names) {
            type = applicationContext.getType(name);
            for(Method m : type.getDeclaredMethods()) {
                //通过反射拿到HttpMapping注解
                MvcMapping mvcMapping = m.getAnnotation(MvcMapping.class);
                if(mvcMapping != null) {
                    //封装成一个MVC bean
                    addMvcBeanMap(mvcMapping,name,m);
                }
            }

        }
    }


    private void addMvcBeanMap(MvcMapping mvcMapping,String beanName,Method method) {
        MvcBean mvcBean = new MvcBean();
        mvcBean.url = mvcMapping.value();
        mvcBean.targetMethod = method;
        mvcBean.targetName = beanName;
        mvcBean.context = this.applicationContext;
        mvcBeanMap.put(mvcMapping.value(),mvcBean);

    }

    public boolean containsUrl(String url,String version) {
        return mvcBeanMap.containsKey(url);
    }
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static class MvcBean {
        /** url */
        String url;
        /** ioc bean 名称 */
        String targetName;
        /** **serviceImpl实例 */
        Object target;
        /** 目标方法 */
        Method targetMethod;
        ApplicationContext context;


        public Object run(Object... args) throws InvocationTargetException, IllegalAccessException {
            // 懒加载
            if(target == null) {
                // spring ioc 容器中去获取Bean
                target = context.getBean(targetName);
            }
            return targetMethod.invoke(target,args);
        }



        public String getApiUrl() {
            return url;
        }

        public String getTargetName() {
            return targetName;
        }

        public Object getTarget() {
            return target;
        }

        public Method getTargetMethod() {
            return targetMethod;
        }

        public ApplicationContext getContext() {
            return context;
        }
    }

    public MvcBean getMvcBean(String uri) {
        return mvcBeanMap.get(uri);
    }

    public HashMap<String, MvcBean> getMvcBean() {
        return mvcBeanMap;
    }


}
