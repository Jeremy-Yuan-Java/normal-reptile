package com.jeremy.normal.util;

import java.net.URI;
import java.net.URL;

/**
 * 链接合法转化工具
 */
public class LinkUtil {

    public static String getAbsoluteURL(String baseURI, String relativePath) {
        String abURL = null;
        try {
            URI base = new URI(baseURI);
            URI abs = base.resolve(relativePath);//解析于上述网页的相对URL，得到绝对URI
            URL absURL = abs.toURL();//转成URL
            abURL = absURL.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return abURL;
    }
}