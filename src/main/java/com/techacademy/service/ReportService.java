package com.techacademy.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;

    }
    // 新規登録
//    @Transactional
//    public ErrorKinds save(Report report) {
//
//        report.setDeleteFlg(false);
//        
//        
//        //作成日時、更新日時
//        LocalDateTime now = LocalDateTime.now();
//        report.setCreatedAt(now);
//        report.setUpdatedAt(now);
//       
//
//        reportRepository.save(report);
//        return ErrorKinds.SUCCESS;
//    }

    // 日報一覧表示処理
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // 一般ユーザーが作成したレポートのみを取得
    public List<Report> findAllByUser(Employee employee) {
        return reportRepository.findByEmployee(employee);

    }

    // 1件を検索
    public Report findById(Integer id) {
        // findByIdで検索
        Optional<Report> option = reportRepository.findById(id);

        Report report = option.orElse(null);
        return report;
    }

}
