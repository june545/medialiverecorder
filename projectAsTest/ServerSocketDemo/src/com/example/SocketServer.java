package com.example;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startServer();
	}

	private static void startServer() {
		try {
			ServerSocket server = null;
			try {
				server = new ServerSocket(8089);
			} catch (Exception e) {
				System.out.println("can not listen to:" + e);
				//	出错，打印出错信息
			}

			Socket socket = null;
			try {
				socket = server.accept();
				//使用accept()阻塞等待客户请求，有客户

				//请求到来则产生一个Socket对象，并继续执行
			} catch (Exception e) {
				System.out.println("Error." + e);
				//出错，打印出错信息
			}
			//			BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			DataInputStream is = new DataInputStream(socket.getInputStream());
			while (true) {
				//在系统标准输出上打印读入的字符串
				//				System.out.println("Client:" + is.readLine());

				byte[] buff = new byte[1024];
				int count = -1;

				count = is.read(buff);
				if (count != -1) {
					String s = new String(buff, 0, count);
					System.out.println(" data " + s.toUpperCase());
				}
			}

		} catch (Exception e) {
			System.out.println("Error:" + e);
			//出错，打印出错信息

			//			os.close(); //关闭Socket输出流
			//			is.close(); //关闭Socket输入流
			//			socket.close(); //关闭Socket
			//			server.close(); //关闭ServerSocket
		}
	}

}
