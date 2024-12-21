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
    private String assignedNo;
    private boolean isDuplicate;
    private String duplicateOf;
    private String comment;
    private List<MultipartFile> attachments; 
    private Boolean priority;

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }
}
