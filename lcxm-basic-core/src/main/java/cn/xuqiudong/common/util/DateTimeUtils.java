package cn.xuqiudong.common.util;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间工具类
 *
 * @author Vic.xu
 * @since 2021/10/27
 */
@SuppressWarnings("PMD")
public class DateTimeUtils extends DateUtils {

    /**
     * 相差月份
     *
     * @param start
     * @param end
     * @return
     */
    public static long monthsDiff(Date start, Date end) {
        LocalDate s = date2LocalDate(start);
        LocalDate e = date2LocalDate(end);
        long months = ChronoUnit.MONTHS.between(s, e);
        return months;
    }

    /**
     * 相差年份
     *
     * @param start
     * @param end
     * @return
     */
    public static long yearsDiff(Date start, Date end) {
        LocalDate s = date2LocalDate(start);
        LocalDate e = date2LocalDate(end);
        long years = ChronoUnit.YEARS.between(s, e);
        return years;
    }

    /**
     * 相差天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long daysDiff(Date start, Date end) {
        LocalDate s = date2LocalDate(start);
        LocalDate e = date2LocalDate(end);
        long days = ChronoUnit.DAYS.between(s, e);
        return days;
    }

    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            java.sql.Date sqlDate = (java.sql.Date) date;
            return sqlDate.toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 将本地时间,转换成对应时区的时间
     *
     * @param localDate local date
     * @param targetTimezone
     *            转换成目标时区所在的时间
     * @return
     */
    public static Date convertTimezone(Date localDate, TimeZone targetTimezone) {
        return convertTimezone(localDate, TimeZone.getDefault(), targetTimezone);
    }

    /**
     * 将sourceDate转换成指定时区的时间
     *
     * @param sourceDate
     * @param sourceTimezone
     *            sourceDate所在的时区
     * @param targetTimezone
     *            转化成目标时间所在的时区
     * @return
     */
    public static Date convertTimezone(Date sourceDate, TimeZone sourceTimezone, TimeZone targetTimezone) {

        // targetDate - sourceDate=targetTimezone-sourceTimezone
        // --->
        // targetDate=sourceDate + (targetTimezone-sourceTimezone)

        Calendar calendar = Calendar.getInstance();
        // date.getTime() 为时间戳, 为格林尼治到系统现在的时间差,世界各个地方获取的时间戳是一样的,
        // 格式化输出时,因为设置了不同的时区,所以输出不一样
        long sourceTime = sourceDate.getTime();

        calendar.setTimeZone(sourceTimezone);
        // 设置之后,calendar会计算各种filed对应的值,并保存
        calendar.setTimeInMillis(sourceTime);

        // 获取源时区的到UTC的时区差
        int sourceZoneOffset = calendar.get(Calendar.ZONE_OFFSET);

        calendar.setTimeZone(targetTimezone);
        calendar.setTimeInMillis(sourceTime);

        int targetZoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int targetDaylightOffset = calendar.get(Calendar.DST_OFFSET); // 夏令时

        long targetTime = sourceTime + (targetZoneOffset + targetDaylightOffset) - sourceZoneOffset;

        return new Date(targetTime);

    }

    /* ***********************************************************************/
    public static final String US_EST = "-5"; // 东部标准时区
    public static final String US_CST = "-6";// 中部标准时区
    public static final String US_MST = "-7";// 山地标准时区
    public static final String US_PST = "America/Los_Angeles"; // 也可以使用"-8" 太平洋标准时区

    public static final String JST = "Asia/Tokyo";// 日本东京

    static String pattern = "yyyy-MM-DD HH:mm:ss";

    public static void timeZone() {
        ZoneId id = ZoneId.of(JST);
        Date date = new Date();
        System.out.println(DateFormatUtils.format(date, pattern));
        TimeZone zone = TimeZone.getTimeZone(id);
        Date date2 = convertTimezone(date, zone);
        System.out.println(DateFormatUtils.format(date2, pattern));
    }

    public static void main(String[] args) throws ParseException {
        Date d1 = DateUtils.parseDate("2021-01-27", "yyyy-MM-dd");
        Date end = new Date();
        System.out.println(daysDiff(d1, end));
        System.out.println(monthsDiff(d1, end));
        System.out.println(yearsDiff(d1, end));

        Period p = Period.between(date2LocalDate(d1), date2LocalDate(end));

        System.out.printf("年龄 : %d 年 %d 月 %d 日", Math.abs(p.getYears()), Math.abs(p.getMonths()), p.getDays());
        TimeZone zone = TimeZone.getDefault();
        System.out.println(zone);
        timeZone();


        java.sql.Date date = new java.sql.Date(new Date().getTime());
        Date date1 = DateUtils.addDays(date, 10);
        int i = DateUtils.truncatedCompareTo(date, date1, Calendar.DAY_OF_MONTH);
        System.out.println(i);
        long l = DateTimeUtils.daysDiff(date, date1);
        System.out.println(l);
    }

}
