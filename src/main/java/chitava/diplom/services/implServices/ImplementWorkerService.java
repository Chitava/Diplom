package chitava.diplom.services.implServices;

import chitava.diplom.models.Hollyday;
import chitava.diplom.models.Hollydays;
import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.repositorys.WorkersRepository;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import static chitava.diplom.models.Hollydays.yearHolidays;


/**
 * Класс работы с записями сотрудников в базе данных
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImplementWorkerService implements WorkerService {


    private Worker worker;
    private WorkedHours workedHours;

    /**
     * Интерфейс для работы с базой данных
     */
    @Autowired
    private WorkersRepository repository;


    /**
     * Метод получения всех сотрудников
     *
     * @return коллекцию сотрудников
     */
    @Override
    public Collection<Worker> getAllWorkers() {
        try {
            Collection<Worker> workers = repository.findAll();
            return workers.stream().sorted(Comparator.comparing(Worker::getName)).sorted(Comparator.comparing
                    (Worker::isNewWorker, (s1, s2) -> {
                        return s2.compareTo(s1);
                    })).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
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
                workerByID.setPost(worker.getPost());
                workerByID.setPaymentInDay(worker.getPaymentInDay());
                workerByID.setPaymentInHour(worker.getPaymentInHour());
                workerByID.setPeymentInHollydays(worker.getPeymentInHollydays());
                workerByID.setNewWorker(false);
                repository.save(workerByID);
                return "Данные о сотруднике успешно обновлены";
            } else return "Неудалось обновить данные";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
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
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Worker findByName(String name) {
        try {
            return repository.findByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Метод получения праздничных дней в году для которого расчитывается зп
     *
     * @param year расчетный год
     *             Данные записываются в коллекцию
     */
    @Override
    public String getHollydays(String year) {
        ResponseEntity<Hollydays> responce;
        try {
            String URL = String.format("https://production-calendar.ru/get/ru/%s/json", year);
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            responce = template.exchange(URL,
                    HttpMethod.GET, entity, Hollydays.class);
        } catch (Exception e) {
            return "Ошибка подключения к календарю " + e.getMessage();
        }
        yearHolidays.clear();
        Hollydays hollydays = responce.getBody();
        for (Hollyday day : hollydays.getDays()) {
            if (day.getType_text().equals("Государственный праздник")) {
                String date = day.getDate();
                yearHolidays.add(date);
            }
        }
        return null;
    }

    public String addWorker(MultipartFile file) throws IOException {
        int count = 0;
        try {
            POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());
            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
            HSSFSheet sheet = hb.getSheetAt(0);
            int lastrow = sheet.getLastRowNum();
            for (int i = 0; i < lastrow; i = i + 2) {
                Row row = sheet.getRow(i);
                int lastCell = row.getLastCellNum();
                try {
                    Integer.parseInt(String.valueOf(row.getCell(0)));
                    String workerName = String.valueOf(row.getCell(1)).replace("\n", " ");
                    worker = findByName(workerName);
                    if (worker == null) {
                        worker = new Worker();
                        worker.setName(workerName);
                        worker.setNewWorker(true);
                        createWorker(worker);
                        count++;
                    }
                    workedHours = new WorkedHours();
                    workedHours.setWorkerName(worker.getName());
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        addTime(fullTime);
                    }
                    System.out.println(worker.getName());
                } catch (NumberFormatException e) {
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        addTime(fullTime);
                    }
                }for (LocalTime time: workedHours.getWorkedHours()) {
                    System.out.print(time + " ч. ");
                }
            }
            if (count > 0){
                switch (count) {
                    case 1:
                        return "Новые данные о сотрудниках загружены успешно, У Вас новый сотрудник";
                    case 2:
                        return "Новые данные о сотрудниках загружены успешно, У Вас 2 новых сотрудника";
                    case 3:
                        return "Новые данные о сотрудниках загружены успешно, У Вас 3 новых сотрудника";
                    case 4:
                        return "Новые данные о сотрудниках загружены успешно, У Вас 4 новых сотрудника";
                    default:
                        return String.format("Новые данные о сотрудниках загружены успешно, У Вас %s новых " +
                                        "сотрудников", count);
                }
            }else {
                return "Новые данные о сотрудниках загружены успешно";
            }
        } catch (Exception e){
            return "В процессе добавления новых сотрудников произошла ошибка " + e.getMessage();
        }
    }

    public void addTime(String times) {
        if (!times.equals("--\n--\n--") && times.length() != 0) {
            String[] str = times.split("\n");
            int hour = Integer.parseInt(str[2].substring(0, str[2].indexOf(":")));
            int minute = Integer.parseInt(str[2].substring(str[2].indexOf(":") + 1));
            LocalTime time = LocalTime.of(hour, minute);
            workedHours.addTime(time);
        }
    }



}