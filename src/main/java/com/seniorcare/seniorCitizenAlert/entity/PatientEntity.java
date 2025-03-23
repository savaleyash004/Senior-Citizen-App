package com.seniorcare.seniorCitizenAlert.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="patients")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PatientEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String userName;
    private String password;
    private String name;
    private String phone;
    private String email;
    private Integer age;
    private double latitude;
    private double longitude;
}
