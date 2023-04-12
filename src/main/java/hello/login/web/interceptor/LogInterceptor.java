package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        MDC.put("UUID", uuid);

        request.setAttribute(LOG_ID, uuid);


        // @RequestMapping: HandlerMethod
        // 정적 리소스: ResourceHttpRequestHandler
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;// 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있음.

        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);

        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("POST-HANDLE [{}] [{}]", MDC.get("UUID"), modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID);
        // MDC 이용해서 해도 되지 않을까

        log.info("RESPONSE [{}][{}][{}]", uuid, requestURI, handler);
        if (null != ex) log.error("AFTER-COMPLETION ERROR!! [{}]", MDC.get("UUID"), ex);
        MDC.clear();
    }
}
