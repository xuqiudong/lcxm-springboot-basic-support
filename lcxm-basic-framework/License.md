# License 功能说明

## 目标

该功能用于给框架增加一个轻量级运行时授权检查能力。应用启动或业务接口访问时，运行端从 classpath 下读取 `license` 文件，解析授权内容并使用内置公钥验签，防止授权内容被直接篡改。

该方案适合内部项目、轻量商业授权、过期提醒等场景；不属于强防破解方案。

## 核心文件

- `src/main/java/cn/xuqiudong/basic/framework/runtime/LcRuntimeHelper.java`
  运行端 license 读取、解析、验签入口。
- `src/main/java/cn/xuqiudong/basic/framework/runtime/RuntimeGuard.java`
  运行时拦截检查入口，后续通过注册该切面生效。
- `src/main/java/cn/xuqiudong/basic/framework/runtime/LcPayload.java`
  license 载荷对象。
- `src/main/java/cn/xuqiudong/basic/framework/runtime/SignaturePayloadBuilder.java`
  构造签名前的固定字符串。
- `src/main/java/cn/xuqiudong/basic/framework/runtime/RsaSignatureUtils.java`
  RSA 签名与验签工具。
- `src/test/java/cn/xuqiudong/basic/framework/license/issuer/LicenseIssuer.java`
  测试侧 license 签发工具。

## License 格式

license 文件名固定为：

```text
license
```

文件应放在 classpath 根路径下，例如：

```text
src/main/resources/license
src/test/resources/license
```

文件内容是 Base62 编码后的 JSON 字符串。原始 JSON 对应 `LcPayload`：

```json
{
  "issueAt": 1785200000000,
  "expireAt": 1785800000000,
  "version": "1.0.0",
  "subject": "Vic.xu",
  "sign": "..."
}
```

## 签发流程

发行端流程：

1. 创建 `LcPayload`，写入 `subject`、`version`、`issueAt`、`expireAt`。
2. 调用 `SignaturePayloadBuilder.buildIssuerPayload(payload)` 构造待签名字符串。
3. 使用私钥调用 `RsaSignatureUtils.privateSign(...)` 生成签名。
4. 将签名写入 `payload.sign`。
5. 将 payload 转成 JSON。
6. 将 JSON 做 Base62 编码，得到最终 license 文件内容。

待签名字符串格式固定为：

```text
subject={subject}&issueAt={issueAt}&expireAt={expireAt}&version={version}
```

`subject` 和 `version` 会进行 UTF-8 URL encode。运行端和发行端必须使用同一个 `SignaturePayloadBuilder`，避免字段顺序或编码规则不一致。

## 运行时校验流程

`LcRuntimeHelper.instance()` 的流程：

1. 从 classpath 读取 `license` 文件。
2. 读取第一行 license 字符串。
3. Base62 解码得到 JSON。
4. JSON 反序列化为 `LcPayload`。
5. 使用 payload 重新构造待签名字符串。
6. 使用 `TextBundle` 中内置的公钥验签。
7. 验签通过后缓存并返回 `LcPayload`。

如果验签失败、文件缺失或解析异常，当前实现会打印提示并尝试退出应用。

## 运行时拦截

`RuntimeGuard` 用于在业务请求前检查授权状态：

- 启动时触发 `LcRuntimeHelper.instance()`，提前加载 license。
- 请求进入时检查 `LcRuntimeHelper.instance().expired()`。
- 过期后按当前策略返回错误响应。

后续需要将 `RuntimeGuard` 注册为 Spring Bean，切面才会实际生效。

## 测试

当前测试类：

```text
src/test/java/cn/xuqiudong/basic/framework/license/LicenseRuntimeTest.java
```

执行：

```bash
mvn -q -Dtest=LicenseRuntimeTest test
```

测试覆盖：

- `LcRuntimeHelper.instance()` 可以读取 classpath 下的 `license` 并返回 payload。
- 发行端生成的 license 可以被运行端公钥验签。
- 篡改 payload 后验签失败。
- 过期 license 可以被识别。

## 注意事项

1. 私钥只能保存在发行端工具或服务中，不能进入运行端分发包。
2. 运行端只应内置公钥。
3. license 文件只读取第一行，生成文件时不要写入额外内容。
4. 失败路径不应只依赖 `System.exit`，建议同时抛出异常，避免退出失败后继续运行。
5. `LicensePayload` 如果继承 `LcPayload`，不要重复声明同名字段，避免 JSON 序列化和 Lombok getter 行为产生歧义。
