package com.common.plugin.sms.exception;

/**
 * description
 *
 * @author roman 2019/06/06 4:23 PM
 */
public class ParamException extends RuntimeException {
    public ParamException() {
        super();
    }

    public ParamException(String message,String ...params) {
        super(String.format(message,params));
    }

    public ParamException(String message, Throwable cause,String ...params) {
        super(String.format(message,params), cause);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }

    protected ParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,String ...params) {
        super(String.format(message,params), cause, enableSuppression, writableStackTrace);
    }
}
