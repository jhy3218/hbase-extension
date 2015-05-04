package com.coupang.pz.hbase.extension;

import com.coupang.pz.hbase.extension.scheme.HColumnDef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

/**
 * Created by samuel281 on 15. 5. 2..
 */
public abstract class HBaseConverter<T, K> {
    private byte[] tableName;

    public List<Get> toGets(List<K> keys) throws JsonProcessingException {
        List<Get> gets = Lists.newArrayList();
        for (K key : keys) {
            Get get = toGet(key);
            gets.add(get);
        }

        return gets;
    }

    public T toRow(Result result, K k) throws NoSuchFieldException, IOException {
        T row = getRowInstance();
        applyRowKey(row, k);

        List<HColumnDef> columns = getColumns();
        for (HColumnDef column : columns) {
            byte[] family = Bytes.toBytes(column.getFamily());
            byte[] qualifier = Bytes.toBytes(column.getQualifier());
            byte[] value = result.getValue(family, qualifier);

            applyColumn(row, column.getFamily(), column.getQualifier(), value);
        }
        return row;
    }

    public abstract Get toGet(K key) throws JsonProcessingException;

    public abstract String getTableName();

    protected abstract void applyColumn(T row, String family, String qualifier, byte[] value) throws IOException;

    protected abstract List<HColumnDef> getColumns() throws NoSuchFieldException;

    protected abstract void applyRowKey(T row, K k);

    protected abstract T getRowInstance();
}
