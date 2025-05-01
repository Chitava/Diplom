package chitava.diplom.services.implServices;

import chitava.diplom.models.*;
import chitava.diplom.repositorys.WorkersRepository;
import chitava.diplom.services.SendTo;
import chitava.diplom.services.WorkerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static chitava.diplom.models.Hollydays.yearHolidaysDates;
import static org.apache.commons.math3.util.Precision.round;


/**
 * Класс работы с записями сотрудников в базе данных
 */

@Service
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties
public class ImplementWorkerService implements WorkerService {
    @Value("${URL_SAVE}")
    private String URL_SAVE;
    private Worker worker;
    private WorkedHours workedHours;
    @Autowired
    private MonthAllWorkersHours monthAllWorkersHours;
    private final Logger logger = Logger.getLogger(ImplementWorkerService.class);
    @Autowired
    private SendTo send;


    /**
     * Интерфейс для работы с базой данных
     */
    @Autowired
    private WorkersRepository repository;

    @Autowired
    ImplementJDBCService jdbc;


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
            logger.error(e.getMessage());
            throw new RuntimeException("Ошибка при получении сотрудников из базы данных");
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
            logger.error(e.getMessage());
            throw new RuntimeException("Ошибка при получении сотрудника из базы данных");
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
            logger.error(e.getMessage());
            throw new RuntimeException("Ошибка при обновлении сотрудника" + worker.getName() + " в базе данных");
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
            logger.error(e.getMessage());
            throw new RuntimeException("Ошибка при создании сотрудника" + worker.getName() + " в базе данных");
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
            logger.error(e.getMessage());
            throw new RuntimeException("Ошибка при удалении сотрудника" + worker.getName() + " из базы данных");
        }
    }

    @Override
    public Worker findByName(String name) {
        try {
            return repository.findByName(name);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Сотрудник с именем " + name + " не найден");
        }
    }

    /**
     * Метод получения праздничных дней в году для которого расчитывается зп
     */
    @Override
    public boolean getHollydays(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class);
            if (response.equals("1")) {
                return true;
            } else return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException("Не удалось получить данные о праздничных днях");
        }
    }


    /**
     * Метод добавление новых данных о посещении сотрудников
     *
     * @param file файл с данными
     * @return
     * @throws IOException
     */
    public String addReportCard(MultipartFile file) throws SQLException, ClassNotFoundException {
        jdbc.deleteTable(EstimatedDate.dateForDB);
        jdbc.createTable(EstimatedDate.dateForDB);
        HSSFWorkbook hb;
        int count = 0;
        try (POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());) {
            hb = new HSSFWorkbook(pSystem);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "Файл " + file.getOriginalFilename() + " не удалось прочитать";
        }
        HSSFSheet sheet = hb.getSheetAt(0);
        int lastrow = sheet.getLastRowNum();
        String fullTime = "";
        for (int i = 0; i < lastrow + 1; i++) {
            Row row = sheet.getRow(i);
            int lastCell = row.getLastCellNum();
            try {
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
                    fullTime = String.valueOf(row.getCell(j));
                    addTimesInWorkedHours(fullTime, workedHours);
                }
            } catch (NumberFormatException e) {
                for (int j = 3; j < lastCell - 1; j++) {
                    fullTime = String.valueOf(row.getCell(j));
                    addTimesInWorkedHours(fullTime, workedHours);
                }
                for (int k = 0; k < workedHours.getTimes().size(); k++) {
                    jdbc.addTime(EstimatedDate.dateForDB, String.valueOf(workedHours.getWorker().getId()), k + 1,
                            workedHours.getTime(k));
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
    }

    /**
     * Метод парсинга времени из файла с данными о посещения сотрудников
     *
     * @param times Время посещения за день
     */

    public void addTimesInWorkedHours(String times, WorkedHours workedHours) {
        String[] time = times.split("\n");
        if (times.equals("")) {
            return;
        } else if (time.equals("--\n--\n--") || time[0].equals("--") || time[1].equals("--")) {
            workedHours.addTime(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                    LocalDateTime.now().getDayOfMonth(), 0, 0));
        } else {
            try {
                int hour = Integer.parseInt(time[2].substring(0, time[2].indexOf(":")));
                int minute = Integer.parseInt(time[2].substring(time[2].indexOf(":") + 1));
                workedHours.addTime(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                        LocalDateTime.now().getDayOfMonth(), hour, minute));
            } catch (Exception e) {
                workedHours.addTime(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(),
                        LocalDateTime.now().getDayOfMonth(), 0, 0));
            }
        }
    }

    /**
     * Метод получения данных посещений конкретного сотрудника в конкретный месяц
     *
     * @param tableName месяц
     * @param id        идентификатор сотрудника
     * @return список времени посещений
     * @throws SQLException
     */
    public Map<String, List> getMonthTimes(String tableName, Long id) throws SQLException {
        Map<String, List> times = new TreeMap<>();
        String[] temp = new StringBuilder(EstimatedDate.dateForDB.replace("times_", ""))
                .toString().split("_");
        LocalDate verificationDate = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), 1);
        int dayWithoutData = verificationDate.getDayOfWeek().getValue();
        for (int i = 0; i < dayWithoutData-1; i++) {
            times.put("-"+(i+1), Arrays.asList("-", false));
        }
        String url;
        //получаем дату месяц и год
        Worker worker;
        Optional<Worker> optionalWorker = repository.findById(id);
        if (optionalWorker.isPresent()) {
            worker = optionalWorker.get();
        } else {
            logger.error("Не удалось найти сотрудника c id " + id + " в базе данных");
            throw new RuntimeException("Не удалось найти сотрудника");
        }
        WorkedHours workedHours = jdbc.getAllMonthTimes(worker, EstimatedDate.dateForDB);
        List<LocalDateTime> hour = workedHours.getTimes();
        for (int i = 0; i < hour.size(); i++) {
            String key = "";
            url ="https://isdayoff.ru/"+temp[0]+temp[1];
            if (i < 9) {
                key = "0" + (i + 1);
            } else {
                key = String.valueOf(i + 1);
            }
            url = url + key;
            double dayTime = 0;
            String dayHour = String.valueOf(hour.get(i).getHour());
            String dayMinute = String.valueOf(hour.get(i).getMinute());
            if (hour.get(i).getMinute() < 9) {
                dayTime = Double.parseDouble(dayHour + ".0" + dayMinute);
            } else {
                dayTime = Double.parseDouble(dayHour + "." + dayMinute);
            }
            times.put(key, Arrays.asList(dayTime, getHollydays(url)));
        }
        return times;
    }

    /**
     * Метод расчета заработной платы
     *
     * @param hours
     * @return
     */

    public MonthSalary salaryCalculation(WorkedHours hours, int startDate, int endDate) {
        String[] temp = new StringBuilder(EstimatedDate.dateForDB.replace("times_", ""))
                .toString().split("_");
        int year = Integer.parseInt(temp[0]);
        int month = Integer.parseInt(temp[1]); //получаем дату месяц год
        String monthName = "";
        String dayName = "";
        if(month < 10){
            monthName = "0"+month;
        }
        Worker worker = hours.getWorker();
        List<LocalDateTime> hour;
        try {
            hour = hours.getTimes(startDate, endDate);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
        if (hour.size() > 0) {
//            LocalDate verificationDay; //переменная для проверки выходной или нет сверяемся со списком полученных
//            // праздников
//            LocalDateTime verificationData = LocalDateTime.of(Integer.parseInt(temp[0]),
//                    Integer.parseInt(temp[1]), 1, 0, 0);
            int workDays = 0;
            LocalDateTime overTimes = LocalDateTime.of(0, 1, 1, 0, 0);
            LocalDateTime hollydayElaborTime = LocalDateTime.of(0, 1, 1, 0, 0);
            double hollydaySalary = 0;
            int hollydays = 0;
            double salary = 0;
            double overSalary = 0;
            double fullSalary = 0;
            double paymentInSmallDayInHour = worker.getPaymentInDay() / 8;
            if (endDate > hour.size()) {
                endDate = hour.size();
            }
            for (int i = startDate - 1; i < endDate; i++) {
//                int day = i + 1;
//                verificationDay = LocalDate.of(year, month, day);
                double dayTime = 0;
                String dayHour = String.valueOf(hour.get(i).getHour());
                String dayMinute = String.valueOf(hour.get(i).getMinute());
                if (dayMinute.length() < 2) {
                    dayMinute = dayMinute + "0";
                }
                if (hour.get(i).getMinute() < 9) {
                    dayTime = Double.parseDouble(dayHour + ".0" + dayMinute);
                } else {
                    dayTime = Double.parseDouble(dayHour + "." + dayMinute);
                }
                if (dayTime > 1) {
                    workDays++;
                    // проверка руководитель или нет
                    if (worker.getPost()) {
                        //проверка или праздничный день или выходной
                        if ((i+1) < 10) {
                            dayName = "0" + (i + 1);
                        }else {
                            dayName = String.valueOf(i + 1);
                        }
                        if (getHollydays("https://isdayoff.ru/" + year + monthName + dayName)) {
                            if (dayTime <= 6) {
                                salary = salary + worker.getPeymentInHollydays();
                                hollydaySalary = hollydaySalary + worker.getPeymentInHollydays();
                                hollydays++;
                            } else {
                                salary = salary + worker.getPeymentInHollydays();
                                overSalary = overSalary + (dayTime - 6) * worker.getPaymentInHour();
                                overTimes =
                                        overTimes.plusHours(hour.get(i).getHour() - 6).plusMinutes(hour.get(i).getMinute());
                                hollydaySalary = hollydaySalary + worker.getPeymentInHollydays();
                                double tempDayTime = (double) Math.round((dayTime - 6) * 100) / 100;
                                String tempHollydayElaborTime = String.valueOf(tempDayTime);
                                int tempHour = Integer.parseInt(tempHollydayElaborTime.substring(0,
                                        tempHollydayElaborTime.indexOf(".")));
                                int tempMinute =
                                        Integer.parseInt(tempHollydayElaborTime.substring(tempHollydayElaborTime.indexOf(".") + 1));
                                hollydayElaborTime = hollydayElaborTime.plusHours(tempHour).plusMinutes(tempMinute);
                                hollydays++;
                            }
                        } else {
                            //если отработано менее 9 часов
                            if (dayTime < 9) {
                                salary = salary + (dayTime - 1) * paymentInSmallDayInHour;
                            }
                            //если переработка
                            else if (dayTime >= 9) {
                                salary = salary + worker.getPaymentInDay();
                                overSalary = overSalary + (dayTime - 9) * worker.getPaymentInHour();
                                overTimes = overTimes.plusHours(hour.get(i).getHour() - 9).plusMinutes(hour.get(i)
                                        .getMinute());
                            }

                        }
                    } else {
                        if (dayTime < 9) {
                            salary = salary + (dayTime - 1) * paymentInSmallDayInHour;
                        } else if (dayTime >= 9) {
                            salary = salary + worker.getPaymentInDay();
                            overSalary = overSalary + (dayTime - 9) * worker.getPaymentInHour();
                            overTimes =
                                    overTimes.plusHours(hour.get(i).getHour() - 9).plusMinutes(hour.get(i).getMinute());
                        } else {
                            salary = salary + worker.getPaymentInDay();
                        }
                    }
                }
//                verificationData = verificationData.plusDays(1);

            }
            String finalOverTimesToString = "";

            if (overTimes.getDayOfMonth() == 1) {
                finalOverTimesToString = overTimes.getHour() + "." + overTimes.getMinute();
            } else {
                int hoerOverTimes = overTimes.getDayOfMonth() * 24 + overTimes.getHour() - 24;
                finalOverTimesToString = hoerOverTimes + "." + overTimes.getMinute();
            }
            double hourOverTimes = Double.parseDouble(finalOverTimesToString);
            fullSalary = salary + hourOverTimes * worker.getPaymentInHour();
            String tempHollydayElaborHours =
                    String.valueOf((hollydayElaborTime.getDayOfMonth() - 1) * 24 + hollydayElaborTime.getHour());
            String tempHollydayElaborMinute = String.valueOf(hollydayElaborTime.getMinute());
            double tempHollydayElaborTimes =
                    Double.parseDouble(tempHollydayElaborHours + "." + tempHollydayElaborMinute);
            MonthSalary monthSalary = new MonthSalary(worker.getId(), worker.getName(), workDays, hollydays,
                    round(hourOverTimes, 2), round(salary, 2), round(hourOverTimes * worker.getPaymentInHour(), 2),
                    tempHollydayElaborTimes, round(hollydaySalary, 2),
                    round(tempHollydayElaborTimes * worker.getPaymentInHour(), 2), round(fullSalary, 2));
            return monthSalary;
        }

        return new MonthSalary(worker.getId(), worker.getName(), 0, 0,
                0, 0, 0,
                0, 0, 0, 0);
    }

    /**
     * Метод расчета зарплаты всех сотрудников
     *
     * @param tableName Расчетный месяц
     * @return
     * @throws SQLException
     */
    public ArrayList<MonthSalary> getAllWorkersSalaryInMonth(String tableName, int startDate, int endDate) throws SQLException {
        List<String> allWorkersId = repository.findAllIdWorker();
        ArrayList<MonthSalary> result = new ArrayList<>();
        for (String id : allWorkersId) {
            if (repository.findById(Long.valueOf(id)).isPresent()) {
                Worker worker = (repository.findById(Long.valueOf(id)).get());
                workedHours = jdbc.getAllMonthTimes(worker, tableName);
                MonthSalary salary = salaryCalculation(workedHours, startDate, endDate);
                result.add(salary);

            }
        }
        return result;
    }

    /**
     * \
     * Метод сохранения полученных данных при расчета зарплаты за месяц
     *
     * @param salarys
     * @return
     */
    public void saveTo(ArrayList<MonthSalary> salarys, HttpServletResponse response) throws IOException {
        send.sendTo(salarys, response);

    }

    /**
     * Метод обновления значений времени поещения конкретного сотрудника
     *
     * @param times
     * @param id
     * @throws SQLException
     */
    public void updateTimes(MonthTime times, Long id) throws SQLException {
        ArrayList<Double> newDoubleTimes = times.getAll();
        ArrayList<LocalTime> newTime = new ArrayList<>();
        for (Double time : newDoubleTimes) {
            int hour = Integer.parseInt(String.valueOf(time).substring(0, String.valueOf(time).indexOf(".")));
            int minute = Integer.parseInt(String.valueOf(time).substring(String.valueOf(time).indexOf(".") + 1));
            newTime.add(LocalTime.of(hour, minute));
        }
        Worker worker;
        Optional<Worker> optionalWorker = repository.findById(id);
        if (optionalWorker.isPresent()) {
            worker = optionalWorker.get();
        } else {
            throw new RuntimeException();
        }
        WorkedHours hours = jdbc.getAllMonthTimes(worker, EstimatedDate.dateForDB);
        for (int i = 0; i < hours.getTimes().size(); i++) {
            if (!LocalTime.from(hours.getTime(i)).equals(newTime.get(i))) {
                LocalDateTime now = hours.getTime(i);
                LocalDateTime newDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(),
                        newTime.get(i).getHour(), newTime.get(i).getMinute());
                jdbc.addTime(EstimatedDate.dateForDB, String.valueOf(id), i + 1, newDate);
            }
        }
    }

    public WorkedHours getAllMonthTimes(Worker worker, String tableName) throws SQLException {
        return jdbc.getAllMonthTimes(worker, tableName);
    }

    public ArrayList<MonthSalary> getOneWorkersSalaryInMonth(String tableName, Long id, int startDate, int endDate) throws SQLException {
        ArrayList<MonthSalary> result = new ArrayList<>();
        if (repository.findById(Long.valueOf(id)).isPresent()) {
            Worker worker = (repository.findById(Long.valueOf(id)).get());
            workedHours = jdbc.getAllMonthTimes(worker, tableName);
            MonthSalary salary = salaryCalculation(workedHours, startDate, endDate);
            result.add(salary);
        }
        return result;
    }
}


