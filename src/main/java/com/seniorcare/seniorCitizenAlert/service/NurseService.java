package com.seniorcare.seniorCitizenAlert.service;

import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.NurseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class NurseService {
    @Autowired
    private NurseRepo nurseRepo;


    @Autowired
    private PasswordEncoder passwordEncoder;
    public NurseEntity saveNewNurse(NurseEntity nurse)
    {
        try{
            nurse.setPassword(passwordEncoder.encode(nurse.getPassword()));
            return nurseRepo.save(nurse);
        }catch (Exception e)
        {
            System.out.println("Exception at saveNew of Nurse Service "+e);
            return null;
        }
    }
    public List<NurseEntity> getAll()
    {
        try{
            return nurseRepo.findAll();
        }catch (Exception e)
        {
            System.out.println("Error in getAll of Nurse Service "+e);
            return null;
        }
    }

    private final String GOOGLE_DISTANCE_MATRIX_API = "https://maps.googleapis.com/maps/api/distancematrix/json";
    @Value("${google.api.key}")
    private String API_KEY; // Replace with actual key

    public List<NurseEntity> getNearestAvailableNurses(PatientEntity patient) {
        List<NurseEntity> availableNurses = nurseRepo.findByStatus("Available");

        if (availableNurses.isEmpty()) {
            return Collections.emptyList(); // No available nurses
        }

        RestTemplate restTemplate = new RestTemplate();

        // Build the "origins" parameter (list of all nurse locations)
        StringBuilder origins = new StringBuilder();
        for (NurseEntity nurse : availableNurses) {
            origins.append(nurse.getLatitude()).append(",").append(nurse.getLongitude()).append("|");
        }
        origins.deleteCharAt(origins.length() - 1); // Remove the last "|"

        // Patient's location as destination
        String destination = patient.getLatitude() + "," + patient.getLongitude();

        // Construct the API URL
        String url = GOOGLE_DISTANCE_MATRIX_API + "?origins=" + origins
                + "&destinations=" + destination
                + "&key=" + API_KEY;

        try {
            DistanceResponse response = restTemplate.getForObject(url, DistanceResponse.class);

            if (response != null && response.getRows() != null) {
                List<Pair<NurseEntity, Integer>> nurseWithTime = new ArrayList<>();

                for (int i = 0; i < response.getRows().size(); i++) {
                    DistanceResponse.Element element = response.getRows().get(i).getElements().get(0);

                    if (element != null && element.getDuration() != null) {
                        int duration = element.getDuration().getValue(); // Travel time in seconds
                        nurseWithTime.add(Pair.of(availableNurses.get(i), duration));
                    }
                }

                // Sort nurses based on travel time (ascending order)
                nurseWithTime.sort(Comparator.comparingInt(Pair::getSecond));

                // Return only the sorted nurse entities
                return nurseWithTime.stream().map(Pair::getFirst).collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println("Error fetching data from Google API: " + e.getMessage());
        }

        return Collections.emptyList(); // If no nurses found
    }
    public ResponseEntity<?> processNurseAssignment(PatientEntity patient, List<NurseEntity> nurses, int index) {
        if (index >= nurses.size()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No nurse accepted the request.");
        }

        NurseEntity nurse = nurses.get(index);
        nurse.setStatus("Pending"); // Set nurse status to "Pending"
        nurseRepo.save(nurse);

        // Notify nurse (via WebSocket, Push Notification, or SMS)
//        notifyNurse(nurse, patient);

        // Wait for X seconds for nurse response
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000); // Wait for 10 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Optional<NurseEntity> updatedNurse = nurseRepo.findById(nurse.getId());

            if (updatedNurse.isPresent() && "Pending".equals(updatedNurse.get().getStatus())) {
                // If still "Pending", move to the next nurse
                NurseEntity newNurse=updatedNurse.get();
                newNurse.setStatus("Available");
                nurseRepo.save(newNurse);
                processNurseAssignment(patient, nurses, index + 1);
            }
        });

        return ResponseEntity.ok("Request sent to nurse: " + nurse.getName());
    }




}
