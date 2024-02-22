package chitava.diplom.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс подсчета зарплаты за месяц
 */
@Data

public class MonthSalary {
    private Long workerId;
    private String workerName;
    private int workDays;
    private int overDays;
    private double salary;
    private double overSalary;
    private double prepayment;
    private double fullSalary;


    public MonthSalary(Long workerId, String workerName, int workDays, int overDays, double salary, double overSalary, double fullSalary) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.workDays = workDays;
        this.overDays = overDays;
        this.salary = salary;
        this.overSalary = overSalary;
        this.fullSalary = fullSalary;
    }
}
