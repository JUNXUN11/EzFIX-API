package dev.theezzfix.controller;

import dev.theezzfix.dto.CreateReportRequest;
import dev.theezzfix.model.Report;
import dev.theezzfix.service.ReportService;
import dev.theezzfix.service.FileStorageService;

import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Report> createReport(@ModelAttribute CreateReportRequest reportRequest) {
        try {
            Report savedReport = reportService.createReportWithAttachments(reportRequest);
            return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input data: ", e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error creating report with attachments", e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            if (reports.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error getting all reports", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/my-reports")  
    public ResponseEntity<List<Report>> getMyReports(@RequestParam("studentId") String studentId) {
        logger.info("Received request for my-reports with studentId: {}", studentId);
        try {
            if (!ObjectId.isValid(studentId)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<Report> reports = reportService.getReportsByStudentId(new ObjectId(studentId));
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    

    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            Report report = reportService.getReportById(objectId)
                    .orElseThrow(() -> new Exception("Report not found"));
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Report> updateReport(@PathVariable("id") String id, @RequestBody Report reportDetails) {
        try {
            ObjectId objectId = new ObjectId(id);
            Report updatedReport = reportService.updateReport(objectId, reportDetails);
            return new ResponseEntity<>(updatedReport, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error updating report with id: " + id, e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Report> patchReport(@PathVariable("id")String id, @RequestBody Report reportPatch){
        try {
            ObjectId objectId = new ObjectId(id);
            Report updatedReport = reportService.patchReport(objectId, reportPatch);
            return new ResponseEntity<>(updatedReport, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error patching report with id: " + id, e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{reportId}/attachments/{fileId}")
    public ResponseEntity<?> getAttachment(@PathVariable("reportId") String reportId, @PathVariable("fileId") String fileId) {
        try {
            if (!ObjectId.isValid(reportId)) {
                logger.error("Invalid report ID format: {}", reportId);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            ObjectId reportObjectId = new ObjectId(reportId);
            Optional<Report> reportOpt = reportService.getReportById(reportObjectId);
            if (!reportOpt.isPresent()) {
                logger.error("Report not found with ID: {}", reportId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Optional<GridFsResource> resourceOpt = reportService.getFileStorageService().getFile(fileId);
            if (resourceOpt.isPresent()) {
                GridFsResource resource = resourceOpt.get();
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(resource.getContentType()))
                        .body(new InputStreamResource(resource.getInputStream()));
            } else {
                logger.error("File not found with ID: {}", fileId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            logger.error("Error retrieving file with ID: {}", fileId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteReport(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            reportService.deleteReport(objectId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
