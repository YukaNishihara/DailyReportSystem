package form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class ReportQuery {
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd") 
    private LocalDate endDate;

    public ReportQuery() {
       
        }

   
}
