package cn.xuqiudong.basic.third.inbound.constant;

/**
 * 入站 token 默认常量。
 *
 * @author Vic.xu
 */
public interface InboundTokenConstants {


    String TOKEN_HEADER_NAME = "X-Third-Token";

    String TOKEN_PARAMETER_NAME = "token";

    String API_PATH_PATTERN = "/inbound/api/**";

    String OBTAIN_TOKEN_PATH = "/inbound/api/obtain-token";

    String REVOKE_TOKEN_PATH = "/inbound/api/revoke-token";
}
