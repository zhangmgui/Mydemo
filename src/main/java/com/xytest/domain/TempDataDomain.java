package com.xytest.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/5.
 */
@Data
public class TempDataDomain {
    //year_month, province,x_ways_id, carserial,salevolume,carWholeSaleVolume,thisProvinceAllCarVolume,CountryAllCarVolume
    private Integer year_month;
    private String province;
    private String carserial;
    private Integer salevolume;
    private Integer carWholeSaleVolume;
    private Integer thisProvinceAllCarVolume;
    private Integer CountryAllCarVolume;
}
