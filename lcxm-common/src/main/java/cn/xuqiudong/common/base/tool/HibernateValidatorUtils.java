package cn.xuqiudong.common.base.tool;


import cn.xuqiudong.common.base.exception.BadParamException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 *  说明 :  基于hibernate Validator 的参数校验
 *  @author Vic.xu
 * @since  2022年3月1日 09:47:41
 */
public class HibernateValidatorUtils {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static <T> void validate(T o) throws BadParamException {
        if (o == null) {
            throw new IllegalArgumentException();
        }

        Set<ConstraintViolation<T>> constraintViolation = VALIDATOR.validate(o);
        if (constraintViolation.size() != 0) {
            throw BadParamException.instanceHibernateVerity(constraintViolation);
        }
    }

    public static <T> void validate(T o, Class<?> cls) throws BadParamException {
        if (o == null) {
            throw new IllegalArgumentException();
        }
        Set<ConstraintViolation<T>> constraintViolation = VALIDATOR.validate(o, cls);
        if (constraintViolation.size() != 0) {
            throw BadParamException.instanceHibernateVerity(constraintViolation);
        }
    }
}
