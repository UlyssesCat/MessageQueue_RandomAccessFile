package pku.java;

import java.io.*;

public class Order implements Serializable {
    // 订单编号
    private String orderNumber;
    // 收货地址
    private String toAdress;
    // 发货地址
    private String sendAdress;
    //序列化版本UID
    private static final long serialVersionUID = -6849793456754667710L;

    public Order(String orderNumber, String toAdress, String sendAdress) {
        this.orderNumber = orderNumber;
        this.toAdress = toAdress;
        this.sendAdress = sendAdress;
    }

    public Order(String orderNumber) {
        this(orderNumber, "北京市大兴工业开发区金源路24号", "北京市海淀区颐和园路5号");
    }

    // 使用 byte[] 作为参数的构造器
    public Order(byte[] data) {
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            ois.close();
            bis.close();
            Order order = (Order) ois.readObject();
            this.orderNumber = order.getOrderNumber();
            this.toAdress = order.getToAdress();
            this.sendAdress = order.getSendAdress();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getToAdress() {
        return toAdress;
    }

    public void setToAdress(String toAdress) {
        this.toAdress = toAdress;
    }

    public String getSendAdress() {
        return sendAdress;
    }

    public void setSendAdress(String sendAdress) {
        this.sendAdress = sendAdress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (orderNumber != null ? !orderNumber.equals(order.orderNumber) : order.orderNumber != null) return false;
        if (toAdress != null ? !toAdress.equals(order.toAdress) : order.toAdress != null) return false;
        return sendAdress != null ? sendAdress.equals(order.sendAdress) : order.sendAdress == null;
    }

    @Override
    public int hashCode() {
        int result = orderNumber != null ? orderNumber.hashCode() : 0;
        result = 31 * result + (toAdress != null ? toAdress.hashCode() : 0);
        result = 31 * result + (sendAdress != null ? sendAdress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber='" + orderNumber + '\'' +
                ", toAdress='" + toAdress + '\'' +
                ", sendAdress='" + sendAdress + '\'' +
                '}';
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            byte[] data = bos.toByteArray();
            oos.close();
            bos.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
