package org.example.service;

import org.example.entity.Patient;
import org.example.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getPatients(){
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id){
        return patientRepository.findById(id);
    }

    public List<Patient> getPatientsByName(String name){
        return patientRepository.findByName(name);
    }

    public void addPatient(Patient patient){
        patientRepository.save(patient);
    }

    public void updatePatient(Patient patient){
        patientRepository.save(patient);
    }

    public void deletePatient(Patient patient){
        patientRepository.delete(patient);
    }

    public void deletePatient(String phone){
        patientRepository.deleteByPhone(phone);
    }
}
