package com.dikers.ioc.impl;

import com.dikers.ioc.ApplicationContext;
import com.dikers.pojo.RequestBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 实现一个简单的xml配置bean 的功能。模拟spring ioc功能。
 * @author dikers
 */
public class ClassPathXmlApplicationContext implements ApplicationContext {


    private File file;
    private Map map = new HashMap();
    //ThreadLocal threadLocal  = new ThreadLocal();

    public ClassPathXmlApplicationContext(String configFile) {
        URL url = this.getClass().getClassLoader().getResource(configFile);

        try {
            file = new File(url.toURI());
            xmlParsing();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void xmlParsing() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file);
        XPath xpath = XPath.newInstance("//bean");
        List beans = xpath.selectNodes(doc);
        Iterator i = beans.iterator();
        while (i.hasNext()) {
            Element bean = (Element) i.next();
            String id = bean.getAttributeValue("id");
            String cls = bean.getAttributeValue("class");
            System.out.println("id: "+id + "  cls: "+ cls);
            Object obj = Class.forName(cls).newInstance();
            Method[] method = obj.getClass().getDeclaredMethods();
            List<Element> list = bean.getChildren("property");
            for (Element el : list) {
                for (int n = 0; n < method.length; n++) {
                    String name = method[n].getName();
                    String temp = null;
                    if (name.startsWith("set")) {
                        temp = name.substring(3, name.length()).toLowerCase();
                        if (el.getAttribute("name") != null) {
                            if (temp.equals(el.getAttribute("name").getValue())) {
                                method[n].invoke(obj, el.getAttribute("value").getValue());
                            }
                        } else {
                            method[n].invoke(obj,map.get(el.getAttribute("ref").getValue()));
                        }
                    }
                }
            }
            map.put(id, obj);
        }
    }

    @Override
    public Object getBean(String name) {
        return map.get(name);
    }

    @Override
    public RequestBean getRequestBeanByPath(String path) {
        return null;
    }

    @Override
    public Object newInstanceBean(String className) {
        return null;
    }

}
