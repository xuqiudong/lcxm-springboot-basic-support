package cn.xuqiudong.basic.secureid.core;

/**
 * Description:
 * 标识某个对象的id 在controller 返回后是加密的
 *
 * @author Vic.xu
 * @since 2026-08-20 18:17
 */
public interface IdEncryptable {

    String getId();

    void setId(String id);
}
