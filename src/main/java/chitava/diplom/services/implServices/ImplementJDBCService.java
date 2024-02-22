package chitava.diplom.services.implServices;
import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.services.JDBCService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Класс работы с SQL базой
 */
@Service
@RequiredArgsConstructor
public class ImplementJDBCService implements JDBCService {

    private static Connection connection;
    private static Statement statment;
    private static ResultSet result;

    /**
     * Метод получения соединения с БД
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
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

    /**
     * Метод создание таблицы в зависимости от месяца расчета
     * @param tableName
     */
    public void createTable(String tableName) throws SQLException {
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
        try {
            getStatment().execute(query.toString());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
    }

    /**
     * Метод добавления идентификатора сотрудника в таблицу с данными посещения
     * @param tableName
     * @param workerid
     */
    public void insert(String tableName, String workerid) throws SQLException {
        String query = "Insert into " + tableName + "(workerid) VALUE (" + workerid + ");";
        try {
            getStatment().executeUpdate(query);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
    }

    /**
     * Метод добавления данных посещения
     * @param tableName
     * @param workerid
     * @param number
     * @param time
     */
    public void addTime(String tableName, String workerid, int number, LocalDateTime time) throws SQLException {
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
            getStatment().executeUpdate(String.valueOf(query));

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
    }


    /**
     * Метод проверки наличия сотрудника с номером в базе данных
     * @param id
     * @param tableName
     * @return
     */
    public boolean selectID(String id, String tableName) throws SQLException {
        String query = "select workerid from " + tableName + " where workerid = " + id + ";";
        try {
            result = getStatment().executeQuery(query);
            while(result.next()) {
                return true;
            }
        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
        return false;
    }


    public ArrayList<Long> selectAllIdInMonth(String tableName) throws SQLException {
        String queryForId = "select workerid from " + tableName + ";";
        ArrayList<Long> monthId= new ArrayList<>();

        try {
            result = getStatment().executeQuery(queryForId);
            while(result.next()) {
                monthId.add(Long.valueOf(result.getString(1)));
            }
        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
        return monthId;
    }


    /**
     * Метод получения всех данных определенного сотрудника из таблицы с данными посещения за определенный месяц
     * @param worker
     * @param tableName
     * @return
     */
    public WorkedHours getAllMonthTimes (Worker worker, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE workerid = "+ worker.getId().toString() + ";";
        WorkedHours hours = new WorkedHours();
        hours.setWorker(worker);
        try {
            result = getStatment().executeQuery(query);
            while(result.next()) {
                for (int i = 2; i < 32; i++) {
                    LocalDateTime time = LocalDateTime.parse(result.getString(i).replace(" ",  "T"));
                    hours.addTime(time);
                }
            }
        } catch (
                SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }connection.close();
        return hours;
    }
}



