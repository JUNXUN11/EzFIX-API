package dev.theezzfix.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "reports")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Report {
    @Id
    private ObjectId id;
    private ObjectId studentId;
    private String title;
    private String location;
    private int roomNo;
    private String category;
    private String description;
    private String reportedBy;
    private String status;
    private String assignedTo;
    private String technicianNo;
    private boolean isDuplicate;
    private ObjectId duplicateOf;
    private List<String> attachments = new ArrayList<>(); 
    private Date createdAt;
    private Date updatedAt;
    public String getId() {
        return id != null ? id.toHexString() : null;
    }
    public String getStudentId() {
        return studentId != null ? studentId.toHexString() : null;
    }
    public void setStudentId(ObjectId studentId) {
        this.studentId = studentId;
    }
}
