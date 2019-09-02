package com.hnu.server.authentication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.hnu.dao.UserDao;
import com.hnu.dao.impl.UserDaoImpl;
import com.hnu.pojo.User;
import com.hnu.server.queue.userMailQueue;

public class authentication {

    private static ServerSocket SERVER_SOCKET =null;
    
    private static String mailServer = "127.0.0.1";
    private static int port = 1024;

    public static void authenticationOpen() throws InterruptedException{
        try {
            SERVER_SOCKET = new ServerSocket(port);
            System.out.println("******�ʼ����������������ȴ��ͻ�������*****");
            Socket socket = null;
//            int cnt=0;
            
            UserDao userDao=new UserDaoImpl();
            List<User> lu=userDao.userShowDao();
            for(User u:lu) {
            	userMailQueue.insert(u.getUid());
            }
            
            while(!SERVER_SOCKET.isClosed()){
                //ѭ�������ͻ��˵�����
                socket = SERVER_SOCKET.accept();
                Thread.sleep(10);
                //�½�һ���߳�ServerSocket��������
                if(!SERVER_SOCKET.isClosed())
                	new authenticationServerSocketThread(socket).start();
                
//                cnt++;
//                if(cnt==2)break;
            }
            System.out.println("******�ʼ��������ѹر�*****");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void authenticationClose() throws InterruptedException {
    	try {
    		new Socket("127.0.0.1",port);
    		Thread.sleep(5);
			SERVER_SOCKET.close();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
    }

    public static void main(String[] args) throws InterruptedException {
    	authenticationOpen();
    }
}