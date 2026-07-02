# SevMerge - IT 외주 매칭 플랫폼

IT 프로젝트를 진행하고 싶은 의뢰인과, 그 프로젝트를 수행할 전문가를 연결해주는 외주 매칭 플랫폼입니다.
단순히 프로필과 연락처를 나열하는 수준을 넘어, 프로젝트 등록부터 제안서 제출, 계약, 에스크로 결제, 작업 진행, 정산, 리뷰까지 거래의 전 과정을 하나의 서비스 안에서 처리할 수 있도록 설계했습니다.
실시간 채팅과 알림으로 의뢰인과 전문가가 원활하게 소통할 수 있게 했고, AI 의뢰 작성 도우미를 붙여 프로젝트 설명을 작성하는 진입장벽도 낮췄습니다.

## 프로젝트 개요

| 항목 | 내용 |
|---|---|
| 프로젝트 유형 | 개인 프로젝트 (백엔드 중심 풀스택) |
| 개발 기간 | 2026.05 ~ 2026.06 (약 4주) |
| 핵심 가치 | 안전한 외주 거래 · 에스크로 결제 · 실시간 소통 · AI 작성 보조 |
| 아키텍처 | Spring Boot 모놀리식 + Mustache 서버사이드 렌더링(SSR) |
| Repository | https://github.com/bin1998-git/SevMerge |

### 해결하고자 하는 문제

기존 외주 플랫폼에서는 다음과 같은 문제가 발생합니다.

- 의뢰인이 수많은 전문가를 직접 탐색·연락해야 하는 비효율적 매칭 구조
- 선불 결제 후 작업 미완료 시 환불이 어려운 거래 신뢰 문제
- 신규 전문가의 실력 파악이 어려운 정보 비대칭 문제

SevMerge는 프로젝트 등록, 제안서 비교, 계약, 결제, 작업 진행, 정산까지 이어지는 흐름을 하나의 시스템으로 통합하고, 플랫폼이 대금을 보관했다가 작업 완료 시 정산하는 에스크로 구조를 통해 위 문제를 해결합니다.

### 제공하는 가치

- **의뢰인**: 제안서 비교를 통한 합리적 전문가 선택, 에스크로 결제를 통한 거래 안심
- **전문가**: 등급 인증을 통한 실력 어필, 광고 입찰을 통한 노출 확대
- **플랫폼**: 수수료 기반 안정적 수익 구조, 통계 기반 운영 관리

## 기술 스택

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security (세션 기반 인증, BCrypt)
- Spring WebSocket (실시간 채팅)
- Spring AI (Gemini AI)
- MySQL
- Gradle

### Frontend
- Mustache (SSR)
- CSS
- JavaScript

### External API
- Toss Payments (잔액 충전 결제, 서버 사이드 승인)
- SolAPI (SMS 인증, 알림 문자 발송)
- Gmail SMTP (이메일 인증, 알림 메일 발송)
- Google Gemini (질의응답)
- Google / Kakao OAuth2 (소셜 로그인)

### Tools
- IntelliJ IDEA
- Git

## 프로젝트 구조

### 도메인 기반 패키지 구조 (Domain-Driven Package Structure)

본 프로젝트는 유지보수성과 확장성을 극대화하기 위해 도메인 기반 패키지 구조를 채택하였습니다.

- 도메인 중심 응집: 각 비즈니스 기능(제안서, 결제, 회원 등)별도 관련 코드를 모아 응집도를 높였습니다.
- 관심사 분리: 기술적 공통 설정(보안로직) 과 비즈니스 로직을 분리하여 코드의 가독성을 확보했습니다.

```
📦com.example.SevMerge
 ┣ 📂Report              # 신고 및 블랙리스트 (댓글 신고 처리, 누적 3회 자동 정지)
 ┣ 📂adbid               # 광고 슬롯 경매 (입찰, 낙찰, 배너 심사)
 ┣ 📂admin               # 관리자 기능 (회원, 신고, 시스템 관리 및 통계그래프)
 ┣ 📂advertisement       # 광고 관리 (플랫폼 내 배너 광고 구매 및 승인)
 ┣ 📂ai                  # AI 연동 기능 (Gemini 기반 Q&A)
 ┣ 📂bid                 # 제안서 시스템 (전문가와 프로젝트 매칭을 위한 제안 기능)
 ┣ 📂board               # 자유/공지/문의 게시판 및 댓글 기능
 ┣ 📂bookmark            # 북마크 (관심 전문가 저장)
 ┣ 📂cancelrequest       # 작업 취소 요청 및 승인 처리
 ┣ 📂charge              # 잔액 충전 (Toss 결제 연동)
 ┣ 📂chatMessage         # 실시간 채팅 메시지 송수신 데이터 처리
 ┣ 📂chatRoom            # 채팅방 생성, 유지 및 목록 관리
 ┣ 📂comment              # 게시글 내 댓글 관리
 ┣ 📂core                # 공통 핵심 로직 (보안 설정, 예외 처리, 글로벌 유틸리티, 인터셉터)
 ┣ 📂deliverable         # 작업 산출물 제출, 수정 요청, 승인
 ┣ 📂expertprofile       # 전문가 프로필 관리 (기술 스택, 경력, 등급 산정)
 ┣ 📂expertwish          # 전문가 찜하기 및 선호 전문가 관리
 ┣ 📂faq                 # 자주 묻는 질문(FAQ) 및 고객 지원 센터
 ┣ 📂footer              # 하단 영역 정보 관리 (약관, 이용정책, 회사소개)
 ┣ 📂member              # 회원 관리 (회원가입, 로그인, 소셜 로그인, 마이페이지)
 ┣ 📂message             # 시스템 쪽지 및 파일 첨부 메시지 기능
 ┣ 📂notification        # 알림 서비스 (SSE 실시간 알림, 문자메시지, 이메일 발송)
 ┣ 📂partnership         # 제휴 및 비즈니스 협력 문의 관리
 ┣ 📂payment             # 에스크로 결제, 정산, 정산 요청 처리
 ┣ 📂portfolio           # 전문가 포트폴리오 등록 및 조회
 ┣ 📂project             # 프로젝트/외주 의뢰 등록 및 매칭 관리
 ┣ 📂refund              # 환불 신청 및 관리자 승인/거절 처리
 ┣ 📂revenue             # 플랫폼 수익 관리 (광고/제휴/수수료 수익 집계)
 ┣ 📂review              # 서비스 이용 후기 및 별점 평점 관리
 ┣ 📂withdrawal          # 전문가 정산금 출금 신청 및 처리
 ┣ 📜main.java            # 메인 페이지 컨트롤러 (엔트리 포인트)
 ┗ 📜SevMergeApplication  # 스프링 부트(Spring Boot) 애플리케이션 메인 실행 클래스
```

리소스 및 빌드 구성은 다음과 같습니다.

```
📦SevMerge
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┗ 📂com
 ┃ ┃ ┃   ┗ 📂example
 ┃ ┃ ┃     ┗ 📂SevMerge          # 위 도메인 패키지
 ┃ ┃ ┣ 📂resources
 ┃ ┃ ┃ ┣ 📂db                    # 초기 데이터 (data.sql)
 ┃ ┃ ┃ ┣ 📂static                # 정적 리소스 (CSS, JS, 이미지)
 ┃ ┃ ┃ ┣ 📂templates             # Mustache 템플릿
 ┃ ┃ ┃ ┗ 📜application*.yml
 ┃ ┗ 📂test
 ┣ 📜.env                        
 ┣ 📜build.gradle
 ┣ 📜gradlew.bat
 ┗ 📜gradlew
```

## 기능 구분

### 의뢰인 (Client)
- 프로젝트 등록 (AI 작성 보조), 프로젝트 수정/삭제/임시저장
- 제안서 비교 및 낙찰/보류/거절 처리
- 에스크로 결제, 잔액 충전 (Toss), 정산 승인
- 작업 취소 요청, 산출물 검수(승인/수정 요청)
- 환불 신청, 리뷰 작성
- 실시간 채팅, 실시간 알림, 쪽지함
- 전문가 북마크, 찜하기

### 전문가 (Expert)
- 관리자 심사를 거친 인증 전문가 활동
- 베이지안 평균 기반 등급 산정 (NORMAL / SKILLED / MASTER)
- 프로젝트 탐색 및 제안서 제출
- 포트폴리오 등록/관리
- 작업 상태 관리(진행중/검토중/완료), 산출물 제출
- 정산금 출금 신청, 광고 슬롯 경매 입찰
- 실시간 채팅, 실시간 알림, 쪽지함

### 관리자 (Admin)
- 대시보드 통계 (매출/수익 현황)
- 회원 관리, 전문가 심사 및 승인
- 신고 처리, 블랙리스트 관리 (누적 3회 자동 정지)
- 환불 신청 승인/거절
- 정산 출금 승인/거절
- 광고 승인, 광고 슬롯 경매 관리
- 공지사항, 1:1 문의, 제휴 문의, FAQ 관리
- 플랫폼 수익 현황 관리

## ERD 및 도메인 모델

<img src="docs/ERD/ERD.img.png" width="800">


## 🗂️ 도메인 모델

| 엔티티 | 테이블명 | 설명 | 핵심 상태 |
|---|---|---|---|
| Member | member_tb | 회원 | role, status, balance |
| ExpertProfile | expert_profile_tb | 전문가 프로필 | isCertified, expertGrade, 베이지안 평균 등급 산정 |
| Project | project_tb | 프로젝트(의뢰) | projectStatus, category, bidFilter |
| Bid | bid_tb | 제안서 | status, workStatus |
| Payment | payment_tb | 에스크로 결제 | status, platformFee |
| EscrowSettlementRequest | escrow_settlement_request_tb | 정산 요청 | status |
| Deliverable | deliverable_tb | 작업 산출물 | status, round |
| CancelRequest | cancel_request_tb | 작업 취소 요청 | status |
| Charge | charge_tb | 잔액 충전 | status |
| Withdrawal | withdrawal_tb | 정산금 출금 | status |
| RefundRequest | refund_application_tb | 환불 신청 | status |
| Review | review_tb | 리뷰 | countStar |
| Notification | notification_tb | 알림 | type(12종), isRead |
| Advertisement | advertisement_tb | 광고 신청 | status, placement |
| AdSlot / AdBid | ad_slot_tb / ad_bid_tb | 광고 슬롯 경매 | AdSlotStatus, AdBidStatus |
| Report / BlackList | comment_report_tb / blacklist_tb | 신고 / 제재 | isProcessed, isActive(누적 3회 자동 정지) |
| PlatformRevenue | platform_revenue_tb | 플랫폼 수익 | type, status |
| Board / Comment | - / comment_tb | 게시판 / 댓글 | boardType, inquiryScope, answerStatus |
| ChatRoom / ChatMessage | chat_room_tb / chat_message_tb | 실시간 채팅 | deletedByClient/Expert, isRead, isDeleted |
| Message | message_tb | 쪽지(파일 첨부) | isRead, isDeletedBySender/Receiver |
| PartnerShip | partner_ship_tb | 제휴 문의 | status, type |

## 기능 시연

## 프로젝트

<table>
<tr>
  <td align="center"><b>검색 다중 필터</b></td>
  <td align="center"><b>작성</b></td>
  <td align="center"><b>수정·삭제</b></td>
</tr>
<tr>
  <td><img src="docs/demo/project/project-search-filter.gif" width="270" alt="이미지"></td>
  <td><img src="docs/demo/project/project-write.gif" width="270"></td>
  <td><img src="docs/demo/project/project-edit-delete.gif" width="270"></td>
</tr>
</table>

<br/>

## 제안서

<table>
<tr>
  <td align="center"><b>작성</b></td>
  <td align="center"><b>목록·수정·삭제</b></td>
  <td align="center"><b>보류 및 낙찰</b></td>
</tr>
<tr>
  <td><img src="docs/demo/bid/bid-write.gif" width="270"></td>
  <td><img src="docs/demo/bid/bid-list-edit-delete.gif" width="270"></td>
  <td><img src="docs/demo/bid/bid-hold-and-win.gif" width="270"></td>
</tr>
</table>

<br/>

## 포트폴리오

<table>
<tr>
  <td align="center"><b>생성</b></td>
  <td align="center"><b>수정·삭제</b></td>
</tr>
<tr>
  <td><img src="docs/demo/portfolio/portfolio-create.gif" width="380"></td>
  <td><img src="docs/demo/portfolio/portfolio-edit-delete.gif" width="380"></td>
</tr>
</table>

<br/>

## 게시글

<table>
<tr>
  <td align="center"><b>목록</b></td>
  <td align="center"><b>작성</b></td>
  <td align="center"><b>상세보기·수정·삭제</b></td>
</tr>
<tr>
  <td><img src="docs/demo/board/board-list.gif" width="270"></td>
  <td><img src="docs/demo/board/board-write.gif" width="270"></td>
  <td><img src="docs/demo/board/board-detail-edit-delete.gif" width="270"></td>
</tr>
</table>

<br/>

## 리뷰

<table>
<tr>
  <td align="center"><b>작성 및 건너뛰기</b></td>
  <td align="center"><b>목록·수정·삭제</b></td>
</tr>
<tr>
  <td><img src="docs/demo/review/review-write-skip.gif" width="380"></td>
  <td><img src="docs/demo/review/review-list-edit-delete.gif" width="380"></td>
</tr>
</table>

<br/>

## 채팅 · 쪽지 · 알림
<table>
<tr>
  <td align="center"><b>채팅</b></td>
  <td align="center"><b>채팅 삭제</b></td>
  <td align="center"><b>쪽지 작성</b></td>
</tr>
<tr>
  <td><img src="docs/demo/chat/chat-socket.gif" width="270"></td>
  <td><img src="docs/demo/chat/chat-delete.gif" width="270"></td>
  <td><img src="docs/demo/message/message-send.gif" width="270"></td>
</tr>
<tr>
  <td align="center"><b>쪽지 받음</b></td>
  <td align="center" colspan="2"><b>실시간 알림</b></td>
</tr>
<tr>
  <td><img src="docs/demo/message/message-receive.gif" width="270"></td>
  <td align="center" colspan="2"><img src="docs/demo/notification/notification.gif" width="270"></td>
</tr>
</table>

<br/>

## 광고

<table>
<tr>
  <td align="center"><b>광고 신청</b></td>
  <td align="center"><b>광고 입찰</b></td>
  <td align="center"><b>광고배너 승인·거절</b></td>
</tr>
<tr>
  <td><img src="docs/demo/ad/ad-apply.gif" width="270"></td>
  <td><img src="docs/demo/ad/ad-bid.gif" width="270"></td>
  <td><img src="docs/demo/ad/ad-banner-approve.gif" width="270"></td>
</tr>
</table>

<br/>

## 관리자

<table>
<tr>
  <td align="center"><b>관리자 차트</b></td>
  <td align="center"><b>전문가 승인·거절</b></td>
  <td align="center"><b>환불 승인·거절</b></td>
</tr>
<tr>
  <td><img src="docs/demo/admin/admin-chart.gif" width="270"></td>
  <td><img src="docs/demo/admin/admin-expert-approve.gif" width="270"></td>
  <td><img src="docs/demo/admin/admin-refund-approve.gif" width="270"></td>
</tr>
</table>

<br/>

## 회원

<table>
<tr>
  <td align="center"><b>회원가입</b></td>
  <td align="center"><b>비밀번호 수정</b></td>
  <td align="center"><b>북마크</b></td>
</tr>
<tr>
  <td><img src="docs/demo/member/member-signup.gif" width="270"></td>
  <td><img src="docs/demo/member/member-password-change.gif" width="270"></td>
  <td><img src="docs/demo/bookmark/bookmark.gif" width="270"></td>
</tr>
<tr>
  <td align="center"><b>전문가 검색</b></td>
  <td align="center"><b>환불 신청</b></td>
</tr>
<tr>
  <td><img src="docs/demo/expert-search/expert-search.gif" width="270"></td>
  <td><img src="docs/demo/refund-request/refund-request.gif" width="270"></td>
</tr>
</table>

<br/>

## 제휴 문의

<table>
<tr>
  <td align="center"><b>제휴 문의</b></td>
</tr>
<tr>
  <td><img src="docs/demo/partnership/partnership.gif" width="270"></td>
</tr>
</table>

<br/>


## 핵심 플로우

- 외주 매칭 + 에스크로 흐름: 프로젝트 등록 → 전문가 제안서 제출 → 의뢰인 낙찰 선택 → 에스크로 결제 → 작업 진행 → 산출물 제출/검수 → 검토 확인 → 정산
- 상태 머신: Bid(PENDING → SELECTED/HOLD/REJECTED), Payment(PAID → SETTLED/REFUNDED), Deliverable(SUBMITTED → REVISION_REQUESTED/APPROVED)

## 기술적 하이라이트

### 1. 에스크로 - 동시성을 고려한 원자적 잔액 처리
낙찰 시 의뢰인 잔액을 에스크로로 묶을 때, 조건부 UPDATE 한 방으로 잔액 검증과 차감을 원자적으로 처리해 동시 요청에서의 이중 차감을 방지합니다.


### 2. 결제 보안 - 서버 사이드 Toss 승인
클라이언트가 아닌 서버에서 Toss confirm API(외부 결제 회사 API)를 직접 호출해 결제 금액 위변조를 방지합니다.

### 3. 전문가 등급 -  평균 별점 으로 왜곡 보정
리뷰가 적은 신규 전문가의 별점 왜곡을 완화하기 위해 리뷰 수 대비 신뢰도 가중치를 적용한 별점 평균을 계산하고, 여기에 작업 완료 점수를 더해 최종 등급 점수를 산정합니다.

### 4. 다단 인터셉터 기반 인가 및 Rate Limiting
Session / Login / Admin / Project / Bid 5종 인터셉터를 계층적으로 적용해 권한과 요청 빈도를 제어합니다. 로그인, 이메일/SMS 인증번호 발송 등 민감 엔드포인트는 IP 기준으로 시간당 요청 횟수를 제한하고 초과 시 일정 시간 잠금 처리합니다.

### 5. 알림의 트랜잭션 커밋 이후 발송
알림을 생성하자마자 바로 SSE로 발송하면, 이후 트랜잭션이 롤백될 경우 존재하지 않는 데이터에 대한 알림이 이미 나가버리는 문제가 생길 수 있습니다. 이를 방지하기 위해 알림 발송 로직을 트랜잭션 커밋이 완료되는 시점까지 미뤄두었습니다. 그 결과 트랜잭션이 성공적으로 커밋된 경우에만 알림이 발송되고, 롤백되면 알림 발송 자체가 실행되지 않아 데이터 정합성을 지킬 수 있습니다.

## 실행 방법

### 사전 요구사항
- JDK 21 이상
- MySQL 8.0 이상
- Gradle 8.x 이상

### 빌드 및 실행

```bash
# 프로젝트 클론
git clone https://github.com/bin1998-git/SevMerge.git
cd SevMerge

# MySQL DB 생성
# CREATE DATABASE sevmerge DEFAULT CHARACTER SET utf8mb4;

# 애플리케이션 실행 (Windows: gradlew.bat bootRun)
./gradlew bootRun
```

실행 후 http://localhost:8080 접속 (초기 구동 시 `db/data.sql` 시드 데이터 자동 적재)

## URL 매핑

### 회원 / 인증

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/` , `/main` | 메인 페이지 | 전체 | - |
| GET/POST | `/join-start`, `/join-role`, `/join` | 회원가입 절차 | 비로그인 | - |
| GET/POST | `/login` | 로그인 | 비로그인 | - |
| POST | `/logout` | 로그아웃 | 로그인 필요 | - |
| GET | `/find-account` | 계정 찾기 화면 | 비로그인 | - |
| POST | `/api/member/find-by-phone`, `/find-email`, `/reset-password` | 계정 찾기/재설정 (API) | 비로그인 | - |
| POST | `/api/email/send`, `/api/email/verify` | 이메일 인증 발송/확인 (API) | 비로그인 | Rate Limit |
| POST | `/api/sms/send`, `/api/sms/verify` | SMS 인증 발송/확인 (API) | 비로그인 | Rate Limit |
| GET | `/my-pages` | 마이페이지 | 로그인 필요 | - |
| PUT | `/my-pages` | 회원정보 수정 (API) | 로그인 필요 | - |
| DELETE | `/my-pages/withdraw` | 회원 탈퇴 (API) | 로그인 필요 | - |
| DELETE | `/my-pages/profile-image` | 프로필 이미지 삭제 (API) | 로그인 필요 | - |
| GET | `/google-redirect`, `/kakao-redirect` | 소셜 로그인 콜백 | 비로그인 | - |
| GET/POST | `/social-role`, `/social-join`, `/social-expert-form` | 소셜 회원가입 절차 | 비로그인 | - |

### 프로젝트 관리

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/projects/save-form` | 프로젝트 등록 폼 | 의뢰인 | - |
| POST | `/projects/save` | 프로젝트 등록 | 의뢰인 | - |
| GET | `/projects` | 프로젝트 목록 조회 | 전체 | 필터/검색/페이징 |
| GET | `/projects/{id}` | 프로젝트 상세 조회 | 전체 | - |
| GET | `/projects/{id}/edit` | 프로젝트 수정 폼 | 의뢰인 | - |
| PUT | `/projects/{id}` | 프로젝트 수정 (API) | 의뢰인 | - |
| DELETE | `/projects/{id}` | 프로젝트 삭제 (API) | 의뢰인 | - |
| POST | `/projects/{id}/done` | 검토 확인 및 정산 승인 | 의뢰인 | - |
| POST | `/projects/{id}/skip-review` | 리뷰 건너뛰기 | 의뢰인 | - |
| POST/GET | `/projects/draft` | 프로젝트 임시저장 저장/조회 (API) | 의뢰인 | - |
| GET | `/admin/projects` | 관리자 프로젝트 목록 | 관리자 | - |
| POST | `/admin/projects/{id}/delete` | 관리자 전용 삭제 | 관리자 | - |

### 제안서(입찰) 관리

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/bids/save-form` | 제안서 등록 폼 | 전문가 | - |
| POST | `/bids` | 제안서 등록 | 전문가 | - |
| GET | `/bids` | 제안서 목록 조회 (의뢰인) | 로그인 필요 | - |
| GET | `/bids/my-list` | 내 제안서 목록 | 전문가 | - |
| GET | `/bids/my-orders` | 주문 관리 | 전문가 | - |
| GET | `/bids/{id}/edit` | 제안서 수정 폼 | 전문가 | - |
| PUT | `/bids/{id}` | 제안서 수정 (API) | 전문가 | - |
| DELETE | `/bids/{id}` | 제안서 취소/삭제 (API) | 전문가 | - |
| POST | `/bids/{id}/select` | 낙찰 처리 (API) | 의뢰인 | - |
| POST | `/bids/{id}/hold` | 제안서 보류 처리 (API) | 의뢰인 | - |
| DELETE | `/bids/{id}/reject` | 제안서 거절 (API) | 의뢰인 | - |
| POST | `/bids/{id}/complete` | 작업 완료 신고 (API) | 전문가 | - |
| PATCH | `/bids/{id}/work-status` | 작업 상태 변경 (API) | 전문가 | - |
| POST | `/bids/{id}/submit-work` | 작업물 파일 제출 (API) | 전문가 | - |

### 작업 산출물 / 취소 요청

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| POST | `/projects/{id}/deliverables` | 작업 산출물 제출 | 전문가 | - |
| POST | `/deliverables/{id}/revision` | 산출물 수정 요청 | 의뢰인 | - |
| POST | `/deliverables/{id}/approve` | 산출물 승인 | 의뢰인 | - |
| POST | `/projects/{id}/cancel-request` | 작업 취소 요청 | 로그인 필요 | - |
| POST | `/cancel-requests/{id}/approve` | 취소 요청 승인 | 로그인 필요 | - |

### 결제 / 충전 / 정산 / 환불

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/payment/form` | 결제(에스크로) 폼 | 의뢰인 | - |
| POST | `/payment/escrow` | 에스크로 결제 생성 | 의뢰인 | - |
| POST | `/payment/{id}/settle` | 정산 처리 (API) | 관리자 | - |
| POST | `/payment/{id}/refund` | 환불 처리 (API) | 관리자 | - |
| POST | `/payment/{paymentId}/request-settle` | 정산 요청 (API) | 전문가 | - |
| GET | `/payment/my` | 내 결제 내역 | 로그인 필요 | - |
| GET | `/payment/project/{projectId}` | 프로젝트별 결제 조회 (API) | 로그인 필요 | - |
| GET | `/charge/form` | 잔액 충전 폼 | 로그인 필요 | - |
| GET | `/charge/success`, `/charge/fail` | 충전 성공/실패 콜백 | 로그인 필요 | Toss 리다이렉트 |
| POST | `/refund-applications` | 환불 신청 | 의뢰인 | - |
| GET | `/refund-applications/form` | 환불 신청 폼 | 의뢰인 | - |
| GET | `/admin/refund-applications` | 환불 신청 목록 | 관리자 | - |
| POST | `/admin/refund-applications/{id}/approve` `/reject` | 환불 승인/거절 (API) | 관리자 | - |
| GET | `/withdrawal/form` | 정산금 출금 신청 폼 | 전문가 | - |
| GET | `/withdrawal/history` | 출금 내역 조회 | 전문가 | - |
| POST | `/withdrawal/request` | 출금 신청 | 전문가 | - |

### 전문가 / 포트폴리오

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/experts` | 전문가 목록 조회 | 전체 | 필터/검색 |
| GET | `/experts/{memberId}` | 전문가 상세 조회 | 전체 | - |
| GET | `/experts/dashboard` | 전문가 대시보드 | 전문가 | - |
| GET/POST | `/experts/my/form` | 전문가 프로필 등록 | 전문가 | - |
| GET/POST | `/experts/my/info-edit` | 전문가 정보 수정 | 전문가 | - |
| POST | `/experts/wish/{expertId}` | 전문가 찜하기 토글 (API) | 로그인 필요 | - |
| POST | `/bookmarks/toggle/{expertId}` | 북마크 토글 (API) | 로그인 필요 | - |
| POST | `/bookmarks/{expertId}` | 북마크 삭제 | 로그인 필요 | - |
| GET | `/portfolios` | 포트폴리오 목록 | 로그인 필요 | - |
| GET | `/portfolios/{portfolioId}` | 포트폴리오 상세 | 로그인 필요 | - |
| GET/POST | `/portfolios/save` | 포트폴리오 등록 | 전문가 | - |
| GET/POST | `/portfolios/{portfolioId}/edit` `/update` | 포트폴리오 수정 | 전문가 | - |
| POST | `/portfolios/{portfolioId}/delete` | 포트폴리오 삭제 | 전문가 | - |

### 리뷰

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/reviews/my` | 내 리뷰 목록 | 로그인 필요 | - |
| GET/POST | `/reviews/save` | 리뷰 작성 | 로그인 필요 | - |
| GET | `/reviews/{reviewId}` | 리뷰 상세 | 로그인 필요 | - |
| GET | `/reviews/{reviewId}/edit` | 리뷰 수정 폼 | 로그인 필요 | - |
| PUT | `/reviews/{reviewId}` | 리뷰 수정 (API) | 로그인 필요 | - |
| POST | `/reviews/{reviewId}/delete` | 리뷰 삭제 | 로그인 필요 | - |

### 채팅 / 쪽지 / 알림

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/chat/room` | 채팅방 목록 | 로그인 필요 | - |
| GET | `/chat/room/{roomId}` | 채팅방 상세 | 로그인 필요 | - |
| POST | `/api/chat/rooms` | 채팅방 생성 (API) | 로그인 필요 | - |
| GET | `/chat/start/expert/{expertId}` `/project/{projectId}` | 채팅 시작 | 로그인 필요 | - |
| POST | `/chat/room/{roomId}/delete` | 채팅방 삭제 | 로그인 필요 | - |
| POST | `/chat/messages/{messageId}/delete` | 채팅 메시지 삭제 (API) | 로그인 필요 | - |
| POST | `/chat/upload` | 채팅 이미지 업로드 (API) | 로그인 필요 | - |
| GET | `/messages` | 쪽지함 목록 | 로그인 필요 | - |
| GET | `/messages/{id}` | 쪽지 상세 | 로그인 필요 | - |
| GET/POST | `/messages/send` | 쪽지 작성/전송 | 로그인 필요 | - |
| POST | `/messages/{id}/delete` | 쪽지 삭제 | 로그인 필요 | - |
| GET | `/messages/files/{messageFilesId}/download` | 쪽지 첨부파일 다운로드 | 로그인 필요 | - |
| GET | `/notifications` | 알림 목록 | 로그인 필요 | - |
| GET | `/notifications/subscribe` | 실시간 알림 구독 (SSE) | 로그인 필요 | - |
| POST | `/notifications/{id}/read` `/read-all` | 알림 읽음 처리 | 로그인 필요 | - |
| POST | `/notifications/{id}/delete` `/delete-all` | 알림 삭제 | 로그인 필요 | - |

### 게시판 / 댓글 / 신고

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/boards` | 게시판 목록 (자유/공지/문의) | 로그인 필요 | - |
| GET | `/boards/{boardId}` | 게시글 상세 | 로그인 필요 | - |
| GET/POST | `/boards/save` | 게시글 작성 | 로그인 필요 | 첨부파일 지원 |
| GET/POST | `/boards/{boardId}/edit` `/update` | 게시글 수정 | 로그인 필요 | - |
| DELETE | `/boards/{boardId}` | 게시글 삭제 (API) | 로그인 필요 | - |
| POST | `/boards/{boardId}/comments/save` | 댓글 작성 | 로그인 필요 | - |
| POST | `/comments/{commentId}/update` `/delete` | 댓글 수정/삭제 | 로그인 필요 | - |
| POST | `/comments/{id}/report` | 댓글 신고 | 로그인 필요 | - |
| GET | `/admin/boards` | 관리자 게시판 목록 | 관리자 | - |
| POST | `/admin/boards/{boardId}/delete` | 관리자 게시글 삭제 | 관리자 | - |
| GET/POST | `/admin/notices/write` | 공지 작성 | 관리자 | - |
| GET | `/admin/inquiry` | 1:1 문의 관리 | 관리자 | - |
| GET | `/admin/comments` | 댓글 관리 | 관리자 | - |
| GET | `/admin/reports` | 신고 목록 관리 | 관리자 | - |
| POST | `/admin/reports/{commentId}/delete` `/{id}/reject` | 신고 처리 | 관리자 | - |

### 광고

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET/POST | `/advertisements/form` `/purchase` | 광고 구매 | 전문가 | - |
| GET | `/advertisements/my` | 내 광고 목록 | 전문가 | - |
| GET | `/ad-auction` | 광고 슬롯 경매 페이지 | 전문가 | - |
| GET | `/ad-auction/my-bids` | 내 입찰 내역 | 전문가 | - |
| POST | `/api/ad-auction/{slotId}/bid` | 광고 입찰 (API) | 전문가 | - |
| POST | `/api/ad-bids/{bidId}/banner` | 배너 이미지 제출 (API) | 전문가 | - |
| GET | `/admin/advertisements` | 광고 승인 관리 | 관리자 | - |
| PATCH | `/api/admin/advertisements/{adId}/approve` `/reject` | 광고 승인/거절 (API) | 관리자 | - |
| GET | `/admin/ad-slots` | 광고 슬롯 관리 | 관리자 | - |
| PATCH | `/api/admin/ad-slots/{slotId}/settings` | 슬롯 설정 변경 (API) | 관리자 | - |
| PATCH | `/api/admin/ad-bids/{bidId}/approve` `/reject` | 광고 입찰 배너 심사 (API) | 관리자 | - |

### 관리자 / 통계 / 기타

| HTTP Method | URL | 설명 | 권한 | 비고 |
|---|---|---|---|---|
| GET | `/admin/main` | 관리자 대시보드 | 관리자 | - |
| GET | `/admin/members` | 회원 관리 | 관리자 | - |
| POST/DELETE | `/admin/members/{id}` | 회원 삭제 | 관리자 | - |
| GET | `/admin/experts` | 전문가 심사 목록 | 관리자 | - |
| PATCH | `/api/admin/experts/{memberId}/approval` | 전문가 승인/거절 (API) | 관리자 | - |
| GET | `/admin/experts/grade` | 전문가 등급 관리 | 관리자 | - |
| GET | `/admin/experts/withdraw` | 출금 요청 관리 | 관리자 | - |
| PATCH | `/api/admin/withdraw/{id}` | 출금 승인/거절 (API) | 관리자 | - |
| GET | `/admin/blacklists` | 블랙리스트 관리 | 관리자 | - |
| POST | `/admin/blacklist/release/{memberId}` | 블랙리스트 해제 | 관리자 | - |
| GET | `/admin/revenue` | 플랫폼 수익 현황 | 관리자 | - |
| GET | `/admin/partnerships` | 제휴 문의 관리 | 관리자 | - |
| POST | `/admin/partnerships/{id}/approve` `/reject` `/delete` | 제휴 문의 처리 | 관리자 | - |
| POST | `/partnership/inquiry` | 제휴 문의 등록 | 비로그인 | - |
| GET | `/policys/faq` | FAQ 페이지 | 전체 | - |
| GET/POST | `/faqs/save` `/edit/{faqId}` `/update/{faqId}` `/delete/{faqId}` | FAQ 관리 | 관리자 | - |
| GET | `/supports` | 고객센터 | 전체 | - |
| GET | `/policys/terms` `/privacy` `/ad` `/operation` | 약관/정책 페이지 | 전체 | - |
| POST | `/api/project/ai/draft` `/ask` | AI 프로젝트 초안 작성 / 질의응답 (API) | 로그인 필요 | Gemini 연동 |

## 주요 기술 특징

### 보안
- Spring Security 기반 세션 인증, BCrypt 비밀번호 암호화
- 5종 인터셉터를 통한 계층적 접근 제어 (Session / Login / Admin / Project / Bid)
- 이메일/SMS 인증 발송 등 민감 엔드포인트의 IP 기준 요청 빈도 제한
- CSRF 토큰 자동 주입 (form 기반 요청)
- 역할별 기능 분리 (CLIENT / EXPERT / ADMIN)
- Google / Kakao OAuth2 소셜 로그인 지원

### 예외 처리
- 커스텀 예외 클래스 (BadRequestException 등)
- `CustomErrorController`를 통해 요청 타입(HTML/JSON)에 따라 에러 응답 형식을 분기 처리
- 전역 예외 처리

### 데이터베이스
- JPA를 통한 ORM
- 지연 로딩된 연관 엔티티를 세션 종료 후 조회하면 발생하는 오류를, 조회 시점에 연관 데이터를 함께 가져오도록 처리해서
  해결. 세션이 열려 있는 동안 필요한 데이터를 모두 로딩해두어, 이후에도 안전하게 접근할 수 있게 설계.
- 조건부 UPDATE를 통한 동시성 제어 (에스크로 잔액 차감)
- 알림을 생성 즉시 발송하면, 트랜잭션이 롤백될 때 존재하지 않는 데이터에 대한 알림이 나가는 문제가 발생. 그래서
  알림 발송을 트랜잭션이 완전히 커밋된 이후로 미뤄, 성공한 경우에만 알림이 나가도록 처리 하도록 설계.

### 파일 업로드
- 이미지 파일 업로드 지원 (포트폴리오, 게시판, 작업 산출물, 채팅, 쪽지 첨부)
- MultipartFile → UUID 파일명 변환 → 디스크 저장
- WebMvcConfig를 통한 정적 리소스 노출

### 실시간 기능
- WebSocket 기반 실시간 채팅
- SSE(Server-Sent Events) 기반 실시간 알림

SevMerge © 2026