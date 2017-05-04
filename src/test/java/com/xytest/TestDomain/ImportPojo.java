package com.xytest.TestDomain;

/**
 * Created by zhangmg on 2017/4/25.
 */
public class ImportPojo {
    private String county_name;
    private String province_name;
    private String year;
    private String finanInc;
    private String taxInc;

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFinanInc() {
        return finanInc;
    }

    public void setFinanInc(String finanInc) {
        this.finanInc = finanInc;
    }

    public String getTaxInc() {
        return taxInc;
    }

    public void setTaxInc(String taxInc) {
        this.taxInc = taxInc;
    }

    @Override
    public String toString() {
        return "ImportPojo{" +
                "county_name='" + county_name + '\'' +
                ", province_name='" + province_name + '\'' +
                ", year='" + year + '\'' +
                ", finanInc='" + finanInc + '\'' +
                ", taxInc='" + taxInc + '\'' +
                '}';
    }
}
