package dev.theezzfix.controller;

import dev.theezzfix.model.Report;
import dev.theezzfix.service.ReportService;


import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<Report> createReport(@RequestBody Report report) {
        try {
            Report savedReport = reportService.createReport(report);
            return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error getting all reports", e);
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
