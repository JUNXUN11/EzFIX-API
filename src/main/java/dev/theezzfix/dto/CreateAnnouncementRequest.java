package dev.theezzfix.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateAnnouncementRequest {
    private String title;
    private String description;
    private MultipartFile image;
}
