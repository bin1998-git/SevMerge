package com.example.SevMerge.bookmark;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;

    @ResponseBody
    @PostMapping("/bookmarks/toggle/{expertId}")
    public Map<String, Boolean> toggleBookmark(@PathVariable(name = "expertId") Long expertId, HttpSession session){

        SessionUser sessionMember = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if(sessionMember == null){
            throw new BadRequestException("로그인 먼저 해주세요");
        }

        boolean bookmarked  = bookMarkService.toggle(sessionMember.getId(),expertId);

        return Map.of("bookmarked", bookmarked); // 자바 스크립트 then 쪽에 데이터로 전달
    }


    @PostMapping("/bookmarks/{expertId}")
    public String deleteBookMark(@PathVariable(name = "expertId") Long expertId,
                                 HttpSession session
                                 ) {

        SessionUser member = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if(member == null) {
            throw new BadRequestException("로그인 먼저 해주세요");
        }
        bookMarkService.delete(expertId,member.getId());
        return "redirect:/my-pages?tab=bookmarks";
    }



}
