package com.canvs.ssm.mvc;

import com.canvs.ssm.ioc.BeanFactory;
import com.canvs.ssm.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@WebServlet("/")
public class DispatcherServlet extends ViewBaseServlet {
    private BeanFactory beanFactory;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext servletContext = getServletContext();
        Object beanFactoryObj = servletContext.getAttribute("beanFactory");
        if (beanFactoryObj == null){
            throw new RuntimeException("IOC容器获取失败");
        }
        beanFactory = (BeanFactory) beanFactoryObj;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        // 检查请求是否是静态资源，例如以.css、.js、.jpg、.png等结尾
        if (Utils.isStaticResource(requestURI)) {
            super.service(request, response); // 调用父类的service方法来处理静态资源
            return;
        }
        String servletPath = request.getServletPath();
        if (servletPath.equals("/")){
            super.processTemplate("index",request,response);
            return;
        }
        servletPath = servletPath.split("/")[1];
        String operate = request.getParameter("operate");
        Object controllerBeanObj = beanFactory.BeanClass(servletPath);
        if (controllerBeanObj == null){
            super.processTemplate("404",request,response);
            return;
        }
        if (Utils.isEmpty(operate)) {
            operate = "index";
        }
        try {
            Method[] methods = controllerBeanObj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (operate.equals(method.getName())) {
                    // 统一获取请求参数
                    Parameter[] parameters = method.getParameters();
                    Object[] parameterValues = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        String parameterName = parameters[i].getName();
                        if ("request".equals(parameterName)){
                            parameterValues[i] = request;
                        }else if ("response".equals(parameterName)){
                            parameterValues[i] = response;
                        }else if ("session".equals(parameterName)){
                            parameterValues[i] = request.getSession();
                        }else {
                            String typeName = parameters[i].getType().getName();
                            String parameterValue = request.getParameter(parameterName);
                            Object parameterObj = parameterValue;
                            if (parameterObj != null){
                                if ("java.lang.Integer".equals(typeName)){
                                    parameterObj = Integer.parseInt(parameterValue);
                                }
                            }
                            parameterValues[i] = parameterObj;
                        }
                    }
                    // controller组件中的方法调用
                    method.setAccessible(true);
                    Object returnObj = method.invoke(controllerBeanObj, parameterValues);
                    // 视图处理
                    if (returnObj == null){
                        return;
                    }else {
                        String methodReturnStr = (String) returnObj;
                        if (methodReturnStr.startsWith("redirect:")) {
                            String redirectStr = methodReturnStr.substring(("redirect:").length());
                            response.sendRedirect(redirectStr);
                        } else if (methodReturnStr.startsWith("json:")){
                            response.setContentType("application/json;charset=utf-8");
                            String jsonStr = methodReturnStr.substring("json:".length());
                            PrintWriter out = response.getWriter();
                            out.print(jsonStr);
                            out.flush();
                        }else {
                            this.processTemplate(methodReturnStr, request, response);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("DispatcherServlet出错");
        }
    }
}
