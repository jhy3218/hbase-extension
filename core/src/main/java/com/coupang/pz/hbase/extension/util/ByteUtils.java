package com.coupang.pz.hbase.extension.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by samuel281 on 15. 4. 18..
 */
public class ByteUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static byte[] toBytes(Object obj) throws JsonProcessingException {
        if (obj == null) {
           return null;
        }

        if (obj instanceof Long) {
            return Bytes.toBytes((Long) obj);
        } else if (obj instanceof Integer) {
            return Bytes.toBytes((Integer) obj);
        } else if (obj instanceof Double) {
            return Bytes.toBytes((Double) obj);
        } else if (obj instanceof Float) {
            return Bytes.toBytes((Float) obj);
        } else if (obj instanceof String) {
            return Bytes.toBytes((String) obj);
        } else {
            return mapper.writeValueAsBytes(obj);
        }
    }

    public static Object fromBytes(byte[] value, Class<?> type) throws IOException {
        return fromBytes(value, type, new ArrayList<Class>());
    }

    public static Object fromBytes(byte[] value, Class<?> type, List<Class> typeArguments) throws IOException {
        if (value == null) {
            return null;
        }

        if (value.length == 0) {
            return null;
        }

        if(type == Long.class) {
            return Bytes.toLong(value);
        } else if (type == Integer.class) {
            return Bytes.toInt(value);
        } else if (type == Double.class) {
            return Bytes.toDouble(value);
        } else if (type == Float.class) {
            return Bytes.toFloat(value);
        } else if (type == String.class) {
            return Bytes.toString(value);
        } else if (type.isAssignableFrom(List.class) && typeArguments.size() == 1){
            JavaType t = TypeFactory.defaultInstance().constructCollectionType(List.class, typeArguments.get(0));
            return mapper.readValue(value, t);
        } else if (type.isAssignableFrom(Map.class) && typeArguments.size() == 2) {
            JavaType t = TypeFactory.defaultInstance().constructMapType(Map.class, typeArguments.get(0), typeArguments.get(1));
            return mapper.readValue(value, t);
        }
        else {
            JavaType t = TypeFactory.defaultInstance().constructFromCanonical(type.getCanonicalName());
            return mapper.readValue(value, t);
        }
    }
}
