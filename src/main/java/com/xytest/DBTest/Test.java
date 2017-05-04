package com.xytest.DBTest;

import com.xytest.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.util.List;

public class Test {
    private static long startTime = System.currentTimeMillis();
    private static final int step = 50000;

    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    public static void main(String args[]) throws Exception {
        process(0, (int) qr.query("select max(id) from tmp_person limit 1", new ScalarHandler<Integer>()) + 10);
        System.out.println("All done!");
    }

    public static void process(int start, int max) throws Exception {
        int end = start + step;
        int total = 0;
        while (start < max) {
            List<Object[]> rsList = qr.query("select m.id,m.org from tmp_person m where m.id>? and m.id<?",
                    new ArrayListHandler(), start, end);
            for (Object[] rs : rsList) {
                int id = (int) rs[0];
                if (rs[1] == null) {
                    continue;
                }
                String org1 = rs[1].toString();
                boolean update = false;
                for (int i = 0; i < org1.length(); i++) {
                    String key = org1.substring(0, org1.length() - i);
                    key = key.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
                    Integer orgId = qr.query("select * from t_org_temp where cn_name like ? order by id limit 1",
                            new ScalarHandler<Integer>(), new Object[]{key + "%"});
                    if (orgId != null) {
                        qr.update("update tmp_person set org_id=? where id=?", new Object[]{orgId, id});
                        update = true;
                        break;
                    }
                }
                if (!update) {
                    qr.update("update tmp_person set org_id=0 where id=?", new Object[]{id});
                }
                ++total;
            }
            start = end - 1;
            end = start + step;
            System.out.println(getPercent(end, max) + " time-cost:" + (System.currentTimeMillis() - startTime) / 1000
                    + "s: " + total + "/" + max);
        }
    }

    private static String getPercent(int a, int all) {
        return (((float) a) / all * 100) + "%";
    }
}