package com.coupang.pz.hbase.extension.connection;

import org.apache.hadoop.hbase.client.HConnection;

import java.io.IOException;

/**
 * Created by samuel281 on 15. 4. 18..
 */
public interface HBaseConnectionFactory {
    HConnection create() throws IOException;
}
