package cn.xuqiudong.basic.trial.mapstruct;

import lombok.Data;

import java.time.LocalDate;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-11-08 13:53
 */
@Data
public class MapStructSourceDto {

    private String id;

    private String name;

    private LocalDate birthDate;

    private String address;

    private int age;

    private double priceDto;

    private MapstructSub sub;
}
