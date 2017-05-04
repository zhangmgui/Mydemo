package com.xytest.utils;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by zhangmg on 2017/4/25.
 */

    public class JDBCUtils {
        private static String driver;
        private static String url;
        private static String user;
        private static String password;
        // 静态代码块
        static {
            try {
                // 1 使用Properties处理流
                // 使用load()方法加载指定的流
                Properties props = new Properties();
                Reader is = new FileReader("src/main/sqlserver.properties");
                props.load(is);
                // 2 使用getProperty(key)，通过key获得需要的值，
                driver = props.getProperty("driver");
                url = props.getProperty("url");
                user = props.getProperty("user");
                password = props.getProperty("password");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        /**
         * 获得连接
         */
        public static Connection getConnection() {
            try {
                // 1 注册驱动
                Class.forName(driver);
                // 2 获得连接
                Connection conn = DriverManager.getConnection(url, user, password);
                return conn;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    /**
     * 开始事务
     * @param cnn
     */
    public static void beginTransaction(Connection cnn){
        if(cnn!=null){
            try {
                if(cnn.getAutoCommit()){
                    cnn.setAutoCommit(false);
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 提交事务
     * @param cnn
     */
    public static void commitTransaction(Connection cnn){
        if(cnn!=null){
            try {
                if(!cnn.getAutoCommit()){
                    cnn.commit();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 回滚事务
     * @param cnn
     */
    public static void rollBackTransaction(Connection cnn){
        if(cnn!=null){
            try {
                if(!cnn.getAutoCommit()){
                    cnn.rollback();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}


