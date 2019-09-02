package com.hnu.server.smtp;

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

import com.hnu.dao.LogDao;
import com.hnu.dao.MailDao;
import com.hnu.dao.UserDao;
import com.hnu.dao.impl.LogDaoImpl;
import com.hnu.dao.impl.MailDaoImpl;
import com.hnu.dao.impl.UserDaoImpl;
import com.hnu.pojo.Log;
import com.hnu.pojo.Mail;
import com.hnu.pojo.User;
import com.hnu.server.queue.userMailQueue;
import com.hnu.service.UserService;
import com.hnu.service.impl.UserServiceImpl;

public class SMTPServerSocketThread extends Thread {

    private Socket socket;

    public SMTPServerSocketThread(Socket socket) {
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
            
            Mail mail = new Mail();
            System.out.println("�ͻ��˷�����Ϣ��");
            String message = null;
            int cnt=0;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                //�������ݿ�
                ++cnt;
                if(cnt==1) {
                	mail.setFrom_user(message);
                }else if(cnt==2) {
                	mail.setTo_user(message);
                }else if(cnt==3) {
                	mail.setSubject(message);
                }else if(cnt==4) {               	
        	 		mail.setDate(Timestamp.valueOf(message));
                }else if(cnt==5){
                	mail.setContent(message);
                }
                else {
                	mail.setContent(mail.getContent()+"\r\n"+message);
                }
            }
            MailDao mailDao = new MailDaoImpl();
            try {
            	mail.setId(mailDao.insert(mail));
            	System.out.println("mailid: "+mail.getId());
    		} catch (Exception e) {
    			// TODO �Զ����ɵ� catch ��
    			e.printStackTrace();
    		}
             
            
//           //�������
//            UserDao userDao=new UserDaoImpl();
//            int uid = userDao.queryByAccount(mail.getTo_user());
//            
//            userMailQueue.receiveMail(uid,mail.getId());
//            System.out.println("queue.size(): "+userMailQueue.user.get(2).size());
            // �����ȹر����������ܻ�ȡ����������
            socket.shutdownInput();
            // ��ȡ�����
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            
            Log log=new Log();
            log.setDate(new Timestamp(System.currentTimeMillis()));
            
            if(mail.getId()==0)
            {
            	pw.write("0");
            	log.setOperation("���������������ʼ�ʧ��");
            	log.setState("0");
            }
            else
            {
            	pw.write("1");
            	log.setOperation("�������ɹ���������"+mail.getFrom_user()+"�ʼ���"+mail.getId());
            	log.setState("1");
            }
            LogDao logDao = new LogDaoImpl();
            logDao.insert(log);
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