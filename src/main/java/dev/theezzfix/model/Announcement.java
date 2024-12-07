// src/main/java/dev/theezzfix/model/Announcement.java

package dev.theezzfix.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Document(collection = "announcement")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private String imageId; 
    private Date timestamp;

    public String getId() {
        return id != null ? id.toHexString() : null;
    }
}
