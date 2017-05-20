package com.xytest.carSerials;

import com.xytest.domain.CSADBase;
import com.xytest.domain.CarSaleAnalysisDomain;
import com.xytest.utils.CalculateUtils;
import com.xytest.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zhangmg on 2017/5/5.
 */
public class ProvinceMonthAnalysis {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());

    @Test
    public void analysisProvince() throws Exception {

        String someDataSQL = "select year_month, province,x_ways_id, carserial,sum(sales_volume) as province_thiscar_sales_volume,(\n" +
                "select sum(sales_volume) from  city_sales_volume as csvIN where  csvIN.year_month = csvOUT.year_month and csvIN.x_ways_id = csvOUT.x_ways_id\n" +
                ") as wholecountry_thiscar_sales_volume,(select sum(sales_volume) from city_sales_volume as csv3 where csv3.year_month = csvOUT.year_month and csv3.province = csvOUT.province ) as province_allcar_sale_volume\n" +
                ",(select sum(sales_volume) from  city_sales_volume as csv4 where csv4.year_month = csvOUT.year_month ) as wholecountry_allcar_sale_volume\n" +
                "\n" +
                "from  city_sales_volume as csvOUT group by  year_month, province, carserial, x_ways_id ";

        long start = System.currentTimeMillis();
        List<CarSaleAnalysisDomain> tempDataDomains = qr.query(someDataSQL, new BeanListHandler<CarSaleAnalysisDomain>(CarSaleAnalysisDomain.class));
        long end = System.currentTimeMillis();
        System.out.println("查询消耗：" + ((end - start) / (1000)) + "秒");
       /* System.out.println("查询消耗：" + (end - start) + "ms");*/

        Map<Integer, List<CarSaleAnalysisDomain>> yearMonthKeyMap = groupByMonth(tempDataDomains);

        for (CarSaleAnalysisDomain tempDataDomain : tempDataDomains) {
            //当地该车型盘量占比
            tempDataDomain.setProvince_thiscar_plate_volume_proportion(CalculateUtils.div(tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getWholecountry_thiscar_sales_volume()));
            //当地该车型偏好占比
            tempDataDomain.setProvince_thiscar_preference_proportion(CalculateUtils.div(tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getProvince_allcar_sale_volume()));

          /*  tempDataDomain.setThiscar_province_plate_volume_proportion(CalculateUtils.div(tempDataDomain.getThiscar_province_sales_volume(),
                    tempDataDomain.getThiscar_wholecountry_sales_volume()));
            //当地该车型偏好占比
            tempDataDomain.setProvince_thiscar_preference_proportion(CalculateUtils.div(tempDataDomain.getThiscar_province_sales_volume(),
                    tempDataDomain.getAllcar_province_sale_volume()));*/

            //当地该车型增长率
            //1、上一期该车型当地销量
            Integer lastMonthThisCarVolume = null;
            Integer lastMonthAllCarInThisProvinceVolume = null;
            Map<String, Integer> lastMonthLocalALLSaleAndOneSaleMap = getLastMonthSaleVolume(tempDataDomain.getYear_month(), tempDataDomain.getProvince(), tempDataDomain.getX_ways_id(), yearMonthKeyMap);
            if (null != lastMonthLocalALLSaleAndOneSaleMap) {
                lastMonthThisCarVolume = lastMonthLocalALLSaleAndOneSaleMap.get("lastMonthThisCar");
                lastMonthAllCarInThisProvinceVolume = lastMonthLocalALLSaleAndOneSaleMap.get("lastMonthAllCar");
            }

            if (null != lastMonthThisCarVolume && 0 != lastMonthThisCarVolume) {
                tempDataDomain.setLast_period_thiscar_sale_volume(lastMonthThisCarVolume);                 //getThiscar_province_sales_volume
                tempDataDomain.setProvince_thiscar_growth_rate(CalculateUtils.div(CalculateUtils.sub(tempDataDomain.getProvince_thiscar_sales_volume(),
                        lastMonthThisCarVolume), lastMonthThisCarVolume));
            }
            if (null != lastMonthAllCarInThisProvinceVolume && 0 != lastMonthAllCarInThisProvinceVolume) {
                try {
                    //当地汽车销量增长率
                    double allCarRate = CalculateUtils.div(CalculateUtils.sub(tempDataDomain.getProvince_allcar_sale_volume(), lastMonthAllCarInThisProvinceVolume), lastMonthAllCarInThisProvinceVolume);
                    tempDataDomain.setProvince_allcar_salevolume_growth_rate(allCarRate);
                } catch (Exception e) {
                    System.out.println(lastMonthAllCarInThisProvinceVolume);
                    System.out.println(tempDataDomain.toString());
                }
            }

            //当地所有车型盘量占比
            tempDataDomain.setProvince_allcar_plate_proportion(CalculateUtils.div(tempDataDomain.getProvince_allcar_sale_volume(),
                    tempDataDomain.getWholecountry_allcar_sale_volume()));

        }
        long end1 = System.currentTimeMillis();

        System.out.println("中间数据处理消耗：" + ((end1 - end) / (1000)) + "秒");
        insertData(tempDataDomains);
        long end2 = System.currentTimeMillis();
        System.out.println("中间数据入库消耗：" + ((end2 - end1) / (1000)) + "秒");

        System.out.println("计算四个指数开始");
        List<CSADBase> csadBases = calculateFourIndex();
        long end3 = System.currentTimeMillis();
        System.out.println("指数计算所需中间参数查询耗时:" + ((end3 - end2) / (1000)) + "秒");
        updateFourIndexIntoDB(csadBases);
        long end4 = System.currentTimeMillis();
        System.out.println("更新四个指数耗时:" + ((end4 - end3) / (1000)) + "秒");
        System.out.println("整个处理过程耗时:" + ((end4 - start) / (1000)) + "秒");
    }

    private void updateFourIndexIntoDB(List<CSADBase> csadBases) throws SQLException {
        String updateSQL = "UPDATE" +
                " bi.dbo.province_month_sales_analysis" +
                " SET" +
                " province_thiscar_preference_proportion = ?,\n" +

                "wholecountry_thiscar_plate_volume_proportion_average = ?,\n" +
                "province_thiscar_plate_volume_index = ?,\n" +
                "province_main_carCount_thisMonth = ?,\n" +
                "province_thiscar_preference_index = ?,\n" +

                "province_thiscar_growth_index = ?,\n" +
                "thisMonthAreaCount = ?,\n" +
                "province_allcar_platevolume_index = ? where ID = ?;";
        for (CSADBase csadBase : csadBases) {
            qr.update(updateSQL,
                    csadBase.getProvince_thiscar_preference_proportion(),
                    csadBase.getWholecountry_thiscar_plate_volume_proportion_average(),
                    csadBase.getProvince_thiscar_plate_volume_index(),
                    csadBase.getProvince_main_carCount_thisMonth(),
                    csadBase.getProvince_thiscar_preference_index(),
                    csadBase.getProvince_thiscar_growth_index(),
                    csadBase.getThisMonthAreaCount(),
                    csadBase.getProvince_allcar_platevolume_index(),
                    csadBase.getID()
                    );
        }

    }

    //中间数据入库
    private void insertData(List<CarSaleAnalysisDomain> tempDataDomains) throws SQLException {
        String inertSQL = "insert into province_month_sales_analysis(" +
                "year_month," +
                "carserial," +
                "x_ways_id," +
                "province," +
                "province_thiscar_sales_volume," +
                "wholecountry_thiscar_sales_volume," +
                "province_thiscar_plate_volume_proportion," +
                "province_allcar_sale_volume," +
                "province_thiscar_preference_proportion," +
                "province_thiscar_growth_rate," +
                "last_period_thiscar_sale_volume," +
                "wholecountry_allcar_sale_volume," +
                "province_allcar_plate_proportion," +
                "province_allcar_salevolume_growth_rate)  \n" +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for (CarSaleAnalysisDomain tempDataDomain : tempDataDomains) {

            qr.update(inertSQL, tempDataDomain.getYear_month(),
                    tempDataDomain.getCarserial(),
                    tempDataDomain.getX_ways_id(),
                    tempDataDomain.getProvince(),
                    tempDataDomain.getProvince_thiscar_sales_volume(),
                    tempDataDomain.getWholecountry_thiscar_sales_volume(),
                    tempDataDomain.getProvince_thiscar_plate_volume_proportion(),
                    tempDataDomain.getProvince_allcar_sale_volume(),
                    tempDataDomain.getProvince_thiscar_preference_proportion(),
                    tempDataDomain.getProvince_thiscar_growth_rate(),
                    tempDataDomain.getLast_period_thiscar_sale_volume(),
                    tempDataDomain.getWholecountry_allcar_sale_volume(),
                    tempDataDomain.getProvince_allcar_plate_proportion(),
                    tempDataDomain.getProvince_allcar_salevolume_growth_rate());

        }
    }


    //获取上一期当地该车型的销量和该地所有车型的销量
    private Map<String, Integer> getLastMonthSaleVolume(Integer curMonth, String province, String x_ways_id, Map<Integer, List<CarSaleAnalysisDomain>> mapBYMonth) {
        Integer lastMonth = null;
        if (curMonth.toString().endsWith("01")) { //如果是一月份，那就与去年一月比较
            lastMonth = curMonth - 100;
            return lastMonthScan(lastMonth, province, x_ways_id, mapBYMonth);
        } else {
            lastMonth = curMonth - 1;
            Map<String, Integer> lastIntegerMap = lastMonthScan(lastMonth, province, x_ways_id, mapBYMonth);
            if (lastIntegerMap == null) {
                lastIntegerMap = lastMonthScan(curMonth - 100, province, x_ways_id, mapBYMonth);
            }
            return lastIntegerMap;
        }

    }

    //根据月份分组
    public Map<Integer, List<CarSaleAnalysisDomain>> groupByMonth(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getYear_month));
    }

    //根据省份分组
    public Map<String, List<CarSaleAnalysisDomain>> groupByProvince(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getProvince));
    }

    //根据车型分组
    public Map<String, List<CarSaleAnalysisDomain>> groupByCS(List<CarSaleAnalysisDomain> list) {
        return list.stream().collect(Collectors.groupingBy(CarSaleAnalysisDomain::getX_ways_id));
    }

    //计算上一期本地所有车型销量和上个月该车型销量
    public Map<String, Integer> lastMonthScan(Integer lastMonth, String province, String x_ways_id, Map<Integer, List<CarSaleAnalysisDomain>> mapBYMonth) {
        Integer lastMonthSV = 0;//当地该车型上个月
        Integer allCarInThisProvinceLastMonthSV = 0;
        HashMap<String, Integer> lastMonthLocalALLSaleAndOneSaleMap = new HashMap<>();
        List<CarSaleAnalysisDomain> lastMonthList = mapBYMonth.get(lastMonth);
        if (null == lastMonthList) {
            return null;
        } else {
            for (CarSaleAnalysisDomain carSaleAnalysisDomain : lastMonthList) {
                if (province.equals(carSaleAnalysisDomain.getProvince())) {
                    allCarInThisProvinceLastMonthSV += carSaleAnalysisDomain.getProvince_thiscar_sales_volume();
                }
                if (carSaleAnalysisDomain.getProvince().equals(province) && carSaleAnalysisDomain.getX_ways_id().equals(x_ways_id)) {
                    lastMonthSV += carSaleAnalysisDomain.getProvince_thiscar_sales_volume();
                }
            }
            lastMonthLocalALLSaleAndOneSaleMap.put("lastMonthThisCar", lastMonthSV);
            lastMonthLocalALLSaleAndOneSaleMap.put("lastMonthAllCar", allCarInThisProvinceLastMonthSV);
            return lastMonthLocalALLSaleAndOneSaleMap;
        }
    }


    public  List<CSADBase>  calculateFourIndex() throws SQLException {
        String secondSQL = "select ID,year_month,x_ways_id,carserial,province,province_thiscar_plate_volume_proportion,(select avg(inansis1.province_thiscar_plate_volume_proportion) from\n" +
                "dbo.province_month_sales_analysis inansis1 where inansis1.year_month = OUTAna.year_month and inansis1.x_ways_id = OUTAna.x_ways_id) as wholecountry_thiscar_plate_volume_proportion_average,\n" +
                "(SELECT max(inansis3.province_allcar_plate_proportion) from dbo.province_month_sales_analysis inansis3 where inansis3.year_month = OUTAna.year_month\n" +
                " AND inansis3.province = OUTAna.province ) as province_allcar_plate_proportion,(select count( 1 )from\n" +
                " (select inansis2.province from dbo.province_month_sales_analysis inansis2 where inansis2.year_month = OUTAna.year_month group by\n" +
                "inansis2.province ) as temp ) as thisMonthAreaCount, province_thiscar_growth_rate,(select min(province_allcar_salevolume_growth_rate) from dbo.province_month_sales_analysis as inansis4 where inansis4.year_month=OUTAna.year_month\n" +
                "and inansis4.province=OUTAna.province ) as province_allcar_salevolume_growth_rate,province_thiscar_preference_proportion from dbo.province_month_sales_analysis as OUTAna";
        List<CSADBase> tempDomains = qr.query(secondSQL, new BeanListHandler<CSADBase>(CSADBase.class));
        Map<Integer, List<CSADBase>> map1 = tempDomains.stream().collect(Collectors.groupingBy(CSADBase::getYear_month));
        Set<Map.Entry<Integer, List<CSADBase>>> yearkeyEntrys = map1.entrySet();
        for (Map.Entry<Integer, List<CSADBase>> entry1 : yearkeyEntrys) {
            List<CSADBase> CSADS = entry1.getValue(); //每个时间一组
            Map<String, List<CSADBase>> provinceKeyMap = CSADS.stream().collect(Collectors.groupingBy(CSADBase::getProvince));
            Set<Map.Entry<String, List<CSADBase>>> entr2 = provinceKeyMap.entrySet();
            for (Map.Entry<String, List<CSADBase>> entry2 : entr2) {
                List<CSADBase> CSADS2 = entry2.getValue(); //时间组中再按省份分组
                CSADS2.sort(new Comparator<CSADBase>() {
                    @Override
                    public int compare(CSADBase o1, CSADBase o2) {
                        return o1.getProvince_thiscar_preference_proportion().compareTo(o2.getProvince_thiscar_preference_proportion());
                    }
                });
                Collections.reverse(CSADS2);
                Integer mainCarCount = 0; //贡献率前百分之95的车型个数
                Double per = 0d; //控制0.95的标记
                for (CSADBase csadBase : CSADS2) {
                    if (CalculateUtils.add(per, csadBase.getProvince_thiscar_preference_proportion()) < 0.95) {
                        per = CalculateUtils.add(per, csadBase.getProvince_thiscar_preference_proportion());
                        mainCarCount++;
                    } else {
                        break;
                    }
                }
                for (CSADBase tempDomain : CSADS2) {
                    //1、计算当地该车型盘量指数   （指数为整形）
                    double index1 = CalculateUtils.div(tempDomain.getProvince_thiscar_plate_volume_proportion(), tempDomain.getWholecountry_thiscar_plate_volume_proportion_average());
                    tempDomain.setProvince_thiscar_plate_volume_index(Integer.valueOf(CalculateUtils.formatDouble4(index1 * 100)));
                    //2、计算当地该车型偏好指数
                    tempDomain.setProvince_main_carCount_thisMonth(mainCarCount);
                    String index2 = CalculateUtils.formatDouble4(CalculateUtils.mul(tempDomain.getProvince_thiscar_preference_proportion(), tempDomain.getProvince_main_carCount_thisMonth()));
                    tempDomain.setProvince_thiscar_preference_index(Integer.valueOf(index2));
                    //3、当地该车型增长率指数
                    Double thiscarGR = tempDomain.getProvince_thiscar_growth_rate();
                    Double allcarGR = tempDomain.getProvince_allcar_salevolume_growth_rate();
                    if (null != thiscarGR && null != allcarGR && allcarGR != 0) {
                        tempDomain.setProvince_thiscar_growth_index(Integer.valueOf(CalculateUtils.formatDouble4(CalculateUtils.div(thiscarGR, allcarGR) * 100)));
                    }
                    //4、当地所有车型盘量指数
                    Double allCarPP = tempDomain.getProvince_allcar_plate_proportion();
                    Integer areaCount = tempDomain.getThisMonthAreaCount();
                    tempDomain.setProvince_allcar_platevolume_index(Integer.valueOf(CalculateUtils.formatDouble4(allCarPP * areaCount)));
                }
            }

        }
        return tempDomains;
    }
}
