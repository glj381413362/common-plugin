package com.common.plugin.sms.exception;

/**
 * description
 *
 * @author roman 2019/06/06 3:13 PM
 */
public class StoreMsgFailException extends RuntimeException {
    public StoreMsgFailException() {
        super();
    }

    public StoreMsgFailException(String message, String ...params) {
        super(String.format(message,params));
    }

    public StoreMsgFailException(String message, Throwable cause, String ...params) {
        super(String.format(message,params), cause);
    }

    public StoreMsgFailException(Throwable cause) {
        super(cause);
    }

    protected StoreMsgFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String ...params) {
        super(String.format(message,params), cause, enableSuppression, writableStackTrace);
    }
}
