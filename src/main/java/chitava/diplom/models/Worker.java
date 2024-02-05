package chitava.diplom.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Класс сотрудник
 * Сохраняется в базе данных в таблице workers
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "workers")
public class Worker {
    /**
     * Поле id - идентификационный номер сотрудника
     * Поле name - ФИО сотрудника
     * Поле post - определяет является ли сотрудник управляющим персоналом, учитывается при расчете заработной платы
     * Поле paymentInDay - ставка сотрудника в рабочий день
     * Поле paymentInHour - ставка сотрудника в час, учитывается при переработке сотрудником стандартного рабочего
     * времени 8 часов
     * Поле peymentInHollydays - ставка в выходные и праздничные дни, учитывается только у управляющего персонала
     */
    //region Поля
    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean post = false;
    private double paymentInDay;
    private double paymentInHour;
    private double peymentInHollydays;
    //endregion

    public boolean getPost() {
        return post;
    }


    public void setPost(boolean post) {
        this.post = post;
    }
}
