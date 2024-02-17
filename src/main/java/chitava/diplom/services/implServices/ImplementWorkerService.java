package chitava.diplom.services.implServices;

import chitava.diplom.models.*;
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
import org.springframework.jdbc.core.JdbcTemplate;
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
    @Autowired
    private WorkedHours workedHours;

    /**
     * Интерфейс для работы с базой данных
     */
    @Autowired
    private WorkersRepository repository;

    @Autowired
    private JdbcTemplate jt;


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

    /**
     * Метод добавление новых данных о посещении сотрудников
     *
     * @param file файл с данными
     * @return
     * @throws IOException
     */
    public String addReportCard(MultipartFile file) throws IOException {
        int count = 0;
        try {
            POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());
            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
            HSSFSheet sheet = hb.getSheetAt(0);
            int lastrow = sheet.getLastRowNum();
            for (int i = 0; i < lastrow+1; i++) {
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
                } catch (NumberFormatException e) {
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        addTime(fullTime);
                    }
                }
            }
            try {
                createTable(EstimatedDate.dateForDB);
            }catch (Exception e) {
                return String.format("В поцессе создания таблицы со временем посещений произошла ошибка %s",
                        e.getMessage());
            }
            try {
                addWorkedTimes(workedHours, EstimatedDate.dateForDB);
            }catch (Exception e){
                return String.format("В поцессе добавление данных о посещении сотрудника %s произошла ошибка %s",
                        worker.getName(), e.getMessage());
            }
            if (count > 0) {
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
            } else {
                return "Новые данные о сотрудниках загружены успешно";
            }
        } catch (Exception e) {
            return "В процессе добавления новых сотрудников произошла ошибка " + e.getMessage();
        }
    }

    /**
     * Метод парсинга времени из файла с данными о посещения сотрудников
     * @param times Время посещения за день
     */
    public void addTime(String times) {
        LocalTime time;
        if (!times.equals("--\n--\n--") && times.length() != 0) {
            String[] str = times.split("\n");
            int hour = Integer.parseInt(str[2].substring(0, str[2].indexOf(":")));
            int minute = Integer.parseInt(str[2].substring(str[2].indexOf(":") + 1));
            time = LocalTime.of(hour, minute);
        }else {
            time = LocalTime.of(0,0);
        }workedHours.addTime(time);
    }


    public void createTable(String name) {
        String query = "create table "+ name +" (name VARCHAR(100), a VARCHAR(8), b VARCHAR(8), c VARCHAR(8), d VARCHAR(8), " +
                "e VARCHAR(8), f VARCHAR(8), g VARCHAR(8), h VARCHAR(8), i VARCHAR(8), j VARCHAR(8), k VARCHAR(8), l VARCHAR(8), " +
                "m VARCHAR(8), n VARCHAR(8), o VARCHAR(8), p VARCHAR(8), q VARCHAR(8), r VARCHAR(8), s VARCHAR(8), t VARCHAR(8), " +
                "u VARCHAR(8), v VARCHAR(8), w VARCHAR(8), x VARCHAR(8), y VARCHAR(8), z VARCHAR(8), aa VARCHAR(8), ab VARCHAR(8), " +
                "ac VARCHAR(8) , ad VARCHAR(8), ae VARCHAR(8), af VARCHAR(8));";

        try {
            jt.execute(query);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    public void addWorkedTimes (WorkedHours data, String tableName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Insert into " + tableName + " (name, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, " +
                "r, s, t, u, v, w, x, y, z, aa, ab, ac, ad, ae, af) values ('"  + data.getWorkerName() + "', ");
        for (LocalTime time: data.getWorkedHours()) {
            stringBuilder.append("'"+time.toString() + "', ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(",")).append(");");
        String query = String.valueOf(stringBuilder);
        try {
            jt.update(query);
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}