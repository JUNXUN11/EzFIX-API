package dev.theezzfix.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CreateReportRequest {
    private String studentId;
    private String title;
    private String location;
    private Integer roomNo;
    private String category;
    private String description;
    private String reportedBy;
    private String status;
    private String assignedTo;
    private String technicianNo;
    private boolean isDuplicate;
    private String duplicateOf;
    private List<MultipartFile> attachments; 
}
