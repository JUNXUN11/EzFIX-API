package dev.theezzfix.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import dev.theezzfix.model.Announcement;
import org.bson.types.ObjectId;

@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, ObjectId> {
    
}
