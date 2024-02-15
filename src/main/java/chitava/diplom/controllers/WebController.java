package chitava.diplom.controllers;

import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.Hollydays;
import chitava.diplom.models.Worker;
import chitava.diplom.services.WorkedHoursService;
import chitava.diplom.services.WorkerService;
import chitava.diplom.services.implServices.ImplementWorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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
    private final EstimatedDate estimatedDate;

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
            model.addAttribute("estimatedDate", estimatedDate);
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
            model.addAttribute("message", "Ошибка базы данных" + e);
            return "result";
        }
        model.addAttribute("workers", workers);
        estimatedDate.setDateForDB(date.getDateForDB());
        estimatedDate.setDateForHTML(date.getDateForDB());
        Hollydays.yearHolidays.clear();
        try {
            String year = estimatedDate.getDateForHTML().substring(estimatedDate.getDateForHTML().indexOf(" "));
            String message = service.getHollydays(year);
            if (message == null) {
                model.addAttribute("estimatedDate", estimatedDate);
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
        Collection<Worker> workers = null;
        try {
            workers = service.getAllWorkers();
        } catch (Exception e) {
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
     *
     * @param worker Новый сотрудник
     * @param model  Создаем новую модель для новой страницы
     * @return страницу с рзультатом операции по добавлению нового сотрудника
     */
    @RequestMapping(value = "/addworker", method = RequestMethod.POST)
    public String addNewWorker(@ModelAttribute("worker") Worker worker, Model model) {
        model.addAttribute("estimatedDate", estimatedDate);
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
        model.addAttribute("estimatedDate", estimatedDate);
        model.addAttribute("workers", workers);
        return "delworker";
    }

    /**
     * Метод обработки удаления сотрудника
     *
     * @param id    идентификатор сотрудника
     * @param model Создаем новую модель для новой страницы
     * @return страницу с рзультатом операции по добавлению нового сотрудника
     */
    @PostMapping("/delworker")
    public String deleteWorker(@ModelAttribute("selected") Long id, Model model) {
        Worker worker = service.getWorkerById(id);
        service.deleteWorker(worker);
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", estimatedDate);
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
        model.addAttribute("estimatedDate", estimatedDate);
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
        model.addAttribute("estimatedDate", estimatedDate);
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
        model.addAttribute("estimatedDate", estimatedDate);
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
        model.addAttribute("estimatedDate", estimatedDate);
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
        String message = service.addWorker(file);
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", estimatedDate);
        model.addAttribute("workers", workers);
        model.addAttribute("message", message);
        return "result";
    }


}




