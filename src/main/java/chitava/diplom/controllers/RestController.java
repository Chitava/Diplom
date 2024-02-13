package chitava.diplom.controllers;

import chitava.diplom.models.Hollyday;
import chitava.diplom.models.Hollydays;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@AllArgsConstructor
@RequestMapping("/inbulk")
public class RestController {
    WorkerService service;

    @GetMapping("/hollydays/{year}")
    public void getHollydays(@PathVariable String year){
        Hollydays hollydays = service.getHollydays(String.format("https://production-calendar.ru/get/ru/%s/json", year));
        for (Hollyday day: hollydays.getDays()) {
            System.out.println(day.getDate().replace("."+year,""));
        }
    }

}
