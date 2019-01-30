package com.dikers.test;

import com.dikers.httpd.TinyHttpd;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class HttpServerTest {


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException {
        new TinyHttpd().start();
    }
}
