package com.coupang.pz.hbase.extension.bundle;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class TRow {
    Long rowKey;
    public Long getRowKey() { return rowKey; }
    public void setRowKey(Long rowKey) {this.rowKey = rowKey;}

    private String stringCol;
    public String getStringCol() { return stringCol;}
    public void setStringCol(String stringCol) { this.stringCol = stringCol; }

    private Pojo pojo;
    public Pojo getPojo() { return pojo; }
    public void setPojo(Pojo pojo) { this.pojo = pojo; }

    private List<Pojo> pojos;
    public List<Pojo> getPojos() {return pojos;}
    public void setPojos(List<Pojo> pojos) {this.pojos = pojos;}

    private Map<Long, Pojo> pojoMap;
    public Map<Long, Pojo> getPojoMap() {return pojoMap; }
    public void setPojoMap(Map<Long, Pojo> pojoMap) {this.pojoMap = pojoMap;}
}
