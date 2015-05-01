package com.coupang.pz.hbase.extension;

import com.coupang.pz.hbase.extension.connection.HBaseConnectionFactory;
import com.coupang.pz.hbase.extension.exception.HBaseExtensionException;
import com.coupang.pz.hbase.extension.scheme.HColumnDef;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.List;

/**
 * Created by samuel281 on 15. 4. 29..
 */
public abstract class HBaseTemplate<T, K> {
    private final HBaseConnectionFactory connectionFactory;
    private final Function<K, Get> transKey = new Function<K, Get>() {
        public Get apply(K k) {
            Get get;
            try {
                get = toGet(k);
            } catch (Exception e) {
                get = new Get();
            }

            return get;
        }
    };

    public HBaseTemplate(HBaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public T get(K rowKey) throws IOException, HBaseExtensionException, NoSuchFieldException {
       List<T> rows = gets(Lists.newArrayList(rowKey));
        if (rows.isEmpty()) {
            return null;
        }

        return rows.get(0);
    }

    public List<T> gets(List<K> keys) throws IOException, HBaseExtensionException, NoSuchFieldException {
        List<T> rows = Lists.newArrayList();

        if (keys == null) {
            return rows;
        }

        if (keys.isEmpty()) {
            return rows;
        }

        List<Get> gets = toGets(keys);
        HTableInterface hTable = connectionFactory.create().getTable(getTableName());
        Result[] results = hTable.get(gets);
        if (results == null) {
            return rows;
        }

        if (results.length == 0) {
            return rows;
        }

        if (keys.size() != results.length) {
            throw new HBaseExtensionException("Inconsistent result. Required key size doesn't match to result length.");
        }

        for (int i = 0; i < results.length; i++) {
            T row = toRow(results[i], keys.get(i));
            if (row == null) {
                continue;
            }

            rows.add(row);
        }

        return rows;
    }

    private T toRow(Result result, K k) throws NoSuchFieldException, IOException {
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

    protected abstract void applyColumn(T row, String family, String qualifier, byte[] value) throws IOException;

    protected abstract void applyRowKey(T row, K k);

    protected abstract List<HColumnDef> getColumns() throws NoSuchFieldException;

    protected abstract T getRowInstance();


    protected List<Get> toGets(List<K> keys) {
        return Lists.transform(keys, transKey);
    }

    protected abstract Get toGet(K k) throws JsonProcessingException;

    protected abstract String getTableName();

}
