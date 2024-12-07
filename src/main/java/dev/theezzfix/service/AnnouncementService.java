package dev.theezzfix.service;

import dev.theezzfix.dto.CreateAnnouncementRequest;
import dev.theezzfix.model.Announcement;
import dev.theezzfix.repository.AnnouncementRepository;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository, FileStorageService fileStorageService) {
        this.announcementRepository = announcementRepository;
        this.fileStorageService = fileStorageService;
    }

    public Announcement createAnnouncement(CreateAnnouncementRequest announcementRequest) throws Exception {
        Announcement announcement = new Announcement();
        announcement.setTitle(announcementRequest.getTitle());
        announcement.setDescription(announcementRequest.getDescription());
        announcement.setTimestamp(new Date());

        
        MultipartFile image = announcementRequest.getImage();
        if (image != null && !image.isEmpty()) {
            String imageId = fileStorageService.storeFile(image);
            announcement.setImageId(imageId);
        }

        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Optional<Announcement> getAnnouncementById(ObjectId id) {
        return announcementRepository.findById(id);
    }

    // Delete announcement by ID
    public void deleteAnnouncement(ObjectId id) throws Exception {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);
        if (optionalAnnouncement.isPresent()) {
            Announcement announcement = optionalAnnouncement.get();
            String imageId = announcement.getImageId();
            if (imageId != null) {
                fileStorageService.deleteFile(imageId);
            }
            announcementRepository.deleteById(id);
        } else {
            throw new Exception("Announcement not found with id: " + id.toHexString());
        }
    }

    // Get image resource by imageId
    public GridFsResource getImageById(String imageId) throws IOException {
        return fileStorageService.getFile(imageId)
            .orElseThrow(() -> new IOException("Image not found with id: " + imageId));
    }
}
