package com.example.SevMerge.bookmark;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookMarkController {

    private final BookMarkService bookMarkService;


    @PostMapping("/bookmarks/toggle")
    public String toggleBookmark(){


        return "redirect:/bookmarks";
    }


    @PostMapping("/bookmarks/{expertId}")
    public String deleteBookMark(@PathVariable(name = "expertId") Long expertId,
                                 HttpSession session
                                 ) {

        Member member = (Member) session.getAttribute(Define.SESSION_USER);
        if(member == null) {
            throw new BadRequestException("로그인 먼저 해주세요");
        }
        bookMarkService.delete(expertId,member.getId());
        return "redirect:/my-pages?tab=bookmarks";
    }

}
