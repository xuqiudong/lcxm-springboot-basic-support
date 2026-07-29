## 打点tag

感觉现在common有点臃肿了  想要拆分一下:

### 第一步：给当前状态打 Tag，保留 “拆分前基线”
- 带注释的 Tag：
```
# 模块名-拆分前-原版本号，注释说明“未拆分的基础模块，包含common及所有starter”
git tag -a lcxm-basic-pre-split-3.5.0-jdk17-1.0.0 -m "拆分前基线：包含lcxm-common、各starter，未拆分专项模块"
```
- 推送 Tag 到 Gitee 远程：
 `git push origin lcxm-basic-pre-split-3.5.0-jdk17-1.0.0`

### 第二步：基于现有结构拆分、修改，升级版本号
1. 拆分逻辑：“最小改动 + 逐步迁移”
   - 先不删除原 lcxm-common，而是新建拆分后的模块（如 lcxm-basic-core、lcxm-basic-mybatis-plus）；
   - 从原 lcxm-common 中 “复制”（而非剪切）代码到新模块（避免误删导致代码丢失，复制后验证功能正常再删除原代码）；
   - 调整各模块的依赖关系（如原 starter 从依赖 lcxm-common 改为依赖新拆分的 lcxm-basic-core 或 lcxm-basic-mybatis-plus）。
2. 版本号升级：用 “修订号 / 次版本号” 体现拆分
 - 若仅拆分模块、不修改核心功能逻辑：升级修订号（最后一位），例如从 3.5.0-jdk17-1.0.0 升到 3.5.0-jdk17-1.1.0；
 - 若拆分同时优化了部分功能（如工具类重构）：升级次版本号（中间一位），例如升到 3.5.0-jdk17-1.1.0 或 3.5.0-jdk17-2.0.0；
 -  版本号变化需在父工程 pom.xml 中统一修改，子模块继承父版本即可。 

### 第三步：拆分完成后，给新状态打 Tag（可选但推荐）
```
git tag -a lcxm-basic-post-split-3.5.0-jdk17-1.1.0 -m "拆分后版本：拆分lcxm-common为core、mybatis-plus、redis等专项模块，各starter依赖调整完成"
git push origin lcxm-basic-post-split-3.5.0-jdk17-1.1.0
```

### 3.5.0-jdk17-2.0.0 
```
git tag -a 3.5.0-jdk17-2.0.0 -m "springboot3.5 + jdk17版本，对应中央仓库中的版本"
git push origin 3.5.0-jdk17-2.0.0
```