package com.coupang.pz.hbase.extension.processor.context;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class AccessorContext {
    private String getterName;
    private String setterName;
    private String typeName;

    public AccessorContext(String typeName, String getterName, String setterName) {
        this.typeName = typeName;
        this.getterName = getterName;
        this.setterName = setterName;

    }

    public String getGetterName(){ return getterName; }
    public String getSetterName() {
        return setterName;
    }
    public String getTypeName() { return typeName; }
}
