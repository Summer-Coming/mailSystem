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
            // socket获取字节输入流
            is = socket.getInputStream();
            // 将字节输入流转字符输入流
            isr = new InputStreamReader(is);
            // 将字符输入流转行输入流
            in = new BufferedReader(isr); 
            
            System.out.println("客户端发来消息：");
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
            
            // 必须先关闭输入流才能获取下面的输出流
            socket.shutdownInput();
            // 获取输出流
            os = socket.getOutputStream();
            pw = new PrintWriter(os);
            if(u!=null)
            	pw.write("1");
            else
            	pw.write("0");
            pw.flush();
            // 关闭输入流
            socket.shutdownOutput();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
				if (pw != null) {
				    pw.close();
				}
			} catch (Exception e1) {
				// TODO 自动生成的 catch 块
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