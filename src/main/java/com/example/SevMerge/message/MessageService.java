package com.example.SevMerge.message;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.bid.BidStatus;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.core.exception.UnauthorizedException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final BidRepository bidRepository;

    // 쪽지함 리스트 페이징 처리 조회
    public Page<MessageResponse.ListDTO> findMessages(Member member, String box, int page) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), 10, Sort.by("createdAt").descending());

        Page<Message> messagePage = box.equals("sent")
                ? messageRepository.findAllSentMessagesByPages(member, pageable)
                : messageRepository.findAllReceivedMessagesByPages(member, pageable);

        return messagePage.map(MessageResponse.ListDTO::new);
    }

    // 메세지 상세 조회
    @Transactional
    public MessageResponse.DetailDTO findMessageByIdWithDetails(@Param("id") Long id, Member sessionMember) {
        Message findMessage = messageRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new NotFoundException("쪽지를 찾을 수 없습니다."));

        boolean isSender = findMessage.getSender().getId().equals(sessionMember.getId());
        boolean isReceiver = findMessage.getReceiver().getId().equals(sessionMember.getId());

        // 메세지를 읽는 사람이 보낸사람과 받은 사람 둘 다 아니라면 예외 처리
        if (!isSender && !isReceiver) {
            throw new UnauthorizedException("본인의 쪽지만 조회할 수 있습니다.");
        }

        // 메세지를 조회 했을 때 받은 사람이 세션멤버이고 메세지를 읽은 상태가 아니라면 메세지를 읽음 상태로 변경
        if (isReceiver && !findMessage.getIsRead()) {
            findMessage.read();
        }

        return new MessageResponse.DetailDTO(findMessage);
    }

    public List<MessageResponse.ContactDTO> findContacts(Member member) {
        if (member.isClient()) {
            return bidRepository.findByProjectMemberId(member.getId())
                    .stream()
                    .map(b -> MessageResponse.ContactDTO
                            .from(b.getExpert(), b.getProject(), b.getStatus() == BidStatus.SELECTED)).toList();
        } else if (member.isExpert()) {
            return bidRepository.findByExpertId(member.getId())
                    .stream()
                    .map(b -> MessageResponse.ContactDTO
                            .from(b.getProject().getMember(), b.getProject(), b.getStatus() == BidStatus.SELECTED)).toList();
        }
        return List.of();
    }

    public void sendMessage(Member sender, MessageRequest.SendDTO reqDTO) {
        Member receiver = memberRepository.findById(reqDTO.getReceiverId())
                .orElseThrow(() -> new NotFoundException("수신자를 찾을 수 없습니다."));

        if (!bidRepository.existsBidRelation(sender.getId(), receiver.getId())) {
            throw new UnauthorizedException("입찰 관계가 있는 상대에게만 쪽지를 보낼 수 있습니다.");
        }

        Project project = reqDTO.getProjectId() != null
                ? projectRepository.findById(reqDTO.getProjectId()).orElse(null)
                : null;
        messageRepository.save(Message
                .builder()
                .sender(sender)
                .receiver(receiver)
                .project(project)
                .title(reqDTO.getTitle())
                .content(reqDTO.getContent())
                .build());
    }



}
