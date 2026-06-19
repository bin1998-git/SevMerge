-- Member 샘플데이터
-- 1번 샘플: 프로젝트를 발주하고 대금을 결제하는 의뢰인 (CLIENT)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client01@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김의뢰', '010-1234-5678',
        'CLIENT', 'ACTIVE', false, NOW(), 0);

-- 2번 샘플: 프로젝트에 입찰 제안서를 제출하는 전문가 1 (EXPERT - 개발자)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert01@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '홍길동', '010-9876-5432',
        'EXPERT', 'ACTIVE', false, NOW(), 0);

-- 3번 샘플: 프로젝트에 입찰 제안서를 제출하는 전문가 2 (EXPERT - 디자이너)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert02@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김디자', '010-5555-4444',
        'EXPERT', 'ACTIVE', false, NOW(), 0);

-- 4번 샘플: 플랫폼을 관리하고 분쟁을 조정하는 최고 관리자 (ADMIN)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('admin@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최관리', '010-0000-0000',
        'ADMIN', 'ACTIVE', false, NOW(), 0);


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
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter,
                        project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '스타트업 전용 중고 부품 거래 매칭 플랫폼 구축', 'WEB',
        '안녕하세요. 초기 스타트업 단계에서 프로토타입으로 사용할 중고 부품 거래 매칭 웹 및 하이브리드 앱 개발을 의뢰합니다. 상세 요구사항 정의서와 피그마 와이어프레임은 완비되어 있습니다. 결제 및 채팅 기능 구현이 필수적입니다.',
        5000000, 10000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 2번 샘플: 딥러닝 기반 이미지 분류 모델 개발 (category: AI_TECH, project_status: RECRUITING)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter,
                        project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발', 'APP',
        '공장 라인에서 촬영된 부품 이미지를 기반으로 불량 여부를 실시간 판별하는 딥러닝 모델 개발 건입니다. 데이터셋(약 1만 장)은 라벨링까지 완료되어 제공 가능합니다. 정확도(Accuracy) 95% 이상을 목표로 합니다. 관련 포트폴리오가 있는 팀만 지원 부탁드립니다.',
        8000000, 15000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 3번 샘플: 리브랜딩 웹 디자인 (category: DESIGN, project_status: IN_PROGRESS)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter,
                        project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화', 'APP',
        '기존 서비스의 웹 디자인 톤앤매너를 전면 수정하고 싶습니다. 반응형 웹 디자인을 지원해야 하며 신규 브랜드 로고(BI) 가이드라인을 제공해 드립니다.',
        2000000, 4000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max,
                        deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '임시저장 중인 프로젝트 제목', 'WEB', '아직 작성 중인 내용입니다.',
        0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, NOW());


-- Bid 더미데이터
-- expert_id는 member_tb의 id (EXPERT 역할인 2번, 3번만 사용)
INSERT INTO bid_tb (project_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, is_deleted,
                    created_at)
VALUES (1, 2, '웹 개발 5년 경력자입니다. 최선을 다하겠습니다.', 'React + Spring Boot로 개발하겠습니다.', 30, 2500000, 'PENDING', false, NOW()),
       (1, 3, '쇼핑몰 개발 다수 경험 있습니다.', 'Vue.js + Node.js로 개발하겠습니다.', 25, 2800000, 'PENDING', false, NOW()),
       (2, 2, '앱 개발 전문가입니다.', 'Flutter + Firebase로 개발하겠습니다.', 60, 8000000, 'PENDING', false, NOW()),
       (3, 3, 'UI/UX 디자인 전문입니다.', '피그마로 작업하겠습니다.', 14, 1500000, 'PENDING', false, NOW()),
       (3, 2, '풀스택으로 디자인까지 커버합니다.', 'React + Figma 병행 작업하겠습니다.', 21, 3500000, 'PENDING', false, NOW());

-- ChatRoom 샘플데이터
-- [1번 채팅방]: 1번 프로젝트('스타트업 중고 부품 거래 플랫폼 구축')를 두고
-- 의뢰인(1번 회원)과 입찰에 참여한 전문가(2번 회원 - 개발자 홍길동) 간의 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (1, 1, 2, NOW());
--
-- -- [2번 채팅방]: 동일한 1번 프로젝트에
-- -- 또 다른 전문가(3번 회원 - 디자이너 김씨)가 입찰하여 의뢰인(1번 회원)과 조율하는 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (1, 1, 3, NOW());
--
-- -- [3번 채팅방]: 2번 프로젝트('제조업 불량품 검출용 AI 모델 개발')를 두고
-- -- 의뢰인(4번 회원)과 입찰에 참여한 전문가(2번 회원 - 개발자 홍길동) 간의 대화방
-- INSERT INTO chat_room_tb (project_id, client_id, expert_id, create_at)
-- VALUES (2, 1, 2, NOW());


-- ChatMessage 샘플데이터
-- [1번 채팅방] 의뢰인(id: 1)과 전문가(id: 2)의 실시간 외주 조율 대화

-- 1. 전문가(2번)의 첫인사 및 요구사항 확인 요청
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
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 3, '오... 기능명세서 초안을 역으로 제안하는 방식은 생각 못 해봤는데 진짜 좋은 팁이네요. 다음 입찰 때 바로 써먹어 보겠습니다!', NOW(), false);

-- 2. 주니어 프리랜서의 추가 질문 댓글 (member_id: 4)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 4, '작성자님, 혹시 클라이언트가 요구사항을 아예 한 줄로만 적어둔 경우에도 이 방식이 통할까요? 기획 방향 잡기가 너무 어렵네요 ㅠㅠ', NOW(), false);



-- [2번 게시글: '스타트업 MVP 앱 외주 비용 질문' 글에 달린 댓글들]

-- 3. 베테랑 전문가의 견적 가이드 답변 댓글 (member_id: 2)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (2, 2, '채팅에 결제까지 포함된 크로스플랫폼 앱이라면 요구사항 정의서 퀄리티에 따라 다르겠지만 700만~900만 원 선이 가장 매칭률이 높을 것 같습니다. 500만 원은 조금 타이트해 보이네요.',
        NOW(), false);

-- 4. 외주를 고민 중인 다른 의뢰인의 공감 댓글 (member_id: 1)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (2, 1, '저도 딱 글쓴이님과 똑같은 스펙으로 SevMerge에 프로젝트 등록하려고 조율 중이었는데, 먼저 질문해 주셔서 좋은 정보 얻고 갑니다!', NOW(), false);

-- 댓글 신고 샘플데이터
-- 1번째 샘플 데이터 (욕설 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (1, 2, '욕설 / 비속어 및 혐오 표현', '특정 사용자를 향해 지속적으로 공격적인 비속어와 욕설을 유포하고 있어 커뮤니티 환경을 해치고 있습니다.', NOW());

-- 2번째 샘플 데이터 (스팸 홍보 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (2, 3, '스팸 광고 / 홍보성 도배', '댓글 내에 허가받지 않은 불법 주식 리딩방 링크와 도박 사이트 홍보 문구를 반복적으로 도배하고 있습니다.', NOW());

-- 3번째 샘플 데이터 (타인 비방 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (3, 1, '타인 비방 및 명예훼손', '근거 없는 허위 사실을 바탕으로 작성자의 명예를 훼손하고 있으며 심각한 인신공격성 발언을 포함하고 있습니다.', NOW());


-- 블랙리스트 샘플 데이터
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (1, '댓글 신고 3회 누적 자동 정지 처리 (욕설 및 비속어 유포)', '4,5,6', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 1;
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (2, '댓글 신고 3회 누적 자동 정지 처리 (홍보성 스팸 도배)', '7,8,9', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 2;




-- ExpertProfile 샘플데이터
-- 1번 샘플: 평점 높은 7년 차 풀스택 개발자 프로필 (member_id: 2)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality,is_certified,expert_grade)
VALUES (2, 'https://sevmerge.com/storage/profiles/expert_01.png',
        '안녕하세요! 대기업 출신 프리랜서 풀스택 개발자 홍길동입니다. 대규모 트래픽 처리 및 쇼핑몰, 커뮤니티 웹/앱 개발 전문입니다. 안정적이고 확장성 있는 아키텍처를 약속드립니다.',
        '- 前 카카오 백엔드 개발자 (3년)\n- 프리랜서 외주 개발 진행 (4년)\n- SevMerge 누적 프로젝트 20건 이상 완료',
        'Spring Boot, Java, AWS, React, Next.js', true,'NORMAL');

-- 2번 샘플: 스타트업 전문 UI/UX 디자이너 프로필 (member_id: 3)
INSERT INTO expert_profile (member_id, profile_image, intro, career, speciality, is_certified,expert_grade)
VALUES (3, 'https://sevmerge.com/storage/profiles/expert_02.png',
        '의뢰인님의 추상적인 아이디어를 트렌디하고 직관적인 화면으로 시각화해 드립니다. 와이어프레임 기획부터 피그마 고도화 프로토타입까지 책임지고 가이드해 드립니다.',
        '- 대형 IT 에이전시 수석 디자이너 (4년)\n- 스타트업 MVP 디자인 전담 프리랜서 활동 중',
        'Figma, UI/UX Design, Adobe XD, GUI, Branding', true,'SKILLED');



-- Payment 샘플데이터
-- 1번 샘플: 웹 구축 계약 완료 및 신용카드 결제 (총 5,000,000원 / 수수료 500,000원 / 전문가 4,500,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                     paid_at)
VALUES (1, 1, 2, 5000000, 500000, 4500000, 'imp_482019482103', 'card', 'PAID', NOW());

-- 2번 샘플: AI 모델 개발 계약 완료 및 카카오페이 결제 (총 12,000,000원 / 수수료 1,200,000원 / 전문가 10,800,000원)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                     paid_at)
VALUES (2, 4, 2, 12000000, 1200000, 10800000, 'imp_910482019482', 'kakaopay', 'PAID', NOW());

-- 3번 샘플: 가상계좌 무통장 입금 대기 중인 상태 연출 (READY 상태)
INSERT INTO payment (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                     paid_at)
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
VALUES (1, '기업 소개 웹사이트 퍼블리싱',
        'HTML5, CSS3, JavaScript 기반 반응형 기업 소개 사이트 퍼블리싱. Vue.js 및 Tailwind CSS 활용하여 빠른 개발 완료.',
        'https://sevmerge.com/storage/portfolios/portfolio_05.png',
        'https://github.com/junior-dev/company-website',
        NOW());



-- Message 샘플데이터
-- 기존 멤버: 1=김의뢰(CLIENT), 2=홍길동(EXPERT), 3=김디자(EXPERT), 4=최관리(ADMIN)
-- 기존 프로젝트: 1=중고부품거래플랫폼, 2=AI비전모델, 3=랜딩페이지디자인

-- 1번: 의뢰인(1) → 홍길동(2), 1번 프로젝트 일정 문의 (읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (1, 2, 1, '[문의] 채팅 및 결제 기능 일정 확인 부탁드립니다',
        '안녕하세요, 홍길동 개발자님. 제출해주신 입찰 제안서 잘 검토하였습니다. 채팅 기능과 결제 연동을 30일 일정 내에 모두 처리 가능하신지 확인 부탁드립니다. 회신 기다리겠습니다.',
        true, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 2번: 홍길동(2) → 의뢰인(1), 답장 (안읽음 - NEW 배지 노출용)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (2, 1, 1, 'Re: [문의] 채팅 및 결제 기능 일정 확인 부탁드립니다',
        '안녕하세요, 클라이언트님! 채팅 기능과 결제 연동 모두 30일 일정 내에 처리 가능합니다. 다만 요구사항 정의서를 빠른 시일 내에 전달해 주시면 일정 여유가 생깁니다. 감사합니다.',
        false, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 3번: 의뢰인(1) → 김디자(3), 3번 프로젝트 시안 일정 문의 (읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender, is_deleted_by_receiver, created_at)
VALUES (1, 3, 3, '랜딩페이지 1차 시안 일정 문의드립니다',
        '안녕하세요 김디자 디자이너님. 브랜드 가이드라인 전달드렸는데 확인하셨나요? 초기 와이어프레임 시안을 언제쯤 받아볼 수 있을지 일정 공유 부탁드립니다.',
        true, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 4번: 김디자(3) → 의뢰인(1), 답장 (안읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (3, 1, 3, 'Re: 랜딩페이지 1차 시안 일정 문의드립니다',
        '안녕하세요! 브랜드 가이드라인 잘 확인하였습니다. 이번 주 금요일까지 1차 와이어프레임 시안 공유드리겠습니다. 컬러 팔레트 방향성도 함께 정리해서 보내드릴게요!',
        false, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 5번: 관리자(4) → 의뢰인(1), 에스크로 안내 (안읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (4, 1, NULL, '[SevMerge 안내] 에스크로 안전 결제 프로세스 업데이트',
        '안녕하세요, SevMerge 운영팀입니다. 에스크로 안전 결제 시스템이 업데이트되었습니다. 계약 완료 후 대금은 SevMerge를 통해 보관되며 프로젝트 완료 확인 후 전문가에게 지급됩니다. 수수료는 10%입니다.',
        false, false, false, DATE_SUB(NOW(), INTERVAL 7 DAY));

-- 6번: 홍길동(2) → 의뢰인(1), 2번 프로젝트 데이터셋 전달 방법 문의 (안읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (2, 1, 2, '[AI 모델] 학습 데이터셋 전달 방법 문의',
        '안녕하세요! AI 컴퓨터 비전 모델 프로젝트 관련하여 라벨링 완료된 데이터셋 1만 장을 어떤 방식으로 전달받을 수 있을지 문의드립니다. AWS S3 공유 혹은 구글 드라이브 중 편하신 방법으로 부탁드립니다.',
        false, false, false, DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- 7번: 의뢰인(1) → 홍길동(2), 6번 답장 (읽음)
INSERT INTO message_tb (sender_id, receiver_id, project_id, title, content, is_read, is_deleted_by_sender,
                        is_deleted_by_receiver, created_at)
VALUES (1, 2, 2, 'Re: [AI 모델] 학습 데이터셋 전달 방법 문의',
        'AWS S3 버킷을 공유드리겠습니다. 잠시 후 이메일로 접근 권한 정보를 보내드리겠습니다. 라벨링 형식은 YOLO 포맷으로 되어 있습니다.',
        true, false, false, NOW());


-- Review 샘플데이터
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 2, 5, '정말 꼼꼼하고 빠르게 작업해 주셨어요. 요구사항을 잘 이해하고 결과물도 깔끔했습니다.', NOW());

INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (4, 2, 4, '소통이 원활하고 일정도 잘 지켜주셨습니다. 다음에도 함께하고 싶어요.', NOW());

INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 2, 5, '기술적으로 매우 뛰어나신 분입니다. 복잡한 작업도 깔끔하게 처리해 주셨어요.', NOW());


-- Notification 샘플데이터
-- 컬럼: receiver_id, type, content, url, is_read, is_deleted, created_at

-- ── 의뢰인 김의뢰(1) 가 받는 알림 ──
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'NEW_BID', '홍길동 전문가가 ''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제안서를 제출했습니다.', '/projects/1', false, false,
        DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
       (1, 'NEW_BID', '김디자 전문가가 ''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제안서를 제출했습니다.', '/projects/1', false, false,
        DATE_SUB(NOW(), INTERVAL 2 HOUR)),
       (1, 'NEW_BID', '홍길동 전문가가 ''제조업 불량품 검출용 AI 컴퓨터 비전 모델 개발'' 프로젝트에 제안서를 제출했습니다.', '/projects/2', true, false,
        DATE_SUB(NOW(), INTERVAL 1 DAY)),
       (1, 'MESSAGE_RECEIVED', '홍길동님이 ''[AI 모델] 학습 데이터셋 전달 방법 문의'' 쪽지를 보냈습니다.', '/messages', false, false,
        DATE_SUB(NOW(), INTERVAL 1 HOUR)),
       (1, 'MESSAGE_RECEIVED', '김디자님이 ''Re: 랜딩페이지 1차 시안 일정 문의드립니다'' 쪽지를 보냈습니다.', '/messages', true, false,
        DATE_SUB(NOW(), INTERVAL 4 DAY));

-- ── 전문가 홍길동(2) 가 받는 알림 ──
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'BID_SELECTED', '축하합니다! ''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제출한 제안서가 낙찰되었습니다.', '/bids/my-orders', false,
        false, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
       (2, 'PAYMENT_COMPLETED', '''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트의 대금 결제가 완료되어 작업을 시작할 수 있습니다.', '/bids/my-orders',
        false, false, DATE_SUB(NOW(), INTERVAL 20
  MINUTE)),
       (2, 'EXPERT_APPROVED', '전문가 신청이 승인되었습니다. 지금 바로 활동을 시작해보세요!', '/experts/dashboard', true, false,
        DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (2, 'MESSAGE_RECEIVED', '김의뢰님이 ''[문의] 채팅 및 결제 기능 일정 확인 부탁드립니다'' 쪽지를 보냈습니다.', '/messages', true, false,
        DATE_SUB(NOW(), INTERVAL 3 DAY));

-- ── 전문가 김디자(3) 가 받는 알림 ──
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (3, 'BID_SELECTED', '축하합니다! ''기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화'' 프로젝트에 제출한 제안서가 낙찰되었습니다.', '/bids/my-list',
        false, false, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
       (3, 'PAYMENT_COMPLETED', '''기업 리브랜딩을 위한 랜딩 페이지 및 UI/UX 디자인 고도화'' 프로젝트의 대금 결제가 완료되어 작업을 시작할 수 있습니다.',
        '/bids/my-orders', false, false, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
       (3, 'BID_REJECTED', '''스타트업 전용 중고 부품 거래 매칭 플랫폼 구축'' 프로젝트에 제출한 제안서가 아쉽게도 선정되지 않았습니다.', '/bids/my-list', true,
        false, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- FAQ 데이터
INSERT INTO faq_tb (question, answer)
VALUES ('질문 내용을 입력하세요', '답변 내용을 입력하세요');

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES
    -- [삭제 대상 1] 35일 지난 알림 (created_at < 30일)
    (1, 'NEW_BID', '[스케줄러테스트] 35일 지난 알림 — 삭제돼야 함', '/projects/1', true, false,
     DATE_SUB(NOW(), INTERVAL 35 DAY)),

    -- [삭제 대상 2] 100일 지난 알림
    (1, 'MESSAGE_RECEIVED', '[스케줄러테스트] 100일 지난 알림 — 삭제돼야 함', '/messages', true, false,
     DATE_SUB(NOW(), INTERVAL 100 DAY)),

    -- [삭제 대상 3] 최근이지만 소프트삭제됨 (is_deleted=true)
    (1, 'BID_SELECTED', '[스케줄러테스트] 소프트삭제된 알림 — 삭제돼야 함', '/bids/my-orders', true, true,
     DATE_SUB(NOW(), INTERVAL 1 HOUR)),

    -- [삭제 대상 4] 딱 경계 넘김 (31일)
    (2, 'EXPERT_APPROVED', '[스케줄러테스트] 31일 지난 알림 — 삭제돼야 함', '/experts/dashboard', true, false,
     DATE_SUB(NOW(), INTERVAL 31 DAY)),

    -- [생존 대상 1] 29일 — 30일 안 지남, 안 지워져야 함 (경계 확인)
    (1, 'NEW_BID', '[스케줄러테스트] 29일 지난 알림 — 남아야 함', '/projects/1', true, false,
     DATE_SUB(NOW(), INTERVAL 29 DAY)),

    -- [생존 대상 2] 최근 + 미삭제
    (1, 'MESSAGE_RECEIVED', '[스케줄러테스트] 1일 지난 알림 — 남아야 함', '/messages', false, false,
     DATE_SUB(NOW(), INTERVAL 1 DAY));


-- ════════════════════════════════════════════════════════════════
-- 관리자 대시보드 차트용 더미 (최근 7일 분산)
--  - 회원 추세   : member_tb.created_at
--  - 프로젝트 추세 : project_tb.created_at (전체)
--  - 완료 추세   : project_tb.created_at + project_status='DONE'
-- ════════════════════════════════════════════════════════════════

-- ── 회원 더미 (일자별 3,5,2,6,4,7,3 = 30명) ──
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES
('chart_m01@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김갑동', '010-0001-0001', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 6 DAY), 0),
('chart_m02@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김을동', '010-0001-0002', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 6 DAY), 0),
('chart_m03@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이갑돌', '010-0001-0003', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 6 DAY), 0),
('chart_m04@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이을동', '010-0001-0004', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
('chart_m05@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박갑동', '010-0001-0005', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
('chart_m06@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박을동', '010-0001-0006', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
('chart_m07@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '나갑동', '010-0001-0007', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
('chart_m08@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '차갑동', '010-0001-0008', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
('chart_m09@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '유을동', '010-0001-0009', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 4 DAY), 0),
('chart_m10@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이뽀동', '010-0001-0010', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 4 DAY), 0),
('chart_m11@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '강뽀순', '010-0001-0011', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m12@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '유영철', '010-0001-0012', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m13@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '강호순', '010-0001-0013', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m14@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이춘재', '010-0001-0014', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m15@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이은해', '010-0001-0015', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m16@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최갑동', '010-0001-0016', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
('chart_m17@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '표창원', '010-0001-0017', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
('chart_m18@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '권일용', '010-0001-0018', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
('chart_m19@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이영학', '010-0001-0019', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
('chart_m20@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김철수', '010-0001-0020', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
('chart_m21@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이영희', '010-0001-0021', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m22@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '아무개', '010-0001-0022', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m23@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '김개동', '010-0001-0023', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m24@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이이경', '010-0001-0024', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m25@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이책임', '010-0001-0025', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m26@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '정해인', '010-0001-0026', 'EXPERT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m27@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이슬', '010-0001-0027', 'CLIENT', 'ACTIVE', false, DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
('chart_m28@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '임창정', '010-0001-0028', 'CLIENT', 'ACTIVE', false, NOW(), 0),
('chart_m29@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '문근영', '010-0001-0029', 'EXPERT', 'ACTIVE', false, NOW(), 0),
('chart_m30@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '차은우', '010-0001-0030', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- ── 프로젝트 더미 (전체 추세용, member_id=1) / 일부 DONE은 완료 추세에도 잡힘 ──
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES
(1, '[차트]프로젝트 6-1', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, '[차트]프로젝트 6-2', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, '[차트]프로젝트 5-1', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '[차트]프로젝트 5-2', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '[차트]프로젝트 5-3', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '[차트]프로젝트 5-4', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, '[차트]프로젝트 4-1', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(1, '[차트]프로젝트 3-1', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '[차트]프로젝트 3-2', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '[차트]프로젝트 3-3', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '[차트]프로젝트 3-4', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '[차트]프로젝트 3-5', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, '[차트]프로젝트 2-1', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '[차트]프로젝트 2-2', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '[차트]프로젝트 2-3', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, '[차트]프로젝트 1-1', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 1-2', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 1-3', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 1-4', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 1-5', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 1-6', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '[차트]프로젝트 0-1', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW()),
(1, '[차트]프로젝트 0-2', 'APP', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'DONE', 0, false, false, NOW());
