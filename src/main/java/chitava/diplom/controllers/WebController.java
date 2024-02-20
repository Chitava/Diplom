package chitava.diplom.controllers;

import chitava.diplom.models.*;
import chitava.diplom.services.JDBCService;
import chitava.diplom.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    private final JDBCService jdbc;
    private MonthAllWorkersHours allMonthHours;


//    private final OneWorkedHours workedHours;

    /**
     * Обработка запроса на стартовую страницу
     *
     * @return стартовая страница
     */
    @GetMapping("")
    public String startPage(Model model) {
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        EstimatedDate.setDateForDB(date);
        EstimatedDate.setDateForHTML(date);
        Hollydays.yearHolidays.clear();
        try {
            String year = EstimatedDate.dateForHTML.substring(EstimatedDate.dateForHTML.indexOf(" "));
            String message = service.getHollydays(year);
            if (message == null) {
                model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
                return "index";
            } else {
                model.addAttribute("message", message);
                return "result";
            }
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        for (Worker w : workers) {
            if (w.getName().equals(worker.getName())) {
                model.addAttribute("message", "Сотрудник с таким именем уже присутствует");
                model.addAttribute("workers", workers);
                return "result";
            }
        }
        service.createWorker(worker);
        workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", String.format("Операция удаления сотрудника %s выполнена " +
                "успешно", worker.getName()));
        return "result";
    }

    /**
     * Метод обработки удаления через GET запрос
     * @param id идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страницу с результатом операции по удалению сотрудника
     */
    @GetMapping("/delw/{id}")
    public String getDelWorker(@PathVariable Long id, Model model){
        Worker worker = service.getWorkerById(id);
        service.deleteWorker(worker);
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
    public String uploadFile(@RequestParam MultipartFile file, Model model) throws IOException {
        if (EstimatedDate.dateForHTML == "не установлена"){
            Collection<Worker> workers = service.getAllWorkers();
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", "Вы не установили дату расчета");
            return "result";
        }else {
            String message = service.addReportCard(file);
            Collection<Worker> workers = service.getAllWorkers();
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", message);
            return "result";
        }
    }

    @GetMapping("/calcall")
    public String allWorkersSallary(Model model) throws SQLException, IOException {
        Collection<Worker> workers = service.getAllWorkers();
        if (EstimatedDate.dateForHTML == "не установлена") {
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);
            model.addAttribute("message", "Вы не установили дату расчета");
            return "result";
        }else {
            allMonthHours = service.getMonthTimes("salary_2024_02");
            ArrayList list = allMonthHours.getMonthAllHours();
            for (int i = 0; i < list.size(); i++) {
                System.out.println(list.get(i));
            }

            workers = service.getAllWorkers();
            model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
            model.addAttribute("workers", workers);

            return "allsallary";
        }
    }



}




