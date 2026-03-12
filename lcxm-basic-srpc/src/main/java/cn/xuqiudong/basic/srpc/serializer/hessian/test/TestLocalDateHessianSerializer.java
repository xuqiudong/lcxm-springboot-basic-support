package cn.xuqiudong.basic.srpc.serializer.hessian.test;

import cn.xuqiudong.basic.srpc.model.XqdResponse;
import cn.xuqiudong.basic.srpc.serializer.hessian.Hessian2Serializer;
import cn.xuqiudong.basic.core.util.JsonUtil;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

/**
 * 描述:
 *
 * @author Vic.xu
 * @since 2025-08-25 11:18
 */
public class TestLocalDateHessianSerializer {

    private static Hessian2Serializer serializer = new Hessian2Serializer();

    public static void main(String[] args) {
            testXqdResponse2(LocalDateTime.now());
            testXqdResponse2(ZonedDateTime.now());
            testXqdResponse2(YearMonth.now());
            testXqdResponse2(LocalTime.now());
//        testSingle();
        System.out.println("");
//        testObject();
        System.out.println("");
//        testXqdResponse();

    }

    // 1 测试单个 LocalDate 成功
    private static void testSingle() {
        System.out.println("test testSingle LocalDate");
        // 1 测试单个 LocalDate 成功
        LocalDate localDate = LocalDate.now();
        System.out.println(" origin:" + localDate);

        byte[] serialize = serializer.serialize(localDate);
        LocalDate deserialize = serializer.deserialize(serialize, LocalDate.class);
        System.out.println(" deserialize:" + deserialize);
    }

    /**
     * 2 测试对象中的 LocalDate   可以成功
     */
    public static void testObject() {
        LocalDate localDate = LocalDate.now();
        TestModel model = new TestModel();
        model.setId("1");
        model.setName("22");
        model.setDate(localDate.now());
        // 测试对象中的 LocalDate
        System.out.println("测试对象中的LocalDate:");
        System.out.println(" origin TestModel:" + JsonUtil.toJson(model));
        TestModel deserializeModel = serializer.deserialize(serializer.serialize(model), TestModel.class);
        System.out.println(" deserialize TestModel:" + JsonUtil.toJson(deserializeModel));


        //  把model放到 XqdResponse 中 再序列号
        XqdResponse localDateXqdResponse2 = XqdResponse.success(model);
        byte[] serialize2 = serializer.serialize(localDateXqdResponse2);
        // 然后反序列化  出 XqdResponse   从里面获取到  TestModel  再从 TestModel 中获取  LocalDate 也是成功
        XqdResponse deserialize2 = serializer.deserialize(serialize2, XqdResponse.class);
        System.out.println("测试XqdResponse中的 对象(对象包含LocalDate) :");
        System.out.println(" origin XqdResponse:" + JsonUtil.toJson(localDateXqdResponse2));
        System.out.println(" deserialize XqdResponse:" + JsonUtil.toJson(deserialize2));
    }

    /**
     * 3 测试 XqdResponse 中的LocalDate
     * XqdResponse 中的对象是泛型
     */
    public static void testXqdResponse() {
        LocalDate localDate = LocalDate.now();
        XqdResponse localDateXqdResponse = XqdResponse.success(localDate);
        System.out.println("测试XqdResponse中的LocalDate:");
        System.out.println(" origin XqdResponse:" + JsonUtil.toJson(localDateXqdResponse));
        byte[] serialize = serializer.serialize(localDateXqdResponse);
        XqdResponse deserialize = serializer.deserialize(serialize, XqdResponse.class);
        System.out.println(" deserialize XqdResponse:" + JsonUtil.toJson(deserialize));

        LocalDateTime localDateTime = LocalDateTime.now();
        XqdResponse localDateTimeXqdResponse = XqdResponse.success(localDateTime);
        System.out.println("测试XqdResponse中的 LocalDateTime:");
        System.out.println(" origin XqdResponse:" + JsonUtil.toJson(localDateTimeXqdResponse));
        byte[] serialize2 = serializer.serialize(localDateTimeXqdResponse);
        XqdResponse deserialize2 = serializer.deserialize(serialize2, XqdResponse.class);
        System.out.println(" deserialize XqdResponse:" + JsonUtil.toJson(deserialize2));
    }

    /**
     * 测试 XqdResponse 泛型测试
     */
    public static void testXqdResponse2(Temporal temporal) {
        XqdResponse<Temporal> xqdResponse = XqdResponse.success(temporal);
        System.out.println("测试XqdResponse中的泛型:");
        System.out.println(" origin XqdResponse:" + JsonUtil.toJson(xqdResponse));
        byte[] serialize = serializer.serialize(xqdResponse);
        XqdResponse<Temporal> deserialize = serializer.deserialize(serialize, XqdResponse.class);
        System.out.println(" class:  " +deserialize.getResultData().getClass());
        System.out.println(" deserialize XqdResponse:" + JsonUtil.toJson(deserialize));
        System.out.println();
    }


    static class TestModel implements Serializable {

        private static final long serialVersionUID = 1L;

        String id;

        String name;

        LocalDate date;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

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
    }
}
