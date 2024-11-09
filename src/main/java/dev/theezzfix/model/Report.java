package dev.theezzfix.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Document(collection = "reports")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Report {
    @Id
    private ObjectId id;

    private String title;
    private String description;
    private String reportedBy;
    private String status;
    private Date createdAt;
    private Date updatedAt;

}
