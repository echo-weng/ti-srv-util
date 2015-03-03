package me.andpay.ti.spring.batch;

import java.util.Map;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.database.HibernateItemReaderHelper;
import org.springframework.batch.item.database.orm.HibernateQueryProvider;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * 累积能力的Hibernate游标项目读取器类。
 * 
 * @author sea.bao
 */
public class AccumulateHibernateCursorItemReader<I, O> extends AbstractItemCountingItemStreamItemReader<O> implements
		ItemStream, InitializingBean {

	private HibernateItemReaderHelper<I> helper = new HibernateItemReaderHelper<I>();

	private Accumulator<I, O> accumulator;
	
	private StepExecution stepExecution;

	public AccumulateHibernateCursorItemReader() {
		setName(ClassUtils.getShortName(HibernateCursorItemReader.class));
	}

	private ScrollableResults cursor;

	private boolean initialized = false;

	private int fetchSize;

	private Map<String, Object> parameterValues;

	public void afterPropertiesSet() throws Exception {
		Assert.state(fetchSize >= 0, "fetchSize must not be negative");
		helper.afterPropertiesSet();
	}

	/**
	 * The parameter values to apply to a query (map of name:value).
	 * 
	 * @param parameterValues
	 *            the parameter values to set
	 */
	public void setParameterValues(Map<String, Object> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * A query name for an externalized query. Either this or the {
	 * {@link #setQueryString(String) query string} or the {
	 * {@link #setQueryProvider(HibernateQueryProvider) query provider} should
	 * be set.
	 * 
	 * @param queryName
	 *            name of a hibernate named query
	 */
	public void setQueryName(String queryName) {
		helper.setQueryName(queryName);
	}

	/**
	 * Fetch size used internally by Hibernate to limit amount of data fetched
	 * from database per round trip.
	 * 
	 * @param fetchSize
	 *            the fetch size to pass down to Hibernate
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * A query provider. Either this or the {{@link #setQueryString(String)
	 * query string} or the {{@link #setQueryName(String) query name} should be
	 * set.
	 * 
	 * @param queryProvider
	 *            Hibernate query provider
	 */
	public void setQueryProvider(HibernateQueryProvider queryProvider) {
		helper.setQueryProvider(queryProvider);
	}

	/**
	 * A query string in HQL. Either this or the {
	 * {@link #setQueryProvider(HibernateQueryProvider) query provider} or the {
	 * {@link #setQueryName(String) query name} should be set.
	 * 
	 * @param queryString
	 *            HQL query string
	 */
	public void setQueryString(String queryString) {
		helper.setQueryString(queryString);
	}

	/**
	 * The Hibernate SessionFactory to use the create a session.
	 * 
	 * @param sessionFactory
	 *            the {@link SessionFactory} to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		helper.setSessionFactory(sessionFactory);
	}

	/**
	 * Can be set only in uninitialized state.
	 * 
	 * @param useStatelessSession
	 *            <code>true</code> to use {@link StatelessSession}
	 *            <code>false</code> to use standard hibernate {@link Session}
	 */
	public void setUseStatelessSession(boolean useStatelessSession) {
		helper.setUseStatelessSession(useStatelessSession);
	}

	protected I doReadDbItem() throws Exception {
		if (cursor.next()) {
			Object[] data = cursor.get();

			if (data.length > 1) {
				// If there are multiple items this must be a projection
				// and T is an array type.
				@SuppressWarnings("unchecked")
				I item = (I) data;
				return item;
			} else {
				// Assume if there is only one item that it is the data the user
				// wants.
				// If there is only one item this is going to be a nasty shock
				// if T is an array type but there's not much else we can do...
				@SuppressWarnings("unchecked")
				I item = (I) data[0];
				return item;
			}
		}

		return null;
	}

	protected O doRead() throws Exception {
		do {
			I item = doReadDbItem();
			O oItem = accumulator.accumulate(item, stepExecution);
			if (oItem != null || item == null) {
				return oItem;
			}
		} while (true);
	}

	/**
	 * Open hibernate session and create a forward-only cursor for the query.
	 */
	protected void doOpen() throws Exception {
		Assert.state(!initialized, "Cannot open an already opened ItemReader, call close first");
		cursor = helper.getForwardOnlyCursor(fetchSize, parameterValues);
		initialized = true;
	}

	/**
	 * Update the context and clear the session if stateful.
	 * 
	 * @param executionContext
	 *            the current {@link ExecutionContext}
	 * @throws ItemStreamException
	 *             if there is a problem
	 */
	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		super.update(executionContext);
		helper.clear();
	}

	/**
	 * Close the cursor and hibernate session.
	 */
	protected void doClose() throws Exception {

		initialized = false;

		if (cursor != null) {
			cursor.close();
		}

		helper.close();
	}

	public Accumulator<I, O> getAccumulator() {
		return accumulator;
	}

	public void setAccumulator(Accumulator<I, O> accumulator) {
		this.accumulator = accumulator;
	}

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
}
