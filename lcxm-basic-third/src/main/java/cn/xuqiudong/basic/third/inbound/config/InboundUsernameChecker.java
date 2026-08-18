package cn.xuqiudong.basic.third.inbound.config;

/**
 * 入站 token 申请用户校验回调。
 *
 * <p>业务线可通过该回调从数据库、缓存或其他业务系统判断 username 是否允许申请 token。</p>
 *
 * @author Vic.xu
 */
@FunctionalInterface
public interface InboundUsernameChecker {

    /**
     * 判断 username 是否允许申请 token。
     *
     * @param username token 申请中的 username
     * @return {@code true} 表示允许，{@code false} 表示拒绝
     */
    boolean checkUsername(String username);
}
