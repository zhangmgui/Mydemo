package com.xytest.DataGrabbing;

import com.xytest.TestDomain.Artical;
import com.xytest.TestDomain.ArticleContent;
import com.xytest.domain.ReportArticle;
import com.xytest.utils.JDBCUtils;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangmg on 2017/4/27.
 */
public class AnalysisAndSaveData {
    private static Map<String, Artical> cacheMap = new HashMap();
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        try {
            FileReader plicySummary = new FileReader(new File("E:\\work_data\\政策文章.txt"));
            BufferedReader bfread = new BufferedReader(plicySummary);
            String temp = null;
            while ((temp = bfread.readLine()) != null) {
                Artical artical = new Artical();
                String[] split = temp.split("\\t");
                artical.setBigType(split[0]);
                artical.setArticalName(split[1]);
                artical.setDate(split[2]);
                artical.setUrl(split[3]);
                cacheMap.put(split[3], artical);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertReportArt() {
        String filepath = "E:\\work_data\\articles";
        File fileDir = new File(filepath);

        Connection conn = JDBCUtils.getConnection();
        JDBCUtils.beginTransaction(conn);

        try {
            if (fileDir.isDirectory()) {
                String[] filelist = fileDir.list();
                for (int i = 0; i < filelist.length; i++) {
                    File singleFile = new File(filepath + "\\" + filelist[i]);
                    String fileUrl = getFileUrl(singleFile);
                    Document document = convertFileToDom(singleFile);
                    if (document == null || cacheMap.get(fileUrl).getBigType().indexOf("解读") == -1) { //如果不是解读文章，则跳过
                        continue;
                    }
                    ReportArticle reportArticle = convertReportDocumentToObject(fileUrl, document);
                    Statement statement = conn.createStatement();
                    if (reportArticle.getTextContent() == null || reportArticle.getTextContent().equals("")) {
                        String failSQL = "insert into policy_report(title) values('" + reportArticle.getTitle() + "') ";
                        statement.execute(failSQL);
                        System.out.println(failSQL);
                        continue;
                    }
                    String sql1 = "insert into policy_report VALUES('" + reportArticle.getTitle() + "','" + reportArticle.getWherefrom() + "','"
                            + reportArticle.getPublishDate() + "','" + reportArticle.getTextContent().replace("'", "''") +
                            "'," + reportArticle.getPolicyRegulationId() + ",'" + reportArticle.getModifydate() + "')";
                    // System.out.println(sql1);
                    statement.execute(sql1);

                }
            }
            JDBCUtils.commitTransaction(conn);
            conn.close();
        } catch (Exception e) {

            JDBCUtils.rollBackTransaction(conn);
            e.printStackTrace();
        }
    }


    @Test
    public void insertRegulation() {
        String filepath = "E:\\work_data\\articles";
        File fileDir = new File(filepath);

        Connection conn = JDBCUtils.getConnection();
        JDBCUtils.beginTransaction(conn);
        String flagSQL = "";
        try {
            if (fileDir.isDirectory()) {
                String[] filelist = fileDir.list();
                for (int i = 0; i < filelist.length; i++) {
                    File singleFile = new File(filepath + "\\" + filelist[i]);
                    String fileUrl = getFileUrl(singleFile);
                    Document document = convertFileToDom(singleFile);
                    if (document == null || cacheMap.get(fileUrl).getBigType().indexOf("解读") != -1) { //如果是解读文章，则跳过
                        continue;
                    }
                    ArticleContent aCont = convertDocToObject(fileUrl, document);
                    Statement statement = conn.createStatement();
                    StringBuilder sb = new StringBuilder("insert into policies_regulations values (");
                    if (aCont.getTextContent() == null || aCont.getTextContent().equals("")) {
                        String failSQL = "insert into policies_regulations(title,type) values('" + aCont.getTitle() + "','" + aCont.getType() + "') ";
                        statement.execute(failSQL);
                        System.out.println(failSQL);
                        continue;
                    }
                    if (aCont.getImplementationDate().equals("20040-09-17")) {
                        aCont.setImplementationDate("2004-09-17");
                    }
                    String insertRegulationssql = "insert into policies_regulations values ('" + aCont.getTitle() + "','" + aCont.getType() + "','" + aCont.getPublishOrganization() + "'" +
                            ",'" + aCont.getArticleNum() + "','" + aCont.getPublishDate() + "','" + aCont.getImplementationDate() +
                            "','" + aCont.getTextContent().replace("'", "''") + "','" + aCont.getPolicyArea() + "','" + aCont.getModifydate() + "')";
                    flagSQL = insertRegulationssql;
                    statement.execute(insertRegulationssql);
                }
            }
            JDBCUtils.commitTransaction(conn);
            conn.close();
        } catch (Exception e) {
            System.out.println(flagSQL);
            JDBCUtils.rollBackTransaction(conn);

        }
    }

    private Document convertFileToDom(File singleFile) throws Exception {
        Document dom = null;
        if (!singleFile.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            BufferedReader bread = new BufferedReader(new FileReader(singleFile));
            String temp = null;
            while ((temp = bread.readLine()) != null) {
                sb.append(temp);
            }
            String contentStr = sb.toString();
            dom = Jsoup.parse(contentStr);
        }
        return dom;
    }

    private ArticleContent convertDocToObject(String fileUrl, Document document) {
        Artical cacheArt = cacheMap.get(fileUrl);
        ArticleContent articleContent = new ArticleContent();
        if (cacheArt != null) {
            articleContent.setTitle(cacheArt.getArticalName());
            articleContent.setType(cacheArt.getBigType());
        }

        Elements fourP = document.getElementsByClass("zcfg_top");
        for (Element element : fourP) {
            String itemStr = element.html();
            if (itemStr.indexOf("颁布单位") != -1) {
                int i = itemStr.lastIndexOf('】');
                String pbOg = itemStr.substring(i + 1, itemStr.length());
                articleContent.setPublishOrganization(pbOg);
            }
            if (itemStr.indexOf("文号") != -1) {
                int i = itemStr.lastIndexOf('】');
                String wenhao = itemStr.substring(i + 1, itemStr.length());
                articleContent.setArticleNum(wenhao);
            }
            if (itemStr.indexOf("颁布日期") != -1) {
                int i = itemStr.lastIndexOf('】');
                String pdateStr = itemStr.substring(i + 1, itemStr.length());
                Date pdate = null;
                try {
                    pdate = sdf.parse(pdateStr);
                    String pdStr = sdf.format(pdate);
                    articleContent.setPublishDate(pdStr);
                } catch (ParseException e) {
                    articleContent.setPublishDate("");
                }
            }
            if (itemStr.indexOf("实施日期") != -1) {
                int i = itemStr.lastIndexOf('】');
                String ddateStr = itemStr.substring(i + 1, itemStr.length());
                Date ddate = null;
                try {
                    ddate = sdf.parse(ddateStr);
                    String dStr = sdf.format(ddate);
                    articleContent.setImplementationDate(dStr);
                } catch (ParseException e) {
                    articleContent.setImplementationDate("");
                }

            }
        }
        Date modifieTime = new Date();
        String modifieTimeStr = sdfTime.format(modifieTime);
        articleContent.setModifydate(modifieTimeStr);
        articleContent.setTextContent(document.getElementsByClass("article_con").toString());
        return articleContent;
    }

    private String getFileUrl(File file) throws Exception {
        String hexName = file.getName();
        return new String(Hex.decodeHex(hexName.substring(0, hexName.length() - 4).toCharArray()));
    }


    @Test
    public void insertSqls() throws SQLException {
        Connection connection = JDBCUtils.getConnection();
        JDBCUtils.beginTransaction(connection);
        Statement statement = connection.createStatement();
        statement.execute("insert into policies_regulations(title,type,text_content) values ('测试1','测试类型1','测试内容1')");
        JDBCUtils.commitTransaction(connection);
        connection.close();
    }

    public ReportArticle convertReportDocumentToObject(String fileUrl, Document document) {
        Artical articalTemp = cacheMap.get(fileUrl);
        ReportArticle reportArticle = new ReportArticle();
        if (articalTemp != null) {
            reportArticle.setTitle(articalTemp.getArticalName());
        }
        // Elements h3 = document.getElementsByTag("h3");
        // String title = h3.get(0).html();
        if (document.getElementsByClass("from").size() == 0 || document.getElementsByClass("from") == null) {
            return reportArticle;
        }
        Element from = document.getElementsByClass("from").get(0);

        char c = 160;
        String dateStr = from.ownText().replace(c, '`').replace("`", "").trim();
        Date date = null;
        try {
            date = sdfTime.parse(dateStr);
            String pbdateSTR = sdfTime.format(date);
            reportArticle.setPublishDate(pbdateSTR);
        } catch (ParseException e) {
            reportArticle.setPublishDate("");
        }

        String fromwhere = from.getElementsByTag("span").get(0).getElementsByTag("a").html();
        String textContent = document.getElementsByClass("article_con").toString().replace("'", "''");
        Date modifyDate = new Date();
        String format1 = sdfTime.format(modifyDate);
        reportArticle.setModifydate(format1);
        // reportArticle.setTitle(title);

        reportArticle.setWherefrom(fromwhere);
        reportArticle.setTextContent(textContent);
        return reportArticle;
    }

    @Test
    public void relatingRepAndReg() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            JDBCUtils.beginTransaction(conn);
            String selcAll = "select * from policies_regulations";
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(selcAll);
            List<ArticleContent> reports = new ArrayList<>();

            while (resultSet.next()) {
                int ID = resultSet.getInt(1);
                String title = resultSet.getString(2);
                ArticleContent articleContent = new ArticleContent();
                articleContent.setID(ID);
                articleContent.setTitle(title);
                reports.add(articleContent);
            }

            for (ArticleContent ArticleContent : reports) {
                updateRepIfExist(ArticleContent, statement);

            }
            JDBCUtils.commitTransaction(conn);
        } catch (Exception e) {
            JDBCUtils.rollBackTransaction(conn);
        }
    }

    private void updateRepIfExist(ArticleContent articleContent, Statement statement) throws SQLException {
        String existSql = "select * from policy_report where title like '%" + articleContent.getTitle() + "%'";

        ResultSet resultSet = statement.executeQuery(existSql);

        if (resultSet.next()) {
            int repAId = resultSet.getInt(1);
            String title = resultSet.getString(2);
            System.out.println(title);
            String updateSql = "update policy_report set policy_regulation_id = " + articleContent.getID() + " where ID = " + repAId;
            System.out.println(updateSql);
            boolean execute = statement.execute(updateSql);
        }
    }
}
