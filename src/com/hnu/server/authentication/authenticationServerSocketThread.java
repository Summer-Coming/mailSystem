package com.hnu.server.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

import com.hnu.dao.UserDao;
import com.hnu.dao.impl.MailDaoImpl;
import com.hnu.dao.impl.UserDaoImpl;
import com.hnu.pojo.Mail;
import com.hnu.pojo.User;
import com.hnu.server.queue.userMailQueue;
import com.hnu.service.UserService;
import com.hnu.service.impl.UserServiceImpl;

public class authenticationServerSocketThread extends Thread {

    private Socket socket;

    public authenticationServerSocketThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        OutputStream os = null;
        PrintWriter pw = null;
        try {
            // socket��ȡ�ֽ�������
            is = socket.getInputStream();
            // ���ֽ�������ת�ַ�������
            isr = new InputStreamReader(is);
            // ���ַ�������ת��������
            in = new BufferedReader(isr); 
            
            System.out.println("�ͻ��˷�����Ϣ��");
            String message = null,uname=null,pwd=null;
            int cnt=0;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                ++cnt;
                if(cnt==1) {
                	uname=message;
                }
                else {
                	pwd=message;
                }
            }
             
            UserDao userDao=new UserDaoImpl();
            User u= userDao.checkUserLoginDao(uname,pwd);
            
            // �����ȹر����������ܻ�ȡ����������
            socket.shutdownInput();
            // ��ȡ�����
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            if(u!=null)
            	pw.write("1");
            else
            	pw.write("0");
            pw.flush();
            // �ر�������
            socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // �ر���Դ
            try {
				if (pw != null) {
				    pw.close();
				}
			} catch (Exception e1) {
				// TODO �Զ����ɵ� catch ��
				e1.printStackTrace();
			}
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}