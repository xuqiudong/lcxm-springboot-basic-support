package cn.xuqiudong.common.base.craw;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.Proxy;

/**
 * 爬虫基类
 * 
 * @author VIC
 *
 */
public abstract class BaseCrawl {
    /**
     * 连接网页
     * 
     * @param url
     * @return
     */
    protected CrawlConnect con(String url) {
        Connection conn = Jsoup.connect(url).ignoreContentType(true).timeout(getTimeout());
        if (isOpenProxy() && getProxy() != null) {
            conn = conn.proxy(getProxy());
        }
        return new CrawlConnect(conn);
    }

    /**
     * 连接超时时间
     * 
     * @return
     */
    protected abstract int getTimeout();

    /**
     * 是否开启代理 子类根据需要复写
     * 
     * @return
     */
    protected boolean isOpenProxy() {
        return false;
    }

    /**
     * 代理配置 子类根据需要复写
     * 
     * @return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
     */
    protected Proxy getProxy() {

        return null;
    }
}
