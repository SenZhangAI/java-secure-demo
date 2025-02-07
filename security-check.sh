#!/bin/bash

set -e  # 遇到错误立即退出
trap 'echo "发生错误，正在清理..." >&2' ERR

echo "开始执行安全检查..."

# 清理旧的报告文件
rm -f target/dependency-check-report.html target/dependency-check-report.json

# 检查依赖版本更新
echo "检查依赖版本..."
mvn versions:display-dependency-updates || true

# 检查插件版本更新
echo "检查插件版本..."
mvn versions:display-plugin-updates || true

# 执行依赖安全检查
echo "执行依赖安全扫描..."
if mvn dependency-check:check; then
    echo "依赖检查完成，未发现高危漏洞。"
else
    echo "警告：依赖检查发现潜在问题，请查看报告了解详情。"
fi

# 检查报告文件
if [ -f "target/dependency-check-report.html" ]; then
    echo "安全检查完成。报告已生成："
    echo "- HTML 报告：target/dependency-check-report.html"
    [ -f "target/dependency-check-report.json" ] && echo "- JSON 报告：target/dependency-check-report.json"
else
    echo "错误：报告文件未生成，检查可能未正确执行。"
    exit 1
fi 