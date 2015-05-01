package com.coupang.pz.hbase.extension.processor.context;

import java.util.List;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class ColumnContext extends  AccessorContext{
    private String family;
    private String qualifier;
    private String declaredTypeName;
    private List<String> typeArguments;
    public ColumnContext(String family, String qualifier, String declaredTypeName, List<String> typeArguments, String typeName, String getterName, String setterName) {
        super(typeName, getterName, setterName);
        this.family = family;
        this.qualifier = qualifier;
        this.declaredTypeName = declaredTypeName;
        this.typeArguments = typeArguments;
    }

    public String getFamily() { return family; }
    public String getQualifier() { return qualifier; }

    public String getDeclaredTypeName() {
        return declaredTypeName;
    }

    public List<String> getTypeArguments() {
        return typeArguments;
    }
}
