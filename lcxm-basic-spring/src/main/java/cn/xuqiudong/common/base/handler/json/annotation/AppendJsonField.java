package cn.xuqiudong.common.base.handler.json.annotation;

import cn.xuqiudong.common.base.handler.json.serializer.AppendJsonFieldSerialize;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 追加JSON字段的的注解 基于JACKSON
 * @author VIC
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = AppendJsonFieldSerialize.class)
public @interface AppendJsonField {

    //转JSON时候的key
    String key();

    //转JSON时候的key:value; 形如{"1:描述1","2:描述2"}
    String[] keyValueDesc() default {};

    /**类型*/
    String appendType() default "direct";

    /**其他信息*/
    String otherInfo() default "";


    public enum AppendType {
        /**
         * 直接的，根据keyValueDesc直接进行转换
         */
        direct,
        ;


    }
}
