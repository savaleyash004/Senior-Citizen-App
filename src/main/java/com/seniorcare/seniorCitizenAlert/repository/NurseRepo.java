package com.seniorcare.seniorCitizenAlert.repository;

import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NurseRepo extends MongoRepository<NurseEntity, ObjectId> {
    NurseEntity findByUserName(String userName);
    List<NurseEntity> findByStatus(String status);
}
