package com.seniorcare.seniorCitizenAlert.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="nurses")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NurseEntity {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String userName;
    private String password;
    private String name;
    private String phone;
    private double latitude;
    private double longitude;
    private String status;
}
