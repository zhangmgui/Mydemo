package com.xytest.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/10.
 */
@Data
public class MediaParamDomain {
    private Integer ID;
    private Integer DimDate;
    private String DimCar; // 产品维度 x_ways_id||carbrand||manufacturer
    private String carserial; //车型独有的
    private String DimArea; // province||city
    private String province; //城市独有的字段

    //中间参数
    private Integer saleVol;
    private Integer last_period_saleVol;
    private Integer region_allDimVal_saleVol;
    private Integer wholeCountry_allDimVal_saleVol;
    private Integer wholeCountry_dimVal_saleVol;
    private Double plateVol_proportion;
    private Double preference_proportion;
    private Double growth_rate;
    private Double region_allDimVal_saleVol_growth_rate;
    private Double region_allDimVal_plateVol_proportion;
    private Integer areaCount;
    //程序算
    private Integer region_main_dimValCount;
    private Double wholecountry_dimVal_plateVol_proportion_avg;

    //结果参数
    //四个指数
    private Integer plateVol_index;
    private Integer preference_index;
    private Integer growth_index;
    private Integer region_allDimVal_plateVol_index;
    //四个指数对应的级别
    private Integer preference_level;
    private Integer plateVol_level;
    private Integer growth_level;
    private Integer region_allDimVal_plateVol_level;
}
