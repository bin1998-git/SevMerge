# 🧑‍💻 IT 외주 입찰 플랫폼
> IT Outsourcing Bidding Platform

**전문가가 먼저 제안서를 제출하는 역제안(Reverse Bid) 방식의 IT 프리랜서 매칭 플랫폼**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)

---

## 📌 프로젝트 소개

기존 숨고·크몽 플랫폼은 의뢰인이 직접 전문가를 검색해야 하는 구조로 탐색 비용이 높습니다.  
이 플랫폼은 **의뢰인이 프로젝트를 올리면 전문가들이 먼저 제안서를 제출**하는 역제안 방식으로 매칭 효율을 극대화합니다.

```
📋 프로젝트 등록  →  💼 전문가 제안서 입찰  →  ✅ 낙찰자 선택  →  💳 결제 & 프로젝트 시작
```

### 핵심 차별점

| 기존 플랫폼 | 이 플랫폼 |
|---|---|
| 의뢰인이 전문가를 검색·탐색 | 전문가가 먼저 제안서 제출 |
| 전문가 신뢰도 파악 어려움 | ⭐ 인증 전문가 뱃지 (별점 4.5+ & 리뷰 3건+) |
| 제안서 필터링 불가 | 인증/일반 전문가 수신 필터 설정 |
| 가격 협상 불투명 | 입찰 시 희망 금액 명시 → 투명 비교 |

---

## 🛠 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot, Spring Security, JPA |
| Frontend | Mustache |
| Database | MySQL |
| 인증 | Session 기반 로그인, OAuth2 (카카오 · 구글) |
| 결제 | 카카오페이 / 포트원(PortOne) |
| 협업 | Git Flow, GitHub |

---

## 👥 팀 구성 및 역할

| 구분 | 담당 도메인 | 핵심 작업 |
|---|---|---|
| 팀장 | 프로젝트 · 입찰 · 관리자 | 프로젝트 등록/조회/마감, 입찰 전 과정, 낙찰 처리, 관리자 페이지 |
| 팀원 A | 결제 · 정산 | 카카오페이/포트원 연동, 에스크로 보관, 정산·환불 처리 |
| 팀원 B | 로그인 · 전문가 승인 · 뱃지 | JWT, OAuth2, PENDING 승인 플로우, 인증 뱃지 로직 |
| 팀원 C | 리뷰 · 포트폴리오 · 뱃지 트리거 | 별점/후기, avgRating 집계, 포트폴리오 CRUD |
| 팀원 D | 게시판 | 자유·공지·1:1문의 CRUD, 페이징·검색, 파일 첨부 |
| 보조 E | 마이페이지 · 알림 | 의뢰인/전문가 대시보드, 알림 Mustache 마크업 |
| 보조 F | 댓글 · CSS 통일 | 댓글 CRUD, 게시판 뷰, 전체 CSS 통일 |

---

## ⚙️ 주요 기능

### 1. 역제안 입찰 시스템
- 의뢰인이 프로젝트(카테고리·예산·마감일)를 등록
- 전문가가 제안서(접근 방식·예상 기간·희망 금액) 제출
- 의뢰인이 제안서 비교 후 낙찰자 선택

### 2. 인증 전문가 뱃지
- 별점 4.5 이상 & 리뷰 3건 이상 달성 시 자동 부여
- 프로필·제안서에 골드 뱃지 표시
- 조건 미달 시 자동 박탈

### 3. 제안서 수신 필터
- 의뢰인이 프로젝트별로 수신 필터 설정 가능
  - `ALL` : 모든 전문가 제안서 수신
  - `CERTIFIED_ONLY` : 인증 전문가만 수신
  - `GENERAL_ONLY` : 일반 전문가만 수신

### 4. 에스크로 결제 & 정산
- 낙찰 후 의뢰인 결제 → 플랫폼 에스크로 보관
- 프로젝트 완료 확인 후 전문가에게 정산 (플랫폼 수수료 차감)
- 분쟁 발생 시 관리자 중재

### 5. 전문가 승인 프로세스
- 전문가 회원가입 → `PENDING` 상태
- 관리자가 포트폴리오·자격증 검토 후 `ACTIVE` 승인 / `REJECTED` 거부
- `PENDING` 상태에서는 일반 회원 권한만 부여

---

## 📁 패키지 구조

```
src/main/java/com/project/
├── domain/
│   ├── member/        # Member Entity, Service, Controller, DTO
│   ├── expert/        # ExpertProfile, BadgeService
│   ├── project/       # Project, BidFilterType(Enum)
│   ├── bid/           # Bid Entity, Service, Controller
│   ├── payment/       # Payment, 에스크로 처리
│   ├── review/        # Review, avgRating 집계
│   └── board/         # Board, Comment
├── global/
│   ├── config/        # WebMvcConfig
│   ├── exception/     # GlobalExceptionHandler, CustomException, ErrorCode
│   └── util/          # ApiResponse, FileUploadUtil
└── resources/
└── templates/     # Mustache 템플릿
```

---

## 🗃 ERD 주요 Entity

| Entity | 주요 컬럼 | 관계 |
|---|---|---|
| Member | id, email, role(CLIENT/EXPERT/ADMIN), status(ACTIVE/PENDING/REJECTED) | Project 1:N, Bid 1:N, Review 1:N |
| ExpertProfile | memberId, avgRating, totalReviews, **isCertified** ★ | Member 1:1 |
| Project | clientId, category, budgetMin/Max, **bidFilter** ★ | Bid 1:N, Payment 1:1 |
| Bid | projectId, expertId, proposedPrice, status(PENDING/SELECTED/REJECTED) | Project N:1, Member N:1 |
| Payment | projectId, amount, platformFee, status(PAID/REFUNDED/SETTLED) | Project 1:1 |
| Review | projectId, reviewerId, targetId, rating, content | Project N:1, Member N:1 |

> ★ 인증 전문가 뱃지 / 수신 필터 기능을 위해 추가된 컬럼

---

## 🔀 Git 브랜치 전략

```
main          ← 최종 배포 (직접 push 금지)
└── develop   ← 통합 테스트 (팀장 코드리뷰 후 PR 머지)
    ├── feature/project   # 팀장 - 프로젝트·입찰·수신필터
    ├── feature/admin     # 팀장 - 관리자 페이지
    ├── feature/payment   # 팀원 A - 결제·정산
    ├── feature/login     # 팀원 B - 로그인·승인·뱃지
    ├── feature/review    # 팀원 C - 리뷰·포트폴리오
    └── feature/board     # 팀원 D - 게시판
```

---

## 📝 커밋 메시지 컨벤션

```
<타입>(<스코프>): <제목>
```

| 타입 | 사용 상황 | 예시 |
|---|---|---|
| `feat` | 새 기능 추가 | `feat(bid): 인증 전문가 수신 필터 차단 로직 추가` |
| `fix` | 버그 수정 | `fix(review): avgRating 계산 오류 수정` |
| `refactor` | 리팩토링 | `refactor(expert): isCertified 뱃지 갱신 로직 분리` |
| `docs` | 문서 수정 | `docs: README 업데이트` |
| `style` | 포맷·공백 등 | `style: 불필요한 공백 제거` |
| `test` | 테스트 코드 | `test(badge): 뱃지 자동 부여 단위 테스트 추가` |
| `chore` | 빌드·설정 변경 | `chore: application.properties 환경 분리` |
| `remove` | 파일·코드 삭제 | `remove: 미사용 DTO 클래스 삭제` |

---

## 📏 코딩 컨벤션

### 네이밍 규칙

| 대상 | 규칙 | 예시 |
|---|---|---|
| 클래스 / 인터페이스 | PascalCase | `ExpertProfileService`, `BidController` |
| 메서드 / 변수 | camelCase | `findByCertified()`, `avgRating` |
| 상수 | UPPER_SNAKE_CASE | `MAX_BID_COUNT`, `PLATFORM_FEE_RATE` |
| 패키지 | 소문자 단수형 | `domain.expert`, `domain.bid` |
| DB 컬럼 | snake_case | `avg_rating`, `is_certified` |
| REST URL | 소문자 복수형 kebab-case | `/api/projects`, `/api/expert-profiles` |
| Mustache 템플릿 | kebab-case | `bid-list.mustache`, `expert-badge.mustache` |

### 공통 규칙

- **DTO** : Entity 직접 반환 금지. 요청/응답 DTO 분리 (`XxxRequestDTO`, `XxxResponseDTO`)
- **Lombok** : `@Data` 단독 사용 금지 → `@Getter + @Builder + @NoArgsConstructor + @AllArgsConstructor` 명시
- **트랜잭션** : Service 계층에 `@Transactional` 적용, 조회 전용은 `@Transactional(readOnly=true)`
- **예외 처리** : `GlobalExceptionHandler` 중앙 처리, `CustomException(ErrorCode)` 사용
- **로그** : `System.out.println` 금지 → SLF4J Logger 사용
- **보안** : `isCertified` 갱신은 서버 사이드에서만 처리

### 공통 API 응답 형식

```json
{
  "success": true,
  "message": "요청 성공",
  "data": { },
  "code": null
}
```

---

## 🚀 로컬 실행 방법

```bash
# 1. 레포지토리 클론
git clone https://github.com/your-repo/it-bid-platform.git
cd it-bid-platform

# 2. application.properties 설정
# src/main/resources/application.properties
# DB, JWT Secret, OAuth2 클라이언트 ID/Secret, 결제 키 입력

# 3. MySQL 데이터베이스 생성
CREATE DATABASE it_bid_platform DEFAULT CHARACTER SET utf8mb4;

# 4. 빌드 & 실행
./gradlew bootRun
```

---

> 본 문서는 팀 킥오프 기준 문서입니다. 개발 진행에 따라 팀장 협의 후 수정될 수 있습니다.
