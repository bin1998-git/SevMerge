package com.example.SevMerge.admin;

import com.example.SevMerge.Report.BlackList;
import com.example.SevMerge.Report.BlacklistRepository;
import com.example.SevMerge.Report.ReportService;
import com.example.SevMerge.adbid.AdBidService;
import com.example.SevMerge.advertisement.AdvertisementService;
import com.example.SevMerge.board.*;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.expertprofile.ExpertProfileResponse;
import com.example.SevMerge.member.*;
import com.example.SevMerge.partnership.PartnerShipService;
import com.example.SevMerge.project.ProjectResponseDTO;
import com.example.SevMerge.project.ProjectService;
import com.example.SevMerge.revenue.PlatformRevenueService;
import com.example.SevMerge.withdrawal.WithdrawalService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.SevMerge.member.SessionUser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;
    private final ProjectService projectService;
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final BlacklistRepository blacklistRepository;
    private final ReportService reportService;
    private final PartnerShipService partnerShipService;
    private final WithdrawalService withdrawalService;
    private final AdvertisementService advertisementService;
    private final PlatformRevenueService platformRevenueService;
    private final AdBidService adBidService;

    @GetMapping("/admin/main")
    public String dashboardPage(HttpSession session, Model model,
                                @RequestParam(value = "startDate", required = false) String startDate,
                                @RequestParam(value = "endDate", required = false) String endDate,
                                @RequestParam(value = "roleFilter", defaultValue = "ALL") String roleFilter,
                                @RequestParam(value = "projectType", required = false) String projectType,
                                @RequestParam(value = "statusFilter", defaultValue = "ALL") String statusFilter,
                                @RequestParam(value = "adRevenue", required = false) String adRevenue) {

        long newMemberCount = memberService.getNewMemberCountThisMonth();

        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser);

        model.addAttribute("stats", memberService.getAllMembers());
        model.addAttribute("newMemberCount", newMemberCount);
        model.addAttribute("projectCount", projectService.getTotalProjectCount());
        model.addAttribute("activeProjectCount", projectService.getActiveProjectsCount());
        model.addAttribute("completedCount", projectService.getDoneProjectsCount());
        model.addAttribute("newCompletedCount", projectService.getMonthDoneProjectsCount());
        model.addAttribute("pendingExpertCount", memberService.getPendingExpertCount());

        // 광고 수익 통계
        model.addAttribute("totalAdRevenue",
                String.format("%,d", advertisementService.getTotalRevenue()));
        model.addAttribute("thisMonthAdRevenue",
                String.format("%,d", advertisementService.getThisMonthRevenue()));

        String defaultStartDate = (startDate != null && !startDate.isEmpty()) ?
                startDate : LocalDate.now().minusDays(6).toString();
        String defaultEndDate = (endDate != null && !endDate.isEmpty()) ?
                endDate : LocalDate.now().toString();

        LocalDate parsedStartDate = LocalDate.parse(defaultStartDate);
        LocalDate parsedEndDate = LocalDate.parse(defaultEndDate);

        List<Integer> adRevenueData = advertisementService.getRevenueTrendByPeriod(parsedStartDate, parsedEndDate);
        model.addAttribute("adRevenueData", adRevenueData);

        String memberChartName = "ALL".equalsIgnoreCase(roleFilter) ? "총 회원수" :
                ("CLIENT".equalsIgnoreCase(roleFilter) ? "일반 회원수" : "전문가 회원수");
        String statusChartName = "IN_PROGRESS".equalsIgnoreCase(statusFilter) ? "진행중 프로젝트 수" :
                ("COMPLETED".equalsIgnoreCase(statusFilter) ? "완료 프로젝트수" : "취소 프로젝트수");

        model.addAttribute("startDate", defaultStartDate);
        model.addAttribute("endDate", defaultEndDate);
        model.addAttribute("roleFilter", roleFilter);
        model.addAttribute("memberChartName", memberChartName);
        model.addAttribute("projectType", projectType);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("statusChartName", statusChartName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        long daysBetween = ChronoUnit.DAYS.between(parsedStartDate, parsedEndDate);

        List<String> chartLabels = IntStream.rangeClosed(0, (int) daysBetween)
                .mapToObj(i -> parsedStartDate.plusDays(i).format(formatter))
                .toList();

        List<Integer> memberData = new ArrayList<>();
        List<Integer> projectData = new ArrayList<>();
        List<Integer> completeData = new ArrayList<>();
        List<Integer> emptyZeroList = chartLabels.stream().map(i -> 0).toList();

        // adRevenue 체크를 맨 앞으로
        if ("true".equals(adRevenue)) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", false);
            model.addAttribute("isAdRevenue", true);
            memberData = emptyZeroList;
            projectData = emptyZeroList;
            completeData = emptyZeroList;
        } else if (projectType != null && !projectType.isEmpty()) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", projectType);
            model.addAttribute("isStatusFilterActive", false);
            model.addAttribute("isAdRevenue", false);
            projectData = projectService.getProjectCountByPeriodAndType(parsedStartDate, parsedEndDate, projectType);
            memberData = emptyZeroList;
            completeData = emptyZeroList;
        } else if (!"ALL".equalsIgnoreCase(roleFilter)) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", false);
            model.addAttribute("isAdRevenue", false);
            memberData = memberService.getMemberTrendByRoleAndPeriod(roleFilter, parsedStartDate, parsedEndDate);
            projectData = emptyZeroList;
            completeData = emptyZeroList;
        } else if (!"ALL".equalsIgnoreCase(statusFilter)) {
            model.addAttribute("isStatusAll", false);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", true);
            model.addAttribute("isAdRevenue", false);
            completeData = projectService.getProjectTrendByStatusAndPeriod(statusFilter, parsedStartDate, parsedEndDate);
            memberData = emptyZeroList;
            projectData = emptyZeroList;
        } else {
            model.addAttribute("isStatusAll", true);
            model.addAttribute("selectedProjectType", null);
            model.addAttribute("isStatusFilterActive", false);
            model.addAttribute("isAdRevenue", false);
            memberData = memberService.getMemberTrendByPeriod(parsedStartDate, parsedEndDate);
            projectData = projectService.getProjectTrendByPeriod(parsedStartDate, parsedEndDate);
            completeData = projectService.getCompletedTrendByPeriod(parsedStartDate, parsedEndDate);
        }

        model.addAttribute("chartLabels", chartLabels != null ? chartLabels : new ArrayList<>());
        model.addAttribute("memberData", memberData != null ? memberData : emptyZeroList);
        model.addAttribute("projectData", projectData != null ? projectData : emptyZeroList);
        model.addAttribute("completedData", completeData != null ? completeData : emptyZeroList);
        model.addAttribute("recentPartnerships", partnerShipService.list());

        List<MemberResponse> recentMembers = memberService.getRecentMembers();
        model.addAttribute("recentMembers", recentMembers);

        List<ProjectResponseDTO.ListDTO> recentProjects = projectService.getRecentProjects();
        model.addAttribute("recentProjects", recentProjects);
        return "admin/admin-main";
    }

    // 관리자 공지사항 관리
    @GetMapping("/admin/notices")
    public String adminNotices(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               Model model, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        model.addAttribute("isAdmin", sessionUser != null && sessionUser.getRole() == Role.ADMIN);

        List<BoardResponse.ListDTO> all = boardService.getAdminBoardsByType(BoardType.NOTICE, keyword);
        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("boards", s < total ? all.subList(s, e) : new ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);

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
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
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
    public String adminExpertPage(@RequestParam(value = "status", required = false) String status,
                                  @RequestParam(defaultValue = "1") int page, Model model) {

        List<ExpertProfileResponse> all;
        if ("PENDING".equals(status)) {
            all = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isPending", true);
        } else if ("APPROVED".equals(status)) {
            all = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.ACTIVE);
            model.addAttribute("isApproved", true);
        } else if ("REJECTED".equals(status)) {
            all = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.REJECTED);
            model.addAttribute("isRejected", true);
        } else {
            all = memberService.getExpertProfilesByStatus(com.example.SevMerge.member.Status.PENDING);
            model.addAttribute("isAll", true);
        }

        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("experts", s < total ? all.subList(s, e) : new ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("currentStatus", status != null ? status : "");
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
    public String blacklistPage(@RequestParam(value = "keyword", required = false) String keyword,
                                @RequestParam(defaultValue = "1") int page, Model model) {
        List<BlackList> all = blacklistRepository.findAllWithMemberOrderByIdDesc();
        List<BlackList> filtered = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            for (BlackList bl : all) {
                if (bl.getReason().contains(keyword) || bl.getMember().getName().contains(keyword)) filtered.add(bl);
            }
        } else {
            filtered = all;
        }

        int ps = 15, total = filtered.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("blacklistLogs", s < total ? filtered.subList(s, e) : new ArrayList<>());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/admin-blacklist";
    }

    // 블랙리스트 차단회원 정지해제
    @PostMapping("/admin/blacklist/release/{memberId}")
    public String releaseBlacklistMember(@PathVariable(name = "memberId") Long memberId,
                                         HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null || sessionUser.getRole() != Role.ADMIN) {
            return "redirect:/login";
        }

        reportService.releaseMember(memberId);
        return "redirect:/admin/blacklists";


    }

    // 출금요청 관리 페이지
    @GetMapping("/admin/experts/withdraw")
    public String adminExpertWithdraw(@RequestParam(value = "status", required = false) String status,
                                      @RequestParam(defaultValue = "1") int page, Model model) {
        List<WithdrawalService.AdminWithdrawalDTO> all = withdrawalService.getAllForAdmin(status);
        long pendingCount = all.stream().filter(WithdrawalService.AdminWithdrawalDTO::isPending).count();

        int ps = 15, total = all.size(), tp = Math.max(1, (int) Math.ceil((double) total / ps));
        int s = (page - 1) * ps, e = Math.min(s + ps, total);
        model.addAttribute("withdrawals", s < total ? all.subList(s, e) : new ArrayList<>());
        model.addAttribute("totalCount", total);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tp);
        model.addAttribute("prevPage", page > 1 ? page - 1 : null);
        model.addAttribute("nextPage", page < tp ? page + 1 : null);
        model.addAttribute("currentStatus", status != null ? status : "");
        model.addAttribute("isWithdrawAll", status == null);
        model.addAttribute("isWithdrawPending", "PENDING".equals(status));
        model.addAttribute("isWithdrawCompleted", "COMPLETED".equals(status));
        model.addAttribute("isWithdrawRejected", "REJECTED".equals(status));
        return "admin/admin-withdraw";
    }

    // 출금 승인/반려 API
    @ResponseBody
    @PatchMapping("/api/admin/withdraw/{id}")
    public ResponseEntity<?> processWithdraw(@PathVariable Long id,
                                             @RequestBody Map<String, String> body) {
        String action = body.get("action");
        withdrawalService.processWithdrawal(id, action);
        return ResponseEntity.ok().build();
    }

    // 광고 승인 관리 페이지
    @GetMapping("/admin/advertisements")
    public String adminAdvertisementPage(Model model) {
        model.addAttribute("pendingAds", advertisementService.getPendingAds());
        model.addAttribute("pendingCount", advertisementService.getPendingAds().size());
        model.addAttribute("processedAds", advertisementService.getProcessedAds());

        // 경매 배너
        List<com.example.SevMerge.adbid.AdBid> pendingBanners = adBidService.getPendingReviewBids();
        List<com.example.SevMerge.adbid.AdBid> processedBanners = adBidService.getProcessedReviewBids();
        model.addAttribute("pendingBanners", pendingBanners);
        model.addAttribute("hasPendingBanners", !pendingBanners.isEmpty());
        model.addAttribute("processedBanners", processedBanners);
        model.addAttribute("hasProcessedBanners", !processedBanners.isEmpty());
        return "admin/admin-advertisement";
    }

    // 광고 승인
    @ResponseBody
    @PatchMapping("/api/admin/advertisements/{adId}/approve")
    public ResponseEntity<?> approveAd(@PathVariable Long adId) {
        advertisementService.approveAd(adId);
        return ResponseEntity.ok().build();
    }

    // 광고 거절
    @ResponseBody
    @PatchMapping("/api/admin/advertisements/{adId}/reject")
    public ResponseEntity<?> rejectAd(@PathVariable Long adId,
                                      @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "");
        advertisementService.rejectAd(adId,reason);
        return ResponseEntity.ok().build();
    }

    // 수익 현황 페이지
    @GetMapping("/admin/revenue")
    public String revenuePage(Model model) {
        model.addAttribute("totalRevenue",
                String.format("%,d", platformRevenueService.getTotalRevenue()));
        model.addAttribute("adRevenue",
                String.format("%,d", platformRevenueService.getRevenueByType(
                        com.example.SevMerge.revenue.PlatformRevenueType.AD)));
        model.addAttribute("thisMonthRevenue",
                String.format("%,d", platformRevenueService.getThisMonthRevenue()));
        model.addAttribute("revenues", platformRevenueService.getAllRevenues());
        return "admin/admin-revenue";
    }

    @GetMapping("/admin/experts/grade")
    public String adminExpertGrade() {
        return "admin/admin-expert"; // 일단 기존 화면으로 연결
    }
}
