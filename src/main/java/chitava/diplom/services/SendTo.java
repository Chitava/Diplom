package chitava.diplom.services;

import chitava.diplom.models.MonthSalary;
import chitava.diplom.models.Worker;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;


public interface SendTo {
    void sendTo(ArrayList<MonthSalary> sallary, HttpServletResponse response) throws IOException;
}
