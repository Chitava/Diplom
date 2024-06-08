package chitava.diplom.services;

import chitava.diplom.models.MonthSalary;
import chitava.diplom.models.MonthTime;
import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Интерфейс для работы с классом сотрудников в базе данных
 */

public interface WorkerService {
    /**
     * Метод получения всех сотрудников
     * @return коллекцию сотрудников
     */
    Collection<Worker> getAllWorkers();

    /**
     * Метод получения сотрудника по идентификацилнному номеру
     * @param id идентификационный номер
     * @return сотрудника с заданным номером
     */
    Worker getWorkerById(Long id);

    /**
     * Метод обновления данных сотрудника
     * @param worker Обновляемый сотрудник
     * @return Обновленный сотрудник
     */
    String updateWorker(Worker worker);

    /**
     * Метод создания нового сотрудника
     * @param worker Данные нового сотрудника
     * @return Новый сотрудник
     */
    String createWorker(Worker worker);

    /**
     * Метод удаления сотрудника из базы данных
     * @param worker сотрудник, запись которого нужно удалить
     */
    String deleteWorker(Worker worker);

    /**
     * Поиск сотрудника в базе данных по имени
     * @param name имя сотрудника
     * @return есть или нет
     */
    Worker findByName(String name);

    /**
     * Метод получения праздничных дней в году
     * @param year расчетный год
     * @return сообщение о выполненой операции
     */
    String getHollydays(String year);

    /**
     * Метод добавление новых данных о посещении сотрудников
     * @param file файл с данными
     * @return
     * @throws IOException
     */
    String addReportCard(MultipartFile file) throws IOException, SQLException, ClassNotFoundException;

    /**
     * Метод расчета зарплаты для одного сотрудника
     * @param hours Сотрудник с данными по посещениям за конкретный месяц
     * @return
     */
    MonthSalary salaryCalculation (WorkedHours hours, int start, int end) throws SQLException;


    /**
     * Метод расчета зарплаты для всех сотрудников за месяц
     * @param tableName Расчетный месяц
     * @return
     */
    ArrayList<MonthSalary> getAllWorkersSalaryInMonth(String tableName, int start, int end) throws SQLException;


    /**\
     * Метод сохранения полученных данных при расчета зарплаты за месяц
     * @param salarys
     * @return
     */
    void saveTo(ArrayList<MonthSalary> salarys, HttpServletResponse response) throws IOException;

    /**
     * Метод получения данных посещений конкретного сотрудника в конкретный месяц
     *
     * @param tableName месяц
     * @param id        идентификатор сотрудника
     * @return список времени посещений
     * @throws SQLException
     */
    Map<String, List> getMonthTimes(String tableName, Long id) throws SQLException;


    /**
     * Метод обновления данных посещений конкретного сотрудника
     * @param times
     * @param id
     */
    public void updateTimes(MonthTime times, Long id) throws SQLException;

    /**
     * Метод получения данных посещения за месяц
     * @param worker
     * @param tableName
     * @return
     * @throws SQLException
     */
    public WorkedHours getAllMonthTimes (Worker worker, String tableName) throws SQLException;

    public ArrayList<MonthSalary> getOneWorkersSalaryInMonth(String tableName, Long id, int start, int end) throws SQLException;
}
