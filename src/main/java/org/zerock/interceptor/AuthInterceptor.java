package org.zerock.interceptor;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import org.zerock.domain.UserVO;
import org.zerock.service.UserService;

public class AuthInterceptor extends HandlerInterceptorAdapter {

  private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
  
  @Inject
  private UserService service;
  
  @Override
  public boolean preHandle(HttpServletRequest request,
      HttpServletResponse response, Object handler) throws Exception {
    
    HttpSession session = request.getSession();   


    if(session.getAttribute("login") == null){
    
      logger.info("current user is not logined");
      
      //dest세션을 만들어 방금 접속한(로그인 후 접속될)페이지를 저장해줌
      saveDest(request);
      
      //loginCookie를 가지고옴
      Cookie loginCookie = WebUtils.getCookie(request, "loginCookie");
      
      if(loginCookie != null) { 
        
    	//loginCookie.getValue()는 세션ID값임.
        UserVO userVO = service.checkLoginBefore(loginCookie.getValue());//tbl_user에있는 모든 값을 가져옴.
        logger.info("loginCookie.getValue() ===== "+loginCookie.getValue());
        
        logger.info("USERVO: " + userVO);
        
        if(userVO != null){
          session.setAttribute("login", userVO);
          return true;
        }
        
      }

      response.sendRedirect("/user/login");
      return false;
    }
    return true;
  }  
  

  private void saveDest(HttpServletRequest req) {

    String uri = req.getRequestURI();//해당 uri
    logger.info("uri ==== "+uri);
    String query = req.getQueryString();//해당 쿼리스트링
    logger.info("query ==== "+uri);

    if (query == null || query.equals("null")) {
      query = "";
    } else {
      query = "?" + query;
    }

    if (req.getMethod().equals("GET")) {
      logger.info("dest: " + (uri + query));
      req.getSession().setAttribute("dest", uri + query);
    }
  }

//  @Override
//  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//    HttpSession session = request.getSession();
//
//    if (session.getAttribute("login") == null) {
//
//      logger.info("current user is not logined");
//
//      saveDest(request);
//      
//      response.sendRedirect("/user/login");
//      return false;
//    }
//    return true;
//  }
}

// if(session.getAttribute("login") == null){
//
// logger.info("current user is not logined");
// response.sendRedirect("/user/login");
// return false;
// }
