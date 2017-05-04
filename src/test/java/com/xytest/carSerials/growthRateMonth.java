package com.xytest.carSerials;

import com.xytest.domain.CarserialsAndSale;
import com.xytest.utils.CalculateUtils;
import com.xytest.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by zhangmg on 2017/5/4.
 */
public class growthRateMonth {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());
    @Test
    public void calculate() throws SQLException {
        //http://bbs.csdn.net/topics/390985921
        //List<Department>> collect = departments.stream().collect(Collectors.groupingBy(Department::getParentId));
        String sql = "select year_month,x_ways_id as csID,province,city,carserial,sales_volume from  city_sales_volume";
        Object[] params = {};
        long start = System.currentTimeMillis();
        List<CarserialsAndSale> list1 = qr.query(sql, new BeanListHandler<CarserialsAndSale>(CarserialsAndSale.class));
        long end = System.currentTimeMillis();
        System.out.println("查询消耗："+((end - start) / (1000 * 60))+"秒");




    }
    //计算省份销量参数
    private List<CarserialsAndSale> calculateProvince(List<CarserialsAndSale> allLogs){


        return null;
    }
    //计算城市销量参数
    private List<CarserialsAndSale> calculateCity(List<CarserialsAndSale> allLogs) {
        List<CarserialsAndSale> calculateList = new ArrayList<>();
        //首先根据城市分组
        Map<String, List<CarserialsAndSale>> mapBYCity = allLogs.stream().collect(Collectors.groupingBy(CarserialsAndSale::getCity));
        Set<Map.Entry<String, List<CarserialsAndSale>>> entries = mapBYCity.entrySet();
        for (Map.Entry<String, List<CarserialsAndSale>> entry : entries) {
            List<CarserialsAndSale> list2 = entry.getValue();
            //在城市组内根据车型(根据x_ways_id )再分组
            Map<String, List<CarserialsAndSale>> mapBYCS = list2.stream().collect(Collectors.groupingBy(CarserialsAndSale::getCsID));
            Set<Map.Entry<String, List<CarserialsAndSale>>> entries1 = mapBYCS.entrySet();
            for (Map.Entry<String, List<CarserialsAndSale>> entry1 : entries1) {
                List<CarserialsAndSale> list3 = entry1.getValue();
                //最后按月进行分组
                Map<Integer, List<CarserialsAndSale>> mapBYMonth = list3.stream().collect(Collectors.groupingBy(CarserialsAndSale::getYear_month));
                Set<Map.Entry<Integer, List<CarserialsAndSale>>> entries2 = mapBYMonth.entrySet();
                for (Map.Entry<Integer, List<CarserialsAndSale>> entry2 : entries2) {
                    Integer currentMonth = entry2.getKey();
                    List<CarserialsAndSale> lastMonthList = getLastMonthList(currentMonth, mapBYMonth);//获取上月分组数据

                    List<CarserialsAndSale> allYearMonthLog = entry2.getValue();//当前月份的分组数据
                    CarserialsAndSale cLog = new CarserialsAndSale();

                    cLog.setCsID(allYearMonthLog.get(0).getCsID());
                    cLog.setYear_month(currentMonth);
                    cLog.setCity(allYearMonthLog.get(0).getCity());
                    cLog.setCarserial(allYearMonthLog.get(0).getCarserial());
                    cLog.setProvince(allYearMonthLog.get(0).getProvince());

                    int currentMonthVolume = 0; //本月该车型在该地的销量
                    int lastMonthVol = 0;
                    //计算该地，该月份，该车型的增长率
                    for (CarserialsAndSale carserialsAndSale : allYearMonthLog) {
                        currentMonthVolume += carserialsAndSale.getSales_volume();
                    }
                    if (lastMonthList != null) {//如果上月有数据，则算
                        for (CarserialsAndSale carserialsAndSale : lastMonthList) {
                            lastMonthVol += carserialsAndSale.getSales_volume();
                        }
                    }  //增长率为null则上月份缺失

                    if (lastMonthVol > 0) {
                        cLog.setGrowthRateOfMonth(CalculateUtils.div(currentMonthVolume - lastMonthVol, lastMonthVol));
                    }
                    calculateList.add(cLog);
                }
            }
        }
        return calculateList;
    }

    //获取当月比较月份的分组
    private List<CarserialsAndSale> getLastMonthList(Integer curMonth,Map<Integer, List<CarserialsAndSale>> mapBYMonth){

        if(curMonth.toString().endsWith("01")){ //如果是一月份，那就与去年一月比较
            return mapBYMonth.get(curMonth-100);
        }
        return mapBYMonth.get(curMonth-1);
    }
}
