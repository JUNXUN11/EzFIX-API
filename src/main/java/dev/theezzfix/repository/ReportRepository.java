package dev.theezzfix.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import dev.theezzfix.model.Report;

import org.bson.types.ObjectId;


@Repository
public interface ReportRepository extends MongoRepository<Report, ObjectId>{

}
