package cn.xuqiudong.mq.bridge.enums;

import java.time.LocalDate;

/**
 * 描述:
 * 归档方式枚举: 年、月
 *
 * @author Vic.xu
 * @since 2026-02-02 14:21
 */
public enum ArchivePeriodEnum {

    YEAR("yyyy"),
    MONTH("yyyyMM");
    private String format;

    ArchivePeriodEnum(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public String format(LocalDate localDate){
        return localDate.format(java.time.format.DateTimeFormatter.ofPattern(format));
    }

    public static void main(String[] args) {
        System.out.println(ArchivePeriodEnum.YEAR.format(LocalDate.now()));
        System.out.println(ArchivePeriodEnum.MONTH.format(LocalDate.now()));
    }

}
