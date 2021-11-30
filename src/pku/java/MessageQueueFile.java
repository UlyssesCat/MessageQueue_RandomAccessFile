package pku.java;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

public class MessageQueueFile extends QueueStore{

    private static final ArrayList<byte[]> EMPTY = new ArrayList<>(0);

    public MessageQueueFile() {
        File dir = new File("data");
        if (dir.exists()) deleteFolder(dir);
        dir.mkdirs();
    }

    private static void deleteFolder(File folder) {
        if (folder == null) return;
        if (folder.isFile()) {
            folder.delete();
        }
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFolder(file);
            }
        }
        folder.delete();
    }

    @Override
    void put(String queueName, byte[] message) {
        File file = new File("data", queueName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(message.length);
            fos.write(message);
            dos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    Collection<byte[]> get(String queueName, long offset, long num) {
        File file = new File("data", queueName);
        if (!file.exists()) {
            return EMPTY;
        }
        ArrayList<byte[]> result = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(fis);
            while (dis.available() > 0 && num > 0) {
                int msgLen = dis.readInt();
                byte[] message = new byte[msgLen];
                fis.read(message);
                Order right = new Order(String.valueOf(offset));
                if (right.equals(new Order(message))) {
                    result.add(message);
                    offset++;
                    num--;
                }
            }
            dis.close();
            fis.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return EMPTY;
    }
}
