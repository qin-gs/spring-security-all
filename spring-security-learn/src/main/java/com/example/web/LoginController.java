package com.example.web;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {

    @GetMapping("/loginSuccess")
    public String login() {
        return "login success";
    }

    @GetMapping("/allPage")
    public String allPage() {
        return "all page";
    }

    @GetMapping("/teacher")
    public String teacher() {
        return "teacher page";
    }

    @GetMapping("/student")
    public String student() {
        return "student page";
    }

    @GetMapping("/anyAuthority")
    public String anyAuthority() {
        return "anyAuthority page";
    }

    @GetMapping("/read")
    public String read() {
        return "read page";
    }

    @GetMapping("/write")
    public String write() {
        return "write page";
    }

    @GetMapping("/anyRole")
    public String anyRole() {
        return "anyRole page";
    }

    @Secured({"ROLE_teacher"})
    @GetMapping("/annotationTeacher")
    public String annotationTeacher() {
        return "annotation teacher";
    }

    @Secured({"ROLE_student"})
    @PreAuthorize("hasAuthority('read')") // 方法执行前进行权限判断
    @PostAuthorize("hasAuthority('write')") // 方法执行后进行权限判断(权限不满足不返回，但是方法会执行)
    @GetMapping("/annotationStudent")
    public String annotationStudent() {
        return "annotation student";
    }

    @PreFilter("id.length() % 2 == 0") // 对传入参数过滤
    @PostFilter("filterObject.length() == 1") // 对返回数据进行过滤
    @GetMapping("/filters")
    public List<String> filters(String id) {
        return List.of("a", "bb", "c");
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
