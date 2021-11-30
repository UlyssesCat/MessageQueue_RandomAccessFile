package pku.java;

import java.util.Collection;

public abstract class QueueStore {
    /**
     * 把一条消息写入一个队列；
     * 每个queue中的内容，按发送顺序存储消息（可以理解为Java中的List），同时每个消息会有一个索引，索引从0开始；
     * 不同queue中的内容，相互独立，互不影响；
     * @param queueName 代表queue序号
     * @param message message，代表消息的内容，本次实验为订单编号
     */
    abstract void put(String queueName, byte[] message);

    /**
     * 从一个队列中读出一批消息，读出的消息要按照发送顺序来；
     * @param queueName 代表队列的序号
     * @param offset 代表消息的在这个队列中的起始消息索引
     * @param num 代表读取的消息的条数，如果消息足够，则返回num条，否则只返回已有的消息即可;没有消息了，则返回一个空的集合
     */
    abstract Collection<byte[]> get(String queueName, long offset, long num);
}
