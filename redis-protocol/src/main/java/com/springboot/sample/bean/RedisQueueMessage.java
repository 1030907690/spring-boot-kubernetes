package com.springboot.sample.bean;

import java.util.Date;

/***
 * zzq
 * 2022年2月13日13:35:29
 * redis队列消息
 * */
public class RedisQueueMessage {
    /**
     * 消息唯一标识
     */
    private String id;
    /**
     * 消息的分类 传入Spring BeanName
     * 为消费时不同类去处理
     */
    private String beanName;
    /**
     * 具体消息 json
     */
    private String body;

    /**
     * 延时时间 被消费时间  取当前时间戳 延迟时间
     */
    private Long delayTime;

    /**
     * 创建时间
     */
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Long delayTime) {
        this.delayTime = delayTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", channel='" + beanName + '\'' +
                ", body='" + body + '\'' +
                ", delayTime=" + delayTime +
                ", createTime=" + createTime +
                '}';
    }
}
