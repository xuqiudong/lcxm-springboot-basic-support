## lcxm-common
- start at 2024-05-20
- 关于groupId, 由于[中央仓库](https://central.sonatype.com/publishing/namespaces)升级，导致namespace暂时无法从OSSRH切换到Central Portal。所以暂时无法使用`cn.xuqiudong` 以及`io.gitee.xuqiudong`, 所以暂时使用`io.github.xuqiudong`

- 2024-05-21 在使用`io.github.xuqiudong`成功首次发布到中央仓库的时候，看见`cn.xuqiudong`这个namespace已经转移到Central Portal。查看了下邮件，果然给了回复。
- 所以，再次切换本项目的groupId为`cn.xuqiudong`.


### 背景
> 原来的所有的模块都放在[boot-support](https://gitee.com/xuqiudong/boot-support), 显得项目很臃肿，现在将其拆分，方便管理。
本项目是原来的[boot-support](https://gitee.com/xuqiudong/boot-support)的子模块的集合体，包含了一些常用的工具类与一些常用的基类或功能点。
-  [lcxm-common-util](https://gitee.com/xuqiudong/boot-support/tree/master/lcxm-common-util)
-  [lcxm-common-base](https://gitee.com/xuqiudong/boot-support/tree/master/lcxm-common-base)



### 打包：
> 打包和发布的时候跳过验证
1. 打包：mvn clean package '-Dskip.spotbugs=true' 
2. install: mvn clean install '-Dskip.spotbugs=true' 
3. 发布： mvn clean deploy '-Dskip.spotbugs=true'  -Pdeploy


### 版本更新说明
- 参见：[CHANGELOG](CHANGELOG.md)
**maven坐标**
```
<dependency>
    <groupId>cn.xuqiudong.common</groupId>
    <artifactId>lcxm-common</artifactId>
    <version>${version}</version>
</dependency>
```