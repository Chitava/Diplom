package chitava.diplom.controllers;

import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.Worker;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Класс обработчик HTTP запросов
 */
@Controller
@RequestMapping("/inbulk")
@AllArgsConstructor
public class WebController {
    /**
     * Сервис для работы с записями сотрудников
     */
    private final WorkerService service;
    /**
     * Получение даты работы с базой данных
     */
    private EstimatedDate estimatedDate;

    /**
     * Обработка запроса на стартовую страницу     *
     *
     * @return стартовая страница
     */
    @GetMapping("")
    public String startPage(Model model) {
        Collection <Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        }catch (Exception e){
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", estimatedDate);
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
    public String setWorkDate(@ModelAttribute("estimatedDate") EstimatedDate date, Model model) {
        Collection <Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        }catch (Exception e){
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        estimatedDate.setDateForDB(date.getDateForDB());
        estimatedDate.setDateForHTML(date.getDateForDB());
        model.addAttribute("estimatedDate", estimatedDate);
        return "index";
    }

    /**
     * Обработка запроса страницы сохранения данных посещения за месяц сотрудниками
     *
     * @return страница с загрузкой файла
     */
    @GetMapping("/writer")
    public String writeData(Model model) {
        Collection <Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        }catch (Exception e){
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", estimatedDate);
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
        Collection <Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        }catch (Exception e){
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", estimatedDate);
        model.addAttribute("worker", new Worker());
        return "addworker";
    }

    /**

     * Обработка страницы добавления нового сотрудника
     * @param worker Новый сотрудник
     * @param model Создаем новую модель для новой страницы
     * @return страницу с рзультатом операции по добавлению нового сотрудника
     */
    @RequestMapping(value = "/addworker", method = RequestMethod.POST)
    public String addNewWorker(@ModelAttribute("worker") Worker worker, Model model) {
        model.addAttribute("estimatedDate", estimatedDate);
        Collection <Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        }catch (Exception e){
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
     * @param model Создаем новую модель для новой страницы
     * @return страницу удаления сотрудника
     */
    @GetMapping("/del")
    public String delWorker(Model model){
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", estimatedDate);
        model.addAttribute("workers", workers);
        return "delworkers";
    }
}




