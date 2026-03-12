package cn.xuqiudong.basic.trial.mapstruct;

import cn.xuqiudong.basic.core.util.JsonUtil;

import java.time.LocalDate;

/**
 * 描述:
 *  https://mapstruct.org/
 * @author Vic.xu
 * @since 2025-11-08 13:54
 */
public class TestMapStruct {


    public static void main(String[] args) {
        MapStructSourceModel model = new MapStructSourceModel();
        model.setId("1");
        model.setName("Vic.xu");
        model.setBirthDate(LocalDate.now());
        model.setAddress("上海");
        model.setPrice(18.032);
        model.setAge(18);
        MapstructSub sub = new MapstructSub();
        sub.setId("2");
        sub.setName("sub");
        model.setSub(sub);
        JsonUtil.printJson(model);
        MapStructSourceDto dto = MapstructConvert.INSTANCE.toDto(model);
        JsonUtil.printJson(dto);
        MapStructSourceModel model1 = MapstructConvert.INSTANCE.toModel(dto);
        JsonUtil.printJson(model1);
    }
}
