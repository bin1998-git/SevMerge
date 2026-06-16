package com.example.SevMerge.message;

import com.example.SevMerge.core.util.MyDateUtil;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.project.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class MessageResponse {

    // 쪽지 목록
    @Getter
    @Setter
    public static class ListDTO {
        private Long id;
        private String senderName;
        private String receiverName;
        private String title;
        private Boolean isRead;
        private String createdAt;

        public ListDTO(Message message) {
            this.id = message.getId();
            this.senderName = message.getSender().getName();
            this.receiverName = message.getReceiver().getName();
            this.title = message.getTitle();
            this.isRead = message.getIsRead();
            this.createdAt = MyDateUtil.timestampFormat(message.getCreatedAt());
        }

        public boolean isRead() {
            return Boolean.TRUE.equals(this.isRead);
        }

        public boolean isUnread() {
            return !Boolean.TRUE.equals(this.isRead);
        }
    }


    // 쪽지 상세
    @Data
    public static class DetailDTO {
        private Long id;
        private Long senderId;
        private String senderName;
        private String receiverName;
        private Long projectId;
        private String projectTitle;  // project nullable이니 String으로
        private String title;
        private String content;
        private Boolean isRead;
        private String createdAt;
        public DetailDTO(Message message) {
            this.id = message.getId();
            this.senderId = message.getSender().getId();
            this.senderName = message.getSender().getName();
            this.receiverName = message.getReceiver().getName();
            this.projectId = message.getProject() != null ? message.getProject().getId() : null;
            this.projectTitle = message.getProject() != null ? message.getProject().getTitle() : null;
            this.title = message.getTitle();
            this.content = message.getContent();
            this.isRead = message.getIsRead();
            this.createdAt = MyDateUtil.timestampFormat(message.getCreatedAt());
        }
    }

    public static class SendFormDTO {
        private Long receiverId;
        private String receiverName;
        private Role receiverRole;
        private Long projectId;
        private String projectTitle;

        public SendFormDTO(Member receiver, Project project) {
            this.receiverId = receiver.getId();
            this.receiverName = receiver.getName();
            this.receiverRole = receiver.getRole();
            this.projectId = project.getId() != null ? project.getId() : null;
            this.projectTitle = project.getTitle() != null ? project.getTitle() : null;
        }
    }

    // 입찰한 프로젝트 보여주기
    @Data
    @AllArgsConstructor
    public static class ContactDTO {
        private Long receiverId;
        private String receiverName;
        private Long projectId;
        private String projectTitle;
        private boolean selected;

        public static ContactDTO from(Member receiver, Project project, boolean isSelected) {
            return new ContactDTO(
                    receiver.getId(),
                    receiver.getName(),
                    project.getId(),
                    project.getTitle(),
                    isSelected
            );
        }
    }

}
