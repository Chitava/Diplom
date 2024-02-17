package chitava.diplom.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Класс дата расчета заработной платы
 * Поле dateForHTML - дата для отображение в HTML
 * Поле dateForDB - дата для названий таблиц в базе данных
 */
@Getter
@Component

public class EstimatedDate {

    //region поля
    public static String dateForHTML;
    public static String dateForDB;

    public EstimatedDate() {
        this.dateForHTML = "не установлена";
    }
    //endregion

    //region setters

    /**
     * Метод конвертации даты полученой с формы HTML
     * @param date дата из формы HTML, приходит в виде гггг-мм
     * переводим в месяц гггг
     */
    public static void setDateForHTML(String date) {
        StringBuilder bilder = new StringBuilder();
        if(date !="" && date != null ) {
            String month = date.substring(date.indexOf("-") + 1);
            String year = date.substring(0, date.indexOf(("-")));
            switch (month) {
                case "01":
                    month = "январь";
                    break;
                case "02":
                    month = "февраль";
                    break;
                case "03":
                    month = "март";
                    break;
                case "04":
                    month = "апрель";
                    break;
                case "05":
                    month = "май";
                    break;
                case "06":
                    month = "июнь";
                    break;
                case "07":
                    month = "июль";
                    break;
                case "08":
                    month = "август";
                    break;
                case "09":
                    month = "сентябрь";
                    break;
                case "10":
                    month = "октябрь";
                    break;
                case "11":
                    month = "ноябрь";
                    break;
                case "12":
                    month = "декабрь";
                    break;
            }
            bilder.append(month).append(" ").append(year);
            dateForHTML = bilder.toString();
        }else{
            dateForHTML ="не устанавливалась";
        }
    }

    /**
     *
     * @param date дата для наименования таблиц в базе данных
     */
    public static void setDateForDB(String date) {
        dateForDB = "salary_" + date.replace("-","_");
    }
    //endregion
}
