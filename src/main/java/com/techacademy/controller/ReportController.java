package com.techacademy.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Employee.Role;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

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
    // 認証されたユーザーのUserDetail取得
    public String getReports(Model model, @AuthenticationPrincipal UserDetail principal) {

        Employee loggedInUser = principal.getEmployee();

        List<Report> reports = null;

        // 権限に基づいて適切な日報情報を取得
        if (loggedInUser.getRole() == Role.ADMIN) {
            reports = reportService.findAll();
        } else {
            reports = reportService.findAllByUser(loggedInUser);
        }

        model.addAttribute("listSize", reports.size());
        model.addAttribute("reportList", reports);

        return "reports/list";
    }

//    // 日報詳細画面
//    @GetMapping(value = "/{id}/")
//    public String detail(@PathVariable String id, Model model) {
//
//        model.addAttribute("report", reportService.findByCode(code));
//        return "reports/detail";
//    }
//
//    // 日報新規登録画面
    @GetMapping(value = "/add")
    public String create( @ModelAttribute Report report,@AuthenticationPrincipal UserDetail userDetail,Model model) {
        
        
            
            String loggedInEmployeeName = userDetail.getEmployee().getName();
            model.addAttribute("loggedInEmployeeName", loggedInEmployeeName);
       

        
        return "reports/new";
    }
//
//    // 日報新規登録処理
    @PostMapping(value = "/add")
    public String add(@Validated Report report,@AuthenticationPrincipal UserDetail userDetail, BindingResult res, Model model) {

        // 入力チェック
        if (res.hasErrors()) {
            
            return create(report, userDetail, model);
        }
        // タイトルの桁数チェック
//        if (report.getTitle().length() > 100) {
//            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.LENGTH_TITLE_ERROR), 
//                    ErrorMessage.getErrorValue(ErrorKinds.LENGTH_TITLE_ERROR));
//            return "reports/new";
//        }
//
//        // 内容の桁数チェック
//        if (report.getContent().length() > 600) {
//            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.LENGTH_CONTENT_ERROR), 
//                    ErrorMessage.getErrorValue(ErrorKinds.LENGTH_CONTENT_ERROR));
//            return "reports/new";
//        }
      
        ErrorKinds result = reportService.save(report);

        if (ErrorMessage.contains(result)) {
            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
            return create(report, null, model);
        }
        
        return "redirect:/reports";
    }

    // 従業員削除処理
//    @PostMapping(value = "/{id}/delete")
//    public String delete(@PathVariable String id, @AuthenticationPrincipal UserDetail userDetail, Model model) {
//
//        ErrorKinds result = reportService.delete(id, userDetail);
//
//        if (ErrorMessage.contains(result)) {
//            model.addAttribute(ErrorMessage.getErrorName(result), ErrorMessage.getErrorValue(result));
//            model.addAttribute("report", reportService.findByCode(id));
//            return detail(id, model);
//        }
//
//        return "redirect:/reports";
//    }
}
