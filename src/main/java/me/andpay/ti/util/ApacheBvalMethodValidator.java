package me.andpay.ti.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.apache.bval.jsr303.ApacheValidationProvider;
import org.apache.bval.jsr303.extensions.MethodValidator;

/**
 * 基于ApacheBval实现的方法校验器实现类。
 * 
 * @author sea.bao
 */
public class ApacheBvalMethodValidator {
	protected static MethodValidator methodValidator;

	static {
		methodValidator = Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory()
				.getValidator().unwrap(MethodValidator.class);
	}

	public static <T> void validateAllParameters(Class<T> clazz, Method method, Object... args)
			throws IllegalArgumentException {
		List<Set<ConstraintViolation<T>>> argsViolations = new ArrayList<Set<ConstraintViolation<T>>>();

		boolean violateFlag = false;
		for (int i = 0; i < args.length; i++) {
			Set<ConstraintViolation<T>> violations = methodValidator.validateParameter(clazz, method, args[i], i);
			if (violations.isEmpty() == false) {
				violateFlag = true;
			}

			argsViolations.add(violations);
		}

		if (violateFlag) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < argsViolations.size(); i++) {
				for (ConstraintViolation<T> violation : argsViolations.get(i)) {
					if (sb.length() > 0) {
						sb.append("; ");
					}

					sb.append("arg");
					sb.append(i);
					if (violation.getPropertyPath().toString().equals("") == false) {
						sb.append(".");
						sb.append(violation.getPropertyPath().toString());
					}

					sb.append(": ");
					sb.append(violation.getMessage());

					sb.append(" [");
					sb.append(violation.getInvalidValue());
					sb.append("]");
				}
			}

			throw new IllegalArgumentException(sb.toString());
		}
	}
}
