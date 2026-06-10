package com.example.SevMerge.member;

import com.example.SevMerge.bid.BidService;
import com.example.SevMerge.board.BoardService;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.portfolio.PortfolioService;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.review.ReviewRepository;
import com.example.SevMerge.review.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Value("${oauth.google.client-id}")
    private String googleClientId;
    private final ProjectService projectService;
    private final ReviewService reviewService;
    private final BoardService boardService;
    private final BidService bidService;
    private final ReviewRepository reviewRepository;
    private final PortfolioService portfolioService;


    // 일반 회원가입 - 역할 선택 화면
    @GetMapping("/join-role")
    public String joinRole() {
        return "member/join-role";
    }

    // 회원가입 role에 따라 의뢰인/전문가 폼 나눔
    @GetMapping("/join")
    public String joinForm(@RequestParam(required = false) String role, Model model) {
        if ("EXPERT".equals(role)) {
            return "member/join-form-expert";
        }
        // role 없거나 CLIENT면 의뢰인
        return "member/join-form-client";
    }

    @PostMapping("/join")
    public String join(MemberRequest.Join request) {
        memberService.join(request);
        return "redirect:/login";
    }

    // 로그인 / 로그아웃
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("email", "");
        model.addAttribute("googleClientId", googleClientId);
        return "member/login-form";
    }

    @PostMapping("/login")
    public String login(MemberRequest.Login request, HttpSession session, Model model) {
        memberService.login(request, session);

        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember != null && loginMember.getRole() == Role.EXPERT) {
            return "redirect:/experts/dashboard";
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        memberService.logout(session);
        log.info("로그아웃완료");
        return "redirect:/";
    }

    // 마이페이지
    @GetMapping("/my-pages")
    public String mypage(@RequestParam(required = false) String tab,
                         HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        // 세션 유저 방어(로그아웃 상태 시 로그인창으로)
        if (loginMember == null) {
            return "redirect:/login";
        }

        if(tab == null) {
            tab = loginMember.isExpert() ? "bids" : "projects";
        }

        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        model.addAttribute("isProjects", tab.equalsIgnoreCase("projects"));
        model.addAttribute("isBoards", tab.equalsIgnoreCase("boards"));
        model.addAttribute("isReviews", tab.equalsIgnoreCase("reviews"));
        model.addAttribute("isBids", tab.equalsIgnoreCase("bids"));
        model.addAttribute("isEdit", tab.equalsIgnoreCase("edit"));
        model.addAttribute("isPortfolio",tab.equalsIgnoreCase("portfolios"));


        model.addAttribute("projectCount", projectService.myProjects(loginMember).size());
        if (loginMember.getRole() == Role.EXPERT) {
            model.addAttribute("bidCount", bidService.findMyBids(loginMember).size());
        }
        if (tab.equals("projects")) {
            model.addAttribute("projects", projectService.myProjects(loginMember));
        } else if (tab.equals("boards")) {
            model.addAttribute("boards", boardService.findAllByMyBoard(loginMember.getId()));
        } else if (tab.equals("reviews")) {
            model.addAttribute("reviews", reviewService.findMyReviews(loginMember.getId()));
        } else if (tab.equals("bids")) {
            model.addAttribute("bids", bidService.findMyBids(loginMember));
        } else if (tab.equals("edit")) {
            model.addAttribute("rawName", loginMember.getName());
            model.addAttribute("rawEmail", loginMember.getEmail());
        } else if(tab.equals("portfolios")) {
            model.addAttribute("portfolios",portfolioService.findByMemberId(loginMember.getId()));
        }

        return "member/mypage";
    }

    // 회원 정보 수정 페이지 이동 (GET)
    @GetMapping("/mypage/update") //
    public String updateMemberPage(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) {
            return "redirect:/login";
        }
        // 머스테치에 주입
        model.addAttribute("member", memberService.getMyInfo(loginMember.getId()));
        return "member/update-form";
    }

    // 회원 정보 수정 처리 (PUT)
    @PutMapping("/my-pages")
    @ResponseBody
    public ResponseEntity<?> updateMember(@RequestBody MemberRequest.Update request, HttpSession session) {
        Member loginMember = (Member) session.getAttribute(Define.SESSION_USER);
        if (loginMember == null) return ResponseEntity.status(401).body("세션 만료");

        memberService.updateMyInfo(loginMember.getId(), request);
        session.setAttribute(Define.SESSION_USER, memberService.findMemberById(loginMember.getId()));
        return ResponseEntity.ok().body("정보 변경 완료");
    }

    // 회원 목록
    @GetMapping("/members")
    public String memberList(Model model) {
        model.addAttribute("members", memberService.getPendingExperts());
        return "member/member-list";
    }

    // 관리자 - 회원 관리
    @GetMapping("/admin/members")
    public String adminMembers(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<MemberResponse> memberList;


        if (keyword != null && !keyword.trim().isEmpty()) {
            memberList = memberService.searchMembers(keyword.trim());
        } else {
            memberList = memberService.getAllMembers();
        }

        model.addAttribute("members", memberList);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("isAdmin", true);

        return "admin/admin-member";
    }

    // 탈퇴처리 DELETE 전환
    @DeleteMapping("/admin/members/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.suspendMember(id);
        return ResponseEntity.ok().body("제재 처리가 성공했습니다.");
    }

//    // 관리자 - 전문가 승인
//    @GetMapping("/admin/experts")
//    public String adminExperts(Model model) {
//        model.addAttribute("experts", memberService.getPendingExperts());
//        model.addAttribute("isAdmin", true);
//        return "admin/admin-expert";
//    }
//
//    // 전문가 승인/거절 상태 제어 PATCH 전환
//    @PatchMapping("/admin/experts/{id}/status")
//    @ResponseBody
//    public ResponseEntity<?> updateExpertStatus(@PathVariable Long id, @RequestParam String action) {
//        if ("approve".equals(action)) {
//            memberService.approveExpert(id);
//            return ResponseEntity.ok().body("승인 완료");
//        } else if ("reject".equals(action)) {
//            memberService.rejectExpert(id);
//            return ResponseEntity.ok().body("반려 완료");
//        }
//        return ResponseEntity.badRequest().body("잘못된 명령입니다.");
//    }

    // --------------------------------------------------------------------

    // 구글 콜백: 기존 회원이면 바로 로그인, 신규면 역할 선택 화면으로
    @GetMapping("/google-redirect")
    public String googleRedirect(@RequestParam String code, HttpSession session) {

        MemberResponse.GoogleProfile profile = memberService.getGoogleProfile(code);
        String googleId = profile.getSub();
        String nickname = profile.getName();
        String email    = profile.getEmail();

        Member existing = memberService.findGoogleMember(googleId);


        if (existing != null) {
            // 승인 대기 중인 전문가
            if (existing.getStatus() == Status.PENDING) {
                log.info("구글 PENDING 전문가 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/social-pending";
            }
            // 정지된 계정
            if (existing.getStatus() == Status.SUSPENDED) {
                log.info("구글 정지 계정 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/login";
            }

            session.setAttribute(Define.SESSION_USER, existing);
            log.info("구글 기존 회원 로그인 - memberId={}", existing.getId());
            return "redirect:/";
        }

        // 신규 회원 -> 세션에 임시 보관 후 역할 선택 화면으로
        session.setAttribute("googleId",       googleId);
        session.setAttribute("googleNickname", nickname);
        session.setAttribute("googleEmail",    email);
        return "redirect:/social-role";
    }

    // 카카오 콜백: 기존 회원이면 바로 로그인, 신규면 역할 선택 화면으로
    @GetMapping("/kakao-redirect")
    public String kakaoRedirect(@RequestParam String code,
                                HttpSession session, Model model) {

        MemberResponse.KakaoProfile profile = memberService.getKakaoProfile(code);
        Long kakaoId = profile.getId();
        String nickname = profile.getKakaoAccount().getProfile().getNickname() + "_" + kakaoId;

        Member existing = memberService.findKakaoMember(kakaoId);

        if (existing != null) {
            // 승인 대기 중인 전문가
            if (existing.getStatus() == Status.PENDING) {
                log.info("카카오 PENDING 전문가 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/social-pending";
            }
            // 정지된 계정
            if (existing.getStatus() == Status.SUSPENDED) {
                log.info("카카오 정지 계정 로그인 차단 - memberId={}", existing.getId());
                return "redirect:/login";
            }

            session.setAttribute(Define.SESSION_USER, existing);
            log.info("카카오 기존 회원 로그인 - memberId={}", existing.getId());
            return "redirect:/";
        }

        // 신규 회원 : 카카오 정보세션에서 잠깐 보관후 역할 선택 화면
        session.setAttribute("kakaoId", kakaoId);
        session.setAttribute("kakaoNickname", nickname);
        return "redirect:/social-role";
    }

    // 역할 선택 화면
    @GetMapping("/social-role")
    public String socialRoleForm(HttpSession session) {
        // 카카오 ID도 없고 구글 ID도 세션에 없으면 차단
        if (session.getAttribute("kakaoId") == null && session.getAttribute("googleId") == null) {
            return "redirect:/login";
        }
        return "member/social-role";
    }

    // 3. 역할 선택 후 가입 처리 (카카오 + 구글 통합 분기 완료)
    @PostMapping("/social-join")
    public String socialJoin(@RequestParam String role, HttpSession session) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        // 둘 다 세션에 없으면 가입 불가
        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        Member member = null;

        if (kakaoId != null) {
            // 카카오 가입 처리
            String nickname = (String) session.getAttribute("kakaoNickname");
            member = memberService.registerKakaoMember(kakaoId, nickname, role);

            session.removeAttribute("kakaoId");
            session.removeAttribute("kakaoNickname");
            log.info("카카오 소셜 가입 완료 - memberId={}", member.getId());
        } else if (googleId != null) {
            // 구글 가입 처리 로직 (CustomSuccessHandler에서 세팅한 값을 꺼냄)
            String nickname = (String) session.getAttribute("googleNickname");
            String email = (String) session.getAttribute("googleEmail");

            member = memberService.registerGoogleMember(googleId, nickname, email, role);

            session.removeAttribute("googleId");
            session.removeAttribute("googleNickname");
            session.removeAttribute("googleEmail");
            log.info("구글 소셜 가입 완료 - memberId={}", member.getId());
        }
        if (member != null) {
            Member freshMember = memberService.findMemberById(member.getId());
            session.setAttribute(Define.SESSION_USER, freshMember);
            log.info("소셜 회원가입 최종 완료 -> 온전한 세션 주입 완료 - memberId={}", freshMember.getId());
        }

        return "redirect:/";
    }


    // 전문가 추가정보 입력 화면
    @GetMapping("/social-expert-form")
    public String socialExpertForm(HttpSession session, Model model) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        // 소셜 인증 안 거치고 직접 들어오면 차단
        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        if (kakaoId != null) {
            // 카카오: 닉네임 자동 채움, 이메일은 직접 입력
            model.addAttribute("name", session.getAttribute("kakaoNickname"));
            model.addAttribute("email", "");
            model.addAttribute("emailReadonly", false);
        } else {
            // 구글: 이름·이메일 자동 채움, 이메일 읽기전용
            model.addAttribute("name", session.getAttribute("googleNickname"));
            model.addAttribute("email", session.getAttribute("googleEmail"));
            model.addAttribute("emailReadonly", true);
        }

        return "member/expert-join";
    }

    // 전문가 추가정보 제출 -> PENDING 가입 (세션 주입 X = 로그인 안 됨)
    @PostMapping("/social-expert-join")
    public String socialExpertJoin(MemberRequest.ExpertJoin request, HttpSession session) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        String googleId = (String) session.getAttribute("googleId");

        if (kakaoId == null && googleId == null) {
            return "redirect:/login";
        }

        if (kakaoId != null) {
            String nickname = (String) session.getAttribute("kakaoNickname");
            memberService.registerKakaoExpert(kakaoId, nickname, request);

            session.removeAttribute("kakaoId");
            session.removeAttribute("kakaoNickname");
            log.info("카카오 전문가 가입(PENDING) 완료");
        } else {
            String nickname = (String) session.getAttribute("googleNickname");
            String email = (String) session.getAttribute("googleEmail");
            request.setEmail(email);  // disabled로 안 넘어온 이메일을 세션에서 보충
            memberService.registerGoogleExpert(googleId, nickname, email, request);

            session.removeAttribute("googleId");
            session.removeAttribute("googleNickname");
            session.removeAttribute("googleEmail");
            log.info("구글 전문가 가입(PENDING) 완료");
        }

        // 전문가는 세션에 안 넣고 승인 대기 안내 화면으로 보냄
        return "redirect:/social-pending";
    }

    // 승인 대기 안내 화면
    @GetMapping("/social-pending")
    public String socialPending() {
        return "member/expert-pending";
    }
}