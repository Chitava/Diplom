package chitava.diplom.services.implServices;

import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.repositorys.WorkedHourRepository;
import chitava.diplom.services.WorkedHoursService;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalTime;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImplementWorkedHoursService implements WorkedHoursService {
    @Autowired
   private  WorkedHourRepository repository;


    @Override
    public void createTable(String name) {
        repository.createTable(name);
    }
}
