package cn.xuqiudong.basic.core.transmission.base;

/**
 * 描述: 相应对象基类的父接口
 * @author Vic.xu
 * @since 2022-08-17 11:06
 */
public interface ThirdResponse {
    /**
     * 子类对象需要重写toString  方便入库， 建议转json
     * @return String
     */
    @Override
    String toString();
}
