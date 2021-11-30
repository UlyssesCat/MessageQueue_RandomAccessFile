package pku.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MessageQueue extends QueueStore{

    private static HashMap<String, ArrayList<Long>> queueMap;
    private static RandomAccessFile raf ;

    public MessageQueue() {
        File dir = new File("data");
        if (dir.exists()) deleteFolder(dir);
        dir.mkdirs();
        try {
            raf = new RandomAccessFile("data/data","rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        queueMap = new HashMap<>();
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
        if(!queueMap.containsKey(queueName)){
            queueMap.put(queueName,new ArrayList<>());
        }
        ArrayList<Long> queue = queueMap.get(queueName);
        try {
            long startPosition = raf.length();
            queue.add(startPosition);
            raf.writeInt(message.length);
            raf.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    Collection<byte[]> get(String queueName, long offset, long num) {
        if(!queueMap.containsKey(queueName))    return null;
        ArrayList<Long> queue = queueMap.get(queueName);
        long realEnd = Math.min(offset+num,queue.size());
        ArrayList<byte[]> result = new ArrayList<>();
        try{
            for(long i =offset;i<realEnd;i++){
                raf.seek(queue.get((int)i));
                int msgLen = raf.readInt();
                byte[] message = new byte[msgLen];
                raf.read(message);
                result.add(message);
            }
            return result;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


}
