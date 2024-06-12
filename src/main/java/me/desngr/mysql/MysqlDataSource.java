package me.desngr.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.desngr.ShiroEconomy;
import me.desngr.util.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.*;

public class MysqlDataSource {

    private static final HikariConfig config = new HikariConfig();
    @Getter
    private static final HikariDataSource dataSource;
    @Getter
    private static final Type type;

    static {
        if (ShiroEconomy.getApi().getPlugin()
                .getConfig().getBoolean("mysql.enabled")) {
            config.setJdbcUrl(ShiroEconomy.getApi().getPlugin()
                    .getConfig().getString("mysql.url"));
            config.setUsername(ShiroEconomy.getApi().getPlugin()
                    .getConfig().getString("mysql.user"));
            config.setPassword(ShiroEconomy.getApi().getPlugin()
                    .getConfig().getString("mysql.password"));

            type = Type.MYSQL;
        } else {
            String url = "jdbc:sqlite:" +
                    new File(ShiroEconomy.getApi().getPlugin().getDataFolder(), "database.db")
                            .getAbsolutePath();

            try {
                Connection connection = DriverManager.getConnection(url);

                if (connection != null) {
                    connection.getMetaData();
                }

                connection.close();
            } catch (SQLException e) {
                new RuntimeException(e);
            }

            config.setJdbcUrl(url);

            type = Type.SQLITE;
        }

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private MysqlDataSource() {}

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            Logger.error("Error while getting DataSource connection");

            throw new RuntimeException("Error while getting DataSource connection");
        }
    }

    public static int getGlobalConnections() {
        if (type == Type.SQLITE)
            return getLocalConnections() != 0 ? 1 : 0;

        try {
            PreparedStatement statement = MysqlDataSource.getConnection()
                    .prepareStatement("SELECT COUNT(DISTINCT SUBSTRING_INDEX(host, ':', 1)) as ConCount FROM INFORMATION_SCHEMA.PROCESSLIST;");
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            return resultSet.getInt("ConCount");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getLocalConnections() {
        return getPool().getTotalConnections();
    }

    private static HikariPool getPool() {
        try {
            Field field = dataSource.getClass().getDeclaredField("pool");

            field.setAccessible(true);

            return (HikariPool) field.get(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Reflection error while getting HikariPool");
        }
    }

    public enum Type {
        MYSQL, SQLITE;
    }
}
