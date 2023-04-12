package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (null == session) return "세션이 없습니다.";

        // 세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("[{}] session name={}, value={}", MDC.get("UUID"), name, session.getAttribute(name)));

        log.info("[{}] sessionId={}", MDC.get("UUID"), session.getId());
        log.info("[{}] getMaxInactiveInterval={}",MDC.get("UUID"), session.getMaxInactiveInterval());
        // GetMaxInactiveInterval: 해당 세션 타임아웃 시간 => 해당 세션을 통해 새로운 요청이 있은 후 해당 interval 만큼 살아있음
        // 만약 1800초(30분)이면, 30분 동안 새로운 http 요청이 없으면, 해당 세션을 삭제한다 (서블릿의 HttpSession 이 제공) => 타임아웃 발생
        // (LastAccessedTime 이후로 timeout 시간이 지나면, WAS 가 내부에서 해당 세션을 제거한다)
        // 실제 세션에서는 최소한의 데이터만 보관해야 함. 보관하는 사용자 데이터가 많으면 메모리 사용량 때문에 장애 발생할 수 있음.
        // 그러므로 member id 정도나 혹은 자주 사용하는 로그인 용 SessionUser 객체를 따로 만들어서 id 나 name 정도만 fit 하게 보관해야 한다.
        log.info("[{}] createTime={}", MDC.get("UUID"), new Date(session.getCreationTime()));
        log.info("[{}] lastAccessedTime={}", MDC.get("UUID"), new Date(session.getLastAccessedTime()));
        log.info("[{}] isNew={}", MDC.get("UUID"), session.isNew());

        return "세션 출력";
    }
}
