package com.coupang.pz.hbase.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The hbase column representation.
 * Each column in hbase is defined by column family(cf) and column name(col).
 * Any Bean annotated with {@link HTableRow} should have zero or more HColumn fields.
 * Each fields is accessed by its accessor method(getter / setter) generated by annotation processor
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface HColumn {
    /***
     * Definition of hbase column family that the column's belonged to.
     * @return String
     */
    String cf();

    /***
     * Definition of hbase column name
     * @return String
     */
    String col();
}
