package me.andpay.ti.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.andpay.ti.base.AppRtException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP客户端支持类
 * 
 * @author alex
 */
public class FtpClientSupport {
	private static final Logger LOG = LoggerFactory.getLogger(FtpClientSupport.class);

	/**
	 * 默认文件编码
	 */
	protected static final String DEF_ENCODING = "UTF-8";

	/**
	 * 路径分隔符
	 */
	protected static final String FILE_SEPARATOR = "/";

	/**
	 * FTP服务器地址
	 */
	protected String hostName;

	/**
	 * FTP服务器端口(默认21)
	 */
	protected int port = 21;

	/**
	 * 用户名
	 */
	protected String userName;

	/**
	 * 密码
	 */
	protected String password;

	/**
	 * 编码
	 */
	protected String encoding = DEF_ENCODING;

	/**
	 * 控制保持超时(单位:秒, 默认3分钟)
	 */
	protected int controlKeepAliveTimeout = 3 * 60;

	/**
	 * 连接超时时间 (单位:毫秒, 默认30秒)
	 */
	protected int connectTimeout = 30;

	/**
	 * 命令读取超时时间 (单位:毫秒, 默认60秒)
	 */
	protected int readTimeout = 60;

	/**
	 * 数据传输超时时间 (单位:毫秒, 默认30秒)
	 */
	protected int dataTimeout = 30;

	/**
	 * 登录重试次数
	 */
	protected int loginRetryLimit = 3;
	
	/**
	 * 下载文件
	 * 
	 * @param dir
	 * @param ftpFileName
	 * @return
	 */
	public FileMap<String> downloadFile(String dir, String ftpFileName){
		return downloadFiles(dir, ArrayUtil.asList(ftpFileName), null);
	}
	
	/**
	 * 下载多个文件
	 * 
	 * @param dir
	 * @param ftpFileNames
	 * @return
	 */
	public FileMap<String> downloadFiles(String dir, List<String> ftpFileNames){
		return downloadFiles(dir, ftpFileNames, null);
	}
	
	/**
	 * 下载多个文件
	 * 
	 * @param dir
	 * @param ftpFileNames
	 * @param fileMap
	 * @return
	 */
	private FileMap<String> downloadFiles(final String dir, final List<String> ftpFileNames, final FileMap<String> fileMap) {
		final FileMap<String> result = fileMap == null ? new FileMap<String>() : fileMap;

		final String host = hostName;
		
		execute(new SimpleTask() {

			public Object execute(Object... args) throws RuntimeException {
				try {
					return execute((FTPClient) args[0]);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			private String exists(FTPClient ftpClient, String ftpFileName) throws IOException{
				String[] names = ftpClient.listNames();
				//全程匹配
				for (String name : names) {
					if(name.equals(ftpFileName)){
						return name;
					}
				}
				//再取出后缀比较
				for (String name : names) {
					if(name.lastIndexOf(".") < 0) continue;
					if(name.substring(0, name.lastIndexOf(".")).equals(ftpFileName)){
						return name;
					}
				}
				return null;
			}

			private Object execute(FTPClient ftpClient) throws Exception {
				if (changeWorkingDirectory(ftpClient, dir, false) == false) {
					LOG.info("Dir not found, dir={}", dir);
					return null;
				}
				// 二进制文件类型
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				// 启用本地被动模式 - 客户端访问服务器建立数据通道
				ftpClient.enterLocalPassiveMode();
				
				List<OutputStream> outs = new ArrayList<OutputStream>();
				try {
					for (String ftpFileName : ftpFileNames) {
						String _dir = getFileDir(ftpFileName);
						if(StringUtil.isNotEmpty(_dir) && changeWorkingDirectory(ftpClient, _dir, false)){
							LOG.info("File dir not found, dir={}", _dir);
						}
						
						//不存在则返回
						String remotefileName = exists(ftpClient, cleanFileName(ftpFileName));
						if(remotefileName == null){
							LOG.info("Not exists fileName={} in ftp dir={}, host={}", new Object[]{cleanFileName(ftpFileName), dir, host});
							return null;
						}
						
						File localReconZipFile = result.createTempFile(ftpFileName);
						OutputStream out = new FileOutputStream(localReconZipFile);
						outs.add(out);
						// 使用本地输出流，如果使用远端输入流每次下载必须重连一次FTP
						ftpClient.retrieveFile(remotefileName, out);
					}
				} finally {
					CloseUtil.close(outs.toArray());
				}

				return null;
			}
			
			private String cleanFileName(String ftpFileName){
				if(ftpFileName.indexOf("/") < 0){
					return ftpFileName;
				}
				return ftpFileName.substring(ftpFileName.lastIndexOf("/") + 1, ftpFileName.length());
			}
			
			private String getFileDir(String ftpFileName){
				if(ftpFileName.indexOf("/") < 0){
					return null;
				}
				
				if(ftpFileName.startsWith("/") == false){
					ftpFileName = "/" + ftpFileName;
				}
				
				return ftpFileName.substring(0, ftpFileName.lastIndexOf("/"));
			}

		});

		return result;
	}

	/**
	 * 执行FTP操作，Task的execute方法第一个参数为FTPClient对象
	 * 
	 * @param task
	 * @return
	 */
	protected Object execute(SimpleTask task) {
		FTPClient ftpClient = buildFtpClient();

		// 登录
		login(ftpClient);

		try {
			// 执行命令
			return task.execute(ftpClient);
		} catch (AppRtException e) {
			LOG.error("Excecute ftp task error, server={}:{}", new Object[] { hostName, port, e });
			throw e;

		} finally {
			// 登出
			logout(ftpClient);
		}
	}

	/**
	 * 构建FTP客户端
	 * 
	 * @return
	 * @throws IOException
	 */
	protected FTPClient buildFtpClient() {
		FTPClient ftpClient = new FTPClient();
		ftpClient.setDefaultTimeout(connectTimeout * 1000);
		ftpClient.setControlEncoding(StringUtil.defaultStringIfBlank(encoding, DEF_ENCODING));

		return ftpClient;
	}

	/**
	 * 登录
	 * 
	 * @param ftpClient
	 */
	protected void login(FTPClient ftpClient) {
		// 重试次数
		int i = loginRetryLimit;
		do {
			if (tryLogin(ftpClient)) {
				// 登录成功
				return;
			}
		} while (--i > 0);

		throw new RuntimeException(String.format("Login ftp server failed, retryTimes=%d, server=%s:%d",
				loginRetryLimit, hostName, port));
	}

	/**
	 * 尝试登录
	 * 
	 * @param ftpClient
	 * @return 登录成功/失败
	 */
	protected boolean tryLogin(FTPClient ftpClient) {
		try {
			ftpClient.connect(hostName, port);

			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				LOG.warn("FTP server refused connection, reply={}, server={}:{}",
						new Object[] { reply, hostName, port });
				ftpClient.disconnect();
				return false;
			}

			ftpClient.setSoTimeout(readTimeout * 1000);
			ftpClient.setDataTimeout(dataTimeout * 1000);
			ftpClient.setControlKeepAliveTimeout(controlKeepAliveTimeout);

			return ftpClient.login(userName, password);
		} catch (Exception e) {
			LOG.warn("Login ftp server error, server={}:{}", new Object[] { hostName, port, e });
			return false;
		}
	}

	/**
	 * 登出并断开连接
	 * 
	 * @param ftpClient
	 */
	protected void logout(FTPClient ftpClient) {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.logout();
			}
		} catch (Exception e) {
			LOG.warn("Logout ftp server error, server={}:{}", new Object[] { hostName, port, e });
		}

		try {
			if (ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			LOG.warn("Disconnect ftp server error, server={}:{}", new Object[] { hostName, port, e });
		}

	}

	/**
	 * 改变工作目录至指定目录，如果失败当前不会不会改变
	 * 
	 * @param ftpClient
	 * @param tgtPath
	 * @param createMissingDir
	 *            自动创建不存在的目录
	 * @return
	 * @throws IOException
	 */
	protected boolean changeWorkingDirectory(FTPClient ftpClient, String tgtPath, boolean createMissingDir)
			throws IOException {
		// 切换目录成功
		if (ftpClient.changeWorkingDirectory(tgtPath)) {
			return true;
		}

		// 无法切换目录且不尝试创建，则直接返回失败
		if (createMissingDir == false) {
			return false;
		}

		// 待处理的目录集
		String[] dirs;
		if (tgtPath.startsWith("/")) {
			// 绝对路径
			dirs = tgtPath.substring(1).split(FILE_SEPARATOR);
			dirs[0] = "/" + dirs[0];

		} else {
			// 相对路径
			dirs = tgtPath.split(FILE_SEPARATOR);
		}

		// 备份原目录
		String srcPath = ftpClient.printWorkingDirectory();

		for (String dir : dirs) {
			if (StringUtil.isEmpty(dir) || ftpClient.changeWorkingDirectory(dir)) {
				// 空白目录 或 切换目录成功
				continue;
			}

			// 无法进入目录，则尝试先创建目录再切换
			if (ftpClient.makeDirectory(dir) && ftpClient.changeWorkingDirectory(dir)) {
				continue;
			}

			// 创建目录或切换目录失败
			LOG.error("Change working dir failed, curPath={}, tgtDir={}, tgtPath={}",
					new Object[] { ftpClient.printWorkingDirectory(), dir, tgtPath, });

			// 跳转到原目录
			ftpClient.changeWorkingDirectory(srcPath);
			return false;
		}

		return true;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @param connectTimeout
	 *            the connectTimeout to set
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @param readTimeout
	 *            the readTimeout to set
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * @param controlKeepAliveTimeout
	 *            the controlKeepAliveTimeout to set
	 */
	public void setControlKeepAliveTimeout(int controlKeepAliveTimeout) {
		this.controlKeepAliveTimeout = controlKeepAliveTimeout;
	}

	/**
	 * @param dataTimeout
	 *            the dataTimeout to set
	 */
	public void setDataTimeout(int dataTimeout) {
		this.dataTimeout = dataTimeout;
	}

	/**
	 * @param loginRetryLimit
	 *            the loginRetryLimit to set
	 */
	public void setLoginRetryLimit(int loginRetryLimit) {
		this.loginRetryLimit = loginRetryLimit;
	}
}
