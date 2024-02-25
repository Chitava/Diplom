package chitava.diplom.controllers;

import chitava.diplom.models.*;
import chitava.diplom.services.WorkerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import netscape.javascript.JSObject;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Header;
import org.apache.tomcat.util.json.JSONParser;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.*;

/**
 * Класс обработчик HTTP запросов работы с записями сотрудников
 */
@Controller
@RequestMapping("/inbulk")

@RequiredArgsConstructor
public class WebController {
    /**
     * Сервис для работы с записями сотрудников
     */

    private final WorkerService service;
    private ArrayList<MonthSalary> monthSalaries;
    private Collection<Worker> workers;

    private void getAllWorkers() {
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Обработка запроса на стартовую страницу
     *
     * @return стартовая страница
     */
    @GetMapping("")
    public String startPage(Model model) {
        getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);

        return "index";
    }

    /**
     * Обработка страницы с установкой даты расчета заработной платы
     *
     * @param date  Возвращаемый из формы HTML объект с датами расчета заработной платы
     * @param model Создаем новую модель для новой страницы
     * @return
     */
    @PostMapping("/setworkdate")
    public String setWorkDate(@ModelAttribute("estimatedDate") String date, Model model) {
        getAllWorkers();
        model.addAttribute("workers", workers);
        EstimatedDate.setDateForDB(date);
        EstimatedDate.setDateForHTML(date);
        Hollydays.yearHolidays.clear();
        try {
            String year = EstimatedDate.dateForHTML.substring(EstimatedDate.dateForHTML.indexOf(" "));
            String message = service.getHollydays(year);
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            if (message == null) {
                model.addAttribute("message", "Дата расчета установлена " + EstimatedDate.dateForHTML);
            } else {
                model.addAttribute("message", message);
            }
            return "result";
        } catch (StringIndexOutOfBoundsException e) {
            model.addAttribute("message", "Ошибка выбора даты");
            return "result";
        }
    }

    /**
     * Обработка запроса страницы сохранения данных посещения за месяц сотрудниками
     *
     * @return страница с загрузкой файла
     */
    @GetMapping("/writer")
    public String writeData(Model model) {
        getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        return "writer";
    }

    /**
     * Обработка запроса страницы добавления нового сотрудника
     *
     * @param model Создаем новую модель для новой страницы
     * @return страницу с добавлением нового сотрудника
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addWorker(Model model) {
        getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("worker", new Worker());
        return "addworker";
    }

    /**
     * Обработка страницы добавления нового сотрудника
     *
     * @param worker Новый сотрудник
     * @param model  Создаем новую модель для новой страницы
     * @return страницу с рзультатом операции по добавлению нового сотрудника
     */
    @RequestMapping(value = "/addworker", method = RequestMethod.POST)
    public String addNewWorker(@ModelAttribute("worker") Worker worker, Model model) {
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        getAllWorkers();
        for (Worker w : workers) {
            if (w.getName().equals(worker.getName())) {
                model.addAttribute("message", "Сотрудник с таким именем уже присутствует");
                model.addAttribute("workers", workers);
                return "result";
            }
        }
        service.createWorker(worker);
        getAllWorkers();
        model.addAttribute("message", "Операция добавления нового сотрудника выполнена " +
                "успешно");
        model.addAttribute("workers", workers);
        return "result";
    }


    /**
     * Обработка запроса на удаление сотрудника
     *
     * @param model Создаем новую модель для новой страницы
     * @return страницу удаления сотрудника
     */
    @GetMapping("/del")
    public String delWorker(Model model) {
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        return "delworker";
    }

    /**
     * Метод обработки удаления сотрудника
     *
     * @param id    идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страницу с результатом операции по удалению сотрудника
     */
    @PostMapping("/delworker")
    public String deleteWorker(@ModelAttribute("selected") Long id, Model model) {
        Worker worker = service.getWorkerById(id);
        service.deleteWorker(worker);
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", String.format("Операция удаления сотрудника %s выполнена " +
                "успешно", worker.getName()));
        return "result";
    }

    /**
     * Метод обработки удаления через GET запрос
     *
     * @param id    идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страницу с результатом операции по удалению сотрудника
     */
    @GetMapping("/delw/{id}")
    public String getDelWorker(@PathVariable Long id, Model model) {
        Worker worker = service.getWorkerById(id);
        service.deleteWorker(worker);
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", String.format("Операция удаления сотрудника %s выполнена " +
                "успешно", worker.getName()));
        return "result";
    }

    /**
     * Обработка запроса на редактирование сотрудника
     *
     * @param model Создаем новую модель для новой страницы
     * @return страницу редактируемого сотрудника
     */
    @GetMapping("/edit")
    public String edit(Model model) {
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        return "edit";
    }

    /**
     * Метод обработки редактирования сотрудника
     *
     * @param id    идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страницу с рзультатом операции по добавлению нового сотрудника
     */
    @PostMapping("/editworker")
    public String editWorker(@ModelAttribute("selected") Long id, Model model) {
        Worker worker = service.getWorkerById(id);
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("worker", worker);
        return "editworker";
    }

    /**
     * Метод обработки редактирования сотрудника с кнопки сотрудника на правой панеле со списком сотрудников в HTML
     *
     * @param id    идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страница редактирования сотрудника
     */
    @GetMapping("/worker/{id}")
    public String eWorker(@PathVariable("id") Long id, Model model) {
        Worker worker = service.getWorkerById(id);
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("worker", worker);
        return "editworker";
    }

    /**
     * Метод редактирования сотрудника
     *
     * @param model  Создаем новую модель для новой страницы
     * @param worker Редактируемый сотрудник
     * @return страница с результатами выполнения метода
     */
    @PostMapping("/workeredit")
    public String saveEditWorker(Model model, Worker worker) {
        service.updateWorker(worker);
        getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", String.format("Операция редактирования сотрудника %s выполнена " +
                "успешно", worker.getName()));
        return "result";
    }

    /**
     * Метод обработки загрузки новых данных о посещение за месяц
     *
     * @param file  файл с данными посещения
     * @param model Создаем новую модель для новой страницы
     * @return страница с результатами выполнения метода
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam MultipartFile file, Model model) throws IOException, SQLException, ClassNotFoundException {
        getAllWorkers();
        if (EstimatedDate.dateForHTML == "не установлена") {
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", "Вы не установили дату расчета");
        } else {
            String message = service.addReportCard(file);
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", message);
        }
        return "result";
    }

    /**
     * Метод обработки запроса на расчет зарплаты всех сотрудников за определенный месяц
     *
     * @param model
     * @return страницу с расчетами зарплаты
     * @throws SQLException
     * @throws IOException
     */

    @GetMapping("/calcall")
    public String allWorkersSallary(Model model) throws SQLException {
        getAllWorkers();
        if (EstimatedDate.dateForHTML == "не установлена") {
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", "Вы не установили дату расчета");
            return "result";
        } else {
            double fullPayment = 0;
            monthSalaries = service.getAllWorkersSalaryInMonth(EstimatedDate.dateForDB);
            for (MonthSalary salary : monthSalaries) {
                fullPayment = fullPayment + salary.getFullSalary();
            }
            model.addAttribute("allsalary", monthSalaries);
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("fullpayment", fullPayment);
            return "allsalary";
        }
    }

    @PostMapping("/prepayment/{id}")
    public String prepayment(@PathVariable("id") Long id, Double prepayment, Model model) {
        double fullPayment = 0;
        for (MonthSalary salary : monthSalaries) {
            fullPayment = fullPayment + salary.getFullSalary();
        }
        for (MonthSalary oneWorkersalary : monthSalaries) {
            if (oneWorkersalary.getWorkerId().equals(id)) {
                oneWorkersalary.setFullSalary(oneWorkersalary.getFullSalary() - prepayment);
                oneWorkersalary.setPrepayment(prepayment);
            }
        }
        workers = service.getAllWorkers();
        model.addAttribute("prepayment", prepayment);
        model.addAttribute("allsalary", monthSalaries);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("allsalary", monthSalaries);
        model.addAttribute("workers", workers);
        model.addAttribute("fullpayment", fullPayment);
        return "allsalary";
    }


    @GetMapping("/save")
    public String save(Model model) throws IOException {
        getAllWorkers();
        String message = service.saveTo(monthSalaries);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", "Данные успешно сохранены " + message);
        return "result";
    }

    @GetMapping("/info/{id}")
    public String getMonthInfo(@PathVariable("id") Long id, Model model) throws SQLException {
        Map times = service.getMonthTimes(EstimatedDate.dateForDB, id);
        getAllWorkers();
        MonthTime edittimes = new MonthTime();
        for (Worker worker: workers) {
            if (id.equals(worker.getId())){
                model.addAttribute("name", worker.getName());
                model.addAttribute("id", worker.getId());
                break;
            }
        }
        model.addAttribute("edittimes", edittimes);
        model.addAttribute("monthtimes", times);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        return "monthinfo";
    }

    @PostMapping("/save/{id}")
     protected void printRequest(@PathVariable("id") Long id, MonthTime times, HttpServletResponse httpServletResponse) throws SQLException {

        service.updateTimes(times, id);
        httpServletResponse.setHeader("Location", "http://localhost:8080/inbulk/calcall");
        httpServletResponse.setStatus(302);

        }




}






