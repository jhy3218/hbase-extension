package com.coupang.pz.hbase.extension.bundle;

import com.coupang.pz.hbase.extension.HBaseTemplate;
import com.coupang.pz.hbase.extension.connection.HBaseConnectionFactory;
import com.coupang.pz.hbase.extension.scheme.HColumnDef;
import com.coupang.pz.hbase.extension.util.ByteUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Get;

import java.io.IOException;
import java.util.List;

public class TRowTemplate extends HBaseTemplate<TRow, Long> {
    private final List<HColumnDef> columns = initColumnDef();

    private static List<HColumnDef> initColumnDef() {
        List<HColumnDef> columns = Lists.newArrayList();
                columns.add(new HColumnDef("cf1", "col1"));
                columns.add(new HColumnDef("cf1", "col2"));
                columns.add(new HColumnDef("cf1", "col3"));
                columns.add(new HColumnDef("cf1", "col4"));
                return columns;
    }

    public TRowTemplate(HBaseConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    @Override
    protected void applyColumn(TRow row, String cf, String col, byte[] value) throws IOException {
                                if (cf.equals("cf1") && col.equals("col1")) {
                            List<Class> typeArguments = Lists.newArrayList();

            row.setStringCol((String)ByteUtils.fromBytes(
                value,
                String.class,
                typeArguments
            ));
        }
                        else if (cf.equals("cf1") && col.equals("col2")) {
                    List<Class> typeArguments = Lists.newArrayList();

            row.setPojo((Pojo)ByteUtils.fromBytes(
                value,
                Pojo.class,
                typeArguments
            ));
        }
                        else if (cf.equals("cf1") && col.equals("col3")) {
                    List<Class> typeArguments = Lists.newArrayList();

            row.setPojos((List<Pojo>)ByteUtils.fromBytes(
                value,
                List.class,
                typeArguments
            ));
        }
                        else if (cf.equals("cf1") && col.equals("col4")) {
                    List<Class> typeArguments = Lists.newArrayList();

            row.setPojoMap((java.util.Map<Long,Pojo>)ByteUtils.fromBytes(
                value,
                java.util.Map.class,
                typeArguments
            ));
        }
            }

    @Override
    protected void applyRowKey(TRow row, Long rowKey) {
        if (row == null) {
            return;
        }

        row.setRowKey(rowKey);
    }

    @Override
    protected List<HColumnDef> getColumns() throws NoSuchFieldException {
        return columns;
    }

    @Override
    protected TRow getRowInstance() {
        return new TRow();
    }

    @Override
    protected Get toGet(Long rowKey) throws JsonProcessingException {
        return new Get(ByteUtils.toBytes(rowKey));
    }

    @Override
    protected String getTableName() {
        return "TestTable";
    }
}