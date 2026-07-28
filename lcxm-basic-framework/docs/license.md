# License

## 用途

License 功能用于做轻量级运行时授权校验。

运行端从 classpath 读取 `license` 文件，解析授权内容，并用内置公钥验签。验签通过后返回授权信息，业务请求再通过 `RuntimeGuard` 判断是否过期。

该方案适合内部项目、简单商业授权、过期提醒等场景，不属于强防破解方案。

## 文件放置

运行项目需要在 classpath 根路径放置 license 文件：

```text
src/main/resources/license
```

测试环境可以放在：

```text
src/test/resources/license
```

`license` 文件只读取第一行，不要写入额外内容。

## License 内容

`license` 文件内容是 Base62 编码后的 JSON。

JSON 原始结构对应 `LcPayload`：

```json
{
  "issueAt": 1785200000000,
  "expireAt": 1785800000000,
  "version": "1.0.0",
  "subject": "Vic.xu",
  "nonce": "...",
  "sign": "..."
}
```

字段说明：

- `issueAt`：签发时间，毫秒时间戳。
- `expireAt`：过期时间，毫秒时间戳。
- `version`：授权版本。
- `subject`：授权主体。
- `nonce`：签发时生成的随机值，参与签名，避免 license 内容过于固定。
- `sign`：RSA 私钥签名结果。

## 签发流程

签发端使用 `LicenseIssuer` 生成 license：

1. 创建 `LcPayload`，写入授权主体、版本、签发时间、过期时间和随机值。
2. 使用 `SignaturePayloadBuilder` 构造待签名字符串。
3. 使用私钥调用 `RsaSignatureUtils` 生成签名。
4. 将 payload 转成 JSON。
5. 将 JSON 做 Base62 编码，得到最终 license 内容。

待签名字符串格式：

```text
subject={subject}&issueAt={issueAt}&expireAt={expireAt}&nonce={nonce}&version={version}
```

运行端和签发端必须使用同一个签名串构造规则。

## 运行时流程

`LcRuntimeHelper.instance()` 的处理流程：

1. 从 classpath 读取 `license` 文件。
2. 读取第一行 license 字符串。
3. Base62 解码得到 JSON。
4. JSON 反序列化为 `LcPayload`。
5. 根据 payload 重新构造待签名字符串。
6. 使用 `TextBundle` 中的公钥验签。
7. 验签通过后缓存并返回 `LcPayload`。

`RuntimeGuard` 用于请求进入前检查授权状态。后续需要将它注册为 Spring Bean，切面才会生效。

## 接入其他项目

运行端项目只需要：

1. 移植 `runtime` 包。
2. 注册 `RuntimeGuard`。
3. 放置 `src/main/resources/license`。
4. 保留 `TextBundle` 中的公钥。

签发端代码不要放进运行端项目：

1. `LicenseIssuer` 只保留在发行方项目中。
2. 私钥只保存在发行端工具或服务中。
3. 运行端不能包含私钥，也不要包含可直接生成正式 license 的代码。

推荐流程：

1. 在发行方项目中用 `LicenseIssuer` 生成 license 字符串。
2. 将生成结果写入目标项目的 `src/main/resources/license`。
3. 目标项目启动后，`LcRuntimeHelper.instance()` 会读取并验签。

## 测试

测试类：

```text
LicenseRuntimeTest
```

执行：

```bash
mvn -q -Dtest=LicenseRuntimeTest test
```

当前覆盖：

- `instance()` 能读取 classpath 下的 `license`。
- 发行端生成的 license 可以被运行端公钥验签。
- 篡改 payload 后验签失败。
- 过期 license 可以被识别。

## 注意事项

1. 运行端只放公钥，不放私钥。
2. 私钥不要提交到运行端项目或客户项目。
3. 失败路径不要只依赖 `System.exit`，建议同时抛出异常。
4. `LicensePayload` 不要重复声明 `LcPayload` 已有字段。
