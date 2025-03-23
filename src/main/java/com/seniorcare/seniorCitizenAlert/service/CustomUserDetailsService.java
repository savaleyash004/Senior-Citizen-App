package com.seniorcare.seniorCitizenAlert.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final NurseDetailsServiceImpl nurseDetailsService;
    private final PatientDetailsServiceImpl patientDetailsService;

    public CustomUserDetailsService(NurseDetailsServiceImpl nurseDetailsService, PatientDetailsServiceImpl patientDetailsService) {
        this.nurseDetailsService = nurseDetailsService;
        this.patientDetailsService = patientDetailsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = null;

        try {
            user = patientDetailsService.loadUserByUsername(username);
            if (user != null) {
                System.out.println("Patient login detected for: " + username);
                return user;
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("No patient found with username: " + username);
        }

        try {
            user = nurseDetailsService.loadUserByUsername(username);
            if (user != null) {
                System.out.println("Nurse login detected for: " + username);
                return user;
            }
        } catch (UsernameNotFoundException e) {
            System.out.println("No nurse found with username: " + username);
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

}
