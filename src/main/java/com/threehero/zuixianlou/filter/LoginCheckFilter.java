package com.threehero.zuixianlou.filter;

import com.alibaba.fastjson.JSON;
import com.threehero.zuixianlou.common.BaseContext;
import com.threehero.zuixianlou.common.R;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

  public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    // 获取url对象
    String requestURI = request.getRequestURI();
    // 判断本次请求是否需要处理
    String[] urls = new String[]{
        "/employee/login",
        "/employee/logout",
        "/backend/**",
        "/front/**",
        "/user/sendMsg",
        "/user/login",
        "/order/again",
        "/doc.html",
        "/webjars/**",
        "/swagger-resources",
        "/v2/api-docs"
    };
    boolean check = check(urls, requestURI);
    // 如果不需要处理直接放行
    if (check) {
      filterChain.doFilter(request, response);
      return ;
    }
    // 判断登录状态 如果登录则直接放行
    if (request.getSession().getAttribute("employee") != null) {
      Long empId = (Long) request.getSession().getAttribute("employee");
      long id = Thread.currentThread().getId();
      BaseContext.setCurrentId(empId);
      filterChain.doFilter(request, response);
      return ;
    }
    if (request.getSession().getAttribute("user") != null) {
      Long userId = (Long) request.getSession().getAttribute("user");
      long id = Thread.currentThread().getId();
      BaseContext.setCurrentId(userId);
      filterChain.doFilter(request, response);
      return ;
    }

    response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

  }

  /**
   * 路径匹配
   * @param urls
   * @param requestURI
   * @return
   */
  public boolean check(String[] urls, String requestURI) {
    for (String url : urls) {
      boolean match = PATH_MATCHER.match(url, requestURI);
      if (match) {
        return true;
      }
    }
    return false;
  }
}
