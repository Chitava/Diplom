package chitava.diplom.services;


import chitava.diplom.models.WorkedHours;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JDBCService {
    private final String URL = "jdbc:mysql://localhost:3306/mysql";
    private static final String USER = "root";
    private static final String PASS = "Vch32396!";
    private static Connection connection;
    private static Statement statment;
    private static ResultSet result;


    public void createTable(String tableName) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(tableName + " (workerid VARCHAR (100) PRIMARY KEY UNIQUE, ");
        int number = 1;
        for (int i = 0; i < 31; i++) {
            String numberDay = "day" + number;
            number++;
            query.append(numberDay + " DATETIME, ");
        }
        query.append("day32 DATETIME);");
        connection = null;
        statment = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            statment = connection.createStatement();
            statment.executeUpdate(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String tableName, String workerid) {
        String query = "Insert into " + tableName + "(workerid) VALUE (" + workerid + ");";
        connection = null;
        statment = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            statment = connection.createStatement();
            statment.executeUpdate(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addTime(String tableName, String workerid, int number, LocalDateTime time) {
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ")
                .append(tableName)
                .append(" SET day")
                .append(number)
                .append(" = '")
                .append(time)
                .append("' WHERE workerid = ")
                .append(workerid)
                .append(";");
        System.out.println(query);
        connection = null;
        statment = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            statment = connection.createStatement();
            statment.executeUpdate(query.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean selectID(String id, String tableName) {
        String query = "select workerid from " + tableName + " where workerid = " + id + ";";
        System.out.println();
        connection = null;
        statment = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            statment = connection.createStatement();
            result = statment.executeQuery(query);
            while(result.next()) {
                return true;
            }
        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}



