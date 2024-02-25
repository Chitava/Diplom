package chitava.diplom.services.implServices;
import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;

import chitava.diplom.services.JDBCService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Класс работы с SQL базой
 */
@Service
public class ImplementJDBCService implements JDBCService {


    @Autowired
    private ConnectToDB connection;

    /**
     * Метод создание таблицы в зависимости от месяца расчета
     * @param tableName
     */
    public void createTable(String tableName) throws SQLException, ClassNotFoundException {
        Connection connect = connection.getConnection();
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ");
        query.append(tableName + " (workerid VARCHAR (100) PRIMARY KEY UNIQUE, ");
        int number = 1;
        for (int i = 0; i < 30; i++) {
            String numberDay = "day" + number;
            number++;
            query.append(numberDay + " DATETIME, ");
        }
        query.append("day31 DATETIME);");
        Statement statement = connect.createStatement();
        statement.execute(query.toString());
        connect.close();
    }

    /**
     * Метод добавления идентификатора сотрудника в таблицу с данными посещения
     * @param tableName
     * @param workerid
     */
    public void insert(String tableName, String workerid) throws SQLException {
        Connection connect = connection.getConnection();
        String query = "Insert into " + tableName + "(workerid) VALUE (" + workerid + ");";
        Statement statement = connect.createStatement();
        statement.executeUpdate(query);
        connect.close();
    }

    /**
     * Метод добавления данных посещения
     * @param tableName
     * @param workerid
     * @param number
     * @param time
     */
    public void addTime(String tableName, String workerid, int number, LocalDateTime time) throws SQLException {
        Connection connect = connection.getConnection();
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
        Statement statement = connect.createStatement();
        statement.executeUpdate(String.valueOf(query));
        connect.close();
    }

    /**
     * Метод проверки наличия сотрудника с номером в базе данных
     * @param id
     * @param tableName
     * @return
     */
    public boolean selectID(String id, String tableName) throws SQLException {
        Connection connect = connection.getConnection();
        String query = "select workerid from " + tableName + " where workerid = " + id + ";";
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery(query);
        while(result.next()) {
            return true;
        }
        connect.close();
        return false;
    }

    /**
     * Метод выбора всех идентификаторов из таблицы времени посещения в определнный месяц
     * @param tableName
     * @return
     * @throws SQLException
     */
    public ArrayList<Long> selectAllIdInMonth(String tableName) throws SQLException {
        Connection connect = connection.getConnection();
        String queryForId = "select workerid from " + tableName + ";";
        ArrayList<Long> monthId= new ArrayList<>();
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery(queryForId);
        while(result.next()) {
            monthId.add(Long.valueOf(result.getString(1)));
        }
        connect.close();
        return monthId;
    }

    /**
     * Метод получения всех данных определенного сотрудника из таблицы с данными посещения за определенный месяц
     * @param worker
     * @param tableName
     * @return
     */
    public WorkedHours getAllMonthTimes (Worker worker, String tableName) throws SQLException {
        Connection connect = connection.getConnection();
        String query = "SELECT * FROM " + tableName + " WHERE workerid = "+ worker.getId().toString() + ";";
        WorkedHours hours = new WorkedHours();
        hours.setWorker(worker);
        Statement statement = connect.createStatement();
        ResultSet result = statement.executeQuery(query);
        int columns = result.getMetaData().getColumnCount();
        while(result.next()) {
            for (int i = 2; i <= columns; i++) {
                if(result.getObject(i) == null){
                    break;
                }else {
                    LocalDateTime time = LocalDateTime.parse(result.getString(i).replace(" ", "T"));
                    hours.addTime(time);
                }
            }
        }
        connect.close();
        return hours;
    }
}



