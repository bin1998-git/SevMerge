

-- Bid 샘플데이터
-- 1번 샘플: 개발 프로젝트 입찰 데이터
INSERT INTO bid (product_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, created_at)
VALUES (1, 2, '안녕하세요. 5년 차 웹 풀스택 개발자입니다. 요구사항을 완벽히 이해했으며 기한 내 구축 가능합니다.', '1주차: 요구사항 정의 및 DB 설계 / 2주차: 백엔드 API 개발 / 3주차: 프론트엔드 연동 및 QA', 21, 4500000, 'PENDING', NOW());

-- 2번 샘플: 매칭이 완료된(낙찰된) 입찰 데이터
INSERT INTO bid (product_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, created_at)
VALUES (1, 3, '디자인 시스템 구축 경력이 풍부한 UI/UX 전문가입니다. 트렌디한 결과물을 보장합니다.', '피그마를 활용한 고도화된 프로토타입 선제공 후 컴포넌트 단위 개발 진행', 14, 3000000, 'ACCEPTED', NOW());

-- 3번 샘플: 거절된 입찰 데이터
INSERT INTO bid (product_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, created_at)
VALUES (2, 2, '앱 개발 관련 포트폴리오 다수 보유 중입니다. 연락 부탁드립니다.', '네이티브 앱 방식으로 안정적인 기능 구현', 30, 8000000, 'REJECTED', NOW());




-- Board 샘플 데이터
-- 1번 샘플 (자유게시판 / FREE): 전문가(개발자)의 네트워킹 및 팁 공유
INSERT INTO board (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '외주 입찰할 때 포트폴리오 구성 꿀팁 공유합니다.', '안녕하세요 SevMerge에서 활동 중인 5년 차 풀스택 개발자입니다. 의뢰인(클라이언트) 마음을 사로잡는 제안서 작성법과 포트폴리오 배치 순서를 정리해 보았습니다. 핵심은 1페이지에 핵심 아키텍처를 요약하는 것입니다...', 142, true, 2, NOW());

-- 2번 샘플 (후기게시판 / REVIEW): 매칭 성공 후기 (플랫폼 활성화 연출용)
INSERT INTO board (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '[매칭후기] 스타트업 MVP 앱 개발 외주 성공적으로 마쳤습니다!', '아이디어만 있던 상태에서 SevMerge에 프로젝트를 등록했는데, 정말 역량 있는 전문가분을 만나 3주 만에 플러터(Flutter) 기반 MVP 앱을 출시했습니다. 소통도 잘 되시고 견적도 합리적이어서 대만족입니다.', 85, true, 1, NOW());

-- 3번 샘플 (공지사항 / NOTICE): 플랫폼 공식 안내
INSERT INTO board (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('NOTICE', '[안정성 강화] 에스크로(안전대금) 결제 시스템 도입 안내', '안녕하세요 SevMerge 팀입니다. 의뢰인과 전문가 분들의 안전한 대금 거래를 보호하기 위해 에스크로 시스템이 정식 도입되었습니다. 이제 프로젝트 계약 체결 시 대금은 플랫폼에서 안전하게 보관되며...', 520, true, 3, NOW());



-- ChatMessage 샘플 데이터
-- [1번 채팅방] 프로젝트 견적 및 일정 조율 대화 (의뢰인 id: 1, 전문가 id: 2)

-- 1. 전문가(2번)가 제안서 제출 후 첫 인사
INSERT INTO chat_message (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
VALUES (1, 2, '안녕하세요 클라이언트님, SevMerge에서 프로젝트 제안서 보낸 개발자 홍길동입니다. 기획서 확인 후 몇 가지 여쭤보고 싶은 점이 있어 메시지 드립니다.', false, NOW(), NOW());

-- 2. 의뢰인(1번)의 답변
INSERT INTO chat_message (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
VALUES (1, 1, '네, 안녕하세요! 제안서 잘 보았습니다. 어떤 부분이 궁금하신가요?', false, NOW(), NOW());

-- 3. 전문가(2번)의 요구사항 확인 및 API 조율
INSERT INTO chat_message (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
VALUES (1, 2, '혹시 결제 모듈 연동할 때 PG사는 토스페이먼츠로 고정이실까요? 아니면 카카오페이나 네이버페이 간편결제 위주로 먼저 연동하길 원하시나요?', false, NOW(), NOW());

-- 4. 의뢰인(1번)의 답변 및 파일 확인 요청
INSERT INTO chat_message (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
VALUES (1, 1, '초기에는 카카오페이 먼저 연동하고 싶습니다. 상세 기능 정의서는 이전에 메일로 드린 본문을 참고해 주세요.', false, NOW(), NOW());


-- [2번 채팅방] 다른 프로젝트의 작업 완료 및 피드백 대화 (의뢰인 id: 3, 전문가 id: 2)

-- 5. 전문가(2번)가 검수 요청
INSERT INTO chat_message (chat_room_id, sender_id, text, is_deleted, created_at, updated_at)
VALUES (2, 2, '요청하신 대시보드 UI 개발 및 API 연동이 완료되었습니다. 테스트 서버 주소 공유해 드리니 확인 부탁드립니다!', false, NOW(), NOW());


-- ChatRoom 샘플 데이터
-- [1번 채팅방]: 1번 프로젝트('스타트업 MVP 앱 개발')를 두고
-- 의뢰인(1번 회원)과 입찰에 참여한 전문가(2번 회원) 간의 대화방
INSERT INTO chat_room (project_id, client_id, expert_id, create_at)
VALUES (1, 1, 2, NOW());

-- [2번 채팅방]: 1번 프로젝트('스타트업 MVP 앱 개발')에
-- 또 다른 전문가(3번 회원)가 입찰하여 의뢰인(1번 회원)과 대화를 시작한 방
INSERT INTO chat_room (project_id, client_id, expert_id, create_at)
VALUES (1, 1, 3, NOW());

-- [3번 채팅방]: 2번 프로젝트('AI 매칭 기능 웹 구축')를 두고
-- 의뢰인(4번 회원)과 전문가(2번 회원) 간의 대화방
INSERT INTO chat_room (project_id, client_id, expert_id, create_at)
VALUES (2, 4, 2, NOW());


-- Comment 샘플데이터
-- [1번 게시글: '외주 입찰할 때 포트폴리오 구성 꿀팁' 글에 달린 댓글들]

-- 1. 다른 프리랜서 개발자의 공감 댓글 (member_id: 3)
INSERT INTO comment (board_id, member_id, content, created_at)
VALUES (1, 3, '진짜 공감합니다! 저도 첫 페이지에 핵심 아키텍처 다이어그램 한 장 넣기 시작하면서부터 의뢰인들 연락 오는 빈도가 확실히 늘었어요. 꿀팁 감사합니다.', NOW());

-- 2. 이제 막 진입한 초보 프리랜서의 질문 댓글 (member_id: 4)
INSERT INTO comment (board_id, member_id, content, created_at)
VALUES (1, 4, '작성자님, 혹시 신입 프리랜서라 포트폴리오에 넣을 상용화 서비스가 없다면 토이 프로젝트 위주로 채워도 입찰 경쟁력이 있을까요? ㅠㅠ', NOW());


-- [2번 게시글: '스타트업 MVP 앱 개발 외주 성공 후기' 글에 달린 댓글들]

-- 3. 외주를 준비 중인 다른 의뢰인의 단가 문의 댓글 (member_id: 5)
INSERT INTO comment (board_id, member_id, content, created_at)
VALUES (2, 5, '와, 성공적인 런칭 축하드립니다! 저도 비슷한 규모의 MVP 앱 개발을 SevMerge에서 진행하려고 기획 중인데, 대략적인 예산(단가) 대가 어느 정도 선이었는지 공유 가능하실까요?', NOW());

-- 4. 플랫폼 활성화를 기뻐하는 다른 유저의 댓글 (member_id: 2)
INSERT INTO comment (board_id, member_id, content, created_at)
VALUES (2, 2, '좋은 전문가 만나서 다행이네요. SevMerge가 확실히 다른 플랫폼보다 역제안서 퀄리티가 높아서 매칭이 잘 되는 것 같아요.', NOW());




-- ExpertProfile 샘플데이터
-- 1번 샘플: 평점 높은 7년 차 베테랑 백엔드/풀스택 개발자 (member_id: 2)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (2, 'https://sevmerge.com/storage/profiles/expert_01.png',
        '안녕하세요! 대기업 출신 프리랜서 개발자 팀 SevMerge_Dev입니다. 대규모 트래픽 처리 및 쇼핑몰, 커뮤니티 앱 개발 전문입니다.',
        '- 前 카카오 백엔드 개발자 (3년)\n- 프리랜서 외주 개발 진행 (4년)\n- 누적 프로젝트 20건 이상 완료',
        'Spring Boot, Java, AWS, React Native', 4.95, 18, true);

-- 2번 샘플: 감각적인 스타트업 전문 UI/UX 디자이너 (member_id: 3)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (3, 'https://sevmerge.com/storage/profiles/expert_02.png',
        '의뢰인의 아이디어를 트렌디한 화면으로 구현해 드립니다. 와이어프레임 기획부터 피그마 고도화 프로토타입까지 책임집니다.',
        '- IT 에이전시 수석 디자이너 (4년)\n- 스타트업 MVP 디자인 전담 프리랜서',
        'Figma, UI/UX Design, Adobe XD, Branding', 4.80, 12, true);

-- 3번 샘플: 이제 막 활동을 시작한 신규 주니어 개발자 (인증 보류 상태 테스트용 - member_id: 5)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, avg_rating, total_reviews, is_certified)
VALUES (5, 'https://sevmerge.com/storage/profiles/default.png',
        '성실함과 빠른 소통으로 무장한 주니어 웹 개발자입니다. 간단한 랜딩 페이지나 회사 소개 사이트 저렴하고 빠르게 만들어 드립니다.',
        '- 컴퓨터공학 전공\n- 웹 퍼블리싱 및 프론트엔드 외주 3회 진행',
        'HTML5, CSS3, JavaScript, Vue.js', 0.00, 0, false);



-- Member 샘플데이터
-- 1번 샘플: 프로젝트를 발주하는 의뢰인 (Client)
INSERT INTO member (email, password, name, phone, role, status, created_at)
VALUES ('client01@sevmerge.com', '$2a$10$7Z25QpxnL3V6VqN0FmGgduWv9F0bM1HshB38aNExXpExZpS/YhV2.', '김의뢰', '010-1234-5678', 'CLIENT', 'ACTIVE', NOW());

-- 2번 샘플: 제안서를 제출하는 전문가 1 (Full-Stack Expert)
INSERT INTO member (email, password, name, phone, role, status, created_at)
VALUES ('expert01@sevmerge.com', '$2a$10$7Z25QpxnL3V6VqN0FmGgduWv9F0bM1HshB38aNExXpExZpS/YhV2.', '홍길동', '010-9876-5432', 'EXPERT', 'ACTIVE', NOW());

-- 3번 샘플: 제안서를 제출하는 전문가 2 (UI/UX Designer)
INSERT INTO member (email, password, name, phone, role, status, created_at)
VALUES ('expert02@sevmerge.com', '$2a$10$7Z25QpxnL3V6VqN0FmGgduWv9F0bM1HshB38aNExXpExZpS/YhV2.', '김디자', '010-5555-4444', 'EXPERT', 'ACTIVE', NOW());

-- 4번 샘플: 플랫폼 총괄 관리자 (Admin)
INSERT INTO member (email, password, name, phone, role, status, created_at)
VALUES ('admin@sevmerge.com', '$2a$10$7Z25QpxnL3V6VqN0FmGgduWv9F0bM1HshB38aNExXpExZpS/YhV2.', '최관리', '010-0000-0000', 'ADMIN', 'ACTIVE', NOW());



-- Payment 샘플데이터
-- 1번 샘플: 신용카드로 결제 완료된 건 (총 금액 5,000,000원 / 수수료 500,000원 / 전문가 4,500,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (1, 1, 2, 5000000, 500000, 4500000, 'imp_482019482103', 'card', 'PAID', NOW());

-- 2번 샘플: 카카오페이로 결제 완료된 건 (총 금액 2,000,000원 / 수수료 200,000원 / 전문가 1,800,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (2, 4, 2, 2000000, 200000, 1800000, 'imp_910482019482', 'kakaopay', 'PAID', NOW());

-- 3번 샘플: 가상계좌 발급 후 입금 대기 중인 건 (payment_key나 paid_at이 아직 유효하지 않거나 대기 상태 연출)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (3, 1, 3, 1500000, 150000, 1350000, 'imp_vbank_019284', 'vbank', 'READY', NOW());




-- Project 샘플데이터
-- 1번 샘플: 스타트업 매칭 플랫폼 웹 및 앱 구축 (category: WEB_APP, projectStatus: RECRUITING)
INSERT INTO project (client_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, created_at)
VALUES (1, '스타트업 전용 중고 부품 거래 매칭 플랫폼 구축', 'DEVELOPMENT',
        '안녕하세요. 초기 스타트업 단계에서 프로토타입으로 사용할 중고 부품 거래 매칭 웹 및 하이브리드 앱 개발을 의뢰합니다. 상세 요구사항 정의서와 피그마 와이어프레임은 완비되어 있습니다. 결제 및 채팅 기능 구현이 필수적입니다.',
        5000000, 10000000, '2026-07-15 18:00:00', 'ALL_EXPERTS', 'RECRUITING', NOW());

-- 2번 샘플: 딥러닝 기반 이미지 분류 모델 개발 (category: AI, projectStatus: RECRUITING)
INSERT INTO project (client_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, created_at)
VALUES (4, '제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발', 'AI_TECH',
        '공장 라인에서 촬영된 부품 이미지를 기반으로 불량 여부를 실시간 판별하는 딥러닝 모델 개발 건입니다. 데이터셋(약 1만 장)은 라벨링까지 완료되어 제공 가능합니다. 정확도(Accuracy) 95% 이상을 목표로 합니다. 관련 포트폴리오가 있는 팀만 지원 부탁드립니다.',
        8000000, 15000000, '2026-06-30 23:59:59', 'CERTIFIED_ONLY', 'RECRUITING', NOW());

-- 3번 샘플: 이미 매칭이 완료되어 진행 중인 프로젝트 (projectStatus: IN_PROGRESS)
INSERT INTO project (client_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, created_at)
VALUES (1, '기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화', 'DESIGN',
        '기존 서비스의 웹 디자인 톤앤매너를 전면 수정하고 싶습니다. 반응형 웹 디자인을 지원해야 하며 신규 브랜드 로고(BI) 가이드라인을 제공해 드립니다.',
        2000000, 4000000, '2026-05-10 12:00:00', 'ALL_EXPERTS', 'IN_PROGRESS', NOW());


