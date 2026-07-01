SET NAMES utf8mb4;

-- ══════════════════════════════════════════
-- 1. MEMBER
-- ══════════════════════════════════════════
INSERT IGNORE INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count, balance)
VALUES
('client01@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김의뢰', '010-1234-5678', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 30 DAY), 0, 20000000),
('expert01@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '홍길동', '010-9876-5432', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 30 DAY), 0, 4500000),
('expert02@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김디자', '010-5555-4444', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 30 DAY), 0, 1350000),
('admin@sevmerge.com',   '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최관리', '010-0000-0000', 'ADMIN',  'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 30 DAY), 0, 1650000),
('client02@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이스타트업', '010-2222-1111', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 25 DAY), 0, 15000000),
('client03@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박대표님',  '010-3333-2222', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 20 DAY), 0, 8000000),
('client04@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최기획자', '010-4444-3333', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 15 DAY), 0, 5000000),
('client05@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '정마케터', '010-5555-4444', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 12 DAY), 0, 3000000),
('client06@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '윤쇼핑몰', '010-6666-5555', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 10 DAY), 0, 12000000),
('client07@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '한제조사', '010-7777-6666', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 8 DAY), 0, 9000000),
('client08@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '강에듀테크','010-8888-7777', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 6 DAY), 0, 7000000),
('client09@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '임헬스케어','010-9999-8888', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0, 10000000),
('client10@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '오핀테크',  '010-1010-9999', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 4 DAY), 0, 25000000),
('expert03@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박모바일', '010-1111-2222', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 10 DAY), 0, 2800000),
('expert04@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최데이터', '010-2222-3333', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 8 DAY), 0, 6700000),
('expert05@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '정영상',   '010-3333-4444', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 6 DAY), 0, 3200000),
('expert06@sevmerge.com','$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '신전문가', '010-4444-5555', 'EXPERT', 'PENDING', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0, 0);

-- ══════════════════════════════════════════
-- 2. EXPERT PROFILE (member 2=expert01, 3=expert02, 14=expert03, 15=expert04, 16=expert05)
-- ══════════════════════════════════════════
INSERT IGNORE INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade, contact_email, github_url)
VALUES
(2, 'https://sevmerge.com/storage/profiles/expert_01.png',
 '안녕하세요! 대기업 출신 프리랜서 풀스택 개발자 홍길동입니다. 대규모 트래픽 처리 및 쇼핑몰, 커뮤니티 웹/앱 개발 전문입니다. 안정적이고 확장성 있는 아키텍처를 약속드립니다.',
 '前 카카오 백엔드 개발자 (3년)\n프리랜서 외주 개발 진행 (4년)\nSevMerge 누적 프로젝트 20건 이상 완료',
 'Spring Boot, Java, AWS, React, Next.js', true, 'NORMAL', 'expert01@sevmerge.com', 'https://github.com/honggildong'),
(3, 'https://sevmerge.com/storage/profiles/expert_02.png',
 '의뢰인님의 추상적인 아이디어를 트렌디하고 직관적인 화면으로 시각화해 드립니다. 와이어프레임 기획부터 피그마 고도화 프로토타입까지 책임지고 가이드해 드립니다.',
 '대형 IT 에이전시 수석 디자이너 (4년)\n스타트업 MVP 디자인 전담 프리랜서 활동 중',
 'Figma, UI/UX Design, Adobe XD, GUI, Branding', true, 'SKILLED', 'expert02@sevmerge.com', NULL),
(14, NULL,
 'iOS/Android 크로스플랫폼 앱 전문 개발자. Flutter 4년, 앱스토어 출시 8건. 빠른 개발과 안정적 품질이 강점.',
 '前 카카오모빌리티 앱 개발자(2년)\n프리랜서 Flutter 전문(2년)\nSevMerge 완료 12건',
 'Flutter, Dart, Firebase, iOS, Android, Kotlin', true, 'SKILLED', 'expert03@sevmerge.com', 'https://github.com/park-mobile'),
(15, NULL,
 'Python 기반 데이터 분석 및 AI 모델 개발 전문. 제조업 품질관리 AI, 추천시스템 다수 경험. Kaggle Expert.',
 '前 네이버 AI 연구팀(2년)\n데이터 컨설팅 프리랜서(3년)\nKaggle Expert',
 'Python, TensorFlow, PyTorch, SQL, Pandas, Scikit-learn, MLflow', true, 'MASTER', 'expert04@sevmerge.com', 'https://github.com/choi-data'),
(16, NULL,
 '유튜브/광고 영상 전문 편집자. 기업 홍보 영상 100편+ 제작. 에프터이펙트 모션그래픽 특기.',
 '광고 에이전시 영상 디렉터(3년)\n프리랜서 영상 편집(2년)\n유튜브 누적 조회수 3천만',
 '영상편집, 프리미어프로, 에프터이펙트, 모션그래픽, 색보정, DaVinci', true, 'SKILLED', 'expert05@sevmerge.com', NULL);

-- ══════════════════════════════════════════
-- 3. PORTFOLIO
-- ══════════════════════════════════════════
INSERT IGNORE INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES
(1, '대형 쇼핑몰 백엔드 시스템 구축', 'Spring Boot 기반 대규모 트래픽 처리 쇼핑몰 백엔드. 일 평균 10만 건 주문 처리.', 'https://sevmerge.com/storage/portfolios/portfolio_01.png', 'https://github.com/honggildong/shopping-mall', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1, '실시간 채팅 기반 커뮤니티 플랫폼', 'WebSocket 실시간 채팅 및 알림. React + Spring Boot 풀스택. AWS EC2 배포.', 'https://sevmerge.com/storage/portfolios/portfolio_02.png', 'https://github.com/honggildong/community-platform', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, '핀테크 스타트업 랜딩페이지 UI/UX 디자인', 'Figma 와이어프레임부터 고도화 프로토타입까지. 브랜드 아이덴티티 정립 및 반응형 디자인 시스템.', 'https://sevmerge.com/storage/portfolios/portfolio_03.png', 'https://www.figma.com/kimdesign/fintech-landing', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(2, '헬스케어 모바일 앱 UI/UX 디자인', '운동 기록 및 식단 관리 앱 전체 화면 설계. UX 리서치 기반 직관적 UI 구현.', 'https://sevmerge.com/storage/portfolios/portfolio_04.png', 'https://www.figma.com/kimdesign/healthcare-app', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, '기업 소개 웹사이트 퍼블리싱', 'HTML5, CSS3, JavaScript 기반 반응형 기업 소개 사이트. Vue.js + Tailwind CSS.', 'https://sevmerge.com/storage/portfolios/portfolio_05.png', 'https://github.com/junior-dev/company-website', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ══════════════════════════════════════════
-- 4. EXPERT WISH
-- ══════════════════════════════════════════
INSERT IGNORE INTO expert_wish_tb (member_id, expert_id, created_at) VALUES
(1, 2, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(1, 3, DATE_SUB(NOW(), INTERVAL 6 DAY));

-- ══════════════════════════════════════════
-- 5. BOOKMARK
-- ══════════════════════════════════════════
INSERT IGNORE INTO book_mark_tb (expert_id, member_id) VALUES (2, 1), (1, 1);

-- ══════════════════════════════════════════
-- 6. PROJECT (id 1-16)
-- ══════════════════════════════════════════
INSERT IGNORE INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES
-- 기존 4개 (id 1-4)
(1, '스타트업 전용 중고 부품 거래 매칭 플랫폼 구축', 'WEB',
 '초기 스타트업용 중고 부품 거래 매칭 웹 및 하이브리드 앱. 결제 및 채팅 기능 필수.',
 5000000, 10000000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'ALL', 'OPEN', 128, false, false, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(1, '제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발', 'APP',
 '공장 라인 부품 이미지 기반 불량 판별 딥러닝 모델. 데이터셋 1만 장 제공. Accuracy 95% 이상 목표.',
 8000000, 15000000, DATE_ADD(NOW(), INTERVAL 45 DAY), 'ALL', 'IN_PROGRESS', 89, false, false, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(1, '기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화', 'APP',
 '반응형 웹 디자인 전면 수정. 신규 브랜드 로고(BI) 가이드라인 제공.',
 2000000, 4000000, DATE_ADD(NOW(), INTERVAL 14 DAY), 'ALL', 'DONE', 67, false, false, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(1, '임시저장 중인 프로젝트 제목', 'WEB', '아직 작성 중인 내용입니다.',
 0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, DATE_SUB(NOW(), INTERVAL 20 DAY)),
-- 신규 12개 (id 5-16)
(5,  '모바일 쇼핑몰 iOS/Android 앱 개발 (React Native)', 'APP',
 'React Native 크로스플랫폼 쇼핑몰 앱. 상품목록, 장바구니, TossPayments 결제, 푸시알림, 관리자 CMS.',
 8000000, 15000000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'ALL', 'OPEN', 42, false, false, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(6,  'AI 기반 고객상담 챗봇 구축 (GPT-4o)', 'WEB',
 'OpenAI GPT-4o API 활용 고객센터 자동화 챗봇. FAQ 학습, 카카오톡 채널 연동, 관리자 대시보드.',
 5000000, 10000000, DATE_ADD(NOW(), INTERVAL 20 DAY), 'ALL', 'OPEN', 37, false, false, DATE_SUB(NOW(), INTERVAL 12 DAY)),
(7,  '핀테크 스타트업 랜딩페이지 & BI 디자인', 'APP',
 'B2B 핀테크 BI 확립 및 반응형 랜딩페이지. 피그마 디자인 시스템 납품 포함.',
 3000000, 6000000, DATE_ADD(NOW(), INTERVAL 14 DAY), 'CERTIFIED_ONLY', 'OPEN', 28, false, false, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(8,  '이커머스 백엔드 API 서버 고도화 (Spring Boot 전환)', 'WEB',
 'Node.js → Spring Boot 마이그레이션. Redis 캐싱, 배치처리, AWS 최적화. TPS 5,000 이상 목표.',
 10000000, 20000000, DATE_ADD(NOW(), INTERVAL 45 DAY), 'CERTIFIED_ONLY', 'OPEN', 56, false, false, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(9,  '헬스케어 앱 운동기록 & 식단관리 ML 기능 추가', 'APP',
 'Flutter + FastAPI. 운동루틴 추천(ML), 식단 사진 칼로리 분석, 소셜 챌린지 기능.',
 6000000, 12000000, DATE_ADD(NOW(), INTERVAL 60 DAY), 'ALL', 'IN_PROGRESS', 19, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(10, '유튜브 채널 홍보영상 시리즈 제작 10편 (4K)', 'APP',
 'B2B SaaS 제품소개 영상 10편. 2분 내외, 모션그래픽, 한영 자막, 4K 납품.',
 4000000, 7000000, DATE_ADD(NOW(), INTERVAL 25 DAY), 'ALL', 'IN_PROGRESS', 33, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(11, '머신러닝 기반 이상거래 탐지 모델 개발', 'WEB',
 '금융 거래 데이터셋 50만건 기반 이상거래 탐지. F1 Score 0.95 이상. 실시간 API 서빙까지.',
 12000000, 22000000, DATE_ADD(NOW(), INTERVAL 90 DAY), 'CERTIFIED_ONLY', 'OPEN', 61, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(12, '기업 내부 인트라넷 그룹웨어 시스템 풀스택 개발', 'WEB',
 '전자결재, 프로젝트관리, 근태관리, 사내메신저 통합. 사용자 200명 규모.',
 20000000, 35000000, DATE_ADD(NOW(), INTERVAL 120 DAY), 'ALL', 'OPEN', 88, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(13, '뷰티 커머스 앱 UI/UX 리뉴얼 디자인 (DAU 5만)', 'APP',
 '기존 앱 전체 리뉴얼. A/B 테스트 기반 UX 개선, 피그마 컴포넌트 라이브러리.',
 5000000, 9000000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'ALL', 'COMPLETED', 45, false, false, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(5,  '실시간 경매 플랫폼 개발 (WebSocket + 에스크로)', 'WEB',
 '실시간 경매 중고품 거래 플랫폼. WebSocket 입찰, Toss 에스크로, 인증, 반응형 전체 개발.',
 7000000, 14000000, DATE_ADD(NOW(), INTERVAL 50 DAY), 'ALL', 'DONE', 72, false, false, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(6,  '데이터 시각화 BI 대시보드 웹 앱 (Chart.js + D3.js)', 'WEB',
 '사내 KPI 모니터링 BI 대시보드. Chart.js + D3.js, MySQL 연동, 실시간 SSE, 역할 기반 권한.',
 4500000, 8000000, DATE_ADD(NOW(), INTERVAL 35 DAY), 'ALL', 'OPEN', 29, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7,  'AWS 인프라 구축 & DevOps CI/CD 파이프라인', 'WEB',
 'AWS EC2/RDS/S3/CloudFront 구축 및 Github Actions CI/CD. Terraform IaC, Grafana/Prometheus.',
 3000000, 6000000, DATE_ADD(NOW(), INTERVAL 21 DAY), 'CERTIFIED_ONLY', 'OPEN', 38, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ══════════════════════════════════════════
-- 7. BID
-- ══════════════════════════════════════════
INSERT IGNORE INTO bid_tb (project_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, is_deleted, created_at)
VALUES
-- 기존 5개 (프로젝트 1-3)
(1, 2, '웹 개발 5년 경력자입니다. 최선을 다하겠습니다.', 'React + Spring Boot로 개발하겠습니다.', 30, 2500000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 29 DAY)),
(1, 3, '쇼핑몰 개발 다수 경험 있습니다.', 'Vue.js + Node.js로 개발하겠습니다.', 25, 2800000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 29 DAY)),
(2, 2, '앱 개발 전문가입니다.', 'Flutter + Firebase로 개발하겠습니다.', 60, 8000000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(3, 3, 'UI/UX 디자인 전문입니다.', '피그마로 작업하겠습니다.', 14, 1500000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(3, 2, '풀스택으로 디자인까지 커버합니다.', 'React + Figma 병행 작업.', 21, 3500000, 'REJECTED', false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
-- 프로젝트 13 (COMPLETED) 낙찰=김디자(3)
(13, 3, 'UI/UX 전문가입니다. 뷰티 커머스 앱 리뉴얼 10건+ 경험. 전환율 최적화 강점.', '1주차 UX 리서치\n2-3주차 피그마 고도화\n4주차 컴포넌트 라이브러리', 28, 7500000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(13, 2, '풀스택이지만 디자인 가능합니다. 전문 디자이너가 더 적합할 것 같습니다.', 'UX 리서치 후 설계하겠습니다.', 30, 8000000, 'REJECTED', false, DATE_SUB(NOW(), INTERVAL 19 DAY)),
-- 프로젝트 14 (DONE) 낙찰=홍길동(2)
(14, 2, '실시간 경매 플랫폼 경험 다수. WebSocket STOMP, 동시성 처리, 에스크로 연동 완벽 구현 가능.', '1-2주차 설계\n3-6주차 핵심 기능\n7-8주차 테스트+배포', 55, 12000000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(14, 14, 'Flutter 경매 플랫폼 개발 경험 있습니다.', 'React Native + Node.js로 빠르게 개발.', 40, 11000000, 'REJECTED', false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
-- 프로젝트 9 (IN_PROGRESS) 낙찰=박모바일(14)
(9,  14, 'Flutter + FastAPI 4년. 헬스케어 앱 ML 추천 기능 구현 경험. PyTorch 비전 모델.', '1-2주차 API 설계\n3-6주차 앱 기능\n7-8주차 베타+출시', 60, 9800000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(9,  15, 'AI 모델 강하지만 앱 개발은 협업 필요합니다.', 'FastAPI + 추천 모델 전담 개발.', 45, 8000000, 'REJECTED', false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- 프로젝트 10 (IN_PROGRESS) 낙찰=정영상(16)
(10, 16, '광고대행사 출신 영상 디렉터. B2B SaaS 영상 시리즈 다수. 4K 납품 가능.', '1주차 기획/스크립트\n2-3주차 촬영+편집\n4주차 수정+납품', 28, 5500000, 'SELECTED', false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 2,  '영상 비전공이지만 기본 편집 가능합니다.', '기본 편집으로 진행.', 20, 3000000, 'REJECTED', false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
-- 오픈 프로젝트 PENDING 입찰
(5,  14, 'React Native 4년, iOS/Android 쇼핑몰 앱 3건. TossPayments SDK 연동 경험.', '1주차 설계\n2-5주차 기능\n6주차 배포', 45, 11000000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5,  2,  'React Native + Spring Boot로 쇼핑몰 구축 가능합니다.', 'API-first 방식으로 개발.', 40, 12000000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6,  2,  'GPT API 연동 및 웹 백엔드 전문. 챗봇 플로우부터 배포까지 원스톱.', 'Spring AI + GPT-4o로 구축.', 30, 7500000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6,  15, 'NLP/LLM 전문가. RAG 아키텍처로 정확도 높은 챗봇 구축 가능.', 'LangChain + FastAPI로 구축.', 25, 8500000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7,  3,  'UI/UX 디자이너. 핀테크 랜딩페이지 다수. 피그마 컴포넌트 라이브러리까지 납품.', '1주차 레퍼런스\n2주차 와이어프레임\n3주차 고도화', 21, 4500000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8,  2,  'Spring Boot 마이그레이션 전문. Redis, AWS 최적화 경험 다수.', '단계적 마이그레이션으로 무중단 전환 보장.', 50, 16000000, 'PENDING', false, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ══════════════════════════════════════════
-- 8. PAYMENT (project_id UNIQUE — 프로젝트당 1건)
-- id=1: project 2 (IN_PROGRESS, PAID)
-- id=2: project 3 (DONE, SETTLED)
-- id=3: project 9 (IN_PROGRESS, PAID)
-- id=4: project 10 (IN_PROGRESS, PAID)
-- id=5: project 13 (COMPLETED, PAID)
-- id=6: project 14 (DONE, SETTLED)
-- ══════════════════════════════════════════
INSERT IGNORE INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES
(2,  1,  2,  8000000,  240000, 7760000,  'toss_key_proj02', 'card',      'PAID',    DATE_SUB(NOW(), INTERVAL 27 DAY)),
(3,  1,  3,  1500000,  45000,  1455000,  'toss_key_proj03', 'kakaopay', 'SETTLED', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(9,  9,  14, 9800000,  294000, 9506000,  'toss_key_proj09', 'card',      'PAID',    DATE_SUB(NOW(), INTERVAL 5 DAY)),
(10, 10, 16, 5500000,  165000, 5335000,  'toss_key_proj10', 'card',      'PAID',    DATE_SUB(NOW(), INTERVAL 4 DAY)),
(13, 13, 3,  7500000,  225000, 7275000,  'toss_key_proj13', 'kakaopay', 'PAID',    DATE_SUB(NOW(), INTERVAL 18 DAY)),
(14, 5,  2,  12000000, 360000, 11640000, 'toss_key_proj14', 'card',      'SETTLED', DATE_SUB(NOW(), INTERVAL 23 DAY));

-- ══════════════════════════════════════════
-- 9. CHARGE HISTORY
-- ══════════════════════════════════════════
INSERT IGNORE INTO charge_tb (member_id, amount, order_id, payment_key, status, created_at)
VALUES
(1, 10000000, 'order_c01_001', 'toss_charge_001', 'DONE', DATE_SUB(NOW(), INTERVAL 35 DAY)),
(1, 10000000, 'order_c01_002', 'toss_charge_002', 'DONE', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1,  5000000, 'order_c01_003', 'toss_charge_003', 'DONE', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(5, 15000000, 'order_c05_001', 'toss_charge_004', 'DONE', DATE_SUB(NOW(), INTERVAL 28 DAY)),
(6,  8000000, 'order_c06_001', 'toss_charge_005', 'DONE', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(9,  9800000, 'order_c09_001', 'toss_charge_006', 'DONE', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(10, 5500000, 'order_c10_001', 'toss_charge_007', 'DONE', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(13,10000000, 'order_c13_001', 'toss_charge_008', 'DONE', DATE_SUB(NOW(), INTERVAL 22 DAY));

-- ══════════════════════════════════════════
-- 10. WITHDRAWAL HISTORY
-- ══════════════════════════════════════════
INSERT IGNORE INTO withdrawal_tb (member_id, amount, bank_name, account_number, account_holder, status, created_at)
VALUES
(2, 3000000, '카카오뱅크', '3333-01-7654321', '홍길동', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 1500000, '토스뱅크',  '1234-56-7891234', '홍길동', 'PENDING',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3,  800000, '신한은행',  '110-123-456789',  '김디자', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 8 DAY));

-- ══════════════════════════════════════════
-- 11. CHAT ROOM + MESSAGES
-- room 1: project 1, client 1, expert 2
-- room 2: project 1, client 1, expert 3
-- room 3: project 9, client 9, expert 14
-- ══════════════════════════════════════════
-- 채팅 더미데이터 삭제됨 (실제 사용자 데이터만 표시)
-- ══════════════════════════════════════════

-- ══════════════════════════════════════════
-- 12. ESCROW SETTLEMENT REQUEST (payment 5 = project 13)
-- ══════════════════════════════════════════
INSERT IGNORE INTO escrow_settlement_request_tb (payment_id, expert_id, project_id, message, status, created_at)
VALUES
(5, 3, 13, '뷰티 커머스 앱 UI/UX 리뉴얼 완료. 최종 피그마 파일 납품 완료, 의뢰인 확인 요청드립니다.', 'PENDING', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ══════════════════════════════════════════
-- 13. REVIEW
-- ══════════════════════════════════════════
INSERT IGNORE INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES
(1,  2, 5, '정말 꼼꼼하고 빠르게 작업해 주셨어요. 요구사항을 잘 이해하고 결과물도 깔끔했습니다.', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(4,  2, 4, '소통이 원활하고 일정도 잘 지켜주셨습니다. 다음에도 함께하고 싶어요.', DATE_SUB(NOW(), INTERVAL 20 DAY)),
(1,  2, 5, '기술적으로 매우 뛰어나신 분입니다. 복잡한 작업도 깔끔하게 처리해 주셨어요.', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(5,  2, 5, '실시간 경매 플랫폼 완벽하게 개발해주셨습니다. 일정 준수, 코드 품질 최상급!', DATE_SUB(NOW(), INTERVAL 22 DAY)),
(6,  2, 4, 'AI 챗봇 연동 잘 해주셨어요. 소통 원활하고 빠른 피드백.', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7,  2, 5, '복잡한 시스템도 거뜬히 처리. 다음 프로젝트도 맡길 예정.', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(8,  2, 5, '최고의 개발자. 예산 내에서 최대 결과물을 뽑아주셨어요.', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(9,  14, 5, '헬스케어 앱 ML 기능 완벽 구현. Flutter 실력 탁월합니다.', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 16, 4, '영상 퀄리티 기대 이상. 모션그래픽도 깔끔하게 잘 해주셨어요.', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(13, 3,  5, '뷰티 앱 UI/UX 리뉴얼 완벽. 피그마 라이브러리도 깔끔하게 납품.', DATE_SUB(NOW(), INTERVAL 16 DAY)),
(5,  3,  4, '디자인 감각 탁월합니다. 스타트업 BI 디자인도 맡기고 싶어요.', DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ══════════════════════════════════════════
-- 14. BOARD (자유/공지/1:1)
-- ══════════════════════════════════════════
INSERT IGNORE INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES
('FREE', '프로젝트 낙찰률 200% 올리는 역제안서 작성 팁',
 'SevMerge에서 활동 중인 개발자입니다. 의뢰인의 기획서가 구체적이지 않을 때, 역으로 기능명세서를 가볍게 초안으로 작성해서 입찰 제안서에 첨부해 보세요. 신뢰도가 확 올라갑니다.',
 145, true, 2, DATE_SUB(NOW(), INTERVAL 20 DAY)),
('FREE', '스타트업 MVP 앱 외주 비용 보통 이 정도면 적당한가요?',
 '채팅이랑 결제 기능이 들어간 크로스플랫폼 앱인데, 예산을 500만 원에서 1,000만 원 사이로 잡고 등록하려 합니다. 전문가분들 견해 부탁드립니다.',
 182, true, 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),
('NOTICE', '[안내] 안심 대금 에스크로 수수료 정산 방식 변경 공지',
 '안녕하세요. SevMerge 팀입니다. 에스크로 안전 결제 시스템이 고도화되었습니다. 수수료 3%. 상세 내용은 결제 가이드를 참고해 주세요.',
 510, true, 4, DATE_SUB(NOW(), INTERVAL 30 DAY)),
('FREE', '처음 SevMerge에서 낙찰 받았습니다!',
 '오늘 첫 낙찰 알림을 받았습니다. 에스크로 결제도 바로 확인돼서 믿음직스럽네요!',
 156, true, 2, DATE_SUB(NOW(), INTERVAL 10 DAY)),
('FREE', '의뢰 예산 설정 노하우 공유',
 '예산 범위를 너무 낮게 잡으면 좋은 전문가 제안서가 안 들어옵니다. 시장가의 80% 이상은 잡으세요.',
 203, true, 1, DATE_SUB(NOW(), INTERVAL 8 DAY)),
('FREE', 'Spring Boot vs Node.js 외주 시장 선호도?',
 '개인적으로 Spring Boot 주력인데 최근 Node.js 선호도가 높아진 것 같아서요. 의견 부탁드립니다.',
 89, true, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('NOTICE', '[공지] AI 의뢰서 작성 도우미 정식 출시',
 'Google Gemini 2.5 Flash 기반 AI 의뢰서 작성 도우미 출시! 프로젝트 등록 시 "AI 도움받기" 버튼을 활용하세요.',
 892, true, 4, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('NOTICE', '[이벤트] 7월 첫 낙찰 전문가 수수료 0%',
 '2026년 7월 첫 낙찰 신규 전문가에게 플랫폼 수수료 0% 혜택! 포트폴리오를 쌓을 기회입니다.',
 1243, true, 4, DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT IGNORE INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES
('INQUIRY', '결제 후 환불이 가능한가요?',
 '프로젝트 진행 중 전문가와 협의가 안 되어서 중단하게 됐는데, 이미 결제한 금액 환불 절차가 어떻게 되는지 궁금합니다.',
 0, true, 1, 'PAYMENT', DATE_SUB(NOW(), INTERVAL 25 DAY)),
('INQUIRY', '카카오 로그인 후 비밀번호 변경이 안 됩니다',
 '카카오 소셜 로그인으로 가입했는데 비밀번호 변경 메뉴가 비활성화되어 있습니다. 일반 로그인으로 전환하는 방법이 있나요?',
 0, true, 1, 'SECURITY', DATE_SUB(NOW(), INTERVAL 20 DAY)),
('INQUIRY', '전문가 등록 심사 기간이 얼마나 걸리나요?',
 '전문가 등록 신청한 지 3일이 지났는데 아직 승인이 안 됐습니다. 보통 심사 기간이 어느 정도 걸리는지 알고 싶습니다.',
 0, true, 1, 'NORMAL', DATE_SUB(NOW(), INTERVAL 15 DAY));

-- ══════════════════════════════════════════
-- 15. COMMENT
-- ══════════════════════════════════════════
INSERT IGNORE INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES
(1, 3, '오... 기능명세서 초안을 역으로 제안하는 방식은 생각 못 해봤는데 좋은 팁이네요!', DATE_SUB(NOW(), INTERVAL 19 DAY), false),
(1, 4, '작성자님, 클라이언트가 요구사항을 한 줄로만 적어둔 경우에도 이 방식이 통할까요?', DATE_SUB(NOW(), INTERVAL 18 DAY), false),
(2, 2, '채팅에 결제까지 포함된 크로스플랫폼 앱이라면 700만~900만 원 선이 가장 매칭률이 높을 것 같습니다.', DATE_SUB(NOW(), INTERVAL 14 DAY), false),
(2, 1, '저도 비슷한 스펙으로 등록하려고 했는데, 먼저 질문해 주셔서 좋은 정보 얻고 갑니다!', DATE_SUB(NOW(), INTERVAL 13 DAY), false);

-- ══════════════════════════════════════════
-- 16. COMMENT REPORT
-- ══════════════════════════════════════════
INSERT IGNORE INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES
(1, 2, '욕설 / 비속어 및 혐오 표현', '특정 사용자를 향해 지속적으로 공격적인 비속어를 유포하고 있어 커뮤니티 환경을 해치고 있습니다.', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(2, 3, '스팸 광고 / 홍보성 도배', '댓글 내에 허가받지 않은 불법 링크를 반복적으로 도배하고 있습니다.', DATE_SUB(NOW(), INTERVAL 17 DAY)),
(3, 1, '타인 비방 및 명예훼손', '근거 없는 허위 사실로 명예훼손하고 있습니다.', DATE_SUB(NOW(), INTERVAL 16 DAY));

-- ══════════════════════════════════════════
-- 17. BLACKLIST
-- ══════════════════════════════════════════
INSERT IGNORE INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES
(1, '댓글 신고 3회 누적 자동 정지 처리 (욕설 및 비속어 유포)', '4,5,6', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), true),
(2, '댓글 신고 3회 누적 자동 정지 처리 (홍보성 스팸 도배)', '7,8,9', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), true);

-- ══════════════════════════════════════════
-- 18. MESSAGE (쪽지)
-- ══════════════════════════════════════════
INSERT IGNORE INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender, is_deleted_by_receiver, created_at)
VALUES
(1, 2, 1, '[문의] 채팅 및 결제 기능 일정 확인 부탁드립니다',
 '안녕하세요, 홍길동 개발자님. 제출해주신 입찰 제안서 잘 검토하였습니다. 채팅 기능과 결제 연동을 30일 일정 내에 모두 처리 가능하신지 확인 부탁드립니다.',
 true, false, false, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(2, 1, 1, 'Re: [문의] 채팅 및 결제 기능 일정 확인 부탁드립니다',
 '안녕하세요! 채팅 기능과 결제 연동 모두 30일 일정 내에 처리 가능합니다. 요구사항 정의서를 빠른 시일 내에 전달해 주시면 감사하겠습니다.',
 false, false, false, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(1, 3, 3, '랜딩페이지 1차 시안 일정 문의드립니다',
 '안녕하세요 김디자 디자이너님. 브랜드 가이드라인 전달드렸는데 확인하셨나요? 초기 와이어프레임 시안 일정 공유 부탁드립니다.',
 true, false, false, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, 1, 3, 'Re: 랜딩페이지 1차 시안 일정 문의드립니다',
 '안녕하세요! 브랜드 가이드라인 잘 확인하였습니다. 이번 주 금요일까지 1차 와이어프레임 시안 공유드리겠습니다.',
 false, false, false, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(4, 1, NULL, '[SevMerge 안내] 에스크로 안전 결제 프로세스 업데이트',
 '안녕하세요, SevMerge 운영팀입니다. 에스크로 안전 결제 시스템이 업데이트되었습니다. 수수료는 3%입니다.',
 false, false, false, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(2, 1, 2, '[AI 모델] 학습 데이터셋 전달 방법 문의',
 '안녕하세요! AI 컴퓨터 비전 모델 관련하여 라벨링 완료된 데이터셋 1만 장을 어떤 방식으로 전달받을 수 있을지 문의드립니다. AWS S3 공유 혹은 구글 드라이브 중 편하신 방법으로 부탁드립니다.',
 false, false, false, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 2, 2, 'Re: [AI 모델] 학습 데이터셋 전달 방법 문의',
 'AWS S3 버킷을 공유드리겠습니다. 잠시 후 이메일로 접근 권한 정보를 보내드리겠습니다. 라벨링 형식은 YOLO 포맷으로 되어 있습니다.',
 true, false, false, NOW());

-- ══════════════════════════════════════════
-- 19. NOTIFICATION
-- ══════════════════════════════════════════
INSERT IGNORE INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES
(1, 'NEW_BID', '홍길동 전문가가 ''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제안서를 제출했습니다.', '/projects/1', false, false, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
(1, 'NEW_BID', '김디자 전문가가 ''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제안서를 제출했습니다.', '/projects/1', false, false, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 'NEW_BID', '홍길동 전문가가 ''제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발'' 프로젝트에 제안서를 제출했습니다.', '/projects/2', true, false, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(1, 'MESSAGE_RECEIVED', '홍길동님이 ''[AI 모델] 학습 데이터셋 전달 방법 문의'' 쪽지를 보냈습니다.', '/messages', false, false, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 'MESSAGE_RECEIVED', '김디자님이 ''Re: 랜딩페이지 1차 시안 일정 문의드립니다'' 쪽지를 보냈습니다.', '/messages', true, false, DATE_SUB(NOW(), INTERVAL 19 DAY)),
(2, 'BID_SELECTED', '축하합니다! ''제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발'' 프로젝트에 제출한 제안서가 낙찰되었습니다.', '/bids/my-orders', false, false, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(2, 'PAYMENT_COMPLETED', '''제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발'' 프로젝트의 대금 결제가 완료되어 작업을 시작할 수 있습니다.', '/bids/my-orders', false, false, DATE_SUB(NOW(), INTERVAL 27 DAY)),
(2, 'EXPERT_APPROVED', '전문가 신청이 승인되었습니다. 지금 바로 활동을 시작해보세요!', '/experts/dashboard', true, false, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(2, 'MESSAGE_RECEIVED', '김의뢰님이 ''[문의] 채팅 및 결제 기능 일정 확인 부탁드립니다'' 쪽지를 보냈습니다.', '/messages', true, false, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(3, 'BID_SELECTED', '축하합니다! ''기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화'' 프로젝트에 제출한 제안서가 낙찰되었습니다.', '/bids/my-list', false, false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
(3, 'PAYMENT_COMPLETED', '''기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화'' 프로젝트의 대금 결제가 완료되어 작업을 시작할 수 있습니다.', '/bids/my-orders', false, false, DATE_SUB(NOW(), INTERVAL 22 DAY)),
(3, 'BID_REJECTED', '''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제출한 제안서가 아쉽게도 선정되지 않았습니다.', '/bids/my-list', true, false, DATE_SUB(NOW(), INTERVAL 24 DAY)),
-- 스케줄러 테스트 데이터
(1, 'NEW_BID', '[스케줄러테스트] 35일 지난 알림 — 삭제돼야 함', '/projects/1', true, false, DATE_SUB(NOW(), INTERVAL 35 DAY)),
(1, 'MESSAGE_RECEIVED', '[스케줄러테스트] 100일 지난 알림 — 삭제돼야 함', '/messages', true, false, DATE_SUB(NOW(), INTERVAL 100 DAY)),
(1, 'BID_SELECTED', '[스케줄러테스트] 소프트삭제된 알림 — 삭제돼야 함', '/bids/my-orders', true, true, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(2, 'EXPERT_APPROVED', '[스케줄러테스트] 31일 지난 알림 — 삭제돼야 함', '/experts/dashboard', true, false, DATE_SUB(NOW(), INTERVAL 31 DAY)),
(1, 'NEW_BID', '[스케줄러테스트] 29일 지난 알림 — 남아야 함', '/projects/1', true, false, DATE_SUB(NOW(), INTERVAL 29 DAY)),
(1, 'MESSAGE_RECEIVED', '[스케줄러테스트] 1일 지난 알림 — 남아야 함', '/messages', false, false, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ══════════════════════════════════════════
-- 20. FAQ
-- ══════════════════════════════════════════
INSERT IGNORE INTO faq_tb (question, answer)
VALUES
('에스크로 결제란 무엇인가요?', '의뢰인이 프로젝트 대금을 플랫폼에 먼저 예치하고, 작업 완료 후 의뢰인이 확인하면 전문가에게 지급되는 안전 결제 방식입니다. 작업 미완료 시 전액 환불이 가능합니다.'),
('전문가로 등록하려면 어떻게 하나요?', '회원가입 시 "전문가"로 역할을 선택하면 관리자 심사를 거칩니다. 포트폴리오와 경력을 상세히 작성하면 승인이 빨라집니다.'),
('플랫폼 수수료는 얼마인가요?', '전문가 정산 시 거래 금액의 3%가 수수료로 차감됩니다. 100만 원 계약 시 전문가 수령액은 97만 원입니다.'),
('프로젝트 환불은 어떻게 하나요?', 'PAID(에스크로 보관 중) 상태에서는 의뢰인 또는 관리자가 환불 처리할 수 있습니다. SETTLED(정산 완료) 이후는 환불 불가입니다.'),
('역제안 입찰 방식이란?', '의뢰인이 프로젝트를 등록하면 전문가들이 먼저 제안서를 제출하는 방식입니다. 여러 제안서를 비교해 최적의 전문가를 선택하세요.'),
('낙찰 후 채팅은 어떻게 하나요?', '낙찰 처리 시 의뢰인과 전문가 사이에 전용 채팅방이 자동 생성됩니다. WebSocket 기반 실시간 채팅으로 파일 첨부도 가능합니다.'),
('전문가 등급제(NORMAL/SKILLED/MASTER)는?', '단순 별점이 아닌 리뷰 수, 완료 프로젝트 건수, 전체 평균을 반영한 베이지안 통계 방식으로 등급이 산출됩니다.'),
('AI 의뢰서 작성 도우미는?', '프로젝트 등록 폼에서 AI 도움 버튼을 클릭하면 Gemini 2.5 Flash가 의뢰 내용 구체화, 예산 산정, 요구사항 정리를 도와줍니다.');

-- ══════════════════════════════════════════
-- 21. PARTNERSHIP
-- ══════════════════════════════════════════
INSERT IGNORE INTO partner_ship_tb (company_name, manager_name, email, partner_file_url, content, status, created_at, deleted_at)
VALUES
('테크이노베이션',   '김철수', 'chulsoo@techinn.com',       '/files/partnership/proposal_tech_2026.pdf',  '인공지능 매칭 시스템 제휴를 제안합니다.',              'PENDING',  DATE_SUB(NOW(), INTERVAL 20 DAY), NULL),
('글로벌네트웍스',   '이영희', 'younghee@globalnet.co.kr',  '/files/partnership/company_profile.docx',   '전문가 풀(Pool) 공유 및 마케팅 협업 문의입니다.',       'APPROVED', DATE_SUB(NOW(), INTERVAL 15 DAY), NULL),
('스패머컴퍼니',     '박배두', 'badguy@spambot.com',         '/files/partnership/ad_flyer.png',           '단순 광고성 제휴 제안서입니다.',                      'REJECTED', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('넥스트스타트업',   '김창업자', 'ceo@nextstartup.kr',       'https://example.com/files/nextstartup_proposal.pdf',  '개발자 매칭 서비스와 교육 콘텐츠 제휴를 제안드립니다. 월 500명 개발자 회원 보유.', 'PENDING', DATE_SUB(NOW(), INTERVAL 5 DAY), NULL),
('에듀테크코리아',   '박교육',   'edu@edutechkorea.com',     'https://example.com/files/edutechkorea_proposal.pdf', 'IT 교육 수료생들의 포트폴리오 프로젝트 연계 협력을 제안합니다.',                   'PENDING', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL);

-- ══════════════════════════════════════════
-- 22. AD SLOT + AD BID
-- ══════════════════════════════════════════
-- slot 1: 메인 배너 (OPEN, 현재 진행 중)
-- slot 2: 사이드 배너 A (AWARDED, 낙찰 완료)
-- slot 3: 프로젝트 목록 상단 배너 (OPEN, 입찰 진행 중)
INSERT IGNORE INTO ad_slot_tb (slot_name, slot_type, min_bid_price, bid_start_at, bid_end_at, display_start_at, display_end_at, winner_expert_id, winning_price, status, created_at)
VALUES
('메인 배너', 'MAIN_BANNER', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE), NULL, NULL, NULL, NULL, 'OPEN', NOW()),
('사이드 배너 A', 'SIDE_BANNER', 5000, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR), DATE_ADD(NOW(), INTERVAL 1 HOUR), 2, 50000, 'AWARDED', DATE_SUB(NOW(), INTERVAL 3 DAY)),
('프로젝트 목록 상단 배너', 'FEATURED_PROJECT', 20000, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 9 HOUR), NULL, NULL, NULL, NULL, 'OPEN', DATE_SUB(NOW(), INTERVAL 2 DAY));

INSERT IGNORE INTO ad_bid_tb (slot_id, expert_id, bid_price, ad_message, review_status, status, created_at)
VALUES
(2, 2,  50000, '풀스택 개발 전문가 홍길동 - 웹/앱 개발 문의주세요!', 'APPROVED', 'WINNER',  DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3,  30000, '김디자 UI/UX 전문 - 트렌디한 디자인 맡겨주세요',    'APPROVED', 'LOST',    DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 14, 20000, '박모바일 앱 전문 - iOS/Android 동시 개발',           'APPROVED', 'LOST',    DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 15, 25000, '최데이터 AI/데이터 분석 전문가',                     'PENDING',  'PENDING', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 16, 35000, '정영상 프로 영상편집 전문가',                         'PENDING',  'PENDING', DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 낙찰 배너 이미지 업데이트 (경매 낙찰 광고 메인 노출용)
UPDATE ad_bid_tb SET banner_image = 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=1200&h=200&fit=crop' WHERE expert_id = 2 AND status = 'WINNER';

-- ══════════════════════════════════════════
-- 23. ADVERTISEMENT (추천 상단 광고 전문가 — MAIN_BANNER ACTIVE)
-- ══════════════════════════════════════════
INSERT IGNORE INTO advertisement_tb (expert_id, placement, price, start_date, end_date, custom_message, banner_image, status, created_at)
VALUES
(2,  'MAIN_BANNER', 10000,
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY),
 '대기업 출신 풀스택 개발자 · Spring Boot + React 전문 · 10년 경력 · 5일 내 MVP 출시 보장',
 'https://picsum.photos/seed/adm2/1200/300',
 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3,  'MAIN_BANNER', 10000,
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY),
 '스타트업 MVP UI/UX 전문 · Figma → 코드 납품 · 1주일 내 고퀄리티 디자인 시스템 완성',
 'https://picsum.photos/seed/adm3/1200/300',
 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(14, 'MAIN_BANNER', 10000,
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY),
 'Flutter 4년 · iOS/Android 동시 출시 · 앱스토어 출시 8건 · 무한 AS 보장',
 'https://picsum.photos/seed/adm14/1200/300',
 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ══════════════════════════════════════════
-- 23-1. ADVERTISEMENT — EXPERT_CAROUSEL ACTIVE (캐러셀 노출 중)
-- ══════════════════════════════════════════
INSERT IGNORE INTO advertisement_tb (expert_id, placement, price, start_date, end_date, custom_message, banner_image, status, created_at)
VALUES
(14, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY),
 'Flutter 크로스플랫폼 · iOS/Android 동시 출시 · 앱스토어 출시 8건',
 'https://picsum.photos/seed/adc14/800/200',
 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(15, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 6 DAY),
 'Python AI 분석 · Kaggle Expert · 데이터 기반 의사결정 전문',
 'https://picsum.photos/seed/adc15/800/200',
 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- ══════════════════════════════════════════
-- 23-2. ADVERTISEMENT — PENDING (관리자 승인 대기 중)
-- ══════════════════════════════════════════
INSERT IGNORE INTO advertisement_tb (expert_id, placement, price, start_date, end_date, custom_message, banner_image, status, created_at)
VALUES
(15, 'MAIN_BANNER', 10000,
 NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY),
 'Kaggle Expert · 제조업 AI 품질관리 · 추천시스템 설계 전문',
 'https://placehold.co/1200x300/4f46e5/ffffff?text=AI+Data+Expert',
 'PENDING', DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(16, 'MAIN_BANNER', 10000,
 NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY),
 '유튜브·광고 영상 전문 편집 · 모션그래픽 · 빠른 납기 · 100편+ 제작 실적',
 'https://placehold.co/1200x300/dc2626/ffffff?text=Video+Editor+Pro',
 'PENDING', DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(16, 'EXPERT_CAROUSEL', 5000,
 NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY),
 '기업 홍보 영상 전문 · 에프터이펙트 모션그래픽 · 유튜브 3천만뷰',
 'https://placehold.co/800x200/f97316/ffffff?text=Motion+Video+Pro',
 'PENDING', DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- ══════════════════════════════════════════
-- 23-3. ADVERTISEMENT — REJECTED (거절, 포인트 환불 처리됨)
-- ══════════════════════════════════════════
INSERT IGNORE INTO advertisement_tb (expert_id, placement, price, start_date, end_date, custom_message, banner_image, reject_reason, status, created_at)
VALUES
(14, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY),
 '앱 개발 최저가 보장 · 무조건 1주 납기 확약',
 'https://placehold.co/800x200/6b7280/ffffff?text=Mobile+Dev',
 '광고 문구에 "최저가 보장", "무조건" 등 과장 표현이 포함되어 있습니다. 수정 후 재신청 해주세요.',
 'REJECTED', DATE_SUB(NOW(), INTERVAL 15 DAY)),
(2, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY),
 '풀스택 개발 전문 · 당일 미팅 가능 · 전화 상담 무제한',
 'https://placehold.co/800x200/1e40af/ffffff?text=Fullstack+Dev',
 '연락처 노출 및 플랫폼 외부 접촉 유도 문구가 운영 정책에 위배됩니다.',
 'REJECTED', DATE_SUB(NOW(), INTERVAL 20 DAY));

-- ══════════════════════════════════════════
-- 23-4. ADVERTISEMENT — EXPIRED (기간 만료)
-- ══════════════════════════════════════════
INSERT IGNORE INTO advertisement_tb (expert_id, placement, price, start_date, end_date, custom_message, banner_image, status, created_at)
VALUES
(2, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY),
 'Spring Boot + React 풀스택 · 대기업 출신 · AWS 인프라 포함 납품',
 'https://picsum.photos/seed/exp2c/800/200',
 'EXPIRED', DATE_SUB(NOW(), INTERVAL 21 DAY)),
(3, 'EXPERT_CAROUSEL', 5000,
 DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY),
 'Figma 고도화 · 모바일 반응형 UI 전문 · 스타트업 MVP 납품 경험 다수',
 'https://picsum.photos/seed/exp3c/800/200',
 'EXPIRED', DATE_SUB(NOW(), INTERVAL 14 DAY)),
(3, 'MAIN_BANNER', 10000,
 DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 33 DAY),
 'UI/UX 전문 디자이너 · Figma → 코드 납품 · 반응형 100% 보장',
 'https://picsum.photos/seed/exp3m/1200/300',
 'EXPIRED', DATE_SUB(NOW(), INTERVAL 40 DAY));
