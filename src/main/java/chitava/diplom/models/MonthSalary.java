package chitava.diplom.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс подсчета зарплаты за месяц
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthSalary {
    private Long workerId;
    private String workerName;
    private int workDays;
    private int overDays;
    private double salary;
    private double overSalary;
    private double prepayment;
    private double fullSalary;
}
