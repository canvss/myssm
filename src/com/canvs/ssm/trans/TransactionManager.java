package com.canvs.ssm.trans;

import com.canvs.ssm.utils.JDBCUtils;

import java.sql.Connection;

public class TransactionManager {
    public static void openTrans() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        connection.setAutoCommit(false);
    }
    public static void commit() throws Exception {
        JDBCUtils.getConnection().commit();
        JDBCUtils.closeConnection();
    }
    public static void rollback() throws Exception {
        JDBCUtils.getConnection().rollback();
        JDBCUtils.closeConnection();
    }
}
