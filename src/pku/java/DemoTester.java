package pku.java;

import java.util.*;

public class DemoTester {

    public static void main(String[] args){
        //评测相关配置
        //发送阶段的发送数量
        int msgNum  = 20 * 1000;
        //队列的数量
        int queueNum = 1000;
        //正确性检测的数量
        int checkNum = 100 * 1000;
        //队列名字集合
        List<String> queueNameList = new ArrayList<>();
        //消息队列存储消息的数量
        Map<String, Integer> queueSizeMap = new HashMap<>();

        //参赛者编写消息队列类的实例
//        QueueStore messageQueue = new MessageQueue();
        QueueStore messageQueue = new MessageQueueFile();

        //生成队列
        for (int i = 0; i < queueNum; ++i) {
            String queueName = "Queue-" + i;
            queueNameList.add(queueName);
            queueSizeMap.put(queueName, 0);
        }

        //创建生产者实例
        Producer producer = new Producer(messageQueue, queueNameList, queueSizeMap, msgNum, queueNum);
        //创建消费者实例
        Consumer consumer = new Consumer(messageQueue, queueNameList, queueSizeMap, checkNum, queueNum);

        //测评开始时间戳
        long startTime = System.currentTimeMillis();

        //生产者发送消息
        producer.sendMessage();

        long writeTime = System.currentTimeMillis();

        //写入时间
        System.out.printf("write time : [%.2f] s\n", (writeTime - startTime + 0.1) / 1000);

        //消费者校验消息
        consumer.checkMessage();

        //测评结束时间戳
        long endTime = System.currentTimeMillis();

        //校验时间
        System.out.printf("check time : [%.2f] s\n", (endTime - writeTime + 0.1) / 1000);

        //统计运行时间
        System.out.printf("cost time : [%.2f] s\n", (endTime - startTime + 0.1) / 1000);
    }

    static class Producer{
        //消息队列的数量
        private final int queueAmount;
        //消息队列名字
        private final List<String> queueNameList;
        //消息队列字典
        private final Map<String, Integer> queueSizeMap;
        //最大消息数量
        private final long maxMsgNum;
        //消息队列类的实例
        private final QueueStore messageQueue;
        //构造器
        public Producer(QueueStore messageQueue, List<String> queueNameList, Map<String, Integer> queueSizeMap, int maxMsgNum, int queueAmount) {
            this.messageQueue = messageQueue;
            this.queueNameList = queueNameList;
            this.queueSizeMap = queueSizeMap;
            this.maxMsgNum = maxMsgNum;
            this.queueAmount = queueAmount;
        }

        public void sendMessage() {
            //发送计数器
            int counter = 0;
            //进度条进度百分比
            int sendProcess = 0;
            Random random = new Random();
            // 循环条件 发送计数器 < 最大消息数量
            while (counter++ < maxMsgNum) {
                //随机选取消息队列序号
                int indexOfQueue = random.nextInt(queueAmount);
                //根据队列序号生成队列名称
                String queueName = queueNameList.get(indexOfQueue);
                //获取选中消息队列当前消息数量
                int messageCount = queueSizeMap.get(queueName);
                //生成新的订单
                Order order = new Order(String.valueOf(messageCount));
                //获取订单数据
                byte[] data;
                //把订单序列化，获取订单数据data
                data = order.getBytes();
                //将订单编号写入消息队列
                messageQueue.put(queueName, data);
                //修改queueSizeMap
                queueSizeMap.put(queueName, messageCount + 1);
                //进度条
                if (counter % (maxMsgNum / 10) == 0) {
                    System.out.printf("send process is %d%% ... \n", sendProcess * 10);
                    ++sendProcess;
                }
            }
        }
    }

    static class Consumer{
        //消息队列的数量
        private final int queueAmount;
        //消息队列名字
        private final List<String> queueNameList;
        //消息队列字典
        private final Map<String, Integer> queueSizeMap;
        //校验次数
        private final int checkNum;
        //消息队列类的实例
        private final QueueStore messageQueue;
        //构造器
        public Consumer(QueueStore messageQueue, List<String> queueNameList, Map<String, Integer> queueSizeMap, int checkNum, int queueAmount) {
            this.messageQueue = messageQueue;
            this.queueNameList = queueNameList;
            this.queueSizeMap = queueSizeMap;
            this.queueAmount = queueAmount;
            this.checkNum = checkNum;
        }

        public void checkMessage() {
            //校验计数器
            int counter = 0;
            //进度条进度百分比
            int checkProcess = 0;
            Random random = new Random();
            while (counter++ < checkNum) {
                //随机选取消息队列序号
                int indexOfQueue = random.nextInt(queueAmount);
                //根据队列序号生成队列名称
                String queueName = queueNameList.get(indexOfQueue);
                //随机生成开始校验的偏移量
                int checkStartIndex = random.nextInt(Math.max(queueSizeMap.get(queueName), 1)) - 10;
                //如果开始校验偏移量小于0，则赋值为0
                checkStartIndex = Math.max(checkStartIndex, 0);
                //在序号为indexOfQueue的消息队列中，从checkStartIndex开始取消息，取10条消息，不足10条，则取最大可以取出的消息数量
                Collection<byte[]> msgs = messageQueue.get(queueName, checkStartIndex, 10);
                //校验订单消息
                int realMessageCount = Math.min(10, queueSizeMap.get(queueName) - checkStartIndex);
                if (msgs.size() != realMessageCount) {
                    System.out.println("check error: wrong result size");
                    System.exit(1);
                }
                for (byte[] msg : msgs) {
                    //从取出的数据中恢复order对象
                    Order order = new Order(msg);
                    //创建正确的对象
                    Order right = new Order(String.valueOf(checkStartIndex++));
                    //比较获取的对象和正确的对象
                    if (!right.equals(order)) {
                        System.out.println("check error: wrong orderObject");
                        System.exit(1);
                    }
                }
                //进度条
                if (counter % (checkNum / 10) == 0) {
                    System.out.printf("check process is %d%% ... \n", checkProcess * 10);
                    checkProcess++;
                }
            }
        }
    }
}


