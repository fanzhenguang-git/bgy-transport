/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpUtil {

    public static final ContentType TEXT_PLAIN = ContentType.create("text/plain", StandardCharsets.UTF_8);

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * HttpClient 连接池
     */
    private static PoolingHttpClientConnectionManager cm = null;

    static {
        // 初始化连接池
        cm = new PoolingHttpClientConnectionManager(getRegistry());
        // 整个连接池最大连接数
        cm.setMaxTotal(200);
        // 每路由最大连接数，默认值是2
        cm.setDefaultMaxPerRoute(5);
    }

    /**
     * DESC: 发送 HTTP GET请求
     *
     * @param url 地址
     * @return
     * @throws Exception
     */
    public static String httpGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return doHttp(httpGet);
    }

    /**
     * DESC: 发送 HTTP GET请求
     *
     * @param url    地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String httpGet(String url, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParams2NVPS(params);
        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ub.setParameters(pairs);
        HttpGet httpGet = new HttpGet(ub.build());
        return doHttp(httpGet);
    }

    /**
     * DESC: 发送 HTTP GET请求
     *
     * @param url     地址
     * @param headers 请求头
     * @param params  请求参数
     * @return
     * @throws Exception
     */
    public static String httpGet(String url, Map<String, Object> headers, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParams2NVPS(params);
        // 装载请求地址和参数
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ub.setParameters(pairs);
        HttpGet httpGet = new HttpGet(ub.build());
        // 设置请求头
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }
        return doHttp(httpGet);
    }

    /**
     * DESC: 发送 HTTP POST请求
     *
     * @param url 地址
     * @return
     * @throws Exception
     */
    public static String httpPost(String url) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        return doHttp(httpPost);
    }

    /**
     * DESC: 发送 HTTP POST请求
     *
     * @param url    地址
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String httpPost(String url, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParams2NVPS(params);
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));
        return doHttp(httpPost);
    }

    /**
     * @description: 发送 HTTP POST请求
     * @author: xwb
     * @param: [url, pairs]
     * @return: java.lang.String
     * @date: 2019/9/17 11:29
     */
    public static String doHttpPost(String url, Map<String, Object> params, Map<String, Object> headerMap)
            throws Exception {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        if (headerMap != null) {
            // 设置请求头
            for (Map.Entry<String, Object> param : headerMap.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        if (params != null) {
            // 设置2个post参数，一个是scope、一个是q
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            for (String key : params.keySet()) {
                Object values = params.get(key);
                if (values instanceof String) {
                    parameters.add(new BasicNameValuePair(key, (String) values));
                } else if (values instanceof List) {
                    List<String> lstValues = (List<String>) values;
                    for (int i = 0; i < lstValues.size(); i++) {
                        parameters.add(new BasicNameValuePair(key, lstValues.get(i)));
                    }
                }
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        return doHttp(httpPost);
    }

    /**
     * DESC: 发送 HTTP POST请求
     *
     * @param url     地址
     * @param headers 请求头
     * @param params  请求参数
     * @return
     * @throws Exception
     */
    public static String httpPost(String url, Map<String, Object> headers, Map<String, Object> params) throws Exception {
        // 转换请求参数
        List<NameValuePair> pairs = covertParams2NVPS(params);
        HttpPost httpPost = new HttpPost(url);
        // 设置请求参数
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, StandardCharsets.UTF_8.name()));
        // 设置请求头
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
        }

        return doHttp(httpPost);
    }

    /**
     * 发送HTTP POST/FORM请求
     * <p>模拟表单文件上传，默认表单名称为“media”，无附加表单</p>
     *
     * @param url 请求地址
     * @return Http POST/FORM 请求结果
     * @throws Exception IO流异常
     */
    public static String httpPostForm(String url, String fileParamName, String filePath) throws Exception {
        return httpPostForm(url, fileParamName, filePath, null);
    }

    /**
     * 发送HTTP POST/FORM请求
     *
     * @param url  请求地址
     * @param file 文件
     * @return Http POST/FORM 请求结果
     * @throws Exception IO流异常
     */
    public static String httpPostForm(String url, String fileParamName,
                                      File file) throws Exception {
        return httpPostForm(url, fileParamName, file, null);
    }

    /**
     * DESC: 发送 HTTP POST/FORM请求 文件上传
     *
     * @param url       请求地址
     * @param mapParams 文件上传表单之外的附加表单
     * @return Http POST/FORM 请求结果
     * @throws Exception IO流异常
     */
    public static String httpPostForm(String url, String fileParamName, String filePath, Map<String, Object> mapParams) throws Exception {
        File file = new File(filePath);
        // 检查文件名是否合法，以及文件是否存在
        if (!file.isFile() || !file.exists()) {
            throw new Exception("HttpClient Post/Form 表单请求的文件名不合法或文件不存在");
        }
        return httpPostForm(url, fileParamName, file, mapParams);
    }

    /**
     * DESC: 发送 HTTP POST/FORM请求 文件上传
     *
     * @param url  请求地址
     * @param file 模拟表单文件
     * @return Http POST/FORM 请求结果
     * @throws Exception IO流异常
     */
    public static String httpPostForm(String url, String fileParamName, File file, Map<String, Object> map) throws Exception {
        // 初始化请求链接
        HttpPost httppost = new HttpPost(url);
        // 组装模拟文件表单
        FileBody body = new FileBody(file);
        // 组装HTTP文件表单请求参数
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(fileParamName, body);
        // 附加表单
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                builder.addTextBody(entry.getKey(), entry.getValue().toString(), TEXT_PLAIN);
            }
        }
        // 构建HttpEntity
        HttpEntity entity = builder.build();
        // 设置请求参数
        httppost.setEntity(entity);
        return doHttp(httppost);
    }

    /**
     * DESC: 发送 HTTP POST请求， 请求参数是JSON格式，数据编码是UTF-8
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String httpPostJson(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/json; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));
        return doHttp(httpPost);
    }

    /**
     * DESC: 发送 HTTP POST请求，请求参数是XML格式，数据编码是UTF-8
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return
     * @throws Exception
     */
    public static String httpPostXml(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求头
        httpPost.addHeader("Content-Type", "application/xml; charset=UTF-8");
        // 设置请求参数
        httpPost.setEntity(new StringEntity(param, StandardCharsets.UTF_8.name()));
        return doHttp(httpPost);
    }

    /**
     * 将Map键值对拼接成QueryString字符串，UTF-8编码
     *
     * @param params
     * @return
     * @throws Exception
     */
    public static String getQueryStr(Map<String, Object> params) throws Exception {
        return URLEncodedUtils.format(covertParams2NVPS(params), StandardCharsets.UTF_8.name());
    }

    /**
     * 将JavaBean属性拼接成QueryString字符串，UTF-8编码
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static String getQueryStr(Object bean) throws Exception {
        // 将JavaBean转换为Map
        Map<String, Object> map = PropertyUtils.describe(bean);
        // 移除Map中键为“class”和值为空的项
        map = Maps.filterEntries(map, new Predicate<Map.Entry<String, Object>>() {
            public boolean apply(Map.Entry<String, Object> input) {
                // 返回false表示排除该项
                return !(input.getKey().equals("class") || input.getValue() == null);
            }
        });
        return URLEncodedUtils.format(covertParams2NVPS(map), StandardCharsets.UTF_8.name());
    }

    /**
     * 将表单字符串转换为JavaBean
     *
     * @param queryStr 表单字符串
     * @param clazz    {@link Class}待转换的JavaBean
     * @return
     * @throws Exception
     */
    public static <T> T parseNVPS2Bean(String queryStr, Class<T> clazz) throws Exception {
        // 将“表单字符串”形式的字符串解析成NameValuePair集合
        List<NameValuePair> list = URLEncodedUtils.parse(queryStr, StandardCharsets.UTF_8);
        Map<String, String> rsMap = new HashMap<String, String>();
        // 将NameValuePair集合中的参数装载到Map中
        for (NameValuePair nvp : list) {
            rsMap.put(nvp.getName(), nvp.getValue());
        }
        // 实例化JavaBean对象
        T t = clazz.newInstance();
        // 将Map键值对装载到JavaBean中
        BeanUtils.populate(t, rsMap);
        return t;
    }

    /**
     * 转换请求参数，将Map键值对拼接成QueryString字符串
     *
     * @param params
     * @return
     */
    public static String covertParams2QueryStr(Map<String, Object> params) {
        List<NameValuePair> pairs = covertParams2NVPS(params);
        return URLEncodedUtils.format(pairs, StandardCharsets.UTF_8.name());
    }

    /**
     * 转换请求参数
     *
     * @param params
     * @return
     */
    public static List<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }

    /**
     * 发送 HTTP 请求
     *
     * @param request
     * @return
     * @throws Exception
     */
    private static String doHttp(HttpRequestBase request) throws Exception {
        // 通过连接池获取连接对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        return doRequest(httpClient, request);
    }

    /**
     * 处理Http/Https请求，并返回请求结果
     * <p>注：默认请求编码方式 UTF-8</p>
     *
     * @param httpClient
     * @param request
     * @return
     * @throws Exception
     */
    private static String doRequest(CloseableHttpClient httpClient, HttpRequestBase request) throws Exception {
        String result = null;
        CloseableHttpResponse response = null;
        try {
            // 获取请求结果
            response = httpClient.execute(request);
            logger.info(response.toString());
            // 解析请求结果
            HttpEntity entity = response.getEntity();

            // 转换结果
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
            // 关闭IO流
            EntityUtils.consume(entity);
        } finally {
            if (null != response) {
                response.close();
            }
        }
        return result;
    }

    /**
     * DESC: 获取 HTTPClient注册器
     *
     * @return
     * @throws Exception
     */
    private static Registry<ConnectionSocketFactory> getRegistry() {
        Registry<ConnectionSocketFactory> registry = null;
        try {
            registry = RegistryBuilder.<ConnectionSocketFactory>create().
                    register("http", new PlainConnectionSocketFactory()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return registry;
    }
}
