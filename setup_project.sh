#!/bin/bash

# 创建目录结构
mkdir -p src/main/java/com/example/security/{config,controller,entity,exception,payload,repository,security}
mkdir -p src/test/java/com/example/security/controller

# 确保所有Java文件都在正确的目录中
for file in $(find . -name "*.java"); do
    # 获取文件名和包名
    filename=$(basename "$file")
    package=$(echo "$filename" | sed 's/\.java$//')
    
    # 根据文件名确定目标目录
    case "$filename" in
        *Application.java)
            target="src/main/java/com/example/security/"
            ;;
        *Config.java)
            target="src/main/java/com/example/security/config/"
            ;;
        *Controller.java)
            target="src/main/java/com/example/security/controller/"
            ;;
        User.java|Role.java)
            target="src/main/java/com/example/security/entity/"
            ;;
        *Repository.java)
            target="src/main/java/com/example/security/repository/"
            ;;
        *Test.java)
            target="src/test/java/com/example/security/controller/"
            ;;
        TestDataInitializer.java)
            target="src/test/java/com/example/security/"
            ;;
        *)
            # 其他文件根据其当前位置决定
            if [[ "$file" == *"/security/"* ]]; then
                target="src/main/java/com/example/security/security/"
            elif [[ "$file" == *"/payload/"* ]]; then
                target="src/main/java/com/example/security/payload/"
            elif [[ "$file" == *"/exception/"* ]]; then
                target="src/main/java/com/example/security/exception/"
            fi
            ;;
    esac
    
    # 如果找到目标目录，移动文件
    if [ ! -z "$target" ]; then
        mkdir -p "$target"
        mv "$file" "$target"
    fi
done 