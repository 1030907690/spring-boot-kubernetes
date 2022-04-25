package com.redis.queue.exception;

/**
 * 消费业务异常
 */
public class RedisConsumerBusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    public RedisConsumerBusinessException(String message) {
        this.message = message;
    }

    public RedisConsumerBusinessException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public RedisConsumerBusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
