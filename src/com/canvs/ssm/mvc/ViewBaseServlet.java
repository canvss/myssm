package com.canvs.ssm.mvc;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ViewBaseServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    @Override
    public void init() throws ServletException {
        //1.获取ServletContext对象
        ServletContext servletContext = this.getServletContext();
        //2.创建Thymeleaf解析器对象
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        //3.给解析器对象设置参数
        //3.1HTML是默认模式，明确设置是为了代码更容易理解
        templateResolver.setTemplateMode(TemplateMode.HTML);
        //3.2设置前缀
        String viewPrefix = servletContext.getInitParameter("view-prefix");
        templateResolver.setPrefix(viewPrefix);
        //3.3设置后缀
        String viewSuffix = servletContext.getInitParameter("view-suffix");
        templateResolver.setSuffix(viewSuffix);
        //3.4设置缓存过期时间（毫秒）
        templateResolver.setCacheTTLMs(60000L);
        //3.5设置是否缓存
        templateResolver.setCacheable(true);
        //3.6设置服务器端编码方式
        templateResolver.setCharacterEncoding("utf-8");
        //4 创建模版引擎对象
        templateEngine = new TemplateEngine();
        //5 给模版引擎对象设置模版解析器
        templateEngine.setTemplateResolver(templateResolver);
    }
    protected void processTemplate(String templateName, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //1 设置响应体内容类型和字符集
        resp.setContentType("text/html;charset=UTF-8");
        //2 创建WebContext对象
        WebContext webContext = new WebContext(req, resp, getServletContext());
        //3.处理模版数据
        templateEngine.process(templateName,webContext,resp.getWriter());
    }
}
