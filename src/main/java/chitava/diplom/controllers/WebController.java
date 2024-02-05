package chitava.diplom.controllers;

import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.Worker;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    private EstimatedDate estimatedDate;

    /**
     * Обработка запроса на стартовую страницу     *
     *
     * @return стартовая страница
     */
    @GetMapping("")
    public String startPage(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
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
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", estimatedDate);
        return "writer";
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String addWorker(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", estimatedDate);
        model.addAttribute("worker", new Worker());
        return "addworker";
    }

    @RequestMapping(value = "/addworker", method = RequestMethod.POST)
    public String addNewWorker(@ModelAttribute("worker") Worker worker, Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", estimatedDate);
        try {
            model.addAttribute("workers", workers);
            service.createWorker(worker);
            model.addAttribute("message", "Операция добавления нового сотрудника выполнена " +
                    "успешно");
            return "result";
        } catch (Exception e) {
            model.addAttribute(e.getMessage());
            return "result";
        }
    }
}

