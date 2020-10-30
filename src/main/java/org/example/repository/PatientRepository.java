package org.example.repository;

import org.example.entity.Doctor;
import org.example.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findAll();

    @Query("from Patient el where concat(el.lastName, ' ', el.firstName, ' ', el.patronymic) like concat('%', :name, '%')")
    List<Patient> findByName(@Param("name") String name);

    void deleteByPhone(String phone);
    Patient findByPhone(String phone);
}
