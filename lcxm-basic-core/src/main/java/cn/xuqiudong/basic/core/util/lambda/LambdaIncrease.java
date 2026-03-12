package cn.xuqiudong.basic.core.util.lambda;

/**
 * 描述:Lambda 中的自增
 * @author Vic.xu
 * @since 2022-04-18 11:34
 */
public class LambdaIncrease {

    private int[] increasingArr = {0};


    private LambdaIncrease() {
    }

    public int value() {
        return increasingArr[0];
    }


    public static LambdaIncrease start() {
        return new LambdaIncrease();
    }

    /**
     * 自增
     */
    public int increasing() {
        return increasing(1);
    }

    /**
     * 自增，减的话 传负数
     * @param num step
     * @return result
     */
    public int increasing(int num) {
        increasingArr[0] = increasingArr[0] + num;
        return increasingArr[0];
    }
}
