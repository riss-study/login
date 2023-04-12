package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

//    @GetMapping("/")
    public String home() {
        return "home";
    }

//    @GetMapping("/")
    public String homeLogin (@CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        if (null == memberId) return "home";

        Member loginMember = memberRepository.findById(memberId);
        if (null == loginMember) return "home";

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV2 (HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원 정보 조회
        Member loginMember = (Member) sessionManager.getSession(request);

        // 로그인
        if (null == loginMember) return "home";

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3 (HttpServletRequest request, Model model) {

        // 로그인 하지 않은 새로운 사용자도 true 로 해놓으면 세션이 만들어지기 때문에 false 로 해놈 (세션은 메모리를 사용하기 때문에 꼭 필요할 때만 생성 추천)
        HttpSession session = request.getSession(false);
        if (null == session) return "home";

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);

        // 세션에 회원 데이터가 없으면 home (로그아웃해도 브라우저에 세션 쿠기 값은 남아 있기에 이 부분 해줘야 함)
        if (null == loginMember) return "home";

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

//    @GetMapping("/")
    public String homeLoginV3Spring (
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {

        // 세션에 회원 데이터가 없으면 home (로그아웃해도 브라우저에 세션 쿠기 값은 남아 있기에 이 부분 해줘야 함)
        if (null == loginMember) return "home";

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3ArgumentResolver (
            @Login Member loginMember,
            Model model) {

        // 세션에 회원 데이터가 없으면 home (로그아웃해도 브라우저에 세션 쿠기 값은 남아 있기에 이 부분 해줘야 함)
        if (null == loginMember) return "home";

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}