package chitava.diplom.services;

import chitava.diplom.models.MonthSalary;
import chitava.diplom.models.Worker;

import java.io.IOException;
import java.util.ArrayList;


public interface SendTo {
    public String sendTo(ArrayList<MonthSalary> sallary) throws IOException;
}
