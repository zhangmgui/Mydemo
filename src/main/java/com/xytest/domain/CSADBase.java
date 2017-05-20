package com.xytest.domain;

import lombok.Data;

/**
 * Created by Administrator on 2017/05/07.
 */
@Data
public class CSADBase {
    private Integer ID;
    private Integer year_month;
    private String carserial;
    private String x_ways_id;
    private String province;
    private Double province_thiscar_plate_volume_proportion;
    private Double wholecountry_thiscar_plate_volume_proportion_average;
    private Double province_allcar_plate_proportion;
    private Integer thisMonthAreaCount;
    private Double province_thiscar_growth_rate;
    private Double province_allcar_salevolume_growth_rate;
    private Double province_thiscar_preference_proportion;
    private Integer province_thiscar_plate_volume_index;
    private Integer province_thiscar_preference_index;
    private Integer province_thiscar_growth_index;
    private Integer province_allcar_platevolume_index;
    private Integer province_main_carCount_thisMonth;
}
