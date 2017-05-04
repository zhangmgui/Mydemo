package com.xytest.DataGrabbing;


import org.apache.commons.codec.binary.Hex;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangmg on 2017/4/27.
 */
public class JonsupSimulateLogin {
    @Test
    public void getArticalContentChitunion() throws IOException {

        String logincookieVal = "BFsRZoIScvXO/GowLak0jyn9DUjxNNLwMh+Ko6xOrlCLWclyZhXKJw1Y1rvFMpqt82U9pDWJl1LZk6F/Dh7Jt1C3Hbw2QMzD9rI+hajfSN+EfbFwzMWh+nkrcZTiml2DZl5JxfRooWG/p+U5ronlXs+E6boYAYcguWKJbj/MQgLgzK4//7J7bKzbklg69BXz";
        // Connection.Response res = Jsoup.connect("http://www.chitunion.com/Login.aspx").data("txtGGZUserName", "18071443653", "txtGGZPwd", "111111").method(Connection.Method.POST).execute();
        String sessioncookieKey = "ASP.NET_SessionId";
        String sessionCookieValue = "1xflvlva1jthrbiporqogbst";
        String loginCookieKey = "ct-uinfo";

        HashMap<String, String> cookieMap = new HashMap<>();
        cookieMap.put(sessioncookieKey, sessionCookieValue);
        cookieMap.put(loginCookieKey, logincookieVal);
        Document document = Jsoup.connect("http://j.chitunion.com/title/mediaNoTitleList").cookies(cookieMap).get();
        System.out.println(document.toString());
    }

    @Test
    public void getArticalCar() throws Exception {
        String jsessionidKey = "JSESSIONID";
        String jsessionidValue = "BqfTdNzTop+MxB1EVZIKDJeY.app2";
        String aisessionid = "aisessionid";
        String val1 = "hrzj11BqfTdNzTop+MxB1EVZIKDJeY.app2";
        String loginCookieKey = "autoinfoid";
        String logincookieVal = "hrzj11tokenidWJ6VUQLXW8FT4EAH71319D7GERCJ320427173013";

        HashMap<String, String> cookieMap = new HashMap<>();
        cookieMap.put(jsessionidKey, jsessionidValue);
        cookieMap.put(loginCookieKey, logincookieVal);
        cookieMap.put(aisessionid,val1);


        FileReader fread = new FileReader(new File("C:\\Users\\zhangmg\\Desktop\\政策文章.txt"));

        BufferedReader bread = new BufferedReader(fread);


        String line = null;
        LinkedHashMap<String, String> urlAndName = new LinkedHashMap<>();
        int fl = 1;
        while ((line = bread.readLine()) != null) {

            String[] split = line.split("\\t");
            String url = split[3];
            String articleName = split[1];
            System.out.println(articleName);

            if (urlAndName.containsKey(url)) {
            System.out.println("<a href='#'>"+articleName+"</a>");
            System.out.println("<a href='#'>"+url+"</a>");

            }
            urlAndName.put(url,articleName);

        }
        bread.close();
        fread.close();

        String s = urlAndName.get("http://www.autoinfo.org.cn/autoinfo_cn/content/zcfg/20050804/1067457.html");
        Set<Map.Entry<String, String>> entries = urlAndName.entrySet();
        FileWriter fileWriter =null;
       for (Map.Entry<String, String> entry : entries) {
            String url = entry.getKey();

            String urlname = Hex.encodeHexString(url.getBytes());

            Document document = Jsoup.connect(url).cookies(cookieMap).get();
            fileWriter = new FileWriter(new File("E:\\work_data\\articles\\" + urlname + ".txt"));
            fileWriter.write(document.toString());
           fileWriter.flush();
           fileWriter.close();
        }

    }
    @Test
    public void testLogin() throws Exception {

        Jsoup.connect("http://www.autoinfo.org.cn/tzm/autoinfo.jsp").data("loginname", "1231", "password", "1231232").method(Connection.Method.POST).execute();

    }
}

 /* Connection.Response res = Jsoup.connect("http://www.example.com/login.php")
                .data("username", "myUsername", "password", "myPassword")
                .method(Method.POST)
                .execute();

        Document doc = res.parse();
        String sessionId = res.cookie("SESSIONID");
        在上面的代码成功登录后，就可以利用登录的cookie来保持会话，抓取网页内容了

        Document objectDoc = Jsoup.connect("http://www.example.com/otherPage")
                .cookie("SESSIONID", sessionId)
                .get();*/