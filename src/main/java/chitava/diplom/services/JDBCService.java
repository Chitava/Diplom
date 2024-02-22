package chitava.diplom.services;


import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Интерфейс работы с SQL базой
 */

public interface JDBCService {

    /**
     * Метод получения соединения с БД
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Statement getStatment() throws ClassNotFoundException, SQLException;

    /**
     * Метод создание таблицы в зависимости от месяца расчета
     *
     * @param tableName
     */
    public void createTable(String tableName) throws SQLException;

    /**
     * Метод добавления идентификатора сотрудника в таблицу с данными посещения
     *
     * @param tableName
     * @param workerid
     */
    public void insert(String tableName, String workerid) throws SQLException;

    /**
     * Метод добавления данных посещения
     *
     * @param tableName
     * @param workerid
     * @param number
     * @param time
     */
    public void addTime(String tableName, String workerid, int number, LocalDateTime time) throws SQLException;


    /**
     * Метод проверки наличия сотрудника с номером в базе данных
     *
     * @param id
     * @param tableName
     */
    public boolean selectID(String id, String tableName) throws SQLException;

    /**
     * Метод получения идентификаторов из таблицы посещений за определнный месяц
     * @param tableName
     * @throws SQLException
     */
    public ArrayList<Long> selectAllIdInMonth(String tableName) throws SQLException;


    /**
     * Метод получения всех данных определенного сотрудника из таблицы с данными посещения за определенный месяц
     *
     * @param worker
     * @param tableName
     */
    public WorkedHours getAllMonthTimes(Worker worker, String tableName) throws SQLException;
}



