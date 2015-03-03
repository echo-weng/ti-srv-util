package me.andpay.ti.spring;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 滚动文件写入器测试类。
 * 
 * @author sea.bao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations = { "classpath:spring-config/ti-srv-util-spring-config.xml" })
public class PropertiesHolderTest {
	@Autowired
	private ApplicationContext appCtx;
	
	@Autowired
	private org.apache.tomcat.jdbc.pool.DataSource dataSource; 

	public static class TestBean {
		private org.apache.tomcat.jdbc.pool.DataSource dataSource;

		public org.apache.tomcat.jdbc.pool.DataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
			this.dataSource = dataSource;
		}
		
	}
	
	@Test
	public void test() throws Exception {
		assertNotNull(dataSource);
		
		PropertiesHolder propsHolder = new PropertiesHolder();
		TestBean testBean = new TestBean();
		propsHolder.setRef(testBean, "dataSource", org.apache.tomcat.jdbc.pool.DataSource.class);
		PropertyUtil.setRefs(appCtx, propsHolder);
		
		assertTrue(testBean.dataSource == dataSource);
		
		propsHolder = new PropertiesHolder();
		testBean = new TestBean();
		propsHolder.setRef(testBean, "dataSource", "dataSource");
		PropertyUtil.setRefs(appCtx, propsHolder);
		
		assertTrue(testBean.dataSource == dataSource);
	}

}
