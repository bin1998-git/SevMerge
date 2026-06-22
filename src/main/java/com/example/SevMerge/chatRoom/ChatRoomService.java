package com.example.SevMerge.chatRoom;

import com.example.SevMerge.chatMessage.ChatMessage;
import com.example.SevMerge.chatMessage.ChatMessageRepository;
import com.example.SevMerge.core.exception.ForbiddenException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ChatMessageRepository chatMessageRepository;


    // 채팅방 생성
    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomRequest reqDTO) {

        // 프로젝트 존재 여부 확인
        Project project = projectRepository.findById(reqDTO.getProjectId())
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));

        // 의뢰인 존재 여부 확인
        Member client = memberRepository.findById(reqDTO.getClientId())
                .orElseThrow(() -> new NotFoundException("클라이언트를 찾을 수 없습니다."));

        // 전문가 존재 여부 확인
        Member expert = memberRepository.findById(reqDTO.getExpertId())
                .orElseThrow(() -> new NotFoundException("전문가를 찾을 수 없습니다."));

        // 1. DB 조회
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProjectAndMember(
                reqDTO.getProjectId(), reqDTO.getClientId(), reqDTO.getExpertId()
        );

        // 2. 이미 방이 존재한다면 바로 리턴
        if (existingRoom.isPresent()) {
            return ChatRoomResponse.from(existingRoom.get());
        }

        // 3. 방이 없다면 아래 코드가 실행되어 새로 만듭니다.
        ChatRoom newChatRoom = ChatRoom.builder()
                .project(project)
                .client(client)
                .expert(expert)
                .build();
        return ChatRoomResponse.from(chatRoomRepository.save(newChatRoom));
    }

    @Transactional(readOnly = true)
    public ChatRoomResponse getRoom(Long roomId, Member sessionMember) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));

        // 권한 검증: 해당 채팅방의 client 또는 expert만 접근 가능
        if (!room.getClient().getId().equals(sessionMember.getId()) && !room.getExpert().getId().equals(sessionMember.getId())) {
            throw new ForbiddenException("접근 권한이 없습니다.");
        }
        return ChatRoomResponse.from(room);
    }

    // 전문가 프로필 문의 (의뢰인 → 전문가, project 없음)
    @Transactional
    public Long getOrCreateProfileRoom(Member client, Long expertId) {
        Member expert = memberRepository.findById(expertId)
                .orElseThrow(() -> new NotFoundException("전문가를 찾을 수 없습니다."));
        return chatRoomRepository.findProfileRoom(client.getId(), expert.getId())
                .map(ChatRoom::getId)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder().client(client).expert(expert).project(null).build()).getId());
    }

    // 프로젝트 문의 (양방향). 상대 회원 id를 받아 역할로 client/expert 판별
    @Transactional
    public Long getOrCreateProjectRoom(Long projectId, Member member, Long withMemberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
        Member other = memberRepository.findById(withMemberId)
                .orElseThrow(() -> new NotFoundException("상대방을 찾을 수 없습니다."));
        Member client = member.isClient() ? member : other;
        Member expert = member.isExpert() ? member : other;
        return chatRoomRepository.findByProjectAndMember(projectId, client.getId(), expert.getId())
                .map(ChatRoom::getId)
                .orElseGet(() -> chatRoomRepository.save(
                        ChatRoom.builder()
                                .project(project)
                                .client(client)
                                .expert(expert)
                                .build()).getId());
    }

    // 내가 참여중인 채팅방 목록 (표시명 = 프로젝트명 또는 상대 이름)
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> findMyRooms(Member member) {
        return chatRoomRepository.findAllByMember(member).stream()
                .map(room -> {
                    ChatRoomResponse resDTO = ChatRoomResponse.from(room);
                    if (resDTO.getProjectTitle() != null) {
                        resDTO.setDisplayName(resDTO.getProjectTitle());
                    } else {
                        resDTO.setDisplayName(member.getId().equals(resDTO.getClientId())
                                ? resDTO.getExpertName()    // 내가 의뢰인 → 상대 전문가
                                : resDTO.getClientName());  // 내가 전문가 → 상대 의뢰인
                    }
                    // 마지막 메시지 미리보기 + 안 읽은 수
                    ChatMessage last = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(room.getId());
                    resDTO.setLastMessage(last != null ? last.getText() : "");
                    long unread = chatMessageRepository.countUnread(room.getId(), member.getId());
                    resDTO.setUnreadCount(unread);
                    resDTO.setHasUnread(unread > 0);
                    return resDTO;
                })
                .toList();
    }

    // 채팅방 삭제 (나에게서). 양쪽 다 삭제되면 방 + 메시지 하드 딜리트
    @Transactional
    public void deleteRoom(Long roomId, Member member) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
        if (!room.validate(member)) {
            throw new ForbiddenException("권한이 없습니다.");
        }
        room.deleteFor(member);
        if (room.isFullyDeleted()) {
            chatMessageRepository.deleteByRoomId(roomId);
            chatRoomRepository.delete(room);
        }
    }

}
