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
     * Метод создание таблицы в зависимости от месяца расчета
     * @param tableName
     */
    void createTable(String tableName) throws SQLException, ClassNotFoundException;

    /**
     * Метод добавления идентификатора сотрудника в таблицу с данными посещения
     * @param tableName
     * @param workerid
     */
    void insert(String tableName, String workerid) throws SQLException;

    /**
     * Метод добавления данных посещения
     * @param tableName
     * @param workerid
     * @param number
     * @param time
     */
    void addTime(String tableName, String workerid, int number, LocalDateTime time) throws SQLException;


    /**
     * Метод проверки наличия сотрудника с номером в базе данных
     * @param id
     * @param tableName
     * @return
     */
    boolean selectID(String id, String tableName) throws SQLException;


    ArrayList<Long> selectAllIdInMonth(String tableName) throws SQLException;


    /**
     * Метод получения всех данных определенного сотрудника из таблицы с данными посещения за определенный месяц
     * @param worker
     * @param tableName
     * @return
     */
    WorkedHours getAllMonthTimes (Worker worker, String tableName) throws SQLException;
}



