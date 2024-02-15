package chitava.diplom.services;

import org.springframework.web.multipart.MultipartFile;

public interface WorkedHoursService {

    void addTime(String times);
    String addWorkedHours(MultipartFile file);


}
