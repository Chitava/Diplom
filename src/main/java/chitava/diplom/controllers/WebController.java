package chitava.diplom.controllers;

import chitava.diplom.models.EstimatedDate;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * Обработка запроса на стартовую страницу
     *
     * @return стартовая страница
     */
    @GetMapping("")
    public String startPage(Model model) {
        model.addAttribute("estimatedDate", estimatedDate);
        return "index";
    }

    @PostMapping("/setworkdate")
    public String setWorkDate(@ModelAttribute("estimatedDate") EstimatedDate date, Model model) {
        estimatedDate.setDateForDB(date.getDateForDB());
        estimatedDate.setDateForHTML(date.getDateForDB());
        model.addAttribute("estimatedDate", estimatedDate);
        return "index";

    }


}
