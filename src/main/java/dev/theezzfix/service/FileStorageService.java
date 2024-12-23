package dev.theezzfix.service;

import java.io.IOException;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Service
public class FileStorageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile(MultipartFile file) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("contentType", file.getContentType());
        metadata.put("filename", file.getOriginalFilename());
        
        ObjectId fileId = gridFsTemplate.store(
            file.getInputStream(), 
            file.getOriginalFilename(),
            file.getContentType(),
            metadata
        );
        return fileId.toString();
    }

    public Optional<GridFsResource> getFile(String fileId) {
        if (!ObjectId.isValid(fileId)) {
            return Optional.empty();
        }
        GridFSFile gridFSFile = gridFsTemplate.findOne(
            Query.query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
        if (gridFSFile == null) {
            return Optional.empty();
        }
        return Optional.of(gridFsTemplate.getResource(gridFSFile));
    }

    public void deleteFile(String fileId) {
        if (ObjectId.isValid(fileId)) {
            gridFsTemplate.delete(
                Query.query(Criteria.where("_id").is(new ObjectId(fileId)))
            );
        }
    }
}
