package com.spring.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.spring.entity.FtpInfo;
import com.spring.init.ECService;
import com.spring.util.MD5Util;
import com.spring.util.SFtpUtil;
import com.spring.util.Util;

@Component
public class BackSource {

	Logger log = LoggerFactory.getLogger(ECService.class);

	@Autowired
	FtpInfo ftpinfo;

	@Value("${backup.path}")
	private String backPath;
	@Value("${backup.temp.path}")
	private String backTempPath;
	@Value("${backup.ignore}")
	private String backIgore;

	@Scheduled(cron = "${cron}")
	public void run() {
		log.info("==========开始备份==========");
		String date = Util.getDate();
		try {	
			new File(backTempPath + "/" + date).mkdirs();
			log.info("==========初始化目录=========="+backTempPath + "/" + date);
			if(!new File(backTempPath+"/Rar.exe").exists()){
				String fileName = this.getClass().getClassLoader().getResource("Rar.exe").getPath();
				if(fileName.contains("!")){
					String jarName = fileName.split("!")[0].replace("file:/","");
					System.out.println(jarName);
					JarFile jar = new JarFile(jarName);
					Enumeration<JarEntry> jarent = jar.entries();
					while(jarent.hasMoreElements()){
						ZipEntry ent = jarent.nextElement();
						if(ent.getName().endsWith("Rar.exe")){
							log.info("==========初始化压缩工具=========="+backTempPath + "/" + date);
							copyFileUsingFileStreams(jar.getInputStream(ent),new File(backTempPath+"/Rar.exe"));
							break;
						}
					}
				}else{
					log.info("==========初始化压缩工具=========="+backTempPath + "/" + date);
					copyFileUsingFileStreams(new File(this.getClass().getClassLoader().getResource("Rar.exe").getPath()),new File(backTempPath+"/Rar.exe"));

				}
			}
		} catch (Exception e) {
			log.info("==========初始化压缩工具报错==========",e);
		}
		for (String backPath : this.backPath.split(",")) {
			String rarName = Util.getCurrTime() + ".rar";
			File ff = new File(backTempPath + "/" + date);
			if (ff.list().length < this.backPath.split(",").length) {
				try {
					log.info("==========备份路径=========="+backPath);
					rar(rarName, getListPath(backPath), date);
				} catch (Exception e) {
					log.info("==========备份出错==========",e);
					e.printStackTrace();
				}
			}
		}
		File ff = new File(backTempPath + "/" + date);
		File[] upfiles = ff.listFiles();
		SFtpUtil ftp = new SFtpUtil(ftpinfo);
		for(File upfile:upfiles){
			try {
				String upPath = ftpinfo.getSavepPath()+"/"+date;
				log.info("==========开始上传文件==========="+upfile.getAbsolutePath());
				String sourcMD5 = MD5Util.getMd5(new FileInputStream(upfile));
				String tarMd5   = MD5Util.getMd5(ftp.download(upPath, upfile.getName()));
				if(sourcMD5.equals(tarMd5)){
					log.info("==========文件上传失败,本次文件已上传==========="+upfile.getName());
				}else{
					log.info("==========开始上传文件==========="+upfile.getName());
					ftp.upload(upPath, new FileInputStream(new File(backTempPath + "/" + date + "/" + upfile.getName())), upfile.getName());
					log.info("==========结束上传文件,上传成功==========="+upfile.getName());
				}
			} catch (Exception e) {
				log.info("==========开始上传文件失败==========="+upfile.getAbsolutePath(),e);
				e.printStackTrace();
			}
		}
		log.info("==========结束备份==========");
	}

	private String getListPath(String backPath) {
		String temp = "";
		File[] list = new File(backPath).listFiles();
		for (File l : list) {
			if (Arrays.binarySearch(backIgore.split(","), l.getName()) < 0) {
				temp += l.getAbsolutePath() + " ";
			}
		}
		return temp;
	}

	public boolean rar(String rarName, String sorce, String date) throws Exception {
		BufferedReader inputErr = null;
		BufferedReader input = null;
		try {
			String fileName = this.getClass().getClassLoader().getResource("Rar.exe").getPath(); // 获取

			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(
					backTempPath+"/Rar.exe" + " a " + backTempPath + "/" + date + "/" + rarName + "  " + sorce + "" + " -x*.svn");
			System.out.println("exec: " +backTempPath+"/Rar.exe" + " a " + backTempPath + "/" + date + "/" + rarName + "  " + sorce + "" + " -x*.svn");
			inputErr = new BufferedReader(new InputStreamReader(pr.getErrorStream(), "GBK"));
			input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
			String line = null;
			StringBuffer out = new StringBuffer();
			while ((line = input.readLine()) != null) {
				out.append(line + "\n");
				System.out.println(line);
			}
			while ((line = inputErr.readLine()) != null) {
				out.append(line + "\n");
				System.out.println(line);
			}
			int exitVal = pr.waitFor();
			System.out.println("Exited with error code " + exitVal);
			System.out.println("Exited with message \n" + out.toString());
			if (out.toString().contains("完成")) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	private static void copyFileUsingFileStreams(File source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(source);
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}
	
	private static void copyFileUsingFileStreams(InputStream source, File dest) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = source;
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

}
