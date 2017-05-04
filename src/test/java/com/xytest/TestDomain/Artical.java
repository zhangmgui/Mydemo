package com.xytest.TestDomain;

import lombok.Data;

/**
 * Created by zhangmg on 2017/4/27.
 */
@Data
public class Artical {
    private String bigType;
    private String url;
    private String articalName;
    private String date;

    @Override
    public String toString() {
        return articalName+"\t"+date+"\t"+url;
    }
}
