package com.seniorcare.seniorCitizenAlert.controller;

import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.PatientRepo;
import com.seniorcare.seniorCitizenAlert.service.NurseService;
import com.seniorcare.seniorCitizenAlert.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Service
@RequestMapping("/patient")
@RestController
public class PatientController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private PatientRepo patientRepo;


    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<PatientEntity> patient = Optional.ofNullable(patientRepo.findByUserName(userName));
        if (patient.isPresent()) {

            return ResponseEntity.ok("Logout successful.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
    }


    @PostMapping("/alert")
    public ResponseEntity<?> alertNearestNurse() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<PatientEntity> patient1 = Optional.ofNullable(patientRepo.findByUserName(userName));
        PatientEntity patient;
        if(patient1.isPresent())
        {
            patient = patient1.get();
        }
        else
        {
            System.out.println("Error in finding logged in patient");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<NurseEntity> availableNurses = nurseService.getNearestAvailableNurses(patient);
        if (availableNurses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available nurses at the moment.");
        }
        return nurseService.processNurseAssignment(patient, availableNurses, 0);
    }
}
