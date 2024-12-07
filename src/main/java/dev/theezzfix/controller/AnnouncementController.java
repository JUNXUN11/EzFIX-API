package dev.theezzfix.controller;

import dev.theezzfix.dto.CreateAnnouncementRequest;
import dev.theezzfix.model.Announcement;
import dev.theezzfix.service.AnnouncementService;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/announcements")
@CrossOrigin(origins = "*") 
public class AnnouncementController {
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    @Autowired
    private AnnouncementService announcementService;

    // POST: Upload a new announcement with image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Announcement> createAnnouncement(@ModelAttribute CreateAnnouncementRequest announcementRequest) {
        try {
            Announcement savedAnnouncement = announcementService.createAnnouncement(announcementRequest);
            return new ResponseEntity<>(savedAnnouncement, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input data: ", e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating announcement with image", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Retrieve all announcements
    @GetMapping
    public ResponseEntity<List<Announcement>> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getAllAnnouncements();
            if (announcements.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(announcements, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all announcements", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Retrieve announcement by ID
    @GetMapping("/{id}")
    public ResponseEntity<Announcement> getAnnouncementById(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Announcement> announcementOpt = announcementService.getAnnouncementById(objectId);
            if (announcementOpt.isPresent()) {
                return new ResponseEntity<>(announcementOpt.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid announcement ID format: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error retrieving announcement with id: {}", id, e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET: Retrieve announcement image by announcement ID
    @GetMapping("/{id}/image")
    public ResponseEntity<?> getAnnouncementImage(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Optional<Announcement> announcementOpt = announcementService.getAnnouncementById(objectId);
            if (!announcementOpt.isPresent()) {
                logger.error("Announcement not found with ID: {}", id);
                return new ResponseEntity<>("Announcement not found.", HttpStatus.NOT_FOUND);
            }

            Announcement announcement = announcementOpt.get();
            String imageId = announcement.getImageId();
            if (imageId == null || imageId.isEmpty()) {
                logger.error("No image associated with announcement ID: {}", id);
                return new ResponseEntity<>("No image associated with this announcement.", HttpStatus.NOT_FOUND);
            }

            GridFsResource imageResource = announcementService.getImageById(imageId);
            if (imageResource == null || !imageResource.exists()) {
                logger.error("Image not found with ID: {}", imageId);
                return new ResponseEntity<>("Image not found.", HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageResource.getContentType()))
                    .body(new InputStreamResource(imageResource.getInputStream()));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid announcement ID format: {}", id, e);
            return new ResponseEntity<>("Invalid announcement ID.", HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            logger.error("Error retrieving image for announcement ID: {}", id, e);
            return new ResponseEntity<>("Error retrieving image.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error retrieving announcement image with id: {}", id, e);
            return new ResponseEntity<>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE: Delete an announcement by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAnnouncement(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            announcementService.deleteAnnouncement(objectId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid announcement ID format: {}", id, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error deleting announcement with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
