package com.seniorcare.seniorCitizenAlert.service;

import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PatientDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private PatientRepo patientRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PatientEntity patient=patientRepo.findByUserName(username);
        if(patient!=null)
        {
            return User.builder().username(patient.getUserName()).password(patient.getPassword()).build();
        }
        throw new UsernameNotFoundException("User not found with username : "+username);

    }
}
