package dev.theezzfix.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.theezzfix.model.Report;
import dev.theezzfix.repository.ReportRepository;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public Report createReport(Report report){
        report.setId(ObjectId.get());
        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());
        return reportRepository.save(report);
    }

    public List<Report> getAllReports(){
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(ObjectId id){
        return reportRepository.findById(id);
    };

    public Report updateReport(ObjectId id, Report reportDetails) throws Exception{
        Report report = reportRepository.findById(id).orElseThrow(()-> new Exception("Report not found for this id : " + id));
        report.setStudentId(reportDetails.getStudentId());
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
        report.setAttachments(null);
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
}
