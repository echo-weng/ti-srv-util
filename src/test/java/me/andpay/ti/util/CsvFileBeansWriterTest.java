package me.andpay.ti.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.math.BigDecimal;

import org.junit.Test;

/**
 * Csv文件Bean写入器测试类。
 * 
 * @author sea.bao
 */
public class CsvFileBeansWriterTest {
	public static class User {
		private String userName;

		private BigDecimal balance;

		private java.util.Date registerTime;

		private java.util.Date lastActiveDate;

		private String nullable;

		private Boolean flag;

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}

		public java.util.Date getRegisterTime() {
			return registerTime;
		}

		public void setRegisterTime(java.util.Date registerTime) {
			this.registerTime = registerTime;
		}

		public java.util.Date getLastActiveDate() {
			return lastActiveDate;
		}

		public void setLastActiveDate(java.util.Date lastActiveDate) {
			this.lastActiveDate = lastActiveDate;
		}

		public Boolean getFlag() {
			return flag;
		}

		public void setFlag(Boolean flag) {
			this.flag = flag;
		}

		public String getNullable() {
			return nullable;
		}

		public void setNullable(String nullable) {
			this.nullable = nullable;
		}
	}

	@Test
	public void test() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CsvFileBeansWriter writer = new CsvFileBeansWriter(new PrintWriter(bout));
		writer.addField("userName");
		writer.addField("balance", "%12.2f");
		writer.addField("registerTime");
		writer.addField("lastActiveDate", "yyyy-MM-dd");
		writer.addField("flag");
		writer.addField("nullable");

		User user = new User();
		user.balance = new BigDecimal("123.45");
		user.registerTime = new java.util.Date();
		user.userName = "user01||";
		user.lastActiveDate = new java.util.Date();
		user.flag = true;

		writer.writeBean(user);
		writer.getOut().flush();

		System.out.println(bout.toString());

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		LineNumberReader in = new LineNumberReader(new InputStreamReader(bin));
		CsvFileBeansReader reader = new CsvFileBeansReader(User.class, in);
		User u = null;
		do {
			u = (User) reader.readBean();
			if (u == null) {
				break;
			}

			System.out.println("userName=" + u.getUserName());
			System.out.println("balance=" + u.getBalance());
			System.out.println("registerTime=" + StringUtil.format("yyyy-MM-dd HH:mm:ss.SSS", u.getRegisterTime()));
			System.out.println("lastActiveDate=" + StringUtil.format("yyyy-MM-dd HH:mm:ss.SSS", u.getLastActiveDate()));
			System.out.println("flag=" + u.getFlag());
			System.out.println("nullable=" + u.getNullable());

			assertEquals(user.userName, u.userName);
			assertTrue(user.balance.doubleValue() == u.balance.doubleValue());
			assertNull(u.nullable);
			assertTrue(u.getFlag());
			assertEquals("00:00:00.000", StringUtil.format("HH:mm:ss.SSS", u.getLastActiveDate()));
			assertEquals(StringUtil.format("yyyy-MM-dd HH:mm:ss.SSS", user.registerTime),
					StringUtil.format("yyyy-MM-dd HH:mm:ss.SSS", u.registerTime));
		} while (true);
		
		bout = new ByteArrayOutputStream();
		writer = new CsvFileBeansWriter(new PrintWriter(bout));
		writer.addAllFields(User.class);
		writer.removeField("flag");
		
		writer.writeBean(user);
		writer.getOut().flush();

		System.out.println(bout.toString());
	}

}
