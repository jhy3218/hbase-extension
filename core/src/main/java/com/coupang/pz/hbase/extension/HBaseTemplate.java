package com.coupang.pz.hbase.extension;

import com.coupang.pz.hbase.extension.connection.HBaseConnectionFactory;
import com.coupang.pz.hbase.extension.exception.HBaseExtensionException;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import java.io.IOException;
import java.util.List;

/**
 * Created by samuel281 on 15. 4. 29..
 */
public abstract class HBaseTemplate<T, K> {
    private final HBaseConnectionFactory connectionFactory;
    private final HBaseConverter<T, K> converter = initConverter();

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

        List<Get> gets = converter.toGets(keys);
        HTableInterface hTable = connectionFactory.create().getTable(converter.getTableName());
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
            T row = converter.toRow(results[i], keys.get(i));
            if (row == null) {
                continue;
            }

            rows.add(row);
        }

        return rows;
    }

    protected abstract HBaseConverter<T,K> initConverter();
}
