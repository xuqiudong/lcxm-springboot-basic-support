## 部署

### maven deploy

#### 版本修改
```shell
mvn versions:set -DnewVersion="3.5.0-jdk21-3.0.0" -DprocessAllModules=true  -DgenerateBackupPoms=false
```

#### 1 先发布父项目 POM

```shell
mvn deploy -N -Pdeploy
```
#### 2 发布基础模块
 此时不要加 -am 了  会重新部署parent 导致重复
```shell
mvn clean deploy -pl lcxm-basic-core,lcxm-basic-framework,lcxm-basic-mybatis-plus,lcxm-basic-srpc,lcxm-basic-third  -Pdeploy
```
####  3 部署starter
```shell
mvn clean deploy -pl lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter,lcxm-spring-boot-starters/lcxm-mq-data-bridge-spring-boot-starter,lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter -Pdeploy
```
发布所有子模块
> 按照正确的依赖顺序，构建并部署所有在 <modules> 中声明的子模块（base, core, xxxx, starters, xxx-starter, yyy-starter）。
```sehll
 mvn clean deploy '-Dskip.spotbugs=true'  -Pdeploy

cd parent
mvn clean deploy \
  -pl lcxm-basic-core,lcxm-basic-excel,lcxm-basic-framework,lcxm-basic-mybatis-plus,lcxm-basic-srpc,lcxm-basic-third,lcxm-trial,lcxm-spring-boot-starters/lcxm-attachment-spring-boot-starter,lcxm-spring-boot-starters/lcxm-generator-spring-boot-starter,lcxm-spring-boot-starters/lcxm-mq-data-bridge-spring-boot-starter,lcxm-spring-boot-starters/lcxm-quartz-spring-boot-starter \
  -am -Pdeploy -Dskip.spotbugs=true
```