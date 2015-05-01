package com.coupang.pz.hbase.extension.processor.context;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class TemplateContext {
    private String packageName;
    private String rowName;
    private String tableName;

    public TemplateContext(String packageName, String rowName, String tableName) {
        this.packageName = packageName;
        this.rowName = rowName;
        this.tableName = tableName;
    }

    public String getPackageName() { return packageName; }

    public String getRowName() { return rowName; }

    public String getTableName() { return tableName; }
}
