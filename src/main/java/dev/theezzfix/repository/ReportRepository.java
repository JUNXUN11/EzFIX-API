package dev.theezzfix.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import dev.theezzfix.model.Report;

import java.util.List;

import org.bson.types.ObjectId;


@Repository
public interface ReportRepository extends MongoRepository<Report, ObjectId>{
    @Query("{'studentId': ?0}")
    List<Report> findByStudentId(ObjectId studentId);
}
