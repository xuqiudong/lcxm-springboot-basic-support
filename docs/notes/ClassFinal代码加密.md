# springboot项目中使用ClassFinal 进行代码加密

## 1. ClassFinal 插件作用

用于 SpringBoot 项目 **class 文件加密 + 防反编译保护**：

- 防止 `.class` 被直接反编译
- 保护核心业务逻辑
- 支持 Maven 构建流程自动加密
- 可对 **指定 package + 外部 jar** 加密

### 版本选择

`fork版本：com.gitee.lcm742320521/classfinal-maven-plugin:1.4.1`

原因：

- 原版 `net.roseboy:classfinal` 最高仅 1.2.1
- 不支持 JDK17 / JDK21
- fork 版本修复 JDK 高版本兼容问题

------

## 2. 父项目配置说明

### 2.1 基础参数

```
<!--classfinal  版本 -->
<classfinal.version>1.4.1</classfinal.version>
<!-- 密码: 加密解密使用-->
<classfinal.password>12345678</classfinal.password>
<!--加密的包, 多个逗号分隔-->
<classfinal.packages>cn.xuqiudong.blog</classfinal.packages>
<!--加密的jar包, 多个逗号分隔-->
<classfinal.libjars>
    lcxm-basic-framework:3.5.0-jdk21-3.0.0.jar,
    lcxm-basic-core:3.5.0-jdk21-3.0.0.jar
</classfinal.libjars>
```

------

### 2.2 pluginManagement

```
<pluginManagement>
    <plugins>
       <plugin>
        <groupId>com.gitee.lcm742320521</groupId>
            <artifactId>classfinal-maven-plugin</artifactId>
            <version>${classfinal.version}</version>
            <configuration>
                <!--加密打包之后pom.xml会被删除，不用担心在jar包里找到此密码-->
                <password>${classfinal.password}</password>
                <!--需要加密的包, 多个包用逗号分隔-->
                <packages>${classfinal.packages}</packages>
                <!-- 需要加密的配置文件, 多个文件用逗号分隔 -->
                <!--<cfgfiles>application.yml</cfgfiles>-->
                <!--<excludes>org.spring</excludes>-->
                <!-- 需要加密的jar包, 多个jar包用逗号分隔 -->
                <libjars>
                    ${classfinal.libjars}
                </libjars>
            </configuration>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>classFinal</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</pluginManagement>
```

### 2.3 profile（用于控制是否启用加密）

```
<profiles>
    <profile>
        <id>classfinal</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>com.gitee.lcm742320521</groupId>
                    <artifactId>classfinal-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

## 3. 使用方式

### 3.1 启用加密构建

```
mvn clean package -Pclassfinal
```

### 3.2 输出结果

```
target/
 ├── xxx-encrypted.jar // 加密后的jar
 ├── xxx.jar   // 原始jar 未加密
```

------

### 3.3 运行方式

```
java -javaagent:xxx-encrypted.jar="-pwd 12345678" -jar xxx-encrypted.jar"
```

## 注意事项:
**在 Spring Boot 项目中，classfinal-maven-plugin 需要在 spring-boot-maven-plugin 之后执行。**
- 其当父项目的 profile 中已经声明了 classfinal-maven-plugin 时，子项目激活该 profile 后，执行顺序通常不容易直接判断。

### 解决方案
1. 将 classfinal-maven-plugin 只定义在实际启动模块中，并放在 spring-boot-maven-plugin 之后。
2. 父项目仅通过 pluginManagement 管理公共配置，不直接下发 classfinal 的执行。
3. 如果无法调整父 profile 结构，可将 classfinal-maven-plugin 的执行阶段从 package 改为 verify，避免早于 spring-boot:repackage 执行。
   (此时打包则 需要先mvn package, 再执行一次mvn verify )

4. 或者直接在具体的项目内的springboot插件下新增classfinal插件， 因为package只会多出个加密的jar，按需使用即可

#### 附录：

- 源库 https://gitee.com/roseboy/classfinal 只到1.2.1版本 不支持jdk 高版本,
- lcm742320521的fork版：https://gitee.com/lcm742320521/class-final 1.4.1 支持更高版本的jdk