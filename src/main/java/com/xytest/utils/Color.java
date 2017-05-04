package com.xytest.utils;

/**
 * Created by zhangmg on 2017/4/26.
 */

public enum Color {
    RED("红色",1,"颜色"),GREEN("绿色",2,"颜料"),BLACK(1);
    private String name;
    private int index;
    private String typeName;

    private Color(String name, int index,String typeName){
        this.name = name;
        this.index = index;
        this.typeName = typeName;
    }

    Color(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
