package com.example.web;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    /**
     * hasPermission 表达式委托给 PermissionEvaluator
     */
    @Secured({"ROLE_student"})
    @PreAuthorize("hasAuthority('read')") // 方法执行前进行权限判断
    @PostAuthorize("hasAuthority('write')") // 方法执行后进行权限判断(权限不满足不返回，但是方法会执行)
    @GetMapping("/annotationStudent")
    public String annotationStudent() {
        return "annotation student";
    }

    /**
     * 过滤不能代替调整数据检索查询。如果要过滤大型集合并删除许多条目，则效率可能很低
     * <p>
     * DefaultSecurityParameterNameDiscoverer 来发现参数名
     * AnnotationParameterNameDiscoverer 处理 org.springframework.data.repository.query.Param 注解
     * <p>
     * 一个内置表达式 authentication，是存储在安全上下文中的 Authentication。
     * 也可以使用表达式principal直接访问其属性，值通常是 UserDetails 实例，
     * 因此可以使用 principal.username 或 principal.enabled 之类的表达式
     * <p>
     * PostFilter 使用内置名称 returnObject 访问返回值
     * filterObject 表示集合中的当前对象，如果有多个参数是集合类型，则必须使用此注解的 filterTarget 属性通过名称选择一个
     */
    @PreFilter("id.length() % 2 == 0") // 对传入参数过滤
    @PostFilter("filterObject.length() == 1 && #user.id != null && user.username == authentication.name") // 对返回数据进行过滤
    @GetMapping("/filters")
    public List<String> filters(String id, @P("user") User user) {
        return List.of("a", "bb", "c");
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    /**
     * 如果这个方法需要验证token，拦截器会提前处理，这里不用管
     */
    @GetMapping("jwt/getInfo")
    public Map<String, String> getInfo() {
        // 完成自己的业务逻辑
        return Map.of("info_1", "message_1", "info_2", "message_2");
    }
}
