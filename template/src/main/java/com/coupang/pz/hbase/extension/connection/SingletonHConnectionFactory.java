package com.coupang.pz.hbase.extension.connection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;

/**
 * Created by samuel281 on 15. 4. 18..
 */
public class SingletonHConnectionFactory implements HBaseConnectionFactory{
    private Configuration hbaseConf;
    private HConnection hConnection;

    public SingletonHConnectionFactory(Configuration hbaseConf) {
        this.hbaseConf = hbaseConf;
    }

    public HConnection create() throws ZooKeeperConnectionException {
        return getHConnection();
    }

    Configuration createConfiguration() {
        return HBaseConfiguration.create();
    }

    HConnection fromManager(Configuration hbaseConf) throws ZooKeeperConnectionException {
        return HConnectionManager.createConnection(hbaseConf);
    }

    private synchronized HConnection getHConnection() throws ZooKeeperConnectionException {
        if (hbaseConf == null) {
            hbaseConf = createConfiguration();
        }

        if (hConnection == null || hConnection.isClosed()) {
            hConnection = fromManager(hbaseConf);
        }

        return hConnection;
    }
}
