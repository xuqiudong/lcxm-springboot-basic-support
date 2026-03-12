package cn.xuqiudong.basic.third.transmission.base;

/**
 * 描述: 请求对象基类的父接口
 * @author Vic.xu
 * @since 2022-08-17 11:05
 */
public interface ThirdRequest {

    /**
     * 子类对象需要重写toString  方便入库， 建议转json
     * @return String
     */
    @Override
    String toString();

    /**
     * 获取当前数据关联的业务id,多个则逗号分隔
     * @return String
     */
    String getFid();
}
