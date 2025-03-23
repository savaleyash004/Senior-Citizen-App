package com.seniorcare.seniorCitizenAlert.controller;

import com.seniorcare.seniorCitizenAlert.dto.LocationDto;
import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import com.seniorcare.seniorCitizenAlert.repository.NurseRepo;
import com.seniorcare.seniorCitizenAlert.service.NurseService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Component
@RestController
@RequestMapping("/nurse")
public class NurseController {
    @Autowired
    private NurseService nurseService;

    @Autowired
    private NurseRepo nurseRepo;
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<NurseEntity> nurse = Optional.ofNullable(nurseRepo.findByUserName(userName));
        if (nurse.isPresent()) {
            NurseEntity updatedNurse = nurse.get();
            updatedNurse.setStatus("Offline");
            nurseRepo.save(updatedNurse);
            return ResponseEntity.ok("Logout successful. Status set to Offline.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse not found");
    }

    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<NurseEntity> nurse = Optional.ofNullable(nurseRepo.findByUserName(userName));

        if (nurse.isPresent()) {
            NurseEntity updatedNurse = nurse.get();
            if (!"Pending".equals(updatedNurse.getStatus())) {
                return ResponseEntity.badRequest().body("Request expired or already handled.");
            }
            updatedNurse.setStatus("Booked");
            nurseRepo.save(updatedNurse);
            return ResponseEntity.ok("Request accepted. Nurse assigned.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse not found");
    }

    @PostMapping("/rejectRequest")
    public ResponseEntity<String> rejectRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<NurseEntity> nurse = Optional.ofNullable(nurseRepo.findByUserName(userName));

        if (nurse.isPresent()) {
            NurseEntity updatedNurse = nurse.get();
            if (!"Pending".equals(updatedNurse.getStatus())) {
                return ResponseEntity.badRequest().body("Request expired or already handled.");
            }

            updatedNurse.setStatus("Available"); // Reset status
            nurseRepo.save(updatedNurse);

            // Trigger next nurse notification
            return ResponseEntity.ok("Request rejected. Searching for next available nurse...");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse not found");
    }

    @PutMapping("/updateLocation")
    public String updateLocation( @RequestBody LocationDto newLocation) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<NurseEntity> nurseOptional = Optional.ofNullable(nurseRepo.findByUserName(userName));
        if (nurseOptional.isPresent()) {
            NurseEntity nurse = nurseOptional.get();
            nurse.setLatitude(newLocation.getLatitude());
            nurse.setLongitude(newLocation.getLongitude());
            nurseRepo.save(nurse);
            return "Location updated successfully!";
        } else {
            return "Nurse not found!";
        }
    }

    @PostMapping("/completeVisit")
    public ResponseEntity<String> completeVisit() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<NurseEntity> nurse = Optional.ofNullable(nurseRepo.findByUserName(userName));
        if (nurse.isPresent()) {
            NurseEntity updatedNurse = nurse.get();
            if (! "Booked".equals(updatedNurse.getStatus())) {
                return ResponseEntity.badRequest().body("The Nurse is Already Available");
            }
            updatedNurse.setStatus("Available"); // Ready for next patient
            nurseRepo.save(updatedNurse);
            return ResponseEntity.ok("Visit completed. Status set to Available.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse not found");
    }


}
