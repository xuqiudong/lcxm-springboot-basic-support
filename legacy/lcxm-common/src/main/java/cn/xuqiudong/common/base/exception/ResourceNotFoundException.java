package cn.xuqiudong.common.base.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 描述:
 * @author Vic.xu
 * @since 2022-03-01 8:51
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String string) {
        super(string);
    }

    public ResourceNotFoundException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public ResourceNotFoundException(Throwable thrwbl) {
        super(thrwbl);
    }

}
