package com.dikers.httpd;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dikers.httpd.exception.InvalidHeaderException;
import com.dikers.ioc.ApplicationContext;
import com.dikers.ioc.impl.AnnotationApplicationContext;
import com.dikers.pojo.RequestBean;
import com.dikers.utils.ResultUtils;
import com.dikers.vo.ResultVo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static com.dikers.httpd.StatusCodeEnum.*;


/**
 * TinyHttpd
 *
 * @author dikers
 * @date 2018-03-26 22:28:44
 */

public class TinyHttpd {

    private static final int DEFAULT_PORT = 8050;

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final String META_RESOURCE_DIR_PREFIX = "/meta/";

    private int port;

    ApplicationContext applicationContext ;


    public TinyHttpd() {
        this(DEFAULT_PORT);
    }

    public TinyHttpd(int port) {
        this.port = port;
    }

    public void start() throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        applicationContext = new AnnotationApplicationContext("com.dikers");

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress("localhost", port));
        ssc.configureBlocking(false);

        System.out.println(String.format("TinyHttpd 已启动，正在监听 %d 端口...", port));
        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {
            int readyNum = selector.select();
            if (readyNum == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while (it.hasNext()) {
                SelectionKey selectionKey = it.next();
                it.remove();

                if (selectionKey.isAcceptable()) {
                    SocketChannel socketChannel = ssc.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    request(selectionKey);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                } else if (selectionKey.isWritable()) {
                    response(selectionKey);
                }
            }
        }
    }

    /**
     * 处理请求
     * @param selectionKey 选择键
     * @throws IOException
     */
    private void request(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        channel.read(buffer);

        buffer.flip();
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        String headerStr = new String(bytes);
        try {
            Headers headers = HeaderUtils.parseHeader(headerStr);
            selectionKey.attach(Optional.of(headers));
        } catch (InvalidHeaderException e) {
            selectionKey.attach(Optional.empty());
        }
    }



    private void response(SelectionKey selectionKey) throws IOException {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        Optional<Headers> op = (Optional<Headers>) selectionKey.attachment();

        // 处理无效请求，返回 400 错误
        if (!op.isPresent()) {
            handleBadRequest(channel);
            channel.close();
            return;
        }

        String ip = channel.getRemoteAddress().toString().replace("/", "");
        Headers headers = op.get();
        // 处理 403
        if (headers.getPath().startsWith(META_RESOURCE_DIR_PREFIX)) {
            handleForbidden(channel);
            channel.close();
            requestLog(ip, headers, FORBIDDEN.getCode());
            return;
        }

        try {

            String path = headers.getPath();
            if(path.startsWith("/")){
                path = path.substring(1, path.length());
            }
            RequestBean requestBean = applicationContext.getRequestBeanByPath(path);
            if(path == null || requestBean  == null){
                handleNotFound(channel);
                requestLog(ip, headers, NOT_FOUND.getCode());
                return;
            }

            handleOK(channel, requestBean , headers);
            requestLog(ip, headers, OK.getCode());
        } catch (FileNotFoundException e) {
            handleNotFound(channel);
            requestLog(ip, headers, NOT_FOUND.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
            requestLog(ip, headers, INTERNAL_SERVER_ERROR.getCode());
        } finally {
            channel.close();
        }
    }

    private void handleOK(SocketChannel channel, RequestBean requestBean, Headers requestHeaders ) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ResponseHeaders responseHeaders = new ResponseHeaders(OK.getCode());

        ByteBuffer bodyBuffer = readController(requestBean , requestHeaders);
        responseHeaders.setContentLength(bodyBuffer.capacity());
        ByteBuffer headerBuffer = ByteBuffer.wrap(responseHeaders.toString().getBytes());

        channel.write(new ByteBuffer[]{headerBuffer, bodyBuffer});
    }


    private void handleNotFound(SocketChannel channel)  {
        try {
            handleError(channel, NOT_FOUND.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
        }
    }

    private void handleBadRequest(SocketChannel channel) {
        try {
            handleError(channel, BAD_REQUEST.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
        }
    }

    private void handleForbidden(SocketChannel channel) {
        try {
            handleError(channel, FORBIDDEN.getCode());
        } catch (Exception e) {
            handleInternalServerError(channel);
        }
    }

    private void handleInternalServerError(SocketChannel channel) {
        try {
            handleError(channel, INTERNAL_SERVER_ERROR.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleError(SocketChannel channel, int statusCode) throws IOException {
        ResponseHeaders headers = new ResponseHeaders(statusCode);


        JSONObject object = new JSONObject();
        object.put("code", statusCode);
        object.put("message", "error "+ statusCode);

        ByteBuffer bodyBuffer = ByteBuffer.wrap(JSON.toJSONString(object).getBytes());
        headers.setContentLength(bodyBuffer.capacity());
        ByteBuffer headerBuffer = ByteBuffer.wrap(headers.toString().getBytes());

        channel.write(new ByteBuffer[]{headerBuffer, bodyBuffer});
    }


    private ByteBuffer readController(RequestBean requestBean , Headers requestHeaders) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

//        System.out.println("==> getPath  "+requestBean.getPath());

        Method method = requestBean.getMethod();
        Object object = applicationContext.newInstanceBean(requestBean.getClassName());

        ResultVo result = ResultUtils.fail(500, "error");
        try {
            result = (ResultVo) method.invoke(object, new Object[] { requestHeaders});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            result.setMsg(e.getMessage());
        }


        ByteBuffer buffer = ByteBuffer.wrap(JSON.toJSONString(result).getBytes());
        return buffer;
    }

    private void requestLog(String ip, Headers headers, int code) {
        // ip [date] "Method path version" code user-agent
        String dateStr = Date.from(Instant.now()).toString();
        String msg = String.format("[%s] %s \"%s %s %s\" %d",
                dateStr, ip, headers.getMethod(), headers.getPath(), headers.getVersion(), code);
//        , headers.get("User-Agent")
        System.out.println(msg);
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
       new TinyHttpd().start();
    }
}
