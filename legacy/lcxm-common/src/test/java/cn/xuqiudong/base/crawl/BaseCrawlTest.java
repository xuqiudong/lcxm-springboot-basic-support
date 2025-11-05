package cn.xuqiudong.base.crawl;

import cn.xuqiudong.common.base.craw.BaseCrawl;
import cn.xuqiudong.common.base.craw.CrawlConnect;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-05-22 17:52
 */
public class BaseCrawlTest extends BaseCrawl {
    @Override
    protected int getTimeout() {
        return 30000;
    }

    public static void main(String[] args) throws IOException {
        BaseCrawlTest baseCrawlTest = new BaseCrawlTest();
        CrawlConnect con =test("https://www.baidu.com");
        con.header(CrawlConnect.CONTENT_TYPE_KEY, "application/json;charset=utf-8");
        con.header("accept", "");
        String bodyText = con.getBodyText();
        System.out.println(bodyText);
    }

    public static CrawlConnect test(String url){
        Connection conn = Jsoup.connect(url).ignoreContentType(false).timeout(3000);
        return new CrawlConnect(conn);
    }
}
