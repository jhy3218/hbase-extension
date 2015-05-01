package com.coupang.pz.hbase.extension.annotation;

/***
 * Define how to convert a rowkey
 */
public enum HRowKeyConversion {
    /***
     * The rowkey will be converted to byte array of bit-reversed long value.
     * Actual rowkey should be instance of {@link java.lang.Long}
     */
    REVERSED_LONG,
    /***
     * The rowkey will be digested by MD5 hash using {@link java.security.MessageDigest}
     */
    MD5,
    /***
     * The rowkey will be converted to plain byte array.
     */
    PLAIN
}
