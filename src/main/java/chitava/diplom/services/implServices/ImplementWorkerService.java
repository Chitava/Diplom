package chitava.diplom.services.implServices;

import chitava.diplom.models.Worker;
import chitava.diplom.repositorys.WorkersRepository;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * Класс работы с записями сотрудников в базе данных
 */

@Service
@AllArgsConstructor
public class ImplementWorkerService implements WorkerService {
    /**
     * Интерфейс для работы с базой данных
     */
    private final WorkersRepository repository;

    /**
     * Метод получения всех сотрудников
     *
     * @return коллекцию сотрудников
     */
    @Override
    public Collection<Worker> getAllWorkers() {
        try {
            Collection<Worker> workers = repository.findAll();
            return workers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Метод получения сотрудника по идентификацилнному номеру
     *
     * @param id идентификационный номер
     * @return сотрудника с заданным номером
     */
    @Override
    public Worker getWorkerById(Long id) {
        try {
            Worker worker = repository.findById(id).get();
            return worker;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Метод обновления данных сотрудника
     *
     * @param worker Обновляемый сотрудник
     * @return Обновленный сотрудник
     */
    @Override
    public String updateWorker(Worker worker) {
        Worker workerByID;
        try {
            if (repository.findById(worker.getId()).isPresent()) {
                workerByID = repository.findById(worker.getId()).get();
                workerByID.setId(worker.getId());
                workerByID.setName(worker.getName());
                workerByID.setPost(worker.isPost());
                workerByID.setPaymentInDay(worker.getPaymentInDay());
                workerByID.setPaymentInHour(worker.getPaymentInHour());
                workerByID.setPaymentInDay(worker.getPaymentInDay());
                workerByID.setPeymentInHollydays(worker.getPeymentInHollydays());
                return "Данные о сотруднике успешно обновлены";
            } else return "Неудалось обновить данные";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Метод создания нового сотрудника
     *
     * @param worker Данные нового сотрудника
     * @return Новый сотрудник
     */
    @Override
    public String createWorker(Worker worker) {
        try {
            repository.save(worker);
            return "Новый сотрудник успешно сохранен";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Метод удаления сотрудника из базы данных
     *
     * @param worker сотрудник, данные о котором необходимо удалить
     */
    @Override
    public String deleteWorker(Worker worker) {
        try {
            repository.delete(worker);
            return "Данные о сотрудник успешно удалены";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
