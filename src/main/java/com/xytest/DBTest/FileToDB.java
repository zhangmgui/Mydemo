package com.xytest.DBTest;

import com.xytest.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileToDB {

	private static final String PATH = "/root/info";
	// private static final String PATH = "D:/work/数据仓库/info";

	private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

	public static void main(String args[]) throws Exception {
		long nowtime = System.currentTimeMillis();
		for (File file : new File(PATH).listFiles()) {
			String name = file.getName();
			for (File txtFile : file.listFiles()) {
				String cityId = txtFile.getName().split("_")[0];
				BufferedReader reader = new BufferedReader(new FileReader(txtFile));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] arry = line.split(",");
					if (name.equals("vehiclenum")) {
						qr.update("insert into vehiclenum values (null,?,?,?,?); -- maxscale route to master",
								new Object[] { arry[0], cityId, arry[1], arry[2] });
					} else {
						qr.update("insert into " + name + " values (null,?,?,?,?,?,?); -- maxscale route to master",
								new Object[] { arry[0], cityId, arry[1], arry[2], arry[3], arry[4] });
					}
				}
				reader.close();
			}
		}
		System.out.println("all time is " + (System.currentTimeMillis() - nowtime) / 1000);
	}

}
