package com.coupang.pz.hbase.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * Annotation for the hbase table row repesentation.
 * Only class can be annotated with HTableRow.
 * Any class annotated with HTable should have one field that annotated with {@link com.coupang.pz.hbase.extension.annotation.HRowKey}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface HTableRow {
    /***
     * plain text name of the hbase table
     * @return table name
     */
    String of();
}
