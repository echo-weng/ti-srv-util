package me.andpay.ti.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.andpay.ti.base.AppBizException;
import me.andpay.ti.base.AppRtException;

/**
 * 异常帮助类
 * 
 * @author sea.bao
 */
public class ExHelper {
	public static AppBizException newAppBizEx(String code) {
		return new AppBizException(code);
	}

	public static AppBizException newAppBizEx(Throwable t, String code) {
		return new AppBizException(code, t);
	}

	public static AppBizException newAppBizEx(String code, String msg) {
		return new AppBizException(code, msg);
	}

	public static AppBizException newAppBizEx(Throwable t, String code, String msg) {
		return new AppBizException(code, msg, t);
	}

	public static AppBizException newAppBizEx(String code, String msg, Object... args) {
		return newAppBizEx(null, code, msg, args);
	}

	public static AppBizException newAppBizEx(Throwable t, String code, String msg, Object... args) {
		Map<String, Object> ctx = new HashMap<String, Object>();
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				ctx.put(SpelFormatter.DEFAULT_ARG_PREFIX + String.valueOf(i), args[i]);
			}
		}
		return newAppBizEx(t, code, msg, ctx);
	}

	public static AppBizException newAppBizEx(String code, String msg, Map<String, Object> ctx) {
		return newAppBizEx(null, code, msg, ctx);
	}

	public static AppBizException newAppBizEx(Throwable t, String code, String msg, Map<String, Object> ctx) {
		try {
			msg = SpelFormatter.format(msg, ctx);
		} catch (RuntimeException e) {
			// ignore the syntax error in spel msgtemplate.
		}

		Map<String, String> nCtx = new HashMap<String, String>();
		for (Entry<String, Object> entry : ctx.entrySet()) {
			nCtx.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
		}

		return new AppBizException(code, msg, nCtx, t);
	}

	public static AppRtException newAppRtEx(String code) {
		return new AppRtException(code);
	}

	public static AppRtException newAppRtEx(Throwable t, String code) {
		return new AppRtException(code, t);
	}

	public static AppRtException newAppRtEx(String code, String msg) {
		return new AppRtException(code, msg);
	}

	public static AppRtException newAppRtEx(Throwable t, String code, String msg) {
		return new AppRtException(code, msg, t);
	}

	public static AppRtException newAppRtEx(String code, String msg, Object... args) {
		return newAppRtEx(null, code, msg, args);
	}

	public static AppRtException newAppRtEx(Throwable t, String code, String msg, Object... args) {
		Map<String, Object> ctx = new HashMap<String, Object>();
		for (int i = 0; i < args.length; i++) {
			ctx.put(SpelFormatter.DEFAULT_ARG_PREFIX + String.valueOf(i), args[i]);
		}

		return newAppRtEx(t, code, msg, ctx);
	}

	public static AppRtException newAppRtEx(String code, String msg, Map<String, Object> ctx) {
		return newAppRtEx(null, code, msg, ctx);
	}

	public static AppRtException newAppRtEx(Throwable t, String code, String msg, Map<String, Object> ctx) {
		try {
			msg = SpelFormatter.format(msg, ctx);
		} catch (RuntimeException e) {
			// ignore the syntax error in spel msgtemplate.
		}

		Map<String, String> nCtx = new HashMap<String, String>();
		for (Entry<String, Object> entry : ctx.entrySet()) {
			nCtx.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
		}

		return new AppRtException(code, msg, nCtx, t);
	}

	/**
	 * 获取应用异常中的异常码，如果非应用异常则返回defCode
	 * 
	 * @param t
	 * @param defCode
	 * @return
	 */
	public static String getAppExcCode(Throwable t, String defCode) {
		String appExcCode = null;
		if (t instanceof AppBizException) {
			appExcCode = ((AppBizException) t).getCode();

		} else if (t instanceof AppRtException) {
			appExcCode = ((AppRtException) t).getCode();
		}

		return (appExcCode != null ? appExcCode : defCode);
	}

	/**
	 * 获得应用异常的上下文
	 * 
	 * @param t
	 * @return
	 */
	public static Map<String, String> getAppExcCtx(Throwable t) {
		if (t instanceof AppBizException) {
			return ((AppBizException) t).getContext();

		} else if (t instanceof AppRtException) {
			return ((AppRtException) t).getContext();
		}

		return null;
	}

	/**
	 * 获得应用异常的上下文的参数值
	 * 
	 * @param t
	 * @return
	 */
	public static String getAppExcCtxPara(Throwable t, String name) {
		Map<String, String> context = getAppExcCtx(t);
		return (context != null ? context.get(name) : null);
	}

	/**
	 * 包装应用业务异常(如果是AppBizException子类则直接返回)
	 * 
	 * @param t
	 * @param defAppExcCode
	 * @return
	 */
	public static AppBizException wrapAppBizEx(Throwable t, String defAppExcCode) {
		if (t instanceof AppBizException) {
			return (AppBizException) t;

		} else {
			return new AppBizException(getAppExcCode(t, defAppExcCode), t);
		}
	}

	/**
	 * 包装应用运行时异常(如果是AppRtException子类则直接返回)
	 * 
	 * @param t
	 * @param defAppExcCode
	 * @return
	 */
	public static AppRtException wrapAppRtEx(Throwable t, String defAppExcCode) {
		if (t instanceof AppRtException) {
			return (AppRtException) t;

		} else {
			return new AppRtException(getAppExcCode(t, defAppExcCode), t);
		}
	}
}
