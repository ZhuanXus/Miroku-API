package cn.net.miroku.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/login")
    public void login() {
        // 关注永雏塔菲谢谢喵
        StpUtil.login("Ace-Taffy-1883");
    }
}
