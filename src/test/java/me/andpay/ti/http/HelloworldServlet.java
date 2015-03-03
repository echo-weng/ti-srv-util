package me.andpay.ti.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TiCamelServlet类。
 * 
 * @author sea.bao
 */
public class HelloworldServlet extends HttpServlet {
	private static final long serialVersionUID = 6732072829030339519L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setCharacterEncoding("UTF-8");
		resp.setHeader("sign", "1234567890");
		resp.getWriter().write("Hello world, " + req.getParameter("name"));
	}

}
