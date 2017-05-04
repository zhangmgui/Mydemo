package com.xytest.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {

	private static Object lockMaster = new Object();
	private static DataSource masterDataSource = null;

	public static DataSource getMasterDataSource() {
		if (masterDataSource == null) {
			synchronized (lockMaster) {
				if (masterDataSource == null) {
					InputStream in = null;
					try {
						in = new FileInputStream("src/config/master.txt");
						Properties props = new Properties();
						props.load(in);
						masterDataSource = DruidDataSourceFactory.createDataSource(props);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return masterDataSource;
	}

}
