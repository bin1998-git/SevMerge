package com.example.SevMerge.comment;

import com.example.SevMerge.board.BoardRepository;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    // 댓글 목록 조회


}
