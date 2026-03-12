package cn.xuqiudong.basic.third.transmission.base;

import cn.xuqiudong.basic.core.craw.BaseCrawl;
import cn.xuqiudong.basic.core.craw.CrawlConnect;
import cn.xuqiudong.basic.third.transmission.util.ThirdCommonUtil;

import java.util.Map;

/**
 * 描述:第三放请求工具基类
 * @author Vic.xu
 * @since 2022-08-19 10:23
 */
public abstract class BaseThirdRequest extends BaseCrawl {


    @Override
    public CrawlConnect con(String url) {
        if (!ThirdCommonUtil.withScheme(url)) {
            url = baseUrl() + url;
        }
        return super.con(url).header(commonHeaders());
    }


    /**
     * 通用前缀
     * @return String or null
     */
    protected abstract String baseUrl();

    /**
     * 通用请求头
     * 比如如果请求和响应参数是json的话：
     * "Content-Type" : "application/json; charset=UTF-8"
     * 	"Accept": "application/json; charset=UTF-8"
     * @return Map<String, String>
     */
    protected abstract Map<String, String> commonHeaders();

}
