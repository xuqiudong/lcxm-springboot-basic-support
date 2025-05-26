package cn.xuqiudong.mq.bridge.consumer.model;

import cn.xuqiudong.mq.bridge.vo.AbstractDataBridgeVo;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-03-11 11:03
 */
public class DemoConsumerModel extends AbstractDataBridgeVo {

    private String id;

    private String name;

    private int age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
