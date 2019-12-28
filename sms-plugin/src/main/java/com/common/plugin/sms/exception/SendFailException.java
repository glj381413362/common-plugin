package com.common.plugin.sms.exception;

/**
 * description
 *
 * @author roman 2019/06/06 3:13 PM
 */
public class SendFailException extends RuntimeException {
    public SendFailException() {
        super();
    }

    public SendFailException(String message,String ...params) {
        super(String.format(message,params));
    }

    public SendFailException(String message, Throwable cause,String ...params) {
        super(String.format(message,params), cause);
    }

    public SendFailException(Throwable cause) {
        super(cause);
    }

    protected SendFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,String ...params) {
        super(String.format(message,params), cause, enableSuppression, writableStackTrace);
    }
}
