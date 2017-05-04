package com.xytest.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/4.
 */
@Data
public class CarserialsAndSale {
    private String csID;
    private Integer year_month;
    private String province;
    private String city;
    private String carserial;
    private String carbrand;
    private Integer sales_volume;
    private Double growthRateOfMonth;
}
