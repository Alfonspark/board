package org.zerock.controller;

import java.util.Date;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.WebUtils;
import org.zerock.domain.UserVO;
import org.zerock.dto.LoginDTO;
import org.zerock.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

  @Inject
  private UserService service;

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public void loginGET(@ModelAttribute("dto") LoginDTO dto) {

  }

//   @RequestMapping(value = "/loginPost", method = RequestMethod.POST)
//   public void loginPOST(LoginDTO dto, HttpSession session, Model model)
//   throws Exception {
//  
//	   UserVO vo = service.login(dto);
//	  
//	   if (vo == null) {
//	   return;
//	   }
//	  
//	   //uid, upw, uname가 담겨있음.
//	   model.addAttribute("userVO", vo);
//  
//   }

  @RequestMapping(value = "/loginPost", method = RequestMethod.POST)
  public void loginPOST(LoginDTO dto, HttpSession session, Model model) throws Exception {
	  //LoginDTO dto 는 로그인 할 때 id,pw,remember me에 대한 정보를 가져온다
	  
	//ID와 비밀번호가 메칭되는 uservo정보들을 가져옴.
    UserVO vo = service.login(dto);//id,pw,name이 담겨있음.

    if (vo == null) {
      return;
    }

    model.addAttribute("userVO", vo);//id,pw,name이 담겨있음.
    
    //remember me를 선택 했을경우
    if (dto.isUseCookie()) {

      int amount = 60 * 60 * 24 * 7;
      
      //currentTimeMillis() == 현재시간을(1970년 1월 1일 부터 계산된) 1/1000초로 나타낸것. 현재시간+1주일을 의미함.
      Date sessionLimit = new Date(System.currentTimeMillis() + (1000 * amount));
      
    //session.getId()는 현재 접속해 있는 페이지의 세션을 가져오는 것 이다.
      service.keepLogin(vo.getUid(), session.getId(), sessionLimit);
    }

  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout(HttpServletRequest request, 
      HttpServletResponse response, HttpSession session) throws Exception {

    Object obj = session.getAttribute("login");

    if (obj != null) {
      UserVO vo = (UserVO) obj;
      
      //로그인 세션 정보들을 삭제함
      session.removeAttribute("login");
      session.invalidate();
      
      //loginCookie쿠키를 불러옴
      Cookie loginCookie = WebUtils.getCookie(request, "loginCookie");

      if (loginCookie != null) {
        loginCookie.setPath("/");
        //loginCookie유효시간을 0으로 만들어줌.
        loginCookie.setMaxAge(0);
        response.addCookie(loginCookie);
        
        //데이타베이스 의 정보를 바꾸어줌.
        service.keepLogin(vo.getUid(), session.getId(), new Date());
      }
    }//if
    
    return "redirect:/sboard/list";
  }

}
