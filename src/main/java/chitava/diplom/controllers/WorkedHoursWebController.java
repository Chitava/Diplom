package chitava.diplom.controllers;


import chitava.diplom.models.EstimatedDate;
import chitava.diplom.services.WorkedHoursService;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

/**
 * Класс обработчик HTTP запросов работы с записями сотрудников
 */
@Controller
@RequestMapping("/inbulk")
@AllArgsConstructor
public class WorkedHoursWebController {

    private  WorkedHoursService service;
    private  WorkerService workerService;



    @GetMapping("/all")
    public String all(Model model){
        service.createTable("2024");
        Collection workers = workerService.getAllWorkers();
        model.addAttribute("workers", workers);
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        return "index";
    }
}
