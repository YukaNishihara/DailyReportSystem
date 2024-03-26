package com.techacademy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

import form.ReportQuery;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping({ "", "/" })
    public String getReports(Model model, @AuthenticationPrincipal UserDetail principal,
            @PageableDefault(page = 0, size = 5, sort = "createdAt") Pageable pageable,
            @ModelAttribute ReportQuery reportQuery) {

        Employee loggedInUser = principal.getEmployee();

        Page<Report> reportPage;
        reportPage = reportService.getSearchReports(reportQuery, loggedInUser, pageable);

        model.addAttribute("reportList", reportPage.getContent());
        model.addAttribute("reportQuery", reportQuery);
        model.addAttribute("page", reportPage);
        model.addAttribute("listSize", reportPage.getTotalElements());
        return "reports/list";
    }

    // 警告画面
    @GetMapping(value = "/caution")
    public String caution() {
        return "reports/caution";

    }

//    // 日報詳細画面
    @GetMapping(value = "/{id}/")
    public String detail(@PathVariable Integer id, Model model, @AuthenticationPrincipal UserDetail principal) {

        Employee loggedInUser = principal.getEmployee();
        Report report = reportService.findById(id);

        // 削除済みの日報にアクセスしようとした場合
        if (report == null) {
            return "redirect:/reports/caution";
        }

        // 管理者または一般ユーザーかつコードが同一のユーザーが作成した日報の場合
        if (loggedInUser.getRole() == Role.ADMIN || (loggedInUser.getRole() != Role.ADMIN
                && report.getEmployee().getCode().equals(loggedInUser.getCode()))) {
            // 自分の日報なのでdetail画面に遷移
            model.addAttribute("report", report);
            return "reports/detail";
        } else {

            return "redirect:/reports/caution";

        }

    }

    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        String loggedInEmployeeName = userDetail.getEmployee().getName();
        model.addAttribute("loggedInEmployeeName", loggedInEmployeeName);

        return "reports/new";
    }


//    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report, BindingResult res, @AuthenticationPrincipal UserDetail userDetail,
            Model model) {

        // 入力チェック
        if (res.hasErrors()) {

            return create(report, userDetail, model);
        }

        Employee loggedInEmployeeCode = userDetail.getEmployee();
        report.setEmployee(loggedInEmployeeCode);

        List<Report> reports = reportService.findByEmployeeAndReportDate(loggedInEmployeeCode, report.getReportDate());

        if (!reports.isEmpty()) {
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                    ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
            return create(report, userDetail, model);
        }

//
        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, userDetail, model);
        }

        return "redirect:/reports";
    }

    // 削除処理
    @PostMapping(value = "/{id}/delete")
    public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model) {

        ErrorKinds result = reportService.delete(id, userDetail);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            model.addAttribute("report", reportService.findById(id));
            return detail(id, model, userDetail);
        }

        return "redirect:/reports";
    }

    // 更新画面
    @GetMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, @AuthenticationPrincipal UserDetail userDetail, Model model,
            Report rep) {

        String loggedInEmployeeName = userDetail.getEmployee().getName();
        model.addAttribute("loggedInEmployeeName", loggedInEmployeeName);

        if (id != null) {
            Report report = reportService.findById(id);
            model.addAttribute("report", report);
        } else {
            model.addAttribute("report", rep);
        }
        return "reports/update";
    }

    // 更新処理
    @PostMapping(value = "/{id}/update")
    public String update(@PathVariable Integer id, @Validated Report report, BindingResult res,
            @AuthenticationPrincipal UserDetail userDetail, Model model) {
        // 入力チェック
        if (res.hasErrors()) {

            return update(null, userDetail, model, report);
        }

        Employee loggedInEmployeeCode = userDetail.getEmployee();
        report.setEmployee(loggedInEmployeeCode);

        // 更新前と更新後同じ日付
        Report existingReport = reportService.findById(id);
        if (existingReport != null && existingReport.getReportDate().equals(report.getReportDate())) {
            // エラー出さない
            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR), null);
        } else {
            // 登録ずみの日付の場合
            List<Report> reports = reportService.findByEmployeeAndReportDate(loggedInEmployeeCode,
                    report.getReportDate());

            if (!reports.isEmpty()) {
                model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DATECHECK_ERROR),
                        ErrorMessage.getErrorValue(ErrorKinds.DATECHECK_ERROR));
                return update(id, userDetail, model, report);
            }
        }


        ErrorKinds result = reportService.update(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return update(id, userDetail, model, report);
        }

        return "redirect:/reports";
    }
}
