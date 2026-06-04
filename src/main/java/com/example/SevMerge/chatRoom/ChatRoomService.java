//package com.example.SevMerge.chatRoom;
//
//import com.example.SevMerge.core.exception.ForbiddenException;
//import com.example.SevMerge.core.exception.NotFoundException;
//import com.example.SevMerge.member.Member;
//import com.example.SevMerge.member.MemberRepository;
//import com.example.SevMerge.project.Project;
//import com.example.SevMerge.project.ProjectRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ChatRoomService {
//
//    private final ChatRoomRepository chatRoomRepository;
//    private final MemberRepository memberRepository;
//    private final ProjectRepository projectRepository;
//
//
//    // 채팅방 생성
//    @Transactional
//    public ChatRoomResponse createChatRoom(ChatRoomRequest reqDTO) {
//
//        // 프로젝트 존재 여부 확인
//        Project project = projectRepository.findById(reqDTO.getProjectId())
//                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
//
//        // 의뢰인 존재 여부 확인
//        Member client = memberRepository.findById(reqDTO.getClientId())
//                .orElseThrow(() -> new NotFoundException("클라이언트를 찾을 수 없습니다."));
//
//        // 전문가 존재 여부 확인
//        Member expert = memberRepository.findById(reqDTO.getExpertId())
//                .orElseThrow(() -> new NotFoundException("전문가를 찾을 수 없습니다."));
//
//        // 1. DB 조회
//        Optional<ChatRoom> existingRoom = chatRoomRepository.findByProjectAndMember(
//                reqDTO.getProjectId(), reqDTO.getClientId(), reqDTO.getExpertId()
//        );
//
//        // 2. 이미 방이 존재한다면 바로 리턴
//        if (existingRoom.isPresent()) {
//            return ChatRoomResponse.from(existingRoom.get());
//        }
//
//        // 3. 방이 없다면 아래 코드가 실행되어 새로 만듭니다.
//        ChatRoom newChatRoom = ChatRoom.builder()
//                .project(project)
//                .client(client)
//                .expert(expert)
//                .build();
//
//        return ChatRoomResponse.from(chatRoomRepository.save(newChatRoom));
//
//    }
//
//    @Transactional(readOnly = true)
//    public ChatRoomResponse getRoom(Long roomId, Member sessionMember) {
//        ChatRoom room = chatRoomRepository.findById(roomId)
//                .orElseThrow(() -> new NotFoundException("채팅방을 찾을 수 없습니다."));
//
//        // 권한 검증: 해당 채팅방의 client 또는 expert만 접근 가능
//        if (!room.getClient().getId().equals(sessionMember.getId()) && !room.getExpert().getId().equals(sessionMember.getId())) {
//            throw new ForbiddenException("접근 권한이 없습니다.");
//        }
//
//        return ChatRoomResponse.from(room);
//    }
//
//
//}
