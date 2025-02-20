package com.elearning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elearning.models.Certificate;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByUserId(Long userId);
    List<Certificate> findByCourseId(Long courseId);
}
