package chitava.diplom.models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Component
@AllArgsConstructor
@NoArgsConstructor
public class EstimatedDate {
    private String date;

    public void setDate(String date) {
        StringBuilder bilder = new StringBuilder();
        String month = date.substring(date.indexOf("-")+1);
        String year = date.substring(0, date.indexOf(("-")));
        switch (month){
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
        System.out.println(bilder);
        this.date = bilder.toString();
    }
}
