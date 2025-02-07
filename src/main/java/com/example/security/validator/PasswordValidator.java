package com.example.security.validator;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    public String getPasswordRequirements() {
        return "密码必须包含：\n" +
                "- 至少8个字符\n" +
                "- 至少1个数字\n" +
                "- 至少1个小写字母\n" +
                "- 至少1个大写字母\n" +
                "- 至少1个特殊字符(@#$%^&+=)\n" +
                "- 不能包含空格";
    }
}