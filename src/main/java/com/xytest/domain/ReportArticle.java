package com.xytest.domain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/4/28.
 */
@Data
public class ReportArticle {
    private Integer id;
    private String title;
    private String wherefrom;
    private String publishDate;
    private String textContent;
    private String policyRegulationId;
    private String modifydate;
}
