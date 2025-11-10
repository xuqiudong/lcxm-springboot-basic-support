package cn.xuqiudong.common.base.exception;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

/**
 * 描述:参数绑定异常 其实这是个400异常@ResponseStatus(HttpStatus.BAD_REQUEST)  但是这里不抛出 而是捕捉 并返回说明
 * @author Vic.xu
 * @since 2022-03-01 8:50
 */
public class BadParamException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**是否是HIBERNATE验证*/
    private boolean validated = false;

    public BadParamException() {
        super();
    }

    /**build  hibernate verify exception*/
    public static <T> BadParamException instanceHibernateVerity(Set<ConstraintViolation<T>> errors) {
        StringBuilder verifyError = new StringBuilder();
        for (ConstraintViolation<?> c : errors) {
            verifyError.append(c.getMessage()).append("； ");
        }
        verifyError.deleteCharAt(verifyError.length() - 1);
        BadParamException e = new BadParamException(verifyError.toString());
        e.validated = true;
        return e;

    }

    public BadParamException(String string) {
        super(string);
    }

    public BadParamException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public BadParamException(Throwable throwable) {
        super(throwable);
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }


}
