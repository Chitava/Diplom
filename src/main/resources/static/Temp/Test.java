import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;


public class Test {
    private WorkedHours hours = new WorkedHours();
    private Worker worker;
    private Collection<LocalTime> workedTimes;
    private LocalTime workedTime;


    public String addWorker(String path) throws IOException {
        int count = 0;
        try {
            POIFSFileSystem pSystem = new POIFSFileSystem(new File(path));
            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
            HSSFSheet sheet = hb.getSheetAt(0);
            int lastrow = sheet.getLastRowNum();
            for (int i = 0; i < lastrow + 1; i++) {
                Row row = sheet.getRow(i);
                int lastCell = row.getLastCellNum()-1;
                try {
                    Integer.parseInt(String.valueOf(row.getCell(0)));
                    String workerName = String.valueOf(row.getCell(1)).replace("\n", "");
                    worker = new Worker();
                    worker.setName(workerName);
                    workedTimes = new ArrayList<>();
                    for (int j = 3; j < lastCell; j++) {
                        workedTime = addTime(row.getCell(j));
                        workedTimes.add(workedTime);
                    }
                } catch (NumberFormatException e) {
                    for (int j = 3; j < lastCell; j++) {
                        workedTime = addTime(row.getCell(j));
                        workedTimes.add(workedTime);
                    }
                    hours.addTime(worker, workedTimes);

                } catch (Exception e) {
                    return "В процессе добавления новых сотрудников произошла ошибка " + e.getMessage();
                }
            }System.out.println(hours.getWorkedHours().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "Добавлено успешно";

    }

    public LocalTime addTime(Cell cell) {
        LocalTime workedTime = LocalTime.of(0, 0);
        String time = cell.getStringCellValue();
        if (!time.equals("--\n--\n--") && time.length() != 0) {
            String[] str = time.split("\n");
            int hour = Integer.parseInt(str[2].substring(0, str[2].indexOf(":")));
            int minute = Integer.parseInt(str[2].substring(str[2].indexOf(":") + 1));
            workedTime = LocalTime.of(hour, minute);

        }
        return workedTime;
    }
}

//
//    public String addWorkedHours(MultipartFile file) throws IOException {
//        Worker worker;
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        try {
//            POIFSFileSystem pSystem = new POIFSFileSystem(file.getInputStream());
//            HSSFWorkbook hb = new HSSFWorkbook(pSystem);
//            HSSFSheet sheet = hb.getSheetAt(0);
//            int lastrow = sheet.getLastRowNum();
//            for (int i = 0; i < lastrow; i = i + 2) {
//                Row row = sheet.getRow(i);
//                int lastCell = row.getLastCellNum();
//                try {
//                    Integer.parseInt(String.valueOf(row.getCell(0)));
//                    worker = repository.findByName(String.valueOf(row.getCell(1)));
//                    workedHours = new WorkedHours();
//                    workedHours.setWorker(worker);
//                    for (int j = 3; j < lastCell - 1; j++) {
//                        String fullTime = String.valueOf(row.getCell(j));
//                        addTime(fullTime);
//                    }
//                    System.out.println(worker.getName());
//                } catch (NumberFormatException e) {
//                    for (int j = 3; j < lastCell - 1; j++) {
//                        String fullTime = String.valueOf(row.getCell(j));
//                        addTime(fullTime);
//                    }
//                }for (LocalDateTime time: workedHours.getWorkedDaysHours()) {
//                    System.out.print(time.format(formatter) + " ч. ");
//                }
//                System.out.println("");
//                return "Новые данные о посещении загружены успешно";
//            }
//        } catch (Exception e) {
//            return "В процессе добавления новых данных произошла ошибка " + e.getMessage();
//        }
//        return null;
//    }