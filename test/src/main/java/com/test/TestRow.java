package com.test;

import com.coupang.pz.hbase.extension.annotation.HColumn;
import com.coupang.pz.hbase.extension.annotation.HRowKey;
import com.coupang.pz.hbase.extension.annotation.HTableRow;

/**
 * Created by samuel281 on 15. 5. 2..
 */
@HTableRow(of = "pz.profile_user")
public class TestRow {
    @HRowKey
    private Long memberSrl;

    public Long getMemberSrl() {
        return memberSrl;
    }

    public void setMemberSrl(Long memberSrl) {
        this.memberSrl = memberSrl;
    }

    @HColumn(cf = "bt", col="age")
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
