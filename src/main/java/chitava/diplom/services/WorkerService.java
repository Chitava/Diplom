package chitava.diplom.services;

import chitava.diplom.models.Worker;

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
     * @param id номер сотрудника
     */
    String deleteWorker(Worker worker);
}
