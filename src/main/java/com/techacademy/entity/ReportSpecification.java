package com.techacademy.entity;

import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

import com.techacademy.entity.Employee.Role;


public class ReportSpecification<T> {

    /**
     * startDateで検索
     */
    public Specification<T> reportDateGreaterThanEqual(LocalDate startDate) {

        return startDate == null ? null : (root, query, builder) ->
            builder.greaterThanOrEqualTo(root.get("reportDate"), startDate);
    }

    /**
     * endDateで検索
     */
    public Specification<T> reportDateLessThanEqual(LocalDate endDate) {

        return endDate == null ? null : (root, query, builder) ->
            builder.lessThanOrEqualTo(root.get("reportDate"), endDate);
    }
    
    /**
     * 権限に基づいて適切な日報情報を取得
     */
    public Specification<T> findReportsForUser(Employee loggedInUser) {
        return (root, query, builder) -> {
            if (loggedInUser.getRole() == Role.ADMIN) {
                return null; // 管理者は制限なし
            } else {
                return builder.equal(root.get("employee"), loggedInUser); // ユーザーに関連する日報を取得
            }
        };
    }

//    public Specification<Report> findReportsForUser(Employee loggedInUser) {
//        // TODO 自動生成されたメソッド・スタブ
//        return null;
//    }
}
