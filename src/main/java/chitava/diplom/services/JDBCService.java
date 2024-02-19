package chitava.diplom.services;


import chitava.diplom.models.Worker;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.List;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.time.LocalDateTime;

/**
 * Класс работы с SQL базой
 */
@Service
@RequiredArgsConstructor
public class JDBCService {

    private static Connection connection;
    private static Statement statment;
    private static ResultSet result;

    public Statement getStatment() throws ClassNotFoundException, SQLException {
        String URL = "jdbc:mysql://localhost:3306/mysql";
        String USER = "root";
        String PASS = "Vch32396!";
        connection = null;
        statment = null;
        connection = DriverManager.getConnection(URL, USER, PASS);
        statment = connection.createStatement();
        return statment;
    }

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
        try {
            getStatment().execute(query.toString());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String tableName, String workerid) {
        String query = "Insert into " + tableName + "(workerid) VALUE (" + workerid + ");";
        try {
            getStatment().executeQuery(query);
        } catch (SQLException | ClassNotFoundException e) {
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
        try {
            getStatment().execute(query.toString());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean selectID(String id, String tableName) {
        String query = "select workerid from " + tableName + " where workerid = " + id + ";";
        try {
            result = getStatment().executeQuery(query);
            while(result.next()) {
                return true;
            }
        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public Worker getAllMonthTimes (Long worker, String tableName){

        String query = "SELECT * FROM " + tableName + " WHERE workerid = "+ worker + ";";
//        String query = "SELECT * FROM " + tableName + " WHERE workerid = "
//                + worker.getId() + ";";

        try {
            result = getStatment().executeQuery(query);
            while(result.next()) {
                for (int i = 2; i < 33; i++) {
                    LocalDateTime time = LocalDateTime.parse(result.getString(i).replace(" ",  "T"));
                    System.out.println(time);
//todo доделать метод получения данных посещения
                }

            }
        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}



