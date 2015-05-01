package com.coupang.pz.hbase.extension.scheme;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class HColumnDef {
    private String family;
    private String qualifier;

    public HColumnDef(String family, String qualifier) {
        this.family = family;
        this.qualifier = qualifier;
    }

    public String getFamily() {
        return family;
    }

    public String getQualifier() {
        return qualifier;
    }
}
