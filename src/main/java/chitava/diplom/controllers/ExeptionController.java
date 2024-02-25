package chitava.diplom.controllers;
import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.Worker;
import chitava.diplom.services.JDBCService;
import chitava.diplom.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Collection;

@ControllerAdvice
@RequiredArgsConstructor
public class ExeptionController {
    private final WorkerService service;
    private final JDBCService jdbc;

    @ExceptionHandler({SQLSyntaxErrorException.class})
    protected String SQLSyntaxErrorException(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", "Данные за " + EstimatedDate.dateForHTML
                + " не обнаружены");
        return "result";
    }


    @ExceptionHandler({StringIndexOutOfBoundsException.class})
    protected String dateError(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", "Не выбрана расчетная дата");
        return "result";
    }

    @ExceptionHandler({IOException.class})
    protected String IoError(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", "Ошибка сохранения данных");
        return "result";
    }




}

