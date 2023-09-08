package com.canvs.ssm.utils;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
    public static Connection createConnection() throws Exception {
        InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        Properties ps = new Properties();
        ps.load(is);
        String className = ps.getProperty("className");
        String url = ps.getProperty("url");
        String username = ps.getProperty("username");
        String password = ps.getProperty("password");
        Class.forName(className);
        return DriverManager.getConnection(url, username,password);
    }
    public static Connection getConnection() throws Exception {
        Connection connection = threadLocal.get();
        if (connection == null){
            connection = createConnection();
            threadLocal.set(connection);
        }
        return connection;
    }
    public static void closeConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if (connection.isClosed()){
            connection.close();
            threadLocal.remove();
        }
    }
    public static void closeResource(Connection conn, Statement ps) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            ExceptionUtils.SQLException(e,"JDBCUtils closeResource 关闭connection出错了");
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            ExceptionUtils.SQLException(e,"JDBCUtils closeResource 关闭Statement出错了");
        }
    }

    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            ExceptionUtils.SQLException(e,"JDBCUtils closeResource 关闭connection出错了");
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            ExceptionUtils.SQLException(e,"JDBCUtils closeResource 关闭Statement出错了");
        }
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            ExceptionUtils.SQLException(e,"JDBCUtils closeResource 关闭ResultSet出错了");
        }
    }
}
