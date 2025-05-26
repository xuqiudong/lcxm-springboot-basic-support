## lcxm-generate-spring-boot-starter
- start at 2024-05-21

### 版本更新说明
- 参见：[CHANGELOG](CHANGELOG.md)
  **maven坐标**
```
<dependency>
    <groupId>cn.xuqiudong.basic</groupId>
    <artifactId>lcxm-generate-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```
### 背景
> 同[lcxm-common](https://gitee.com/basic-support/lcxm-common/)
> 原来的所有的模块都放在[boot-support](https://gitee.com/xuqiudong/boot-support), 显得项目很臃肿，现在将其拆分，方便管理。
本项目是原来的[lcxm-generate-starter](https://gitee.com/xuqiudong/boot-support/tree/master/module-starter/lcxm-generate-starter)，现在独立拆分为一个独立项目。



### 打包：
> 打包和发布的时候跳过验证
1. 打包：mvn clean package '-Dskip.spotbugs=true' 
2. install: mvn clean install '-Dskip.spotbugs=true' 
3. 发布： mvn clean deploy '-Dskip.spotbugs=true' -Pdeploy


