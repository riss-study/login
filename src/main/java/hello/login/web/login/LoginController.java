package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm (@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

//    @PostMapping("/login")
    public String login (@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) return "login/loginForm";

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (null == loginMember) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료 시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV2 (@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) return "login/loginForm";

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (null == loginMember) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }

//    @PostMapping("/login")
    public String loginV3 (@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                           HttpServletRequest request) {
        if (bindingResult.hasErrors()) return "login/loginForm";

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (null == loginMember) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환(재사용), 없으면 신규 세션 생성 후 반환
        HttpSession session = request.getSession(true); // create parameter default 가 true 라서 생략 가능
        // true: 세션이 있으면 기존 세션을 반환, 세션이 없으면 새로운 세션을 생성해서 반환
        // false: 세션이 있으면 기존 세션 반환, 기존 세션이 없으면 신규 세션을 생성하지 않고 null 반환

        // 세션에 로그인 회원 정보 보관 (key, value 형태로 여러 값 보관 가능)
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
//        sessionManager.createSession(loginMember, response);

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4 (@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                           HttpServletRequest request,
                           @RequestParam(defaultValue = "/") String redirectURL) {
        if (bindingResult.hasErrors()) return "login/loginForm";

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (null == loginMember) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환(재사용), 없으면 신규 세션 생성 후 반환
        HttpSession session = request.getSession(true); // create parameter default 가 true 라서 생략 가능
        // true: 세션이 있으면 기존 세션을 반환, 세션이 없으면 새로운 세션을 생성해서 반환
        // false: 세션이 있으면 기존 세션 반환, 기존 세션이 없으면 신규 세션을 생성하지 않고 null 반환

        // 세션에 로그인 회원 정보 보관 (key, value 형태로 여러 값 보관 가능)
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
//        sessionManager.createSession(loginMember, response);

        return "redirect:"+redirectURL;
    }


//    @PostMapping("/logout")
    public String logout (HttpServletResponse response) {
        response.addCookie(getExpiredCookie("memberId"));
        return "redirect:/";
    }

//    @PostMapping("/logout")
    public String logoutV2 (HttpServletRequest request) {
        sessionManager.expireSession(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3 (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 세션 삭제가 목적이기 때문에 세션이 없더라도 새로운 세션을 만들지 않기 위해 create false 로 가져옴
        if (null != session) session.invalidate();  //HttpSession.invalidate() => 해당 세션과 그 안의 데이터 모두 삭제
        return "redirect:/";
    }

    private Cookie getExpiredCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        return cookie;
    }
}
