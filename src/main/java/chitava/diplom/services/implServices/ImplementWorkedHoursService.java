package chitava.diplom.services.implServices;

import chitava.diplom.models.WorkedHours;
import chitava.diplom.models.Worker;
import chitava.diplom.services.WorkerService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalTime;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class ImplementWorkedHoursService {
    private Worker worker;
    private WorkerService workerService;
    private WorkedHours workedHours;

}
