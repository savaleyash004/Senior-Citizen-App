package com.seniorcare.seniorCitizenAlert.service;

import com.seniorcare.seniorCitizenAlert.entity.NurseEntity;
import com.seniorcare.seniorCitizenAlert.entity.PatientEntity;
import com.seniorcare.seniorCitizenAlert.repository.NurseRepo;
import com.seniorcare.seniorCitizenAlert.repository.PatientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class NurseDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private NurseRepo nurseRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NurseEntity nurse=nurseRepo.findByUserName(username);
        if(nurse!=null)
        {
            return User.builder()
                    .username(nurse.getUserName()).password(nurse.getPassword()).build();
        }
        throw new UsernameNotFoundException("User not found with username : "+username);
    }
}
