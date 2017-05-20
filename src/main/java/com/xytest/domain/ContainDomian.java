package com.xytest.domain;

import lombok.Data;

/**
 * Created by Administrator on 2017/05/17.
 */
@Data
public class ContainDomian {
    private Integer ID;
    private String name;
    private String age;
    private Double num;

    public ContainDomian() {
    }

    public ContainDomian(Integer ID, String name, String age, Double num) {
        this.ID = ID;
        this.name = name;
        this.age = age;
        this.num = num;
    }

    public ContainDomian(Integer ID, String name, String age) {
        this.ID = ID;
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContainDomian that = (ContainDomian) o;

        if (ID != null ? !ID.equals(that.ID) : that.ID != null) return false;
        return age != null ? age.equals(that.age) : that.age == null;

    }

    @Override
    public int hashCode() {
        int result = ID != null ? ID.hashCode() : 0;
        result = 31 * result + (age != null ? age.hashCode() : 0);
        return result;
    }
}
