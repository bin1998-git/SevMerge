package com.example.SevMerge.board;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.core.util.FileUtil;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // ── 파일 저장 유틸 ──────────────────────────────────────────────
    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            String savedName = FileUtil.saveFile(file, FileUtil.IMAGES_DIR);
            return "/images/" + savedName;
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new BadRequestException("파일 저장에 실패했습니다.");
        }
    }

    // ── 조회 ────────────────────────────────────────────────────────
    public Page<BoardResponse.ListDTO> findAllByBoardType(BoardType boardType, String keyword, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());
        return boardRepository.findAllByBoardTypeAndKeyword(boardType, keyword, pageable)
                .map(BoardResponse.ListDTO::new);
    }

    public List<BoardResponse.ListDTO> findAllByMyBoard(Long memberId) {
        return boardRepository.findByMyBoard(memberId).stream()
                .map(BoardResponse.ListDTO::new).toList();
    }

    public List<BoardResponse.ListDTO> findAllInquiry(BoardType boardType, Member member, String keyword) {
        memberRepository.findById(member.getId())
                .orElseThrow(() -> new NotFoundException("멤버를 찾을 수 없습니다."));
        if (member.getRole().equals(Role.ADMIN)) {
            List<Board> boards = (keyword == null || keyword.trim().isEmpty())
                    ? boardRepository.findAllByBoardTypeIsActive(boardType)
                    : boardRepository.findAllByBoardTypeAndKeyword(boardType, keyword);
            return boards.stream().map(BoardResponse.ListDTO::new).toList();
        }
        return boardRepository.findInquiryByBoardTypeWithMemberIdAndIsActive(boardType, member.getId()).stream()
                .map(BoardResponse.ListDTO::new).toList();
    }

    public BoardResponse.DetailDTO detailBoard(Long boardId) {
        Board board = boardRepository.findByIdWithMember(boardId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        return new BoardResponse.DetailDTO(board);
    }

    // ── 저장 ────────────────────────────────────────────────────────
    @Transactional
    public void saveBoard(Member member, BoardRequest.SaveBoardDTO dto) {
        Member memberEntity = memberRepository.findById(member.getId())
                .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다."));

        String url = saveFile(dto.getAttachmentFile());
        String name = (dto.getAttachmentFile() != null && !dto.getAttachmentFile().isEmpty())
                ? dto.getAttachmentFile().getOriginalFilename() : null;

        boardRepository.save(dto.toEntity(memberEntity, url, name));
    }

    // ── 수정 ────────────────────────────────────────────────────────
    @Transactional
    public void updateBoard(Long boardId, BoardRequest.UpdateBoardDTO dto,
                            Long memberId, MultipartFile file) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        if (!board.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("수정 권한이 없습니다.");
        }
        dto.validate();

        // 새 파일이 올라온 경우에만 교체
        if (file != null && !file.isEmpty()) {
            dto.setAttachmentUrl(saveFile(file));
            dto.setAttachmentName(file.getOriginalFilename());
        }
        board.update(dto);
        boardRepository.save(board);
    }

    // ── 삭제 ────────────────────────────────────────────────────────
    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = boardRepository.findByIdWithMember(boardId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        if (!board.getMember().getId().equals(memberId)) {
            throw new UnauthorizedException("삭제 권한이 없습니다.");
        }
        board.softDelete();
    }

    // ── 관리자 ──────────────────────────────────────────────────────
    public List<BoardResponse.ListDTO> getAdminBoardsByType(BoardType boardType, String keyword) {
        List<Board> boards = (keyword == null || keyword.trim().isEmpty())
                ? boardRepository.findAllByBoardTypeIsActive(boardType)
                : boardRepository.findAllByBoardTypeAndKeyword(boardType, keyword);
        return boards.stream().map(BoardResponse.ListDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public void deleteBoardByAdmin(Long boardId) {
        boardRepository.findByIdWithMember(boardId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다.")).softDelete();
    }

    @Transactional
    public void createNotice(String title, String content, Long memberId) {
        Member admin = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다. id=" + memberId));
        boardRepository.save(new Board(null, BoardType.NOTICE, title, content, 0, null, admin, true, null, null));
    }

    @Transactional
    public void updateNotice(Long id, BoardRequest.UpdateBoardDTO dto) {
        boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id)).update(dto);
    }

    @Transactional
    public void deleteNotice(Long id) {
        boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id)).softDelete();
    }

    @Transactional
    public void increaseViewCount(Long boardId) {
        boardRepository.increaseViewCount(boardId);
    }
}