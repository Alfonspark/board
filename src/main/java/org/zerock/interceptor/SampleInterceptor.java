package org.zerock.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SampleInterceptor extends HandlerInterceptorAdapter {


  @Override
  public void postHandle(HttpServletRequest request,
      HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
  
    System.out.println("post handle........................");
    
    Object result = modelAndView.getModel().get("result");
    
    if(result != null){
      System.out.println("result exists");
      request.getSession().setAttribute("result", result);
      response.sendRedirect("/doA");
    }
    
  }



  @Override
  public boolean preHandle(HttpServletRequest request, 
      HttpServletResponse response, Object handler) throws Exception {

    System.out.println("pre handle..........................");
    
    //현재 사용되고있는 메소드와 빈의 정보를 가져온다
    HandlerMethod method = (HandlerMethod) handler;
    Method methodObj = method.getMethod();

    System.out.println("Bean: " + method.getBean());
    System.out.println("Method: " + method.getMethod());

    return true;

  }

//
//	@Override
//	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//	  ModelAndView modelAndView) throws Exception {
//	
//		System.out.println("post handle.........");
//	
//	}
//	
//	 @Override
//	 public boolean preHandle(HttpServletRequest request,
//	 HttpServletResponse response, Object handler) throws Exception {
//	
//		 System.out.println("pre handle..........");
//	
//		 return true;
//	 }
  
  
  
}