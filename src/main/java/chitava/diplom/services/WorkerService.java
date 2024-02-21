package chitava.diplom.services;

import chitava.diplom.models.MonthAllWorkersHours;
import chitava.diplom.models.MonthSalary;
import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
    String addReportCard(MultipartFile file) throws IOException, SQLException;

    /**
     * Метод расчета зарплаты для одного сотрудника
     * @param hours Сотрудник с данными по посещениям за конкретный месяц
     * @return
     */
    MonthSalary salaryCalculation (WorkedHours hours);


    /**
     * Метод расчета зарплаты для всех сотрудников за месяц
     * @param tableName Расчетный месяц
     * @return
     */
    ArrayList<MonthSalary> getAllWorkersSalaryInMonth(String tableName) throws SQLException;




}
