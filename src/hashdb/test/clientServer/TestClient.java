package hashdb.test.clientServer;

import org.apache.log4j.Logger;
import hashdb.Settings;
import hashdb.Utilities;
import hashdb.main.Client;

import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: filip
 * Date: 6/22/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class
        TestClient extends Thread {

    public static final Logger log=Logger.getLogger(TestClient.class);

    static String directory="/media/E2BE1FF5BE1FC149/3";
    static boolean recursive = false;
    static int numOfClients = 4;
    static int exitOneOf = 25;
    static boolean probability = false;

    static LinkedList<TestClient> tclist=new LinkedList<TestClient>();

    static int inactive = 1;

    static LinkedList<File> allFolders = new LinkedList<File>();
    static LinkedList<File> allFiles = new LinkedList<File>();

    private static Long putSucess = Long.valueOf(0);
    private static Long getSucess = Long.valueOf(0);
    private static Long delSucess = Long.valueOf(0);
    private static Long chkSucess = Long.valueOf(0);
    private static Long putFail = Long.valueOf(0);
    private static Long getFail = Long.valueOf(0);
    private static Long delFail = Long.valueOf(0);
    private static Long chkFail = Long.valueOf(0);

    private static Long putSucessTime = Long.valueOf(0);
    private static Long getSucessTime = Long.valueOf(0);
    private static Long delSucessTime = Long.valueOf(0);
    private static Long chkSucessTime = Long.valueOf(0);
    private static Long putFailTime = Long.valueOf(0);
    private static Long getFailTime = Long.valueOf(0);
    private static Long delFailTime = Long.valueOf(0);
    private static Long chkFailTime = Long.valueOf(0);




    public static void main(String[] args) {
        for (int i=0;i<args.length;) {
            String curr=args[i++];
            if ("-d".equals(curr)) {
                directory = args[i++];
                break;
            }
            if ("-r".equals(curr)) {
                recursive = true;
                break;
            }
            if ("-n".equals(curr)) {
                numOfClients = Integer.parseInt(args[i++]);
                break;
            }
            if ("-xp".equals(curr)) {
                exitOneOf = Integer.parseInt(args[i++]);
                probability = true;
                break;
            }
            if ("-xs".equals(curr)) {
                exitOneOf = Integer.parseInt(args[i++]);
                probability = false;
            }
        }


        allFolders.add(new File(directory));
        while (allFolders.size()>0) {
            File currentFolder = allFolders.remove(0);
            for(File f: currentFolder.listFiles())
                if (!f.isDirectory()) allFiles.add(f);
                else if (recursive) allFolders.add(f);
        }

        for (int i=0;i< numOfClients;i++) {
            TestClient tc=new TestClient();
            tclist.add(tc);
            tc.start();
        }
        for (int i=0;i< numOfClients;i++) {
            try {
                tclist.remove().join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        stats();
    }

    Random r=new Random();

    public void run() {

        Client c=new Client();

        c.connect();

        if (probability) {
            int order = r.nextInt(exitOneOf);
            while (order!=0) {
                log.info("Working...");
                singleOperation(c);

            }
        } else {
            for (int i=0;i<exitOneOf;i++) {
                log.info(i+"/"+exitOneOf);
                singleOperation(c);

            }
        }

        c.disconnect();
    }

    static void stats() {
        try {
            FileWriter fw=new FileWriter(directory+"/report_"+new Date().getTime()+".txt");
            fw.write(String.valueOf(recursive)+"\n");
            fw.write(numOfClients+"\n");
            fw.write(exitOneOf+"\n");
            fw.write(String.valueOf(probability)+"\n");

            double putS = putSucessTime*1.0/putSucess;
            double putF = putFailTime*1.0/putFail;
            double getS = getSucessTime*1.0/getSucess;
            double getF = getFailTime*1.0/getFail;
            double chkS = chkSucessTime*1.0/chkSucess;
            double chkF = chkFailTime*1.0/chkFail;
            double delS = delSucessTime*1.0/delSucess;
            double delF = delFailTime*1.0/delFail;
            fw.write(putS + "\t" + putF +"\n");
            fw.write(getS + "\t" + getF +"\n");
            fw.write(chkS + "\t" + chkF +"\n");
            fw.write(delS + "\t" + delF +"\n");
            fw.flush();
            fw.close();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private static int[] opprob = new int[]{0,0,10,1,1,10};

    long globalTotal = 0;

    private void singleOperation(Client c) {
        try {
            long globalStart = new Date().getTime();
            File f = allFiles.get(r.nextInt(allFiles.size()));
            FileInputStream fis = new FileInputStream(f);
            int size = (int) f.length();
            if (size>Settings.Fields.DATA.getSize()) size = Settings.Fields.DATA.getSize();
            byte[] data = new byte[size];
            byte[] _data = new byte[Settings.Fields.DATA.getSize()];
            byte[] len = new byte[Settings.Fields.LENGTH.getSize()];
            byte[] link = new byte[Settings.Fields.LINK.getSize()];
            fis.read(data);
            fis.close();
            f=null;
            int op=getNextOp();
            switch (op) {
                case 0: {
                    c.reconnect();
                    break;
                }
                case 1: {
                    c.reRequestSlave();
                    break;
                }
                case 2: {
                    long start = new Date().getTime();
                    boolean result = c.put(Utilities.autoKey(data),data,link);
                    long end = new Date().getTime();
                    newPut(end-start,result);
                    break;
                }
                case 3: {
                    long start = new Date().getTime();
                    boolean result = c.getFirst(Utilities.autoKey(data), len, _data, link);
                    long end = new Date().getTime();
                    newGet(end-start,result);
                    break;
                }
                case 4: {
                    long start = new Date().getTime();
                    boolean result = c.check(Utilities.autoKey(data));
                    long end = new Date().getTime();
                    newCheck(end - start, result);
                    break;
                }
                case 5: {
                    long start = new Date().getTime();
                    boolean result = c.deleteFirst(Utilities.autoKey(data));
                    long end = new Date().getTime();
                    newDelete(end - start, result);
                    break;
                }
            }
            globalTotal += new Date().getTime() - globalStart;
            sleep(inactive);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally {

        }

    }

    private void newPut(long time, boolean sucess) {
        if (sucess) {
            synchronized (putSucess) {
                putSucess++;
                putSucessTime+=time;
            }
            return;
        }
        synchronized (putFail) {
            putFail++;
            putFailTime+=time;
        }
    }

    private void newGet(long time, boolean sucess) {
        if (sucess) {
            synchronized (getSucess) {
                getSucess++;
                getSucessTime+=time;
            }
            return;
        }
        synchronized (getFail) {
            getFail++;
            getFailTime+=time;
        }
    }

    private void newCheck(long time, boolean sucess) {
        if (sucess) {
            synchronized (chkSucess) {
                chkSucess++;
                chkSucessTime+=time;
            }
            return;
        }
        synchronized (chkFail) {
            chkFail++;
            chkFailTime+=time;
        }
    }

    private void newDelete(long time, boolean sucess) {
        if (sucess) {
            synchronized (delSucess) {
                delSucess++;
                delSucessTime+=time;
            }
            return;
        }
        synchronized (delFail) {
            delFail++;
            delFailTime+=time;
        }
    }

    private int getNextOp() {
        int totalSum = 0;
        for (int i:opprob)
            totalSum+=i;
        totalSum = r.nextInt(totalSum);
        for (int i=0;i<opprob.length;i++) {
            if (totalSum<=0 && opprob[i]!=0) return i;
            totalSum -=opprob[i];
        }
        return opprob.length-1;

    }

}
