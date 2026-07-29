package cn.xuqiudong.basic.third.common.model;

import cn.hutool.core.util.StrUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 第三方标识。
 *
 * <p>公共模块不维护统一厂商枚举，具体项目自行定义 thirdCode。</p>
 *
 * @author Vic.xu
 */
@Getter
@ToString
@EqualsAndHashCode(of = "code")
public final class ThirdIdentity {

    /**
     * 当前项目内唯一的第三方编码。
     */
    private final String code;

    /**
     * 第三方显示名称；为空时使用 {@link #code}。
     */
    private final String name;

    public ThirdIdentity(String code, String name) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("third code can not be blank");
        }
        this.code = code;
        this.name = StrUtil.isBlank(name) ? code : name;
    }

    public static ThirdIdentity of(String code) {
        return new ThirdIdentity(code, code);
    }

    public static ThirdIdentity of(String code, String name) {
        return new ThirdIdentity(code, name);
    }
}
