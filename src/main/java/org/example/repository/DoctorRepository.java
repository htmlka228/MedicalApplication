package org.example.repository;

import org.example.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findAll();

    @Query("from Doctor el where concat(el.lastName, ' ', el.firstName, ' ', el.patronymic) like concat('%', :name, '%')")
    List<Doctor> findByName(@Param("name") String name);
}
