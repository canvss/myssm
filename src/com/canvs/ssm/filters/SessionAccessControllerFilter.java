package com.canvs.ssm.filters;

import com.canvs.ssm.utils.Utils;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class SessionAccessControllerFilter implements Filter {
    private List<String> whiteList = null;

    boolean isSessionAccess;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext servletContext = filterConfig.getServletContext();
        isSessionAccess = Boolean.parseBoolean(servletContext.getInitParameter("isSessionAccess"));
        if (isSessionAccess) {
            String whiteStr = servletContext.getInitParameter("whiteList").replaceAll("#", "&")
                    .replaceAll("\n", "").replaceAll("\\s+", "");
            whiteStr = "/" + "," + whiteStr;
            String[] whiteArr = whiteStr.split(",");
            whiteList = Arrays.asList(whiteArr);
            System.out.println("--------白名单--------");
            whiteList.forEach(System.out::println);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        if (!isSessionAccess || Utils.isStaticResource(uri)) {
            filterChain.doFilter(request, response);
        } else {
            String queryString = request.getQueryString();
            if (Utils.isNotEmpty(queryString)) {
                uri = uri + "?" + queryString;
            }
            if (whiteList.contains(uri)) {
                filterChain.doFilter(request, response);
            } else {
                HttpSession session = request.getSession();
                Object currUser = session.getAttribute("currUser");
                if (currUser == null) {
                    response.sendRedirect("/");
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }

    @Override
    public void destroy() {

    }
}
