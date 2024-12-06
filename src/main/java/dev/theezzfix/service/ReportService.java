package dev.theezzfix.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.theezzfix.dto.CreateReportRequest;
import dev.theezzfix.model.Report;
import dev.theezzfix.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public ReportService(ReportRepository reportRepository, FileStorageService fileStorageService) {
        this.reportRepository = reportRepository;
        this.fileStorageService = fileStorageService;
    }

    public Report createReportWithAttachments(CreateReportRequest reportRequest) throws Exception {
        ObjectId studentObjectId;
        if (reportRequest.getStudentId() != null && ObjectId.isValid(reportRequest.getStudentId())) {
            studentObjectId = new ObjectId(reportRequest.getStudentId());
        } else {
            throw new IllegalArgumentException("Invalid or missing studentId");
        }
    
        Report report = new Report();
        report.setStudentId(studentObjectId);
        report.setTitle(reportRequest.getTitle());
        report.setLocation(reportRequest.getLocation());
        report.setRoomNo(reportRequest.getRoomNo());
        report.setCategory(reportRequest.getCategory());
        report.setDescription(reportRequest.getDescription());
        report.setReportedBy(reportRequest.getReportedBy());
        report.setStatus(reportRequest.getStatus());
        report.setAssignedTo(reportRequest.getAssignedTo());
        report.setTechnicianNo(reportRequest.getTechnicianNo());
        report.setDuplicate(reportRequest.isDuplicate());
    
        if (reportRequest.getDuplicateOf() != null && ObjectId.isValid(reportRequest.getDuplicateOf())) {
            report.setDuplicateOf(new ObjectId(reportRequest.getDuplicateOf()));
        }
    
        List<String> attachmentIds = new ArrayList<>();
    
        if (reportRequest.getAttachments() != null && !reportRequest.getAttachments().isEmpty()) {
            for (MultipartFile file : reportRequest.getAttachments()) {
                if (!file.isEmpty()) {
                    String fileId = fileStorageService.storeFile(file);
                    attachmentIds.add(fileId);
                }
            }
        }
    
        if (!attachmentIds.isEmpty()) {
            report.setAttachments(attachmentIds);
        } else {
            report.setAttachments(null);
        }
    
        Date now = new Date();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);
    
        return reportRepository.save(report);
    }

    public List<Report> getAllReports(){
        return reportRepository.findAll();
    }

    public List<Report> getReportsByStudentId(ObjectId studentId) {
        return reportRepository.findByStudentId(studentId);
    }    
    
    public Optional<Report> getReportById(ObjectId id){
        return reportRepository.findById(id);
    };

    public Report updateReport(ObjectId id, Report reportDetails) throws Exception {
        Report report = reportRepository.findById(id).orElseThrow(() -> new Exception("Report not found for this id: " + id));
        if (reportDetails.getStudentId() != null) {
            report.setStudentId(new ObjectId(reportDetails.getStudentId())); 
        }
        report.setTitle(reportDetails.getTitle());
        report.setDescription(reportDetails.getDescription());
        report.setStatus("Pending");
        report.setLocation(reportDetails.getLocation());
        report.setRoomNo(reportDetails.getRoomNo());
        report.setCategory(reportDetails.getCategory());
        report.setAssignedTo(reportDetails.getAssignedTo());
        report.setTechnicianNo(reportDetails.getTechnicianNo());
        report.setDuplicate(false);
        report.setDuplicateOf(null);
        report.setUpdatedAt(new Date());
    
        return reportRepository.save(report);
    }
    
    public Report patchReport(ObjectId id, Report reportPatch) throws Exception{
        Report existingReport = reportRepository.findById(id).orElseThrow(()-> new Exception("Report not found for this id : " + id));
        if(reportPatch.getStatus() != null){
            existingReport.setStatus(reportPatch.getStatus());
        }
        if(reportPatch.getAssignedTo() != null){
            existingReport.setAssignedTo(reportPatch.getAssignedTo());
        }
        if(reportPatch.getTechnicianNo() != null){
            existingReport.setTechnicianNo(reportPatch.getTechnicianNo());
        }
        existingReport.setUpdatedAt(new Date());
        return reportRepository.save(existingReport);
    }

    public void deleteReport(ObjectId id) throws Exception{
        Report report = reportRepository.findById(id).orElseThrow(()-> new Exception("Report not found for this id : " + id));  
        reportRepository.delete(report);
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }
}
