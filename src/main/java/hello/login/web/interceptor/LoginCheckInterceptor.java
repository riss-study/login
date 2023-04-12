package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // handler adaptor 가기 전에 로그인 체크만 하면 되므로 preHandle 만 오버라이드해서 구현하면 됨

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info("PRE-HANDLE 인증 체크 인터셉터 실행 [{}][{}]", MDC.get("UUID"), requestURI);

        HttpSession session = request.getSession();

        if (null == session || null == session.getAttribute(SessionConst.LOGIN_MEMBER)) {
            log.info("PRE-HANDLE 미 인증 사용자 요청 [{}][{}]", MDC.get("UUID"), requestURI);
            // 로그인으로 redirect
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false;
        }

        return true;
    }
}
