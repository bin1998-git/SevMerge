INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at)
VALUES ('client01@sevmerge.com', '$2b$10$6om5ZhIXnA0qfyQwh7Y5xefzlXoacZzVMQEjBnpWskmVB/zWkhpJ2', '김의뢰', '010-1234-5678', 'CLIENT', 'ACTIVE', false,NOW());

INSERT INTO member_tb (email, password, name, phone, role, status,is_deleted, created_at)
VALUES ('expert01@sevmerge.com', '$2b$10$6om5ZhIXnA0qfyQwh7Y5xefzlXoacZzVMQEjBnpWskmVB/zWkhpJ2', '홍길동', '010-9876-5432', 'EXPERT', 'ACTIVE',false, NOW());

INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at)
VALUES ('expert02@sevmerge.com', '$2b$10$6om5ZhIXnA0qfyQwh7Y5xefzlXoacZzVMQEjBnpWskmVB/zWkhpJ2', '김디자', '010-5555-4444', 'EXPERT', 'ACTIVE',false, NOW());

INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at)
VALUES ('admin@sevmerge.com', '$2b$10$6om5ZhIXnA0qfyQwh7Y5xefzlXoacZzVMQEjBnpWskmVB/zWkhpJ2', '최관리', '010-0000-0000', 'ADMIN', 'ACTIVE', false,NOW());



-- Board 샘플데이터
-- 1번 샘플 (자유게시판): 프리랜서 전문가의 제안서 작성 노하우 공유
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
        VALUES ('FREE', '프로젝트 낙찰률 200% 올리는 역제안서 작성 팁',
        'SevMerge에서 활동 중인 개발자입니다. 의뢰인의 기획서가 구체적이지 않을 때, 역으로 기능명세서를 가볍게 초안으로 작성해서 입찰 제안서에 첨부해 보세요. 클라이언트 신뢰도가 확 올라갑니다.',
        45, true, 2, NOW());

-- 2번 샘플 (자유게시판): 의뢰인의 외주 비용 관련 질문
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '스타트업 MVP 앱 외주 비용 보통 이 정도면 적당한가요?',
        '채팅이랑 결제 기능이 들어간 크로스플랫폼 앱인데, 예산을 500만 원에서 1,000만 원 사이로 잡고 등록하려 합니다. 전문가분들이 보시기에 입찰에 많이 참여하실 만한 합리적인 금액대인지 궁금합니다.',
        82, true, 1, NOW());

-- 3번 샘플 (공지사항): SevMerge 플랫폼의 공식 서비스 안내
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('NOTICE', '[안내] 안심 대금 에스크로 수수료 정산 방식 변경 공지',
        '안녕하세요. SevMerge 팀입니다. 투명하고 공정한 외주 생태계를 위해 에스크로 안전 결제 시스템을 통한 전문가 정산 프로세스가 고도화되었습니다. 상세한 수수료 계산 방식(10%)은 결제 가이드를 참고해 주세요.',
        310, true, 3, NOW());


-- Project 샘플데이터
-- 1번 샘플: 중고 부품 거래 매칭 웹 및 앱 구축 (category: DEVELOPMENT, project_status: RECRUITING)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, created_at)
VALUES (1, '스타트업 전용 중고 부품 거래 매칭 플랫폼 구축', 'WEB',
        '안녕하세요...', 5000000, 10000000, DATE_ADD(NOW(), INTERVAL 14 DAY), 'ALL', 'OPEN', 0, false, NOW());

INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, created_at)
VALUES (4, '제조업 불량품 검출용 AI...', 'APP',
        '공장 라인...', 8000000, 15000000, DATE_ADD(NOW(), INTERVAL 30 DAY), 'CERTIFIED_ONLY', 'OPEN', 0, false, NOW());

INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, created_at)
VALUES (1, '기업 리브랜딩...', 'APP',
        '기존 서비스...', 2000000, 4000000, DATE_ADD(NOW(), INTERVAL 7 DAY), 'ALL', 'OPEN', 0, false, NOW());


-- Bid 더미데이터
INSERT INTO bid_tb (project_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, is_deleted, created_at) VALUES
(1, 2, '웹 개발 5년 경력자입니다. 최선을 다하겠습니다.', 'React + Spring Boot로 개발하겠습니다.', 30, 2500000, 'PENDING', false, NOW()),
(1, 3, '쇼핑몰 개발 다수 경험 있습니다.', 'Vue.js + Node.js로 개발하겠습니다.', 25, 2800000, 'PENDING', false, NOW()),
(2, 2, '앱 개발 전문가입니다.', 'Flutter + Firebase로 개발하겠습니다.', 60, 8000000, 'PENDING', false, NOW()),
(2, 3, 'UI/UX 디자인 전문입니다.', '피그마로 작업하겠습니다.', 14, 1500000, 'PENDING', false, NOW()),
(3, 3, '데이터 분석 전문가입니다.', 'Python + Tableau로 개발하겠습니다.', 21, 3500000, 'PENDING', false, NOW());

-- ChatRoom 샘플데이터
-- [1번 채팅방]: 1번 프로젝트('스타트업 중고 부품 거래 플랫폼 구축')를 두고
-- 의뢰인(1번 회원)과 입찰에 참여한 전문가(2번 회원 - 개발자 홍길동) 간의 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (1, 1, 2, NOW());
--
-- -- [2번 채팅방]: 동일한 1번 프로젝트에
-- -- 또 다른 전문가(3번 회원 - 디자이너 김씨)가 입찰하여 의뢰인(1번 회원)과 조율하는 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (1, 1, 2, NOW());
--
-- -- [3번 채팅방]: 2번 프로젝트('제조업 불량품 검출용 AI 모델 개발')를 두고
-- -- 의뢰인(4번 회원)과 입찰에 참여한 전문가(2번 회원 - 개발자 홍길동) 간의 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (1, 1, 2, NOW());


-- ChatMessage 샘플데이터
-- [1번 채팅방] 의뢰인(id: 1)과 전문가(id: 2)의 실시간 외주 조율 대화

-- -- 1. 전문가(2번)의 첫인사 및 요구사항 확인 요청
-- INSERT INTO chat_message_tb (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
-- VALUES (1, 2, '안녕하세요 클라이언트님, SevMerge에서 프로젝트 제안서 제출한 풀스택 개발자 홍길동입니다. 보내주신 첨부 파일 문서 잘 검토해보았습니다.', false, NOW(), NOW());
--
-- -- 2. 의뢰인(1번)의 답변
-- INSERT INTO chat_message_tb (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
-- VALUES (1, 1, '아, 네 반갑습니다 개발자님! 제안서에 아키텍처 구성을 꼼꼼하게 잘 적어주셔서 인상 깊었습니다.', false, NOW(), NOW());
--
-- -- 3. 전문가(2번)의 일정 및 마일스톤 조율
-- INSERT INTO chat_message_tb (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
-- VALUES (1, 2, '감사합니다. 혹시 기획서 상의 안심 대금 결제 연동 범위가 1차 런칭 스펙에 포함되는 걸까요? 그에 따라 일정을 3일 정도 조정해야 할 수도 있어서 여쭤봅니다.', false, NOW(), NOW());
--
-- -- 4. 의뢰인(1번)의 피드백
-- INSERT INTO chat_message_tb (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
-- VALUES (1, 1, '결제 연동은 필수 스펙입니다! 대신 알림톡 발송 부분은 2차 개발로 미뤄도 괜찮으니, 결제 기능 위주로 일정 먼저 잡아주시면 감사하겠습니다.', false, NOW(), NOW());
--
-- -- 5. 전문가(2번)의 확답
-- INSERT INTO chat_message_tb (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
-- VALUES (1, 2, '확인했습니다. 그럼 말씀하신 범위로 WBS(작업스케줄러) 수정해서 채팅방에 공유해 드린 뒤 바로 에스크로 계약 요청 진행하겠습니다!', false, NOW(), NOW());






-- Comment 샘플데이터
-- [1번 게시글: '역제안서 작성 팁' 글에 달린 전문가들의 소통 댓글]

-- 1. 다른 프리랜서 개발자의 공감 댓글 (member_id: 3)
INSERT  INTO comment_tb (board_id, member_id, content, created_at)
VALUES (1, 3, '오... 기능명세서 초안을 역으로 제안하는 방식은 생각 못 해봤는데 진짜 좋은 팁이네요. 다음 입찰 때 바로 써먹어 보겠습니다!', NOW());

-- 2. 주니어 프리랜서의 추가 질문 댓글 (member_id: 4)
INSERT INTO comment_tb (board_id, member_id, content, created_at)
VALUES (1, 4, '작성자님, 혹시 클라이언트가 요구사항을 아예 한 줄로만 적어둔 경우에도 이 방식이 통할까요? 기획 방향 잡기가 너무 어렵네요 ㅠㅠ', NOW());


-- [2번 게시글: '스타트업 MVP 앱 외주 비용 질문' 글에 달린 댓글들]

-- 3. 베테랑 전문가의 견적 가이드 답변 댓글 (member_id: 2)
INSERT INTO comment_tb (board_id, member_id, content, created_at)
VALUES (2, 2, '채팅에 결제까지 포함된 크로스플랫폼 앱이라면 요구사항 정의서 퀄리티에 따라 다르겠지만 700만~900만 원 선이 가장 매칭률이 높을 것 같습니다. 500만 원은 조금 타이트해 보이네요.', NOW());

-- 4. 외주를 고민 중인 다른 의뢰인의 공감 댓글 (member_id: 1)
INSERT INTO comment_tb (board_id, member_id, content, created_at)
VALUES (2, 1, '저도 딱 글쓴이님과 똑같은 스펙으로 SevMerge에 프로젝트 등록하려고 조율 중이었는데, 먼저 질문해 주셔서 좋은 정보 얻고 갑니다!', NOW());



-- ExpertProfile 샘플데이터
-- 1번 샘플: 평점 높은 7년 차 풀스택 개발자 프로필 (member_id: 2)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (2, 'https://sevmerge.com/storage/profiles/expert_01.png',
        '안녕하세요! 대기업 출신 프리랜서 풀스택 개발자 홍길동입니다. 대규모 트래픽 처리 및 쇼핑몰, 커뮤니티 웹/앱 개발 전문입니다. 안정적이고 확장성 있는 아키텍처를 약속드립니다.',
        '- 前 카카오 백엔드 개발자 (3년)\n- 프리랜서 외주 개발 진행 (4년)\n- SevMerge 누적 프로젝트 20건 이상 완료',
        'Spring Boot, Java, AWS, React, Next.js', 4.95, 18, true);

-- 2번 샘플: 스타트업 전문 UI/UX 디자이너 프로필 (member_id: 3)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (3, 'https://sevmerge.com/storage/profiles/expert_02.png',
        '의뢰인님의 추상적인 아이디어를 트렌디하고 직관적인 화면으로 시각화해 드립니다. 와이어프레임 기획부터 피그마 고도화 프로토타입까지 책임지고 가이드해 드립니다.',
        '- 대형 IT 에이전시 수석 디자이너 (4년)\n- 스타트업 MVP 디자인 전담 프리랜서 활동 중',
        'Figma, UI/UX Design, Adobe XD, GUI, Branding', 4.80, 12, true);

-- 3번 샘플: 이제 막 활동을 시작한 신규 주니어 웹 퍼블리셔 (인증 보류 상태 테스트용 - member_id: 5)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (4, 'https://sevmerge.com/storage/profiles/default.png',
        '성실함과 빠른 피드백으로 무장한 주니어 웹 개발자입니다. 스타트업 랜딩 페이지나 간단한 기업 소개 사이트를 합리적인 비용으로 빠르게 구축해 드립니다.',
        '- 컴퓨터공학과 졸업\n- 프론트엔드 외주 및 퍼블리싱 프로젝트 3회 진행',
        'HTML5, CSS3, JavaScript, Vue.js, Tailwind CSS', 0.00, 0, false);



-- Payment 샘플데이터
-- 1번 샘플: 웹 구축 계약 완료 및 신용카드 결제 (총 5,000,000원 / 수수료 500,000원 / 전문가 4,500,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (1, 1, 2, 5000000, 500000, 4500000, 'imp_482019482103', 'card', 'PAID', NOW());

-- 2번 샘플: AI 모델 개발 계약 완료 및 카카오페이 결제 (총 12,000,000원 / 수수료 1,200,000원 / 전문가 10,800,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (2, 4, 2, 12000000, 1200000, 10800000, 'imp_910482019482', 'kakaopay', 'PAID', NOW());

-- 3번 샘플: 가상계좌 무통장 입금 대기 중인 상태 연출 (READY 상태)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (3, 1, 3, 1500000, 150000, 1350000, 'imp_vbank_009182', 'vbank', 'PAID', NOW());

-- Portfolio 샘플데이터

-- 1번 샘플: 홍길동(expert_profile_id: 1) 포트폴리오 - 쇼핑몰 개발
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (1, '대형 쇼핑몰 백엔드 시스템 구축',
        'Spring Boot 기반 대규모 트래픽 처리 쇼핑몰 백엔드 개발. 일 평균 10만 건 이상의 주문을 안정적으로 처리하는 아키텍처 설계 및 구현.',
        'https://sevmerge.com/storage/portfolios/portfolio_01.png',
        'https://github.com/honggildong/shopping-mall',
        NOW());

-- 2번 샘플: 홍길동(expert_profile_id: 1) 포트폴리오 - 커뮤니티 플랫폼
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (1, '실시간 채팅 기반 커뮤니티 플랫폼',
        'WebSocket을 활용한 실시간 채팅 및 알림 기능 구현. React + Spring Boot 풀스택 개발. AWS EC2 배포 및 RDS 운영.',
        'https://sevmerge.com/storage/portfolios/portfolio_02.png',
        'https://github.com/honggildong/community-platform',
        NOW());

-- 3번 샘플: 김디자(expert_profile_id: 2) 포트폴리오 - 스타트업 랜딩페이지
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '핀테크 스타트업 랜딩페이지 UI/UX 디자인',
        'Figma 기반 와이어프레임부터 고도화 프로토타입까지 전담. 브랜드 아이덴티티 정립 및 반응형 디자인 시스템 구축.',
        'https://sevmerge.com/storage/portfolios/portfolio_03.png',
        'https://www.figma.com/kimdesign/fintech-landing',
        NOW());

-- 4번 샘플: 김디자(expert_profile_id: 2) 포트폴리오 - 앱 UI 디자인
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '헬스케어 모바일 앱 UI/UX 디자인',
        '운동 기록 및 식단 관리 앱 전체 화면 설계. 사용자 인터뷰 기반 UX 리서치 진행 후 직관적인 UI 구현. Adobe XD 프로토타입 납품.',
        'https://sevmerge.com/storage/portfolios/portfolio_04.png',
        'https://www.figma.com/kimdesign/healthcare-app',
        NOW());

-- 5번 샘플: 관리자(expert_profile_id: 3) 포트폴리오 - 주니어 웹 퍼블리싱
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, '기업 소개 웹사이트 퍼블리싱',
        'HTML5, CSS3, JavaScript 기반 반응형 기업 소개 사이트 퍼블리싱. Vue.js 및 Tailwind CSS 활용하여 빠른 개발 완료.',
        'https://sevmerge.com/storage/portfolios/portfolio_05.png',
        'https://github.com/junior-dev/company-website',
        NOW());



INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 2, 5, '정말 꼼꼼하고 빠르게 작업해 주셨어요. 요구사항을 잘 이해하고 결과물도 깔끔했습니다.', NOW());

INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (4, 2, 4, '소통이 원활하고 일정도 잘 지켜주셨습니다. 다음에도 함께하고 싶어요.', NOW());

INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 2, 5, '기술적으로 매우 뛰어나신 분입니다. 복잡한 작업도 깔끔하게 처리해 주셨어요.', NOW());


