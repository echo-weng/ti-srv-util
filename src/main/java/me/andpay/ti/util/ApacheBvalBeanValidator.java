package me.andpay.ti.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.bval.jsr303.ClassValidator;

/**
 * 基于ApacheBval的Bean校验器类。
 * 
 * @author sea.bao
 */
public class ApacheBvalBeanValidator {
	protected static ClassValidator classValidator;

	static {
		classValidator = Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory()
				.getValidator().unwrap(ClassValidator.class);
	}

	public static <T> void validateBean(T bean) throws IllegalArgumentException {
		Set<ConstraintViolation<T>> violations = classValidator.validate(bean);
		if (violations.isEmpty()) {
			return;
		}

		StringBuffer sb = new StringBuffer();
		boolean first = true;

		for (ConstraintViolation<T> violation : violations) {
			if (first) {
				first = false;
			} else {
				sb.append("; ");
			}

			if (violation.getPropertyPath().toString().equals("") == false) {
				sb.append(violation.getPropertyPath().toString());
			}

			sb.append(": ");
			sb.append(violation.getMessage());
		}

		throw new IllegalArgumentException(sb.toString());
	}
}
