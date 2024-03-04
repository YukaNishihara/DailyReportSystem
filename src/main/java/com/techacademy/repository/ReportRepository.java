package com.techacademy.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Integer> ,JpaSpecificationExecutor<Report> {
    List<Report> findByEmployee(Employee employee);
    List<Report> findByEmployeeAndReportDate(Employee employee,LocalDate reportDate);
    //追加
    Page<Report> findByEmployee(Employee employee, Pageable pageable); // ページング対応のメソッド
    //追加期間
    Page<Report> findByReportDateBetweenOrderByReportDateAsc(LocalDate startDate, LocalDate endDate, Pageable pageable);
    //追加
    Page<Report> findByCreatedAtAfterOrderByCreatedAtAsc(LocalDate startDate, Pageable pageable);

    Page<Report> findByCreatedAtBeforeOrderByCreatedAtAsc(LocalDate endDate, Pageable pageable);

}