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

        report.setTitle(reportDetails.getTitle());
        report.setDescription(reportDetails.getDescription());
        report.setStatus(reportDetails.getStatus());
        report.setUpdatedAt(new Date());

        return reportRepository.save(report);
    }

    public void deleteReport(ObjectId id) throws Exception{
        Report report = reportRepository.findById(id).orElseThrow(()-> new Exception("Report not found for this id : " + id));  
        reportRepository.delete(report);
    }
}
