package cn.xuqiudong.basic.framework.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * some constants
 * @author Vic.xu
 * @since 2026-07-27 17:11
 */
public class TextBundle {


    private static final Map<String, String> BUNDLE = new HashMap<>();


    static {
        BUNDLE.put("p1", "jY9T3E8jiLg0d0WCf642iMOKzi4sro5WshBGRQYuWiXTjavD0ivkWNWPsKxV3QnGjmMz7wGiQ0YzJEKuQVR0SF0D1ufluuRcoKIjSZCLO2cxedqhm3QcRo37nPbBRblSaRtU7XOyxlWSCHah7BcHzyMRvgGY8wmT00Vypl3nFOO36xwv9HaOR649jkseBrC8wo6PmOjhmoC9WvVu8SM1ZJ1uPIxsGOUSVJvEO8wUhqHjfTq8pbe2jfh7JZTD3CqbLz7Il8Ltq9o2pNjVkghUfEQx7BPLfdc0ME");
        BUNDLE.put("l", "2Fl6jZ7bMb");
        BUNDLE.put("tip_1", "tAvGrGHEOrmAMgJwAbQ83W8J4xoX32y6p7FZvxCaSoL");
        BUNDLE.put("tip_2", "tAyolEkZiWATkYhlTBD55xjQiDGs3spBRuNzNeNy4qW");

    }

    public static void main(String[] args) {

    }

    public static String get(String key) {
        return BUNDLE.get(key);
    }

    public static String getTip(int i) {
        return BUNDLE.get("tip_" + i);
    }


}
