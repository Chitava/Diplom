package chitava.diplom.services.implServices;

import chitava.diplom.models.EstimatedDate;
import chitava.diplom.models.MonthSalary;
import chitava.diplom.services.SendTo;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;



@Service
@ConfigurationProperties
public class ExcelSender implements SendTo {

    @Value("${URL_SAVE}")
    private  String URL_SAVE;

    @Override
    public void sendTo(ArrayList<MonthSalary> salarys, HttpServletResponse response) throws IOException {

        int rowIndex = 1;
        Row row;
        Files.createDirectories(Paths.get(URL_SAVE));
        File file = new File("Зарплата за " + EstimatedDate.dateForHTML + ".xls");
        String[] nameCol = {"№", "ФИО", "Отработано дней", "Выходные и праздники", "Часов переработки", "Зарплата за дни",
                "Зарплата за переработку", "Зарплата за выходные", "Время переработки в выходные", "Зарплата за переработку в выходные", "Зарплата за месяц", "Аванс", "Итого на руки"};
        try (FileOutputStream stream = new FileOutputStream(file)) {
            Workbook book = new HSSFWorkbook();
            Sheet sheet = book.createSheet("Зарплата за +" + EstimatedDate.dateForHTML);
            row = sheet.createRow(0);
            for (int i = 0; i < nameCol.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(nameCol[i]);
            }
            for (MonthSalary salary : salarys) {
                int colIndex = 0;
                row = sheet.createRow(rowIndex);
                Cell cell = row.createCell(colIndex);
                cell.setCellValue(rowIndex);
                rowIndex++;
                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getWorkerName());//Имя сотрудника

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getWorkDays());//Количество отработанных дней

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getHollydays());//Кличество выходных и праздников

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getOverTimes());//Время переработки

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getSalary());//Зарплата за основоное время

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getOverSalary());//Зарплата за переработку

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getHollydaySalary());//Зарплата за выходной

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getHollydayElaborTime());//Время переработки в выходные

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getHollydayOverSalary());//Зарплата за переработку в выходные

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(salary.getFullSalary());//Полная зарплата

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellValue(0.0);//аванс

                colIndex++;
                cell = row.createCell(colIndex);
                cell.setCellFormula("K" + rowIndex + "-" + "L" + rowIndex);

                book.write(new FileOutputStream(file));
                book.write(stream);

            }
            try {

                if (stream != null)
                    stream.close();
            } catch (Exception e) {
                throw new IOException("Ошибка сохранения файла");
            }

        } catch (IOException e) {
            throw new IOException("Ошибка сохранения файла");
        }
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        response.setContentType(contentType);
        response.setContentLengthLong(Files.size(file.toPath()));
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(file.getName(), StandardCharsets.UTF_8)
                .build()
                .toString());
        Files.copy(file.toPath(), response.getOutputStream());
        response.flushBuffer();
        FileUtils.deleteDirectory(Paths.get(URL_SAVE).toFile());

    }

}

