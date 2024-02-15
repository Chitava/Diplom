package chitava.diplom.services.implServices;

import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.repositorys.WorkersRepository;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImplementWorkedHoursService {


    private Worker worker;
    private WorkerService workerService;
    private WorkedHours workedHours;


    public void addTime(String times) {
        if (!times.equals("--\n--\n--") && times.length() != 0) {
            String[] str = times.split("\n");
            int hour = Integer.parseInt(str[2].substring(0, str[2].indexOf(":")));
            int minute = Integer.parseInt(str[2].substring(str[2].indexOf(":") + 1));
            LocalDateTime time = LocalDateTime.of(2024, 02, 1, hour, minute);
            workedHours.addTime(time);
        }
    }

    public String addWorkedHours(MultipartFile file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());
            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
            HSSFSheet sheet = hb.getSheetAt(0);
            int lastrow = sheet.getLastRowNum();
            for (int i = 0; i < lastrow; i = i + 2) {
                Row row = sheet.getRow(i);
                int lastCell = row.getLastCellNum();
                try {
                    Integer.parseInt(String.valueOf(row.getCell(0)));
                    worker = workerService.findByName(String.valueOf(row.getCell(1)));
                    workedHours = new WorkedHours();
                    workedHours.setWorker(worker);
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        addTime(fullTime);
                    }
                    System.out.println(worker.getName());
                } catch (NumberFormatException e) {
                    for (int j = 3; j < lastCell - 1; j++) {
                        String fullTime = String.valueOf(row.getCell(j));
                        addTime(fullTime);
                    }
                }for (LocalDateTime time: workedHours.getWorkedDaysHours()) {
                    System.out.print(time.format(formatter) + " ч. ");
                }
                System.out.println("");
                return "Новые данные о посещении загружены успешно";
            }
        } catch (Exception e) {
            return "В процессе добавления новых данных произошла ошибка " + e.getMessage();
        }
        return null;
    }
}
