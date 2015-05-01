package com.coupang.pz.hbase.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * HBase table's rowkey representation.
 * Only fields can be annotated with this annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface HRowKey {
    /***
     * Definition of how to convert the actual rowkey field into hbase rowkey representation.
     * @return HRowKetConversion
     */
    HRowKeyConversion conversion() default HRowKeyConversion.PLAIN;
}
