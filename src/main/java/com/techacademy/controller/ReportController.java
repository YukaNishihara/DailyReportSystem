package com.techacademy.controller;


import org.springframework.beans.factory.annotation.Autowired;
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
import com.techacademy.service.ReportService;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;

   

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 日報一覧画面
    @GetMapping
    public String list(Model model) {

        model.addAttribute("listSize", reportService.findAll().size());
        model.addAttribute("reportList", reportService.findAll());

        return "reports/list";
    }

//    // 従業員詳細画面
//    @GetMapping(value = "/{code}/")
//    public String detail(@PathVariable String code, Model model) {
//
//        model.addAttribute("employee", reportService.findByCode(code));
//        return "employees/detail";
//    }
//
//    // 従業員新規登録画面
//    @GetMapping(value = "/add")
//    public String create(@ModelAttribute Employee employee) {
//
//        return "employees/new";
//    }
//
//    // 従業員新規登録処理
//    @PostMapping(value = "/add")
//    public String add(@Validated Employee employee, BindingResult res, Model model) {
//
//        // パスワード空白チェック
//        /*
//         * エンティティ側の入力チェックでも実装は行えるが、更新の方でパスワードが空白でもチェックエラーを出さずに
//         * 更新出来る仕様となっているため上記を考慮した場合に別でエラーメッセージを出す方法が簡単だと判断
//         */
//        if ("".equals(employee.getPassword())) {
//            // パスワードが空白だった場合
//            model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.BLANK_ERROR),
//                    ErrorMessage.getErrorValue(ErrorKinds.BLANK_ERROR));
//
//            return create(employee);
//
//        }
//
//        // 入力チェック
//        if (res.hasErrors()) {
//            return create(employee);
//        }
//
//       
//
//    
//            
//
//   
//       
//
//        return "redirect:/employees";
//    }

       
}
