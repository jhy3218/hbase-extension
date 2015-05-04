package com.coupang.pz.hbase.extension;

import com.coupang.pz.hbase.extension.connection.HBaseConnectionFactory;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by samuel281 on 15. 5. 2..
 */
@RunWith(MockitoJUnitRunner.class)
public class HBaseTemplateTest {
    HBaseTemplate template;

    @Mock
    HBaseConnectionFactory hBaseConnectionFactory;

    @Mock
    HConnection hConnection;

    @Mock
    HTableInterface hTableInterface;


    @Before
    public void setUp() throws Exception {
        template = mock(HBaseTemplate.class, CALLS_REAL_METHODS);
        when(hBaseConnectionFactory.create()).thenReturn(hConnection);
        when(hConnection.getTable(anyString())).thenReturn(hTableInterface);
    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testGets() throws Exception {

    }
}