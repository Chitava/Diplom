package chitava.diplom.services.implServices;

import chitava.diplom.models.*;

import chitava.diplom.repositorys.WorkersRepository;
import chitava.diplom.services.JDBCService;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    @Autowired
    private MonthAllWorkersHours monthAllWorkersHours;


    /**
     * Интерфейс для работы с базой данных
     */
    @Autowired
    private WorkersRepository repository;

    @Autowired
    JDBCService jdbc;


    /**
     * Метод получения всех сотрудников
     *
     * @return коллекцию сотрудников
     */
    @Override
    public List<Worker> getAllWorkers() {
        try {
            List<Worker> workers = repository.findAll();
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
            String URL = String.format("https://production-calendar.ru/get/ru/%s/json", year); //адрес общедоступного
            //ресурса с расписанными календарными днями на год
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);
            responce = template.exchange(URL,
                    HttpMethod.GET, entity, Hollydays.class);
        } catch (Exception e) {
            return "Ошибка получения праздничных дней " + e.getMessage();
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
    public String addReportCard(MultipartFile file) throws SQLException {
        jdbc.createTable(EstimatedDate.dateForDB);
        int count = 0;
        try {
            POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());
            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
            HSSFSheet sheet = hb.getSheetAt(0);
            int lastrow = sheet.getLastRowNum();
            int number;
            for (int i = 0; i < lastrow + 1; i++) {
                Row row = sheet.getRow(i);
                int lastCell = row.getLastCellNum();
                try {
                    number = 1;
                    Integer.parseInt(String.valueOf(row.getCell(0)));
                    String workerName = String.valueOf(row.getCell(1)).replace("\n", "");
                    worker = findByName(workerName);
                    if (worker == null) {
                        worker = new Worker();
                        worker.setName(workerName);
                        worker.setNewWorker(true);
                        createWorker(worker);
                        count++;
                    }
                    workedHours = new WorkedHours();
                    workedHours.setWorker(worker);
                    monthAllWorkersHours.addWorkedHours(workedHours);
                    if (!jdbc.selectID(worker.getId().toString(), EstimatedDate.dateForDB)) {
                        jdbc.insert(EstimatedDate.dateForDB, worker.getId().toString());
                    }
                    for (int j = 3; j < lastCell - 2; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        LocalDateTime time = addTime(fullTime, number);
                        if (time.equals("")) {
                            break;
                        } else {
                            jdbc.addTime(EstimatedDate.dateForDB, worker.getId().toString(), number, time);
                            number++;
                        }
                    }
                } catch (NumberFormatException e) {
                    number = 16;
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        LocalDateTime time = addTime(fullTime, number);
                        jdbc.addTime(EstimatedDate.dateForDB, worker.getId().toString(), number, time);
                        number++;
                    }
                }
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
     *
     * @param times Время посещения за день
     */
    public LocalDateTime addTime(String times, int day) {
        LocalDateTime time = null;
        if (!times.equals("--\n--\n--") && times.length() > 0) {
            String[] str = times.split("\n");
            int comeHour = Integer.parseInt(str[0].substring(0, str[0].indexOf(":")));
            int comeMinute = Integer.parseInt(str[0].substring(str[0].indexOf(":") + 1));
            int leftHour = Integer.parseInt(str[1].substring(0, str[1].indexOf(":")));
            int leftMinute = Integer.parseInt(str[1].substring(str[1].indexOf(":") + 1));
            LocalTime comeWork = LocalTime.of(comeHour, comeMinute);
            LocalTime leftWork = LocalTime.of(leftHour, leftMinute);
            if (comeWork.compareTo(leftWork) > 0) {
                LocalTime fullDay = LocalTime.of(00, 00);
                LocalTime addTime = fullDay.minusHours(comeHour)
                        .minusMinutes(comeMinute)
                        .plusHours(leftHour)
                        .plusMinutes(leftMinute);
                time = LocalDateTime.of(2024, 1, day, addTime.getHour(), addTime.getMinute());
            } else {
                int hour = Integer.parseInt(str[2].substring(0, str[2].indexOf(":")));
                int minute = Integer.parseInt(str[2].substring(str[2].indexOf(":") + 1));
                time = LocalDateTime.of(2024, 1, day, hour, minute);
            }
        } else {
            time = LocalDateTime.of(2024, 1, day, 0, 0);
        }
        workedHours.addTime(time);
        return time;
    }

    /**
     * Метод получения данных посещений всех сотрудников за определеннный месяц
     *
     * @param tableName
     * @return
     */
    public MonthAllWorkersHours getMonthTimes(String tableName) throws SQLException {
        monthAllWorkersHours = new MonthAllWorkersHours();
        ArrayList<Long> ids = jdbc.selectAllIdInMonth(tableName);
        WorkedHours monthTimes = new WorkedHours();
        for (Long id : ids) {
            Worker worker = repository.findById(id).get();
            monthTimes.setWorker(worker);
            monthTimes = jdbc.getAllMonthTimes(worker, tableName);
            monthAllWorkersHours.addWorkedHours(monthTimes);
        }
        return monthAllWorkersHours;
    }

    /**
     * Метод расчета заработной платы
     *
     * @param hours
     * @return
     */
    public MonthSalary salaryCalculation(WorkedHours hours) {
        String[] temp = new StringBuilder(EstimatedDate.dateForDB.replace("salary_", ""))
                .toString().split("_");
        String monthYears = new StringBuilder(".").append(temp[1]).append(".").append(temp[0]).toString();
        //получаем дату месяц.год
        Worker worker = hours.getWorker();
        List<LocalDateTime> hour = hours.getTimes();
        String verificationDays = ""; //переменная для проверки выходной или нет сверяемся со списком полученных
        // праздников
        LocalDateTime verificationData = LocalDateTime.of(Integer.parseInt(temp[0]),
                Integer.parseInt(temp[1]), 1, 0, 0);
        int workDays = 0;
        int overDays = 0;
        double salary = 0;
        double overSalary = 0;
        double fullSalary = 0;
        double paymentInSmallDayInHour = worker.getPaymentInDay() / 8;
        for (int i = 0; i < hour.size(); i++) {
            String day = String.valueOf(i + 1);
            if (i < 10) {
                verificationDays = "0" + day + monthYears;
            } else {
                verificationDays = day + monthYears;
            }
            double dayTime = 0;
            String dayHour = String.valueOf(hour.get(i).getHour());
            String dayMinute = String.valueOf(hour.get(i).getMinute());
            if (hour.get(i).getMinute() < 10) {
                dayTime = Double.parseDouble(dayHour + ".0" + dayMinute);
            } else {
                dayTime = Double.parseDouble(dayHour + "." + dayMinute);
            }
            if (dayTime > 1) {
                workDays++;
                // проверка руководитель или нет
                if (worker.getPost()) {
                    //проверка или праздничный день или выходной
                    if (yearHolidays.contains(String.valueOf(verificationDays)) || String.valueOf(verificationData.getDayOfWeek()) ==
                            "SATURDAY" || String.valueOf(verificationData.getDayOfWeek()) == "SUNDAY") {
                        salary = salary + worker.getPeymentInHollydays();
                    } else {
                        //если отработано менее 9 часов
                        if (dayTime < 9) {
                            salary = salary + (dayTime - 1) * paymentInSmallDayInHour;
                        }
                        //если переработка
                        else if (dayTime > 9.20) {
                            salary = salary + worker.getPaymentInDay();
                            overSalary = overSalary + (dayTime - 9) * worker.getPaymentInHour();
                            overDays++;
                        }
                        //если ровно 9 часов
                        else {
                            salary = salary + worker.getPaymentInDay();
                        }
                    }
                }
                //если не руководитель
                else {
                    if (dayTime < 9) {
                        salary = salary + (dayTime - 1) * paymentInSmallDayInHour;
                    } else if (dayTime > 9.20) {
                        salary = salary + worker.getPaymentInDay();
                        overSalary = overSalary + (dayTime - 9) * worker.getPaymentInHour();
                        overDays++;
                    } else {
                        salary = salary + worker.getPaymentInDay();
                    }
                }
            }
            verificationData = verificationData.plusDays(1);
            fullSalary = salary + overSalary;
        }
        return new MonthSalary(worker.getId(), worker.getName(), workDays, overDays, Math.round(salary * 100 / 100),
                Math.round(overSalary * 100 / 100), 0, Math.round(fullSalary * 100 / 100));
    }

    public ArrayList<MonthSalary> getAllWorkersSalaryInMonth(String tableName) throws SQLException {
        List<String> allWorkersId = repository.findAllIdWorker();
        ArrayList<MonthSalary> result = new ArrayList<>();
        for (String id : allWorkersId) {
            if (repository.findById(Long.valueOf(id)).isPresent()) {
                Worker worker = (repository.findById(Long.valueOf(id)).get());
                workedHours = jdbc.getAllMonthTimes(worker, tableName);
                MonthSalary salary = salaryCalculation(workedHours);
                result.add(salary);
            }
        }
        return result;
    }
}