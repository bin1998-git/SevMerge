# 🖥️ SevMerge - IT 외주 입찰 플랫폼

> 의뢰인이 프로젝트를 등록하면 전문가들이 제안서를 제출하여 경쟁하는  
> 역제안 입찰 방식의 프리랜서 매칭 서비스

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|---|---|
| Backend | Spring Boot 3.4.5, Spring MVC |
| ORM | Spring Data JPA, Hibernate |
| View | Mustache |
| DB | MySQL 8.0 |
| 인증 | Session 기반 로그인 (Interceptor) |
| 실시간 통신 | WebSocket (STOMP) |
| 결제 | 카카오페이 / 포트원(PortOne) |
| 빌드 | Gradle |
| 언어 | Java 21 |

---

## 📁 프로젝트 구조

```
src/main/java/com/project/
├── domain/
│   ├── member/        # Member Entity, Role(Enum), Status(Enum), Service, Controller, DTO
│   ├── expert/        # ExpertProfile, BadgeService
│   ├── project/       # Project, BidFilterType(Enum), Service, Controller
│   ├── bid/           # Bid Entity, Service, Controller
│   ├── payment/       # Payment, 에스크로 처리
│   ├── review/        # Review, avgRating 집계
│   ├── board/         # Board, Comment
│   └── chat/          # ChatRoom, ChatMessage, WebSocket 1:1 채팅
├── global/
│   ├── config/        # WebMvcConfig, WebSocketConfig
│   ├── exception/     # GlobalExceptionHandler, CustomException, ErrorCode
│   └── util/          # ApiResponse, FileUploadUtil
└── resources/
    └── templates/     # Mustache 템플릿
```

---

## ⚙️ 로컬 환경 세팅

### 1. 레포 클론
```bash
git clone https://github.com/bin1998-git/SevMerge.git
cd SevMerge
```

### 2. MySQL DB 생성
```sql
CREATE DATABASE sevmerge DEFAULT CHARACTER SET utf8mb4;
```

### 3. .env 파일 생성
프로젝트 루트(`SevMerge/`)에 `.env` 파일 생성 후 아래 내용 입력
```
DB_USERNAME=root
DB_PASSWORD=본인DB비밀번호
```

### 4. data.sql 파일 생성
`src/main/resources/db/data.sql` 경로에 빈 파일 생성

### 5. 서버 실행
```bash
./gradlew bootRun
```
서버 실행 후 `http://localhost:8080` 접속

---

## 🌿 브랜치 전략

| 브랜치 | 용도 | 규칙 |
|---|---|---|
| `main` | 최종 배포용 | 직접 push 금지 |
| `develop` | 통합 테스트용 | 팀장 코드리뷰 후 PR 머지 |
| `feature/project` | 팀장 - 프로젝트·입찰·수신필터 | PR 후 머지 |
| `feature/admin` | 팀장 - 관리자 페이지 | PR 후 머지 |
| `feature/payment` | 팀원 A - 결제·정산 | PR 후 머지 |
| `feature/login` | 팀원 B - 로그인·승인·뱃지 | PR 후 머지 |
| `feature/review` | 팀원 C - 리뷰·포트폴리오 | PR 후 머지 |
| `feature/board` | 팀원 D - 게시판 | PR 후 머지 |
| `feature/chat` | 보조 E - WebSocket 채팅 | PR 후 머지 |

---

## 📝 커밋 컨벤션

```
<타입>(<스코프>): <제목>
```

| 타입 | 설명 | 예시 |
|---|---|---|
| `feat` | 새 기능 추가 | `feat(bid): 수신 필터 차단 로직 추가` |
| `fix` | 버그 수정 | `fix(review): avgRating 계산 오류 수정` |
| `refactor` | 코드 리팩토링 | `refactor(chat): WebSocket 핸들러 분리` |
| `docs` | 문서 수정 | `docs: README 업데이트` |
| `style` | 포맷 변경 | `style: 불필요한 공백 제거` |
| `test` | 테스트 코드 | `test(badge): 뱃지 자동 부여 테스트` |
| `chore` | 빌드·설정 변경 | `chore: WebSocket 의존성 추가` |
| `remove` | 파일·코드 삭제 | `remove: 미사용 DTO 삭제` |

---

## 👥 팀 구성

| 역할 | 담당 도메인 | 핵심 작업 |
|---|---|---|
| 박정빈 (팀장) | 프로젝트 · 입찰 · 관리자 | 프로젝트 등록/조회/마감, 입찰 전 과정, 낙찰, 수신 필터, 관리자 페이지 |
| 최원종 A | 결제 · 정산 | 카카오페이/포트원 연동, 에스크로, 정산, 환불 |
| 박효균 B | 로그인 · 전문가 승인 | 세션 로그인, 인터셉터, 전문가 승인, 뱃지 로직 |
| 이상현 C | 리뷰 · 포트폴리오 | 별점 집계, 뱃지 트리거, 포트폴리오 CRUD |
| 안준현 D | 게시판 | 자유·공지·문의 CRUD, 페이징·검색, 댓글 |
| 이학산 E | 알림 · 채팅 · 마이페이지 | WebSocket 1:1 채팅, 채팅 저장/수정/삭제, 알림 |
| 김다영 F | 댓글 · CSS 통일 | 댓글 CRUD, Mustache 뷰, CSS 통일, 뱃지 스타일링 |

---

## ✨ 주요 기능

- **역제안 입찰** — 전문가가 먼저 제안서를 제출하여 경쟁
- **⭐ 인증 전문가 뱃지** — 평균 별점 4.5 이상 + 리뷰 3건 이상 자동 부여
- **제안서 수신 필터** — 인증 전문가만 / 일반 전문가만 / 전체 선택 수신
- **WebSocket 1:1 채팅** — 의뢰인 ↔ 전문가 실시간 채팅, 메시지 저장·수정·삭제
- **에스크로 결제** — 프로젝트 완료 후 전문가에게 안전 정산
