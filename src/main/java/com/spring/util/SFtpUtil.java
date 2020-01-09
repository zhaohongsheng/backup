package com.spring.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.spring.entity.FtpInfo;

/**
 * 类说明 sftp工具类
 */
public class SFtpUtil {

	FtpInfo ftpinfo;
	String privateKey;
	
	public SFtpUtil(FtpInfo ftpinfo) {
		this.ftpinfo = ftpinfo;
	}

	Log log = LogFactory.getLog(getClass());


	/**
	 * 创建文件夹。
	 * 
	 * @param directory
	 *            下载目录
	 * @throws Exception
	 */
	public void  mkdirs(String remotePath) throws Exception {
		if(remotePath == null){
			System.out.println(ftpinfo.toString());
			remotePath = ftpinfo.getSavepPath();
		}
		JSch jsch = new JSch();
		if (privateKey != null) {
			
		}
		Session session = jsch.getSession(ftpinfo.getUsername(), ftpinfo.getHost(), Integer.valueOf(ftpinfo.getPort()));
		
		session.setPassword(ftpinfo.getPasswd());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		try {
			String folders[] = null;
			if (remotePath.contains("\\\\")) {
				folders = remotePath.split("\\\\");
			} else if (remotePath.contains("/")) {
				folders = remotePath.split("/");
			}
			if (folders == null || folders.length == 0) {
				throw new Exception("目录不符合规则请检查" + remotePath);
			}
			String path = "";
			for (String folder : folders) {
				path += "/" + folder;
			}
			try {
				sftp.cd(remotePath);
			} catch (Exception e1) {
				path = "";
				for (String folder : folders) {
					try {
						if (Util.isNullOrEmpty(folder))
							continue;
						path += "/" + folder;
						sftp.cd(path);
					} catch (Exception e) {
						try {
							sftp.mkdir(path);
							sftp.cd(path);
						} catch (Exception e2) {
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}finally{
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
		}
	}

	/**
	 * 创建文件夹。
	 * 
	 * @param directory
	 *            下载目录
	 * @throws Exception
	 */
	public void upload(String remotePath, InputStream is, String targetFile) throws Exception {
		mkdirs(remotePath);
		if(remotePath == null){
			System.out.println(ftpinfo.toString());
			remotePath = ftpinfo.getSavepPath();
		}
		JSch jsch = new JSch();
		Session session = jsch.getSession(ftpinfo.getUsername(), ftpinfo.getHost(), Integer.valueOf(ftpinfo.getPort()));
		session.setPassword(ftpinfo.getPasswd());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		if (is == null) {
			throw new Exception("文件上传输入流错误！！");
		}
		try {
			sftp.cd(remotePath);
			sftp.put(is, targetFile);
		} catch (Exception e) {
			throw e;
		} finally{
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
		}
	}

	/**
	 * 下载文件。
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 */
	public void downloadFile(String remotePath, String remoteFileName, String localPath)
			throws Exception, FileNotFoundException {

		JSch jsch = new JSch();
		if (privateKey != null) {
			
		}
		Session session = jsch.getSession(ftpinfo.getUsername(), ftpinfo.getHost(), Integer.valueOf(ftpinfo.getPort()));

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setTimeout(2000);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;

		try {
			File dir = new File(localPath);
			if (!dir.exists()) {
				dir.mkdirs();
				log.info("本地保存路径[" + localPath + "]不存在,已自动创建!");
			}
			sftp.cd(remotePath);
			File file = new File(localPath + "/" + remoteFileName);
			sftp.get(remoteFileName, new FileOutputStream(file));
		} catch (Exception e) {
			throw e;
		} finally{
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
		}
	}

	/**
	 * 下载文件。
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 */
	public ByteArrayInputStream download(String remotePath, String remoteFileName) throws Exception {
		JSch jsch = new JSch();
		if (privateKey != null) {
			
		}
		Session session = jsch.getSession(ftpinfo.getUsername(), ftpinfo.getHost(), Integer.valueOf(ftpinfo.getPort()));
		session.setPassword(ftpinfo.getPasswd());
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setTimeout(10000);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		ByteArrayOutputStream bao = null;
		InputStream is = null;
		try {
			sftp.cd(remotePath);
			is = sftp.get(remoteFileName);
			bao = new ByteArrayOutputStream();
			byte[] b = new byte[4096];
			int len;
			while((len = is.read(b)) != -1){
				bao.write(b,0,len);
			}
			bao.flush();
			return new ByteArrayInputStream(bao.toByteArray());
		} catch (Exception e) {
			return null;
		} finally{
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
		}
	}

	/**
	 * 下载文件。
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 */
	public List<StringBuilder> download(String remotePath, String remoteFileName, String code) throws Exception {
		JSch jsch = new JSch();
		if (privateKey != null) {
			
		}
		
		Session session = jsch.getSession(ftpinfo.getUsername(), ftpinfo.getHost(), Integer.valueOf(ftpinfo.getPort()));

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.setTimeout(2000);
		session.connect();
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftp = (ChannelSftp) channel;
		BufferedReader br = null;
		InputStream is = null;
		List<StringBuilder> list = new ArrayList<StringBuilder>();
		try {
			sftp.cd(remotePath);
			is= sftp.get(remoteFileName);
			br = new BufferedReader(new InputStreamReader(is, code));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (Util.isNullOrEmpty(line.trim()))
					continue;
				list.add(new StringBuilder(line));
			}
		} catch (UnsupportedEncodingException e) {
			throw new Exception("文件[" + remotePath + remoteFileName + "]读取编码[" + code + "]不正确，请检查", e);
		} catch (IOException e) {
			throw new Exception("文件读取发送I/O异常请检查!", e);
		} finally {
			if (sftp != null) {
				if (sftp.isConnected()) {
					sftp.disconnect();
				}
			}
			if (session != null) {
				if (session.isConnected()) {
					session.disconnect();
				}
			}
		}
		return list;
	}
}