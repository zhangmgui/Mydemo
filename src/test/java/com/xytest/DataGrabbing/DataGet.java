package com.xytest.DataGrabbing;

import com.xytest.TestDomain.Artical;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by zhangmg on 2017/4/27.
 */
public class DataGet {
    private String testurl ="http://www.baidu.com";
    @Test
    public void test1() throws IOException {
        Document document = Jsoup.connect(testurl).get();
        Elements a = document.getElementsByTag("a");
        a.attr("url","随便来个url");
        String url = a.attr("url");
        System.out.println(url);
    }

    private  String url = "http://www.autoinfo.org.cn/autoinfo_cn/channel/zcfg/hgzclist.html";
    /**
     * 获取指定HTML 文档指定的body
     * @throws IOException
     */
    @Test
    public void BolgBody() throws Exception {
        LinkedHashMap<String, List<Artical>> listMap = new LinkedHashMap<>();

        Document doc = Jsoup.connect(url).get();
        Elements nav = doc.body().getElementsByClass("nav");
        Element e = nav.get(0);

        Elements aTabs = e.getElementsByTag("a");
            for(Element aTab : aTabs){
                String text = aTab.html();
                if(text.equals("首页")){
                    continue;
                }
                String href = aTab.attr("href");
                Document document = Jsoup.connect(href).get();//当前tab的dom对象
                Elements pagination = document.getElementsByClass("pagination");
                String replace = pagination.get(0).html().split("/")[1].replace(" ", "");
                int indexY = replace.indexOf("页");
                Integer tabPageNums = Integer.valueOf(pagination.get(0).html().split("/")[1].substring(0, indexY));

                ArrayList<Artical> articals = new ArrayList<>();

                for (Integer i = 1; i <=tabPageNums; i++) {
                    String everyHref = "";
                    if(i==1){
                        everyHref = href;
                    }else {
                        everyHref = href.substring(0,href.length()-5)+"_"+i+".html";
                    }
                    Document document1 = Jsoup.connect(everyHref).get();
                    addStrList(document1,articals);
                }



                listMap.put(text,articals);
            }

        File file = new File("E:\\work_data\\政策文章.txt");
        FileWriter fileWriter = new FileWriter(file);
        Set<String> strKey = listMap.keySet();
        for (String s : strKey) {
            List<Artical> articals = listMap.get(s);
            for (Artical artical : articals) {
                String txtLine = s+"\t"+artical.toString()+"\r\n";
                fileWriter.write(txtLine);
            }

        }
        fileWriter.close();
        System.out.println(listMap);


    }


    private void addStrList(Document document,List<Artical> articals) throws Exception {

        Elements elementsByClass = document.getElementsByClass("c1-bline");
        for (Element e : elementsByClass) {
            Elements Taga = e.getElementsByTag("a");
            String articalHref = Taga.get(0).attr("href");
            String articalName = Taga.get(0).attr("title");
            Elements elementsByClass1 = e.getElementsByClass("gray f-right");
            String date = elementsByClass1.get(0).html();
            Artical artical = new Artical();
            artical.setArticalName(articalName);
            artical.setUrl(articalHref);
            artical.setDate(date);

            articals.add(artical);
        }

    }

    /**
     * 获取博客上的文章标题和链接
     */
    public static void article() {
        Document doc;
        try {
            doc = Jsoup.connect("http://www.cnblogs.com/zyw-205520/").get();
            Elements ListDiv = doc.getElementsByAttributeValue("class","postTitle");
            for (Element element :ListDiv) {
                Elements links = element.getElementsByTag("a");
                for (Element link : links) {
                    String linkHref = link.attr("href");
                    String linkText = link.text().trim();
                    System.out.println(linkHref);
                    System.out.println(linkText);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    /**
     * 获取指定博客文章的内容
     */
    public static void Blog() {
        Document doc;
        try {
            doc = Jsoup.connect("http://www.cnblogs.com/zyw-205520/archive/2012/12/20/2826402.html").get();
            Elements ListDiv = doc.getElementsByAttributeValue("class","postBody");
            for (Element element :ListDiv) {
                System.out.println(element.html());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

