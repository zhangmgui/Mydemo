package com.xytest.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/5/5.
 */
@Data
public class CarSaleAnalysisDomain extends CSADBase{
    private Integer province_thiscar_sales_volume;
    private Integer wholecountry_thiscar_sales_volume;
    private Integer province_allcar_sale_volume;
    private Integer last_period_thiscar_sale_volume;
    private Integer wholecountry_allcar_sale_volume;
}
/*public class CarSaleAnalysisDomain {
    private Integer ID;
    private Integer year_month;
    private String carserial;
    private String x_ways_id;
    private String province;
    private Integer province_thiscar_sales_volume;
    private Integer wholecountry_thiscar_sales_volume;
    private Double province_thiscar_plate_volume_proportion;
    private Integer province_allcar_sale_volume;
    private Double province_thiscar_preference_proportion;
    private Double province_thiscar_growth_rate;
    private Integer last_period_thiscar_sale_volume;
    private Integer wholecountry_allcar_sale_volume;
    private Double province_allcar_plate_proportion;
    private Double wholecountry_thiscar_plate_volume_proportion_average;
    private Integer province_thiscar_plate_volume_index;
    private Integer province_main_carCount_thisMonth;
    private Integer province_thiscar_preference_index;
    private Double province_allcar_salevolume_growth_rate;
    private Integer province_thiscar_growth_index;
    private Integer thisMonthAreaCount;
    private Integer province_allcar_platevolume_index;
}*/

