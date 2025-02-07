package com.example.security.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return stripXSS(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int length = values.length;
        String[] escapedValues = new String[length];
        for (int i = 0; i < length; i++) {
            escapedValues[i] = stripXSS(values[i]);
        }
        return escapedValues;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return stripXSS(value);
    }

    private String stripXSS(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }

        // 使用 commons-text 进行 HTML 转义
        value = StringEscapeUtils.escapeHtml4(value);

        // 过滤常见的 XSS 攻击向量
        value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "")
                .replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "")
                .replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");

        return value;
    }
}