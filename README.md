# 🖥️ SevMerge — IT 외주 역제안 입찰 플랫폼

> 의뢰인이 프로젝트를 등록하면, 전문가들이 먼저 제안서를 제출해 경쟁하는 역(逆)제안 입찰 방식의 프리랜서 매칭 플랫폼
> 안전한 거래를 위한 에스크로 결제, 실시간 채팅·알림, AI 의뢰 작성 도우미까지 갖춘 풀스택 웹 서비스

---

## 🚀 프로젝트 개요

기존 외주 플랫폼은 의뢰인이 일일이 전문가를 찾아 연락해야 했습니다.
**SevMerge**는 이 흐름을 뒤집어, 의뢰인이 프로젝트만 올리면 전문가들이 제안서를 들고 경쟁하는 **역제안(reverse-bidding)** 구조를 채택했습니다.
거래 신뢰 문제는 플랫폼이 대금을 보관했다가 작업 완료 시 정산하는 **에스크로**로 해결합니다.

| 항목 | 내용 |
|------|------|
| 프로젝트 유형 | 팀 프로젝트 (백엔드 중심 풀스택) |
| 개발 기간 | 2026.05 ~ 2026.06 (약 4주) |
| 팀 구성 | 7명 |
| 핵심 가치 | 역제안 입찰 · 에스크로 안전거래 · 실시간 소통 · AI 작성 보조 |
| 아키텍처 | Spring Boot 모놀리식 + Mustache 서버사이드 렌더링(SSR) |

---

## 🔍 해결하고자 하는 문제

기존 외주 플랫폼에서는 다음과 같은 문제가 발생합니다:

- 의뢰인이 수많은 전문가를 직접 탐색·연락해야 하는 **비효율적 매칭 구조**
- 선불 결제 후 작업 미완료 시 환불이 어려운 **거래 신뢰 문제**
- 신규 전문가의 실력 파악이 어려운 **정보 비대칭 문제**
- 의뢰 내용 작성에 익숙하지 않은 **비전문 의뢰인의 진입 장벽**

---

## ✨ 제공하는 가치

- **의뢰인**: 프로젝트만 올리면 전문가들이 먼저 제안 → 비교 후 최적 낙찰, 에스크로로 안전한 결제
- **전문가**: 관심 프로젝트에 직접 제안서 제출, 베이지안 등급제로 실력 공정 평가, 실시간 채팅으로 원활한 소통
- **관리자**: 전문가 심사·신고·분쟁·광고·공지 등 플랫폼 전체 운영 도구 제공

---

## 📌 프로젝트 문서

- 노션 링크: *(추가 예정)*
- ERD: *(추가 예정)*
- Flowchart: *(추가 예정)*

---

## 🛠 기술 스택

**Backend**

![Java](https://img.shields.io/badge/Java_21-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3.4.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8.0-4479A1?style=flat-square&logo=mysql&logoColor=white)

**Frontend**

![Mustache](https://img.shields.io/badge/Mustache_SSR-1E1E1E?style=flat-square&logo=mustache&logoColor=white)
![JavaScript](https://img.shields.io/badge/Vanilla_JS-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![Chart.js](https://img.shields.io/badge/Chart.js-FF6384?style=flat-square&logo=chartdotjs&logoColor=white)

**External API**

![Toss Payments](https://img.shields.io/badge/Toss_Payments-0064FF?style=flat-square)
![SolAPI](https://img.shields.io/badge/SolAPI_SMS-00C73C?style=flat-square)
![Gmail SMTP](https://img.shields.io/badge/Gmail_SMTP-EA4335?style=flat-square&logo=gmail&logoColor=white)
![Google Gemini](https://img.shields.io/badge/Google_Gemini_2.5_Flash-4285F4?style=flat-square&logo=google&logoColor=white)
![Kakao OAuth](https://img.shields.io/badge/Kakao_OAuth-FFCD00?style=flat-square&logo=kakao&logoColor=black)
![Google OAuth](https://img.shields.io/badge/Google_OAuth-4285F4?style=flat-square&logo=google&logoColor=white)

**Tools**

![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=flat-square)

---

## 📂 프로젝트 구조

> 기능(도메인) 단위로 패키지를 나누고, 공통 인프라는 `core`로 분리했습니다.

```
src/main/java/com/example/SevMerge
├── member              # 회원, 로그인, OAuth(Kakao/Google), 이메일/SMS 인증, 마이페이지
├── expertprofile       # 전문가 프로필, 등급 산정(베이지안), 인증 전문가
├── expertwish          # 전문가 찜
├── bookmark            # 북마크
├── project             # 프로젝트(의뢰) 등록·검색·상태 관리
├── bid                 # 제안서(입찰), 수신 필터, 낙찰
├── payment             # 에스크로 생성·정산·환불
├── charge              # Toss 잔액 충전
├── refund              # 환불 신청·심사
├── withdrawal          # 전문가 정산금 출금
├── review              # 리뷰·별점
├── portfolio           # 전문가 포트폴리오
├── board / comment     # 게시판, 댓글
├── chatRoom / chatMessage  # WebSocket(STOMP) 실시간 채팅
├── message             # 쪽지(첨부파일)
├── notification        # SSE 실시간 알림 + 정리 스케줄러
├── ai                  # Spring AI(Gemini) 의뢰 작성·Q&A
├── advertisement       # 광고 슬롯
├── partnership         # 제휴 문의
├── faq / footer        # FAQ, 정책/약관
├── Report              # 신고, 블랙리스트
├── admin               # 관리자 페이지
└── core
    ├── config          # WebMvcConfig, WebSocketConfig, SolApiConfig
    ├── interceptor     # Session / Login / Admin / Project / Bid
    ├── filter          # RateLimitFilter
    ├── exception       # GlobalExceptionHandler, CustomException, ErrorCode
    └── util            # ApiResponse, FileUploadUtil 등
```

---

## 📌 주요 기능

### 1. 역제안 입찰 (핵심)
- 의뢰인이 프로젝트(카테고리·예산·기간) 등록 → 전문가들이 제안서를 먼저 제출해 경쟁
- 의뢰인은 제안서를 비교 후 낙찰(select) → 그 순간 에스크로 결제 체결
- 제안서 수신 필터 — 프로젝트별로 전체 / 인증 전문가만 수신 선택
- 입찰 상태 머신: `PENDING` → `SELECTED` / `HOLD` / `REJECTED`

### 2. 에스크로 결제 & 정산
- Toss Payments로 지갑에 잔액 충전 (서버 사이드 confirm API 검증)
- 낙찰 시 의뢰인 잔액을 에스크로로 보관(`PAID`), 작업 완료 확인 시 전문가에게 정산(`SETTLED`)
- 플랫폼 수수료 10% 자동 차감 후 전문가 지급, 분쟁 시 환불(`REFUNDED`)으로 잔액 복구
- 전문가 정산금 출금(Withdrawal) 신청 (최소 10,000원)

### 3. 전문가 시스템
- 전문가 가입은 관리자 심사·승인을 거쳐 활동 가능 (승인 시 인증 전문가 부여)
- 전문가 등급 자동 산정 — 평균 별점·리뷰 수·작업 완료 수를 **베이지안 평균** 기반으로 보정해 `NORMAL` / `SKILLED` / `MASTER` 3등급으로 분류
- 포트폴리오 CRUD, 전문가 찜/북마크, 전문가 대시보드

### 4. 실시간 소통 & 알림
- WebSocket(STOMP) 1:1 채팅 — 의뢰인 ↔ 전문가 실시간 대화, 메시지 저장·삭제
- SSE 기반 실시간 알림 — 입찰/낙찰/결제/승인/쪽지 등 이벤트 푸시
- 파일 첨부가 가능한 쪽지(Message) 기능
- 이메일(SMTP)·SMS(SolAPI) 인증코드 및 상태 알림 발송

### 5. AI 의뢰 작성 도우미 (Gemini)
- 자연어 한 줄을 입력하면 제목·카테고리·예산·상세 설명·수신필터 초안을 AI가 생성
- 플랫폼 이용 방법을 묻는 Q&A 챗봇
- Google Gemini 2.5 Flash 모델, Spring AI ChatClient 연동

### 6. 신뢰 & 운영 관리
- 게시글/댓글 신고 누적 3회 시 자동 정지(`SUSPENDED`) + 블랙리스트 등록
- 게시판/공지/FAQ/제휴 문의, 광고 슬롯 구매·노출
- 관리자 페이지 — 전문가 심사, 회원·신고·환불·광고·제휴·공지 관리, Chart.js 통계

---

## 🧩 기술적 하이라이트

> 면접에서 설명하기 좋은, 의도를 가지고 설계한 부분들입니다.

### 1. 에스크로 — 동시성을 고려한 원자적 잔액 처리
낙찰 시 의뢰인 잔액을 차감해 에스크로로 묶을 때, 조건부 `UPDATE` 한 방으로 잔액 검증과 차감을 원자적으로 처리해 동시 요청에서의 이중 차감을 방지합니다.

```java
// PaymentService.createEscrow — 잔액이 충분할 때만 차감되는 원자적 UPDATE
int updated = em.createQuery(
    "UPDATE Member m SET m.balance = m.balance - :amount " +
    "WHERE m.id = :id AND m.balance >= :amount")
    .setParameter("amount", amount)
    .setParameter("id", clientId)
    .executeUpdate();

if (updated == 0) throw new BadRequestException("잔액 차감에 실패했습니다.");
```

에스크로는 `PAID` → `SETTLED`(정산) / `REFUNDED`(환불)의 명확한 상태 머신을 가지며, 정산·환불 시 요청자 소유/권한 검증(`ForbiddenException`)을 거칩니다.

### 2. 결제 보안 — 서버 사이드 Toss 승인
클라이언트가 아닌 서버에서 Toss confirm API를 직접 호출해 결제 금액 위변조를 방지합니다.

### 3. 전문가 등급 — 베이지안 평균으로 별점 왜곡 보정
리뷰가 적은 신규 전문가의 별점 왜곡을 완화하기 위해 전체 평균을 사전(prior)으로 활용하는 베이지안 평균을 적용합니다.

### 4. 다단 인터셉터 기반 인가 + Rate Limiting
Session / Login / Admin / Project / Bid 5종 인터셉터와 RateLimitFilter를 계층적으로 적용해 권한과 요청 빈도를 제어합니다.

### 5. 알림·외부연동의 트랜잭션 분리
SMS·이메일·SSE 알림 등 외부 연동을 트랜잭션 커밋 이후에 실행해 롤백 시 불필요한 외부 호출을 방지합니다.

---

## 🗂️ 도메인 모델 (주요 엔티티)

| 엔티티 | 설명 | 핵심 상태/필드 |
|--------|------|----------------|
| Member | 회원 | `role`(CLIENT/EXPERT/ADMIN), `status`(ACTIVE/PENDING/REJECTED/SUSPENDED/BLACKLISTED), `balance` |
| ExpertProfile | 전문가 프로필 | `isCertified`, `grade`(NORMAL/SKILLED/MASTER) |
| Project | 의뢰 | `status`(OPEN/IN_PROGRESS/DONE/CANCELLED…), `category`, `bidFilter` |
| Bid | 제안서 | `status`(PENDING/SELECTED/HOLD/REJECTED), 제안 금액/기간 |
| Payment | 에스크로 | `status`(PAID/SETTLED/REFUNDED), `platformFee`, `netAmount` |
| Charge | 충전 내역 | `orderId`, `paymentKey`, `status`(DONE) |
| Review | 리뷰 | 별점, 대상 전문가 |
| ChatRoom/ChatMessage | 채팅 | 1:1 방, 메시지 |
| Notification | 알림 | 읽음 여부, 30일 후 자동 정리 |
| Report/BlackList | 신고/제재 | 누적 3회 자동 정지 |

---

## 📑 ERD

*(ERD 이미지 추가 예정)*

---

## ⚙️ 실행 방법

### 사전 요구사항
- JDK 21 이상
- MySQL 8.0 이상
- Gradle 8.x 이상

### 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수를 설정하세요:

```env
# Database
DB_USERNAME=root
DB_PASSWORD=

# OAuth 2.0
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
KAKAO_CLIENT_ID=
KAKAO_CLIENT_SECRET=

# Toss Payments
TOSS_CLIENT_KEY=
TOSS_SECRET_KEY=

# 이메일 인증 (Gmail SMTP)
MAIL_USERNAME=
MAIL_PASSWORD=

# SolAPI (SMS)
SOLAPI_KEY=
SOLAPI_SECRET_KEY=
SOLAPI_SENDER_NUMBER=

# AI (Google Gemini)
GEMINI_API_KEY=
```

### 빌드 및 실행

**Windows 환경**
```bash
# 레포 클론
git clone https://github.com/bin1998-git/SevMerge.git
cd SevMerge

# DB 생성
CREATE DATABASE sevmerge DEFAULT CHARACTER SET utf8mb4;

# 빌드 & 실행
gradlew.bat bootRun
```

**Linux/Mac 환경**
```bash
git clone https://github.com/bin1998-git/SevMerge.git
cd SevMerge

./gradlew bootRun
```

> 실행 후 http://localhost:8080 접속 (초기 구동 시 `db/data.sql` 시드 데이터 자동 적재)

---

## 🔗 URL 매핑

### 회원 / 인증

| HTTP Method | URL | 설명 | 권한 |
|-------------|-----|------|------|
| GET | `/` | 메인 페이지 | 비로그인 |
| GET | `/members/login` | 로그인 화면 | 비로그인 |
| POST | `/members/login` | 로그인 처리 | 비로그인 |
| GET | `/members/join` | 회원가입 화면 | 비로그인 |
| POST | `/members/join` | 회원가입 처리 | 비로그인 |
| DELETE | `/api/v1/sessions` | 로그아웃 (API) | 로그인 필요 |
| GET | `/members/me` | 마이페이지 | 로그인 필요 |
| PUT | `/api/v1/members/me` | 내 정보 수정 (API) | 로그인 필요 |
| POST | `/api/v1/email/send` | 이메일 인증코드 발송 (API) | 비로그인 |
| POST | `/api/v1/email/verify` | 이메일 인증코드 확인 (API) | 비로그인 |

### 프로젝트 / 입찰 / 결제

| HTTP Method | URL | 설명 | 권한 |
|-------------|-----|------|------|
| GET | `/projects` | 프로젝트 목록 조회 | 로그인 필요 |
| GET | `/projects/new` | 프로젝트 등록 화면 | 의뢰인 |
| POST | `/projects/new` | 프로젝트 등록 처리 | 의뢰인 |
| GET | `/projects/{projectId}` | 프로젝트 상세 조회 | 로그인 필요 |
| PUT | `/api/v1/projects/{projectId}` | 프로젝트 수정 (API) | 의뢰인 |
| DELETE | `/api/v1/projects/{projectId}` | 프로젝트 삭제 (API) | 의뢰인 |
| POST | `/api/v1/bids` | 제안서 제출 (API) | 전문가 |
| POST | `/api/v1/bids/{bidId}/select` | 낙찰 처리 (API) | 의뢰인 |
| POST | `/api/v1/payments/escrow` | 에스크로 생성 (API) | 의뢰인 |
| POST | `/api/v1/payments/{paymentId}/settle` | 정산 처리 (API) | 의뢰인 |
| POST | `/api/v1/refunds` | 환불 신청 (API) | 로그인 필요 |
| POST | `/api/v1/charges` | 잔액 충전 (API) | 로그인 필요 |
| POST | `/api/v1/withdrawals` | 출금 신청 (API) | 전문가 |
| POST | `/api/v1/project/ai/draft` | AI 의뢰 초안 생성 (API) | 의뢰인 |
| POST | `/api/v1/project/ai/ask` | AI Q&A 챗봇 (API) | 로그인 필요 |

### 소통 / 관리자

| HTTP Method | URL | 설명 | 권한 |
|-------------|-----|------|------|
| GET | `/chats/{roomId}` | 채팅방 화면 | 로그인 필요 |
| GET | `/api/v1/notifications` | 알림 목록 (SSE) | 로그인 필요 |
| GET | `/admin` | 관리자 대시보드 | 관리자 |
| GET | `/admin/experts/pending` | 전문가 심사 목록 | 관리자 |
| POST | `/admin/experts/{expertId}` | 전문가 승인/거부 | 관리자 |
| GET | `/admin/reports` | 신고 목록 | 관리자 |
| GET | `/admin/refunds` | 환불 분쟁 목록 | 관리자 |

---

## 🛠️ 주요 기술 특징

**보안**
- BCrypt를 이용한 비밀번호 암호화
- 세션 기반 인증
- 5종 인터셉터를 통한 계층적 접근 제어
- RateLimitFilter를 이용한 로그인/인증코드 요청 제한
- OAuth 2.0 (Kakao / Google) 소셜 로그인

**예외 처리**
- 커스텀 예외 클래스 (Exception400, 401, 403, 404, 500)
- `GlobalExceptionHandler`를 통한 전역 예외 처리
- 사용자 친화적 에러 메시지 제공

**데이터베이스**
- JPA를 통한 ORM
- 조건부 UPDATE로 동시성 제어 (에스크로 잔액 차감)
- 트랜잭션 관리

**실시간**
- WebSocket(STOMP) 기반 1:1 채팅
- SSE(Server-Sent Events) 기반 실시간 알림
- 30일 경과 알림 자동 정리 스케줄러

---

## 🌿 협업 규칙

**브랜치 전략**
```
main(배포) ← develop(통합, 팀장 리뷰 후 PR 머지) ← feature/*(기능별)
```

**커밋 컨벤션** — `<타입>(<스코프>): <제목>`

| 타입 | 설명 |
|------|------|
| `feat` | 새로운 기능 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩터링 |
| `docs` | 문서 수정 |
| `style` | 코드 스타일 변경 |
| `test` | 테스트 추가/수정 |
| `chore` | 빌드 설정, 기타 |
| `remove` | 파일/코드 삭제 |

---

## 👥 기여자

*(팀원별 담당 기능 추가 예정)*
