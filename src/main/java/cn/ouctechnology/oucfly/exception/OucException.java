package cn.ouctechnology.oucfly.exception;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 12:10
 * @description: TODO
 **/
public class OucException  extends RuntimeException {
    public OucException() {
        super();
    }

    public OucException(String message) {
        super(message);
    }

    public OucException(String message, Throwable cause) {
        super(message, cause);
    }

    public OucException(Throwable cause) {
        super(cause);
    }

    protected OucException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
