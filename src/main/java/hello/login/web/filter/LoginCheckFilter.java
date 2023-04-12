package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@Component
public class LoginCheckFilter implements Filter {

    // default 인 init, destroy 는 구현하지 않아도 됨

    private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest=(HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("[{}] 인증 체크 필터 시작 [{}]", MDC.get("UUID"), requestURI);

            // 인증 체크를 해야하는 경로인지 체크
            if (isLoginCheckPath(requestURI)) {
                log.info("[{}] 인증 체크 로직 실행 [{}]", MDC.get("UUID"), requestURI);
                HttpSession session = httpRequest.getSession(false);

                // httpSession 에 멤버 데이터가 들어있는지 체크
                if (null == session || null == session.getAttribute(SessionConst.LOGIN_MEMBER)) {
                    log.info("[{}] 미 인증 사용자 요청 [{}]", MDC.get("UUID"), requestURI);

                    // 로그인으로 redirect
                    // 로그인 페이지에서 로그인 후 현재 페이지로 리다이렉트되도록 뒤에 ?redirectURL=requestURI 넣어줌
                    // 이 query parameter 를 받아서 /login controller 에서 추가하면 됨
                    httpResponse.sendRedirect("/login?redirectURL="+requestURI);
                    return;
                }
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            throw e; // 예외 로깅이 가능하지만, 톰캣가지 예외를 보내주어야 함, 서블릿 필터에서 예외가 터지면 여기까지 올라오는데,
            // 여기서 예외를 안던지고 먹어버리면 정상인 것처럼 동작해버리므로 Servlet Container -> WAS 까지 올려줘야 함
        } finally {
            log.info("[{}] 인증 체크 필터 종료 [{}]", MDC.get("UUID"), requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean isLoginCheckPath (String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
