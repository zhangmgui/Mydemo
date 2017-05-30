package callback;

import com.xytest.domain.MediaParamDomain;
import com.xytest.utils.DBConnection;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2017/05/30.
 */

public class CacheMap {
    private static QueryRunner qr = new QueryRunner(DBConnection.getMasterDataSource());
    public static String cityCarserialQuery = "SELECT\n" +
            "\toutTB.year_month as DimDate,\n" +
            "\toutTB.province,\n" +
            "\toutTB.city as DimArea,\n" +
            "\toutTB.x_ways_id as DimCar,\n" +
            "\toutTB.carserial,\n" +
            "\tSUM(outTB.sales_volume) as saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700\n" +
            "\t) as last_period_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city\n" +
            "\t) as region_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month\n" +
            "\t) as wholeCountry_allDimVal_saleVol,\n" +
            "\t(\n" +
            "\t\tSELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id\n" +
            "\t) as wholeCountry_dimVal_saleVol,\n" +
            "\t(\t\t/*city_thiscar_saleVol*1.0/wholeCountry_thiscar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB4.sales_volume) FROM dbo.city_sales_volume inTB4 WHERE \n" +
            "\t\tinTB4.year_month=outTB.year_month AND inTB4.x_ways_id = outTB.x_ways_id)\n" +
            "\t) as plateVol_proportion,\n" +
            "\t(/*city_thiscar_saleVol*1.0/city_allcar_saleVol*/\n" +
            "\t\tSUM(outTB.sales_volume)*1.0/(SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)\n" +
            "\t) as preference_proportion,\n" +
            "\t( /*city_thiscar_saleVol-last_period_city_thiscar_saleVol/last_period_city_thiscar_saleVol*/\n" +
            "\t\t(SUM(outTB.sales_volume)-(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700))*1.0/(SELECT SUM(inTB1.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB1 WHERE\n" +
            "\t\tinTB1.year_month = outTB.year_month-100 AND inTB1.province =outTB.province AND \n" +
            "\t\tinTB1.x_ways_id=outTB.x_ways_id AND inTB1.city = outTB.city  AND outTB.year_month>201700)\n" +
            "\t\n" +
            "\t) as growth_rate,\n" +
            "\t(/*(city_allcar_saleVol-lastZIJI)*1.0/lastZIJI  */\n" +
            "\t\t((SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)-(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\t inTB5.city = outTB.city  AND outTB.year_month>201700\n" +
            "\t))*1.0/(SELECT SUM(inTB5.sales_volume)\n" +
            "\t\tFROM dbo.city_sales_volume inTB5 WHERE\n" +
            "\t\tinTB5.year_month = outTB.year_month-100 AND inTB5.province =outTB.province AND \n" +
            "\t\t inTB5.city = outTB.city  AND outTB.year_month>201700)\n" +
            "\t) as region_allDimVal_saleVol_growth_rate,\n" +
            "\t(/*city_allcar_saleVol/wholeCountry_allcar_saleVol  */\n" +
            "\t  (SELECT SUM(inTB2.sales_volume) from dbo.city_sales_volume inTB2 WHERE\n" +
            "\t\tinTB2.year_month=outTB.year_month \n" +
            "\t\tand inTB2.province = outTB.province and \n" +
            "\t\tinTB2.city=outTB.city)*1.0/(SELECT SUM(inTB3.sales_volume) from dbo.city_sales_volume inTB3 WHERE\n" +
            "\t\tinTB3.year_month=outTB.year_month)\n" +
            "\t\t\n" +
            "\t) as region_allDimVal_plateVol_proportion,\n" +
            "\t(\n" +
            "\t\t SELECT count(1) from (SELECT province,city FROM dbo.city_sales_volume inTBLast where inTBLast.year_month=outTB.year_month AND inTBLast.province=outTB.province  GROUP BY province,city ) as A\n" +
            "\t\n" +
            "\t) as areaCount\n" +
            "FROM\n" +
            "\tdbo.city_sales_volume outTB\n" +
            "GROUP BY\n" +
            "\tyear_month,\n" +
            "\tprovince,\n" +
            "\tcity,\n" +
            "\tx_ways_id,\n" +
            "\tcarserial order by outTB.year_month DESC\n" +
            "\t";
    private static Map<Integer,Map<String,Map<String,MediaParamDomain>>> cacheMap = new HashMap<Integer,Map<String,Map<String,MediaParamDomain>>>();
    static {
        try {
            List<MediaParamDomain> somedatas = qr.query(cityCarserialQuery, new BeanListHandler<MediaParamDomain>(MediaParamDomain.class));
            Map<Integer, List<MediaParamDomain>> map1 = somedatas.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimDate));
            map1.forEach((date,list1)->{
                Map<String, List<MediaParamDomain>> map2 = list1.stream().collect(Collectors.groupingBy(MediaParamDomain::getDimArea));
                HashMap<String, Map<String,MediaParamDomain>> temMap2 = new HashMap<String, Map<String,MediaParamDomain>>();
                map2.forEach((area,list2)->{
                    Map<String, List<MediaParamDomain>> map3 = list2.stream().collect(Collectors.groupingBy(MediaParamDomain::getCarserial));
                    HashMap<String, MediaParamDomain> temMap3 = new HashMap<String, MediaParamDomain>();
                    map3.forEach((car,list3)->{
                        temMap3.put(car,list3.get(0));
                    });
                    temMap2.put(area,temMap3);
                });
                cacheMap.put(date,temMap2);
            });
          /*  Set<Integer> integers = map1.keySet();
            for (Integer date : integers) {  //时间
                List<MediaParamDomain> list1 = map1.get(date);

                Set<String> strings = map2.keySet();
                HashMap<String, Map<String,MediaParamDomain>> temMap2 = new HashMap<String, Map<String,MediaParamDomain>>();
                for (String area : strings) {  //地区
                    List<MediaParamDomain> list2 = map2.get(area);
                    Map<String, List<MediaParamDomain>> map3 = list2.stream().collect(Collectors.groupingBy(MediaParamDomain::getCarserial));//车型
                    HashMap<String, MediaParamDomain> temMap3 = new HashMap<String, MediaParamDomain>();
                    map3.forEach((k,v)->{
                        temMap3.put(k,v.get(0));
                    });
                    temMap2.put(area,temMap3);
                }
                cacheMap.put(date,temMap2);
            }*/
            System.out.println("缓存完成");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static MediaParamDomain getOneLog(Integer date,String province,String car){
       return  cacheMap.get(date).get(province).get(car);
    }

    public static void main(String[] args){
        long t1 = System.currentTimeMillis();
        MediaParamDomain oneLog = getOneLog(201703, "贵阳市", "宝骏730");
        long t2 = System.currentTimeMillis();
        System.out.println("time consume:" + (t2 - t1));
        System.out.println(oneLog);

    }
}
