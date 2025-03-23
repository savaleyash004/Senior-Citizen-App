package com.seniorcare.seniorCitizenAlert.service;

import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public PatientEntity saveNewPatient(PatientEntity patient)
    {
        try{
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
            return patientRepo.save(patient);
        }catch (Exception e)
        {
            System.out.println("Exception at saveNew of Patient Service "+e);
            return null;
        }
    }
    public List<PatientEntity> getAll()
    {
        try{
            return patientRepo.findAll();
        }catch (Exception e)
        {
            System.out.println("Error in getAll of Patient Service "+e);
            return null;
        }
    }

}
