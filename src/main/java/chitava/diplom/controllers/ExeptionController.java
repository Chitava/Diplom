package chitava.diplom.controllers;
import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.Worker;
import chitava.diplom.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.sql.SQLSyntaxErrorException;
import java.util.Collection;

@ControllerAdvice
@RequiredArgsConstructor
public class ExeptionController {
    private final WorkerService service;

    @ExceptionHandler({SQLSyntaxErrorException.class})
    protected String SQLSyntaxErrorException(Model model) {
        Collection<Worker> workers = service.getAllWorkers();
        model.addAttribute("estimatedDate", EstimatedDate.dateForHTML);
        model.addAttribute("workers", workers);
        model.addAttribute("message", "Данные за " + EstimatedDate.dateForHTML
                + " не обнаружены");
        return "result";
    }
}

