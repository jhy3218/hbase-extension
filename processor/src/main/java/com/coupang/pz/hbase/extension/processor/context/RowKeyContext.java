package com.coupang.pz.hbase.extension.processor.context;

import com.coupang.pz.hbase.extension.annotation.HRowKeyConversion;

/**
 * Created by samuel281 on 15. 5. 1..
 */
public class RowKeyContext extends AccessorContext {
    private HRowKeyConversion conversion;

    public RowKeyContext(HRowKeyConversion conversion, String typeName, String getterName, String setterName) {
        super(typeName, getterName, setterName);
        this.conversion = conversion;
    }
}
