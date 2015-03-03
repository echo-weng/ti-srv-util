package me.andpay.ti.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.MethodExecutor;
import org.springframework.expression.MethodResolver;
import org.springframework.expression.ParserContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * SPEL格式化器类。使用Spring Expression Language语法描述的StringTemplate进行格式化处理。
 * 
 * @author sea.bao
 */
public class SpelFormatter {
	public static final String DEFAULT_ARG_PREFIX = "a";

	private static class ExtMapAccessor extends MapAccessor {
		@Override
		public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
			Map<?, ?> m = (Map<?, ?>) target;
			Object value = m.get(name);
			if (value == null) {
				return TypedValue.NULL;
			} else {
				return new TypedValue(value);
			}
		}

		@Override
		public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
			return true;
		}
	}
	
	private static class SpelValueContextAccessor implements org.springframework.expression.PropertyAccessor {
		private static Class<?>[] classes = new Class[]{SpelValueContext.class};
		
		public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
			SpelValueContext ctx = (SpelValueContext) target;
			Object value = ctx.getValue(name);
			if (value == null) {
				return TypedValue.NULL;
			} else {
				return new TypedValue(value);
			}
		}

		public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
			return true;
		}

		@SuppressWarnings("rawtypes")
		public Class[] getSpecificTargetClasses() {
			return classes;
		}

		public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
			return false;
		}

		public void write(EvaluationContext context, Object target, String name, Object newValue)
				throws AccessException {
		}
	}

	private static SpelValueContextAccessor spelValueContextAccessor = new SpelValueContextAccessor();
	
	private static ExtMapAccessor extMapAccessor = new ExtMapAccessor();

	private static class FormatMethodExecutor implements MethodExecutor {

		public TypedValue execute(EvaluationContext context, Object target, Object... arguments) throws AccessException {
			Object[] nargs = new Object[arguments.length - 1];
			for (int i = 1; i < arguments.length; i++) {
				nargs[i - 1] = arguments[i];
			}

			String value = String.format((String) arguments[0], nargs);
			return new TypedValue(value);
		}

	}

	private static FormatMethodExecutor fmtMethodExec = new FormatMethodExecutor();

	private static class FormatMethodResolver implements MethodResolver {

		public MethodExecutor resolve(EvaluationContext context, Object targetObject, String name,
				List<TypeDescriptor> argumentTypes) throws AccessException {
			name = name.toUpperCase();
			if (!name.equals("FORMAT") && !name.equals("FMT") && !name.equals("F")) {
				return null;
			}

			return fmtMethodExec;
		}

	}

	private static FormatMethodResolver fmtMethodResolver = new FormatMethodResolver();

	public static String format(String msgTemplate, Map<String, ?> args, MethodResolver... methodResolvers) {
		StandardEvaluationContext ctx = new StandardEvaluationContext(args);
		ctx.addPropertyAccessor(extMapAccessor);
		ctx.addMethodResolver(fmtMethodResolver);
		if (methodResolvers != null) {
			for (MethodResolver methodResolver : methodResolvers) {
				ctx.addMethodResolver(methodResolver);
			}
		}

		ExpressionParser parser = new SpelExpressionParser();

		Expression exp = parser.parseExpression(msgTemplate, ParserContext.TEMPLATE_EXPRESSION);

		return exp.getValue(ctx, String.class);
	}
	
	public static String format(String msgTemplate, SpelValueContext valCtx, MethodResolver... methodResolvers) {
		StandardEvaluationContext ctx = new StandardEvaluationContext(valCtx);
		ctx.addPropertyAccessor(spelValueContextAccessor);
		ctx.addMethodResolver(fmtMethodResolver);
		if (methodResolvers != null) {
			for (MethodResolver methodResolver : methodResolvers) {
				ctx.addMethodResolver(methodResolver);
			}
		}

		ExpressionParser parser = new SpelExpressionParser();

		Expression exp = parser.parseExpression(msgTemplate, ParserContext.TEMPLATE_EXPRESSION);

		return exp.getValue(ctx, String.class);
	}

	public static String format(String msgTemplate, Map<String, ?> args) {
		StandardEvaluationContext ctx = new StandardEvaluationContext(args);
		ctx.addPropertyAccessor(extMapAccessor);
		ctx.addMethodResolver(fmtMethodResolver);

		ExpressionParser parser = new SpelExpressionParser();

		Expression exp = parser.parseExpression(msgTemplate, ParserContext.TEMPLATE_EXPRESSION);

		return exp.getValue(ctx, String.class);
	}

	public static String format(String msgTemplate, Object... args) {
		Map<String, Object> margs = new HashMap<String, Object>();
		for (int i = 0; i < args.length; i++) {
			margs.put(DEFAULT_ARG_PREFIX + String.valueOf(i), args[i]);
		}

		return format(msgTemplate, margs);
	}
}
