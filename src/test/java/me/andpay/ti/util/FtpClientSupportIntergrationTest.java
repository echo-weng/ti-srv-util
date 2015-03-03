package me.andpay.ti.util;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FTP客户端工具集成类
 * 
 * @author alex
 */
public class FtpClientSupportIntergrationTest {
	private static final Logger LOG = LoggerFactory.getLogger(FtpClientSupportIntergrationTest.class);

	public static void main(String[] args) {
		MyFtpClient client = new MyFtpClient();
		client.setUserName("ti");
		client.setPassword("ti123");
		client.setHostName("localhost");
		client.setPort(21);

		// relative path
		String[] paths = { "test/r/1", "2/3/" };
		String[] results = client.changeDirs(paths, true);

		String[] exps = { "/test/r/1", "/test/r/1/2/3" };
		Assert.assertArrayEquals(exps, results);

		// absolute path
		paths = new String[] { "/test/a/1/2", "/test/a/2/3" };
		results = client.changeDirs(paths, true);

		exps = new String[] { "/test/a/1/2", "/test/a/2/3" };
		Assert.assertArrayEquals(exps, results);

		// absolute + relative path
		paths = new String[] { "/test/a/r", "1/2" };
		results = client.changeDirs(paths, true);

		exps = new String[] { "/test/a/r", "/test/a/r/1/2" };
		Assert.assertArrayEquals(exps, results);

		// change dir failed will be reenter to original dir
		client.changeDirs(new String[] { "/" }, false);

		results = client.changeDirs(new String[] { "/inexisted", "test/a/r" }, false);
		Assert.assertArrayEquals(new String[] { "/", "/test/a/r" }, results);

		// Chinese dir name
		paths = new String[] { "/测试", "一/二" };
		results = client.changeDirs(paths, true);
		exps = new String[] { "/测试", "/测试/一/二" };
		
		System.out.println("Ftp client support test success");
	}

	protected static class MyFtpClient extends FtpClientSupport {
		public String[] changeDirs(final String[] paths, final boolean createDir) {
			return (String[]) execute(new SimpleTask() {
				public Object execute(Object... args) {
					FTPClient ftpClient = (FTPClient) args[0];

					// 切换接收后所在目录
					String[] result = new String[paths.length];
					for (int i = 0; i < paths.length; i++) {
						try {
							changeWorkingDirectory(ftpClient, paths[i], createDir);
							result[i] = ftpClient.printWorkingDirectory();
						} catch (Exception ex) {
							LOG.error("Chanage working dir error, path={}", paths[i], ex);
							result[i] = ex.getMessage();
						}
					}

					return result;
				}
			});
		}
	}
}
