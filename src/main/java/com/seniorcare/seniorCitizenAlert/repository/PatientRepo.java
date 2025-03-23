package com.seniorcare.seniorCitizenAlert.repository;

import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatientRepo extends MongoRepository<PatientEntity,String> {
    PatientEntity findByUserName(String userName);
}
