package com.seniorcare.seniorCitizenAlert.controller;

import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.NurseRepo;
import com.seniorcare.seniorCitizenAlert.repository.PatientRepo;
import com.seniorcare.seniorCitizenAlert.service.CustomUserDetailsService;
import com.seniorcare.seniorCitizenAlert.service.NurseService;
import com.seniorcare.seniorCitizenAlert.service.PatientService;
import com.seniorcare.seniorCitizenAlert.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequestMapping("/public")
@RestController
public class PublicController {
    @Autowired
    private PatientService patientService;

    @Autowired
    private NurseService nurseService;

    @Autowired
    private NurseRepo nurseRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/get-health")
    public String healthcheck() {
        return "OK";
    }

    @GetMapping("/get-all-patient")
    public List<PatientEntity> getAllPatient() {
        List<PatientEntity> all = patientService.getAll();
        return all;
    }

    @GetMapping("/get-all-nurse")
    public List<NurseEntity> getAllNurse()
    {
        List<NurseEntity> all = nurseService.getAll();
        return all;

    }
    @PostMapping("/register-patient")
    public PatientEntity createPatient(@RequestBody PatientEntity person) {
        return patientService.saveNewPatient(person);
    }


    @PostMapping("/register-nurse")
    public NurseEntity createNurse(@RequestBody NurseEntity nurse)
    {
        return nurseService.saveNewNurse(nurse);
    }

    @PostMapping("/login-patient")
    public ResponseEntity<?> loginPatient(@RequestBody PatientEntity patient) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(patient.getUserName(), patient.getPassword())
            );

            UserDetails patientDetails = customUserDetailsService.loadUserByUsername(patient.getUserName());

            // Generate JWT token
            String token = jwtUtil.generateToken(patientDetails.getUsername());

            Optional<PatientEntity> existingPatient = Optional.ofNullable(patientRepo.findByUserName(patient.getUserName()));
            if (existingPatient.isPresent()) {

                return ResponseEntity.ok(Map.of(
                        "message", "Login successful. Status set to Available.",
                        "token", token
                ));
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
        } catch (Exception e) {
            System.out.println("Error in login of patient: " + e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
    @PostMapping("/login-nurse")
    public ResponseEntity<?> loginNurse(@RequestBody NurseEntity nurse) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(nurse.getUserName(), nurse.getPassword())
            );

            UserDetails nurseDetails = customUserDetailsService.loadUserByUsername(nurse.getUserName());

            // Generate JWT token
            String token = jwtUtil.generateToken(nurseDetails.getUsername());

            Optional<NurseEntity> existingNurse = Optional.ofNullable(nurseRepo.findByUserName(nurse.getUserName()));
            if (existingNurse.isPresent()) {
                NurseEntity updatedNurse = existingNurse.get();
                updatedNurse.setStatus("Available");
                nurseRepo.save(updatedNurse);
                return ResponseEntity.ok(Map.of(
                        "message", "Login successful. Status set to Available.",
                        "token", token
                ));

            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nurse not found");
        } catch (Exception e) {
            System.out.println("Error in login of nurse: " + e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


}
