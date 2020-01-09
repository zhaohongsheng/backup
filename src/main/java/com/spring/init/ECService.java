package com.spring.init;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.spring"})
@EnableScheduling
public class ECService {
	private static Thread thread = null;
	private static Service service = null;
 
	/**
	 * 退出服务方法(该方法必须有参数 String [] args)
	 * 
	 * @param args
	 */
	public static void StopService(String[] args) {
		System.out.println(service.getLocalTime()+"停止服务");
		service.setRunFlag(false);
	}
 
	/**
	 * 启动服务方法(该方法必须有参数 String [] args)
	 * 
	 * @param args
	 */
	public static void StartService(String[] args) {
		// 产生服务线程
		service = new Service();
		thread = new Thread(service);
		System.out.println("\r\n"+service.getLocalTime()+"-----------启动服务-----------");
		
		try {
			// 将服务线程设定为用户线程，以避免StartService方法结束后线程退出
			thread.setDaemon(false);
			if (!thread.isDaemon()) {
				System.out.println(service.getLocalTime()+"成功设定线程为用户线程！");
			}
			// 启动服务线程
			thread.start();
		} catch (SecurityException se) {
			System.out.println(service.getLocalTime()+"线程类型设定失败！");
		}
	}
 
	public static void main(String[] args) {
		new Service().run();
	}
}
 
class Service implements Runnable {
	private boolean runFlag = true;
 
	/**
	 * 设定服务线程运行标志值
	 * 
	 * @param runFlag
	 */
	public synchronized void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}
 
	/**
	 * 取得服务线程运行标志值
	 * 
	 * @param void
	 */
	@SuppressWarnings("unused")
	private synchronized boolean getRunFlag() {
		return runFlag;
	}
 
	@Override
	public void run() {
		System.out.println(getLocalTime()+"服务线程开始运行");
		SpringApplication.run(ECService.class);
		System.out.println(getLocalTime()+"服务线程结束运行");
	}
	
	public String getLocalTime(){
		String time ="[";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		time += df.format(new Date()).toString();
		time+="]";
		return time;
	}
 
}
