package com.techacademy.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.entity.ReportSpecification;
import com.techacademy.repository.ReportRepository;

import form.ReportQuery;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }

    // 新規登録
    @Transactional
    public ErrorKinds save(Report report) {

        report.setDeleteFlg(false);

        // 作成日時、更新日時
        LocalDateTime now = LocalDateTime.now();
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 更新
    public ErrorKinds update(Report report) {

        report.setDeleteFlg(false);
        
        //作成日時は既存
        Report existingReport = findById(report.getId());
        report.setCreatedAt(existingReport.getCreatedAt());

        // 作成日時、更新日時
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);

        reportRepository.save(report);
        return ErrorKinds.SUCCESS;
    }

    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {

        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
        report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 一般ユーザーが作成したレポートのみを取得
    public List<Report> findAllByUser(Employee employee) {
        return reportRepository.findByEmployee(employee);

    }

    public List<Report> findByEmployeeAndReportDate(Employee employee, LocalDate reportDate) {
        return reportRepository.findByEmployeeAndReportDate(employee, reportDate);
    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);

        Report report = option.orElse(null);
        return report;
    }
    
 // ページングで全ての日報を取得
    public Page<Report> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    // ページングで特定の従業員の日報を取得
    public Page<Report> findAllByUser(Employee employee, Pageable pageable) {
        return reportRepository.findByEmployee(employee, pageable);
    }
    
    // 指定された期間内のレポートを取得するメソッド
    public Page<Report> searchByQuery(ReportQuery reportQuery, Pageable pageable) {
        return reportRepository.findByReportDateBetweenOrderByReportDateAsc(reportQuery.getStartDate(),reportQuery.getEndDate(), pageable);
    }
  //追加
    public Page<Report> searchByStartDateAfter(ReportQuery reportQuery, Pageable pageable) {
        return reportRepository.findByCreatedAtAfterOrderByCreatedAtAsc(reportQuery.getStartDate(), pageable);
    }

    public Page<Report> searchByEndDateBefore(ReportQuery reportQuery, Pageable pageable) {
        return reportRepository.findByCreatedAtBeforeOrderByCreatedAtAsc(reportQuery.getEndDate(), pageable);
    }
    public Page<Report> getSearchReports(ReportQuery reportQuery,Employee loggedInUser ,Pageable pageable) {

        
        ReportSpecification<Report> spec = new ReportSpecification<>();

        return reportRepository.findAll(
                Specification.where(spec.reportDateGreaterThanEqual(reportQuery.getStartDate()))
                    .and(spec.reportDateLessThanEqual(reportQuery.getEndDate()))
                    .and(spec.findReportsForUser(loggedInUser))
                ,pageable);
    }
   

}
