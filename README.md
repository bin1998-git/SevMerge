# 🌊 BusanTrip — 부산 여행 통합 플랫폼

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Mustache](https://img.shields.io/badge/Mustache-SSR-FF6600?style=for-the-badge)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-010101?style=for-the-badge)

> 부산을 여행하는 사람들을 위한 **지도 기반 발자취 기록 · 맛집/여행지 추천 · 실시간 예약 메시지** 통합 플랫폼

</div>

---

## 📌 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [주요 기능](#-주요-기능)
3. [기술 스택](#-기술-스택)
4. [시스템 아키텍처](#-시스템-아키텍처)
5. [WebSocket 구조](#-websocket-구조)
6. [DB ERD](#-db-erd)
7. [화면 구성](#-화면-구성)
8. [수익 구조](#-수익-구조)
9. [팀 구성 및 역할](#-팀-구성-및-역할)
10. [Git Flow 전략](#-git-flow-전략)
11. [프로젝트 실행 방법](#-프로젝트-실행-방법)
12. [API 문서](#-api-문서)

---

## 🗺 프로젝트 소개

**BusanTrip**은 부산을 방문하는 국내외 여행자들이  
맛집·여행지·야경 명소를 기록하고, 지역 식당과 실시간으로 소통하며,  
다른 여행자들과 커뮤니티를 형성할 수 있는 **부산 특화 여행 플랫폼**입니다.

| 항목 | 내용 |
|------|------|
| 프로젝트명 | BusanTrip — 부산 여행 통합 플랫폼 |
| 개발 기간 | 2025년 __ 월 ~ 2025년 __ 월 (약 8주) |
| 팀 구성 | 7인 (팀장 1, 팀원 6) |
| 배포 환경 | AWS EC2 / 로컬 서버 |

---

## ✨ 주요 기능

### 🗺 지도 기반 발자취
- 로그인 시 **부산 카카오맵** 위에 내가 방문한 장소가 마커로 표시
- 카테고리(맛집/여행지/야경)별 마커 색상 구분 및 필터링
- 마커 클릭 시 장소명·방문일·사진 팝업

### 📷 발자취 기록
- **네이버 카메라 OCR (영수증 스캔)** → 식당명·날짜 자동 추출 → 방문 기록 자동 저장
- 여행지 **사진 직접 업로드** 및 장소 정보 입력으로 발자취 등록

### 💬 실시간 WebSocket 통신
- **회원 ↔ 식당** : 식당 예약 문의 실시간 메시지 (STOMP)
- **식당(가게) ↔ 관리자** : 입점 신청 승인·거절 실시간 알림

### 🍽 추천 콘텐츠
- 추천 맛집 · 추천 여행지 · 추천 야경지 카드형 리스트
- 렌트카 외부 링크 연결 / 지하철 노선도 앱 연결

### 👥 커뮤니티 & 게시판
- 게시글 CRUD, 댓글, 사진 첨부, 키워드 검색, 페이지네이션

### 💳 결제 시스템
- **광고 제거 평생이용권** : 990원 1회 결제 (카카오페이 / 포트원)
- **식당 입점 월정기결제** : 포트원 정기결제(빌링키) 자동 청구

### 🔐 인증 시스템
- 세션 기반 일반 로그인 / **카카오 · 구글 OAuth2 소셜 로그인**

---

## 🛠 기술 스택

### Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 | 주 개발 언어 |
| Spring Boot | 3.x | 애플리케이션 프레임워크 |
| Spring Security | 6.x | 인증/인가, CSRF, 세션 관리 |
| Spring Data JPA | - | ORM, DB 연동 |
| Spring WebSocket | - | STOMP 기반 실시간 통신 |
| OAuth2 Client | - | 카카오·구글 소셜 로그인 |
| Hibernate | - | JPA 구현체 |

### Frontend
| 기술 | 용도 |
|------|------|
| Mustache | 서버사이드 렌더링 (SSR) 템플릿 엔진 |
| HTML5 / CSS3 / JavaScript ES6+ | 마크업·스타일·동작 |
| Bootstrap 5 | 반응형 레이아웃 |
| SockJS + STOMP.js | WebSocket 클라이언트 |
| 카카오맵 API | 부산 지도, 마커, 발자취 시각화 |

### Database
| 기술 | 버전 | 용도 |
|------|------|------|
| MySQL | 8.x | 메인 RDB |

### 결제 / 외부 API
| 기술 | 용도 |
|------|------|
| 카카오페이 API | 단건 결제 (광고제거 990원) |
| 포트원(PortOne) | PG 연동, 정기결제(빌링키) |
| 네이버 OCR API | 영수증 스캔 → 텍스트 추출 |
| 카카오맵 API | 지도, 검색, 좌표 변환 |

### 협업 / 환경
| 기술 | 용도 |
|------|------|
| Git Flow | 브랜치 전략 |
| GitHub | 원격 저장소, PR, 코드 리뷰 |
| IntelliJ IDEA | IDE |
| Notion | 일정 관리, 문서화 |

---

## 🏗 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                        Client                           │
│         Browser (Mustache SSR + JS)                     │
│    ┌──────────────┐    ┌──────────────────────────┐     │
│    │  HTTP 요청   │    │  WebSocket (SockJS/STOMP) │     │
│    └──────┬───────┘    └────────────┬─────────────┘     │
└───────────┼────────────────────────┼───────────────────-┘
            │                        │
┌───────────▼────────────────────────▼───────────────────-┐
│                   Spring Boot Application               │
│  ┌─────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  Controller │  │  WS Handler  │  │Spring Security│  │
│  │  (REST/MVC) │  │ (STOMP/STOMP)│  │  (Session +   │  │
│  └──────┬──────┘  └──────┬───────┘  │   OAuth2)     │  │
│         │                │          └───────────────-┘  │
│  ┌──────▼────────────────▼───────────────────────────┐  │
│  │              Service Layer                        │  │
│  └──────────────────────┬────────────────────────────┘  │
│  ┌───────────────────────▼───────────────────────────┐  │
│  │          Repository (Spring Data JPA)             │  │
│  └───────────────────────┬───────────────────────────┘  │
└──────────────────────────┼─────────────────────────────-┘
                           │
              ┌────────────▼────────────┐
              │       MySQL 8.x         │
              └─────────────────────────┘
                           │
          ┌────────────────┼─────────────────┐
          │                │                 │
   ┌──────▼──────┐  ┌──────▼──────┐  ┌──────▼──────┐
   │ 카카오페이  │  │  포트원 PG  │  │ 네이버 OCR  │
   │     API     │  │    (결제)   │  │    API      │
   └─────────────┘  └─────────────┘  └─────────────┘
```

---

## 🔌 WebSocket 구조

BusanTrip은 **Spring WebSocket + STOMP** 프로토콜을 사용하여  
두 가지 실시간 채널을 운영합니다.

### 채널 구성

```
[채널 1] 회원 ↔ 식당 — 예약 문의 메시지
┌──────────┐   STOMP SEND              ┌──────────────┐
│  회원    │ ──/pub/chat/restaurant/{id}──▶│   식당(가게) │
│ (Member) │ ◀─/sub/chat/member/{memberId}─│  (Restaurant)│
└──────────┘                           └──────────────┘

[채널 2] 식당 ↔ 관리자 — 입점 신청 실시간 알림
┌──────────────┐   STOMP SEND           ┌────────────┐
│  식당(가게)  │ ──/pub/admin/notify───▶│   관리자   │
│  (Restaurant)│ ◀─/sub/admin/alert─────│  (Admin)   │
└──────────────┘                        └────────────┘
```

### STOMP 엔드포인트 정의

| 구분 | 엔드포인트 | 설명 |
|------|-----------|------|
| WebSocket 연결 | `/ws-connect` | SockJS fallback 지원 |
| 메시지 발행 prefix | `/pub` | 클라이언트 → 서버 |
| 메시지 구독 prefix | `/sub` | 서버 → 클라이언트 |
| 예약 문의 발행 | `/pub/chat/restaurant/{id}` | 회원이 식당으로 메시지 전송 |
| 예약 문의 수신 | `/sub/chat/member/{memberId}` | 회원의 메시지 수신 구독 |
| 식당 답변 발행 | `/pub/chat/reply/{memberId}` | 식당이 회원에게 답변 전송 |
| 식당 답변 수신 | `/sub/chat/restaurant/{id}` | 식당의 답변 수신 구독 |
| 관리자 알림 발행 | `/pub/admin/notify` | 식당이 관리자에게 알림 발송 |
| 관리자 알림 수신 | `/sub/admin/alert` | 관리자 알림 수신 구독 |

### WebSocket 설정 코드 구조

```java
// WebSocketConfig.java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");        // 구독 채널 prefix
        registry.setApplicationDestinationPrefixes("/pub"); // 발행 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")
                .setAllowedOriginPatterns("*")
                .withSockJS();                      // SockJS fallback 지원
    }
}
```

```java
// ChatController.java (예시)
@Controller
public class ChatController {

    @MessageMapping("/chat/restaurant/{restaurantId}")   // /pub/chat/restaurant/{id}
    public void sendToRestaurant(@DestinationVariable Long restaurantId,
                                  ChatMessageDto message,
                                  Principal principal) {
        // 식당 담당자 구독 채널로 전달
        messagingTemplate.convertAndSend(
            "/sub/chat/restaurant/" + restaurantId, message);
    }

    @MessageMapping("/chat/reply/{memberId}")            // /pub/chat/reply/{memberId}
    public void replyToMember(@DestinationVariable Long memberId,
                               ChatMessageDto message) {
        messagingTemplate.convertAndSend(
            "/sub/chat/member/" + memberId, message);
    }

    @MessageMapping("/admin/notify")                     // /pub/admin/notify
    public void notifyAdmin(AdminNotifyDto notify) {
        messagingTemplate.convertAndSend("/sub/admin/alert", notify);
    }
}
```

### 프론트엔드 연결 예시 (SockJS + STOMP.js)

```javascript
// WebSocket 연결
const socket = new SockJS('/ws-connect');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    // 회원 - 식당 답변 구독
    stompClient.subscribe('/sub/chat/member/' + memberId, function (message) {
        const data = JSON.parse(message.body);
        renderMessage(data);
    });

    // 관리자 - 알림 구독
    stompClient.subscribe('/sub/admin/alert', function (notify) {
        const data = JSON.parse(notify.body);
        showAdminAlert(data);
    });
});

// 메시지 전송 (회원 → 식당)
function sendMessage(restaurantId, content) {
    stompClient.send('/pub/chat/restaurant/' + restaurantId,
        {},
        JSON.stringify({ senderId: memberId, content: content, sentAt: new Date() })
    );
}
```

---

## 🗄 DB ERD

### 핵심 테이블 관계

```
member (회원)
  ├── 1:N ──▶ footprint       (발자취 기록)
  ├── 1:N ──▶ community       (커뮤니티 게시글)
  ├── 1:N ──▶ comment         (댓글)
  ├── 1:N ──▶ subscription    (구독/결제 내역)
  └── 1:N ──▶ chat_message    (채팅 메시지 발송)

restaurant (식당)
  ├── 1:N ──▶ subscription    (월정기결제)
  ├── 1:1 ──▶ ad              (광고 배너)
  └── 1:N ──▶ chat_message    (채팅 메시지 수신)

place (추천 장소 - 여행지/야경지)
  └── 1:N ──▶ attachment      (첨부 사진)

community (게시글)
  ├── 1:N ──▶ comment         (댓글)
  └── 1:N ──▶ attachment      (첨부 사진)
```

### 주요 테이블 정의

```sql
-- 회원
CREATE TABLE member (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255),                          -- OAuth 회원은 NULL
    nickname        VARCHAR(50)  NOT NULL,
    profile_img     VARCHAR(500),
    role            ENUM('USER','ADMIN') DEFAULT 'USER',
    provider        ENUM('LOCAL','KAKAO','GOOGLE'),
    is_ad_free      BOOLEAN DEFAULT FALSE,                 -- 광고제거 여부
    deleted_at      DATETIME,                              -- Soft Delete
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 발자취 기록
CREATE TABLE footprint (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id       BIGINT NOT NULL,
    place_name      VARCHAR(200) NOT NULL,
    category        ENUM('RESTAURANT','TRAVEL','NIGHT') NOT NULL,
    lat             DECIMAL(10,7),
    lng             DECIMAL(10,7),
    memo            TEXT,
    visited_at      DATE NOT NULL,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 식당 입점 정보
CREATE TABLE restaurant (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    address             VARCHAR(300),
    phone               VARCHAR(20),
    description         TEXT,
    subscription_status ENUM('PENDING','ACTIVE','EXPIRED','REJECTED') DEFAULT 'PENDING',
    paid_at             DATETIME,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 채팅 메시지 (WebSocket)
CREATE TABLE chat_message (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_type       ENUM('MEMBER_RESTAURANT','RESTAURANT_ADMIN') NOT NULL,
    sender_id       BIGINT NOT NULL,
    receiver_id     BIGINT NOT NULL,
    content         TEXT NOT NULL,
    is_read         BOOLEAN DEFAULT FALSE,
    sent_at         DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 구독/결제
CREATE TABLE subscription (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id   BIGINT,
    restaurant_id BIGINT,
    type        ENUM('AD_FREE','RESTAURANT_MONTHLY') NOT NULL,
    amount      INT NOT NULL,
    imp_uid     VARCHAR(100),                              -- 포트원 결제 고유번호
    status      ENUM('PAID','FAILED','CANCELLED') NOT NULL,
    paid_at     DATETIME
);
```

---

## 📱 화면 구성

| 화면 | URL | 설명 |
|------|-----|------|
| 메인 (지도) | `/` | 부산 카카오맵 + 발자취 마커 |
| 회원가입 | `/member/join` | 이메일·닉네임·비밀번호 |
| 로그인 | `/member/login` | 일반 + 카카오/구글 OAuth2 |
| 마이페이지 | `/member/mypage` | 발자취 목록, 회원정보 수정, 결제내역 |
| 추천 맛집 | `/restaurant/list` | 카드형 리스트, 카테고리 필터 |
| 추천 여행지 | `/place/travel` | 카드형 리스트 |
| 추천 야경지 | `/place/night` | 사진 갤러리형 |
| 장소 상세 | `/place/{id}` | 사진·지도·설명·예약 채팅 |
| 커뮤니티 목록 | `/community/list` | 게시글 목록, 검색 |
| 게시글 상세 | `/community/{id}` | 본문 + 댓글 |
| 렌트카 | `/extra/rentcar` | 외부 링크 연결 |
| 지하철 노선도 | `/extra/subway` | 앱 연결 |
| 결제 | `/payment` | 카카오페이 / 포트원 |
| 관리자 대시보드 | `/admin` | 통계, 메뉴 |
| 관리자 식당 관리 | `/admin/restaurant` | 입점 승인/거절 + 실시간 알림 |
| 관리자 회원 관리 | `/admin/member` | 회원 목록, 강제탈퇴 |

---

## 💰 수익 구조

```
┌────────────────────────────────────────────────────────┐
│                  BusanTrip 수익 모델                    │
├───────────────────────┬────────────────────────────────┤
│  광고 제거 평생이용권  │       식당 입점 월정기결제      │
│                       │                                │
│  • 일반 회원 → 990원  │  • 식당 사업자 → 월 정기 청구  │
│  • 1회 결제 후 영구   │  • 포트원 빌링키 자동 결제     │
│  • 광고 배너 제거     │  • 추천 맛집 섹션 상단 노출    │
│  • 카카오페이/포트원  │  • ACTIVE 상태에서만 노출      │
└───────────────────────┴────────────────────────────────┘
```

---

## 👥 팀 구성 및 역할

| 이름 | 역할 | 담당 기능 |
|------|------|-----------|
| 팀장 | 팀장 / Backend | 인증(세션+OAuth2), Spring Security, 카카오맵 연동, 발자취 기록, 배포 |
| 팀원A | Frontend / Backend | 메인 UI/UX, 추천 맛집·여행지·야경지 CRUD, 반응형 레이아웃 |
| 팀원B | Backend | 커뮤니티·게시판 CRUD, 사진 업로드, 네이버 OCR(영수증 스캔) 연동 |
| 팀원C | Backend / 결제 | WebSocket(채팅·알림), 카카오페이/포트원 결제, 관리자 페이지 |

---

## 🌿 Git Flow 전략

```
main          ─────────────────────────────────────── 배포 브랜치
                  ↑ merge (PR)
release/v1.0  ────────────────── QA·버그수정
                  ↑ merge
develop       ──────────────────────────────────────── 통합 개발
              ↑ merge   ↑ merge   ↑ merge   ↑ merge
feature/login feature/map feature/chat feature/payment
```

### 브랜치 네이밍 규칙

```
feature/login               # 로그인/인증
feature/map                 # 카카오맵 연동
feature/footprint           # 발자취 기록
feature/community           # 커뮤니티/게시판
feature/websocket-chat      # 회원↔식당 채팅
feature/websocket-admin     # 식당↔관리자 알림
feature/payment             # 결제 시스템
feature/admin               # 관리자 페이지
feature/ocr-receipt         # 영수증 스캔
```

### 커밋 컨벤션

```
feat     : 새 기능 추가
fix      : 버그 수정
docs     : 문서 수정 (README 등)
style    : 코드 포맷팅 (로직 변경 없음)
refactor : 코드 리팩토링
test     : 테스트 코드
chore    : 빌드 설정, 의존성 변경

예시)
feat: 카카오 OAuth2 로그인 구현
fix: WebSocket 세션 만료 시 재연결 처리
feat: 포트원 정기결제 빌링키 등록 구현
```

---

## ⚙ 프로젝트 실행 방법

### 사전 준비

- Java 17+
- MySQL 8.x
- API 키 발급 필요
  - 카카오 개발자 콘솔 (카카오맵 API, 카카오 로그인, 카카오페이)
  - 구글 클라우드 콘솔 (Google OAuth2)
  - 포트원(PortOne) 상점 계정
  - 네이버 클라우드 플랫폼 (OCR API)

### 1. 저장소 클론

```bash
git clone https://github.com/{팀GitHub계정}/busantrip.git
cd busantrip
git checkout develop
```

### 2. 데이터베이스 생성

```sql
CREATE DATABASE busantrip DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. application.yml 설정

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/busantrip?useSSL=false&serverTimezone=Asia/Seoul
    username: your_db_username
    password: your_db_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        default_batch_fetch_size: 100

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
            client-secret: YOUR_KAKAO_CLIENT_SECRET
            redirect-uri: "{baseUrl}/login/oauth2/code/kakao"
            authorization-grant-type: authorization_code
            scope: profile_nickname, account_email
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
            redirect-uri: "{baseUrl}/login/oauth2/code/google"
            scope: profile, email

# 카카오맵 API
kakao:
  map:
    api-key: YOUR_KAKAO_MAP_API_KEY

# 포트원(PortOne)
portone:
  api-key: YOUR_PORTONE_API_KEY
  api-secret: YOUR_PORTONE_API_SECRET

# 네이버 OCR
naver:
  ocr:
    api-url: YOUR_NAVER_OCR_INVOKE_URL
    secret-key: YOUR_NAVER_OCR_SECRET_KEY
```

### 4. 빌드 및 실행

```bash
./gradlew clean build
./gradlew bootRun
```

접속: [http://localhost:8080](http://localhost:8080)

---

## 📋 API 문서

### 인증

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|-----------|
| POST | `/member/join` | 회원가입 | ❌ |
| POST | `/member/login` | 로그인 | ❌ |
| GET | `/oauth2/authorization/kakao` | 카카오 OAuth2 시작 | ❌ |
| GET | `/oauth2/authorization/google` | 구글 OAuth2 시작 | ❌ |
| POST | `/member/logout` | 로그아웃 | ✅ |

### 발자취

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|-----------|
| GET | `/footprint/list` | 내 발자취 목록 | ✅ |
| POST | `/footprint` | 발자취 등록 | ✅ |
| PUT | `/footprint/{id}` | 발자취 수정 | ✅ |
| DELETE | `/footprint/{id}` | 발자취 삭제 | ✅ |
| POST | `/footprint/ocr` | 영수증 스캔 자동 등록 | ✅ |

### 커뮤니티

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|-----------|
| GET | `/community/list` | 게시글 목록 (페이지네이션) | ❌ |
| GET | `/community/{id}` | 게시글 상세 | ❌ |
| POST | `/community` | 게시글 작성 | ✅ |
| PUT | `/community/{id}` | 게시글 수정 | ✅ |
| DELETE | `/community/{id}` | 게시글 삭제 | ✅ |

### 결제

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|-----------|
| POST | `/payment/ad-free` | 광고제거 990원 결제 요청 | ✅ |
| POST | `/payment/webhook` | 포트원 웹훅 수신 | ❌ |
| GET | `/payment/history` | 결제 내역 조회 | ✅ |

### WebSocket (STOMP)

| 구분 | 경로 | 설명 |
|------|------|------|
| 연결 엔드포인트 | `/ws-connect` | SockJS |
| 예약 문의 발행 | `/pub/chat/restaurant/{id}` | 회원 → 식당 |
| 식당 답변 발행 | `/pub/chat/reply/{memberId}` | 식당 → 회원 |
| 관리자 알림 발행 | `/pub/admin/notify` | 식당 → 관리자 |
| 예약 문의 구독 | `/sub/chat/restaurant/{id}` | 식당 수신 |
| 답변 구독 | `/sub/chat/member/{memberId}` | 회원 수신 |
| 관리자 알림 구독 | `/sub/admin/alert` | 관리자 수신 |

---

## 📁 프로젝트 구조

```
src/main/java/com/busantrip/
├── BusantripApplication.java
├── config/
│   ├── SecurityConfig.java          # Spring Security 설정
│   ├── WebSocketConfig.java         # STOMP WebSocket 설정
│   └── OAuth2Config.java            # OAuth2 설정
├── member/
│   ├── controller/MemberController.java
│   ├── service/MemberService.java
│   ├── repository/MemberRepository.java
│   ├── domain/Member.java
│   └── dto/
├── footprint/
│   ├── controller/FootprintController.java
│   ├── service/FootprintService.java
│   ├── repository/FootprintRepository.java
│   └── domain/Footprint.java
├── restaurant/
│   ├── controller/RestaurantController.java
│   ├── service/RestaurantService.java
│   └── domain/Restaurant.java
├── community/
│   ├── controller/CommunityController.java
│   ├── service/CommunityService.java
│   └── domain/Community.java
├── chat/
│   ├── controller/ChatController.java   # STOMP @MessageMapping
│   ├── service/ChatService.java
│   └── domain/ChatMessage.java
├── payment/
│   ├── controller/PaymentController.java
│   ├── service/PaymentService.java
│   └── domain/Subscription.java
├── admin/
│   ├── controller/AdminController.java
│   └── service/AdminService.java
└── common/
    ├── exception/GlobalExceptionHandler.java
    └── dto/PageDTO.java

src/main/resources/
├── application.yml
├── templates/                        # Mustache 템플릿
│   ├── layout/
│   │   ├── header.mustache
│   │   └── footer.mustache
│   ├── member/
│   ├── community/
│   ├── restaurant/
│   └── admin/
└── static/
    ├── css/
    ├── js/
    │   └── websocket.js              # SockJS + STOMP 클라이언트
    └── img/
```

---

<div align="center">

**BusanTrip** — 부산의 모든 여행을 한 곳에서 🌊

</div>
