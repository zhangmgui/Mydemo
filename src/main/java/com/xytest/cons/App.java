package com.xytest.cons;

import com.xytest.domain.ContainDomian;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/05/17.
 */
public class App {
    public static  void main(String[] args){
        ContainDomian containDomian = new ContainDomian(1,null,null,null);
        ContainDomian containDomian1 = new ContainDomian(1,"李四","12");
        ContainDomian containDomian2 = new ContainDomian(1,"王五","13");

        ArrayList<ContainDomian> list = new ArrayList<>();
        list.add(containDomian);
        list.add(containDomian1);
        list.add(containDomian2);
        ContainDomian test = new ContainDomian(1,null,null,12d);
        System.out.println(list.contains(test));

    }
}
