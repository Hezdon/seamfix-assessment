package com.example.repository;

import com.example.dto.LogRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRequestRepo extends JpaRepository<LogRequest, String> {
}
