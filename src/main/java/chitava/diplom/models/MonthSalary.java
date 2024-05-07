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
    private int hollydays;
    private double overTimes;
    private double salary;
    private double overSalary;
    private double prepayment;
    private double hollydayElaborTime;
    private double hollydaySalary;
    private double fullSalary;


    public MonthSalary(Long workerId, String workerName, int workDays, int hollydays, double overTimes, double salary,
                       double overSalary, double hollydayElaborTime, double hollydaySalary, double fullSalary) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.workDays = workDays;
        this.hollydays = hollydays;
        this.overTimes = overTimes;
        this.salary = salary;
        this.overSalary = overSalary;
        this.hollydayElaborTime = hollydayElaborTime;
        this.hollydaySalary = hollydaySalary;
        this.fullSalary = fullSalary;
    }
}
