package com.example.SevMerge.admin;

import com.example.SevMerge.Report.BlackList;
import com.example.SevMerge.Report.BlacklistRepository;
import com.example.SevMerge.Report.ReportService;
import com.example.SevMerge.board.*;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.member.*;
import com.example.SevMerge.project.ProjectRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.MemberService;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.partnership.PartnerShipService;
import com.example.SevMerge.project.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final BlacklistRepository blacklistRepository;
    private final ReportService reportService;
    private final PartnerShipService partnerShipService;

    @GetMapping("/admin/main")
    public String dashboardPage(HttpSession session, Model model,
                                @RequestParam(value = "startDate", required = false) String startDate,
                                @RequestParam(value = "endDate", required = false) String endDate,
                                @RequestParam(value = "roleFilter", defaultValue = "ALL") String roleFilter,
                                @RequestParam(value = "projectType", required = false) String projectType,
                                @RequestParam(value = "statusFilter", defaultValue = "ALL") String statusFilter) {

        long newMemberCount = memberService.getNewMemberCountThisMonth();

        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser);

        // 전체회원 몇명이고 이번달 몇명 회원가입했는지 보여주는 코드
        model.addAttribute("stats", memberService.getAllMembers());
        model.addAttribute("newMemberCount", newMemberCount);

        // 전체 프로젝트 보여주고 진행중 몇건인지 보여주는 코드
        model.addAttribute("projectCount", projectService.findAllProjects().size());
        model.addAttribute("activeProjectCount", projectService.getActiveProjectsCount());

        // 완료한 프로젝트 보여주고 이번달 몇번 완료했는지 보여주는 코드
        model.addAttribute("completedCount", projectService.getDoneProjectsCount());
        model.addAttribute("newCompletedCount", projectService.getMonthDoneProjectsCount());

        // 승인 대기 조회하는 코드
        model.addAttribute("pendingExpertCount", memberService.getPendingExpertCount());

        String defaultStartDate = (startDate != null && !startDate.isEmpty()) ?
                startDate : LocalDate.now().minusDays(6).toString();
        String defaultEndDate = (endDate != null && !endDate.isEmpty()) ?
                endDate : LocalDate.now().toString();

        String memberChartName = "ALL".equalsIgnoreCase(roleFilter) ? "총 회원수" : ("CLIENT".equalsIgnoreCase(roleFilter) ? "일반 회원수" : "전문가 회원수");

        String statusChartName = "IN_PROGRESS".equalsIgnoreCase(statusFilter) ? "진행중 프로젝트 수" :
                ("COMPLETED".equalsIgnoreCase(statusFilter) ? "완료 프로젝트수" : "취소 프로젝트수");

        model.addAttribute("startDate", defaultStartDate);
        model.addAttribute("endDate", defaultEndDate);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("memberChartName", memberChartName);
        model.addAttribute("projectType", projectType);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("statusChartName", statusChartName);

        LocalDate parsedStartDate = LocalDate.parse(defaultStartDate);
        LocalDate parsedEndDate = LocalDate.parse(defaultEndDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(parsedStartDate, parsedEndDate);

        List<String> chartLabels = IntStream.rangeClosed(0, (int) daysBetween)
                .mapToObj(i -> parsedStartDate.plusDays(i).format(formatter))
                .toList();

        List<Integer> memberData = new ArrayList<>();
        List<Integer> projectData = new ArrayList<>();
        List<Integer> completeData = new ArrayList<>();
        List<Integer> emptyZeroList = chartLabels.stream().map(i -> 0).toList();

        if (projectType != null && !projectType.isEmpty()) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", projectType);
            model.addAttribute("isStatusFilterActive", false);
            projectData = projectService.getProjectCountByPeriodAndType(parsedStartDate, parsedEndDate, projectType);
            memberData = emptyZeroList;
            completeData = emptyZeroList;
        } else if (!"ALL".equalsIgnoreCase(roleFilter)) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", false);
            memberData = memberService.getMemberTrendByRoleAndPeriod(roleFilter, parsedStartDate, parsedEndDate);
            projectData = emptyZeroList;
            completeData = emptyZeroList;
        } else if (!"ALL".equalsIgnoreCase(statusFilter)) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", true); // 완료 프로젝트 단독 활성화 플래그 온
            completeData = projectService.getProjectTrendByStatusAndPeriod(statusFilter, parsedStartDate, parsedEndDate);
            memberData = emptyZeroList;
            projectData = emptyZeroList;
        } else {
            model.addAttribute("isStatusAll", true);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", false);
            memberData = memberService.getMemberTrendByPeriod(parsedStartDate, parsedEndDate);
            projectData = projectService.getProjectTrendByPeriod(parsedStartDate, parsedEndDate);
            completeData = projectService.getCompletedTrendByPeriod(parsedStartDate, parsedEndDate);
        }

        model.addAttribute("chartLabels", chartLabels != null ? chartLabels : new ArrayList<>());
        model.addAttribute("memberData", memberData != null ? memberData : emptyZeroList);
        model.addAttribute("projectData", projectData != null ? projectData : emptyZeroList);
        model.addAttribute("completedData", completeData != null ? completeData : emptyZeroList);
        model.addAttribute("recentPartnerships", partnerShipService.list());
        return "admin/admin-main";
    }



    // 관리자 공지사항 관리
    @GetMapping("/admin/notices")
    public String adminNotices(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        BoardType type = BoardType.NOTICE;

        List<BoardResponse.ListDTO> adminNotices = boardService.getAdminBoardsByType(type, keyword);

        model.addAttribute("boards", adminNotices);
        model.addAttribute("isFree", false);
        model.addAttribute("isNotice", true);
        model.addAttribute("isInquiry", false);
        model.addAttribute("boardType", "NOTICE");
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "admin/admin-notices";
    }

    // 관리자 공지사항 수정화면 띄우기
    @GetMapping("/admin/notices/{id}/update")
    public String updateNoticeForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        boolean isAdmin = sessionUser != null && sessionUser.getRole() == Role.ADMIN;

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));

        model.addAttribute("board", board);
        model.addAttribute("isNotice", true);
        model.addAttribute("isFree", false);
        model.addAttribute("isAdmin", isAdmin);

        return "admin/admin-noticeupdate";
    }

    // 관리자 공지사항 수정처리
    @Transactional
    @PostMapping("/admin/notices/{id}/update")
    public String updateNotice(@PathVariable("id") Long id,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content) {
        Board board = boardRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

        board.setTitle(title);
        board.setContent(content);

        return "redirect:/admin/notices";
    }

    // 전문가 승인 관리 페이지
    @GetMapping("/admin/experts")
    public String adminExpertPage(@RequestParam(value = "status", required = false) String status, Model model) {

        List<ExpertProfileResponse> expertsList;

        if ("PENDING".equals(status)) {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isPending", true);
        } else if ("APPROVED".equals(status)) {
            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.ACTIVE);
            model.addAttribute("isApproved", true);
        } else if ("REJECTED".equals(status)) {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.REJECTED);
            model.addAttribute("isRejected", true);
        } else {

            expertsList = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isAll", true);
        }

        model.addAttribute("experts", expertsList);
        return "admin/admin-expert";
    }

    // 전문가 승인/거절
    @ResponseBody
    @PatchMapping("/api/admin/experts/{memberId}/approval")
    public ResponseEntity<?> processExpertApproval(
            @PathVariable Long memberId,
            @RequestBody Map<String, String> body) {

        String action = body.get("action");

        if ("APPROVE".equals(action)) {
            memberService.approveExpert(memberId);
        } else if ("REJECT".equals(action)) {
            String reason = body.get("reason"); // 거절사유 추가
            memberService.rejectExpert(memberId, reason);
        } else {
            throw new IllegalArgumentException("잘못된 요청 액션입니다.");
        }

        return ResponseEntity.ok().build();
    }

    // 블랙리스트 관리 페이지 조회
    @GetMapping("/admin/blacklists")
    public String blacklistPage(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<BlackList> allblacklist = blacklistRepository.findAllWithMemberOrderByIdDesc();
        List<BlackList> blackListLogs = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            for (BlackList blackList : allblacklist) {
                if (blackList.getReason().contains(keyword) || blackList.getMember().getName().contains(keyword)) {
                    blackListLogs.add(blackList);
                }
            }
        } else {
            blackListLogs = allblacklist;
        }

        model.addAttribute("blacklistLogs", blackListLogs);
        model.addAttribute("keyword", keyword != null ? keyword : "");

        return "admin/admin-blacklist";
    }

    // 블랙리스트 차단회원 정지해제
    @PostMapping("/admin/blacklist/release/{memberId}")
    public String releaseBlacklistMember(@PathVariable(name = "memberId") Long memberId,
                                         HttpSession session) {
        Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        reportService.releaseMember(memberId);
        return "redirect:/admin/blacklists";


    }

    // 출금요청 관리 페이지
    @GetMapping("/admin/experts/withdraw")
    public String adminExpertWithdraw(Model model) {
        // 여기에 출금 요청 목록을 가져오는 로직 추가 예정 )  memberService.getWithdrawList
        return "admin/admin-withdraw";
    }

    @GetMapping("/admin/experts/grade")
    public String adminExpertGrade() {
        return "admin/admin-expert"; // 일단 기존 화면으로 연결
    }
}
