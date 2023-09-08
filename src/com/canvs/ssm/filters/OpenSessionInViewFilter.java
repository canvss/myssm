package com.canvs.ssm.filters;

import com.canvs.ssm.trans.TransactionManager;

import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class OpenSessionInViewFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            TransactionManager.openTrans();
            filterChain.doFilter(servletRequest,servletResponse);
            TransactionManager.commit();
        }catch (Exception e){
            e.printStackTrace();
            try {
                TransactionManager.rollback();
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {

    }
}
