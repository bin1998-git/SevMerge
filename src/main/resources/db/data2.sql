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

-- [5번 유저 자동 생성] 추가 의뢰인 2 (CLIENT)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client02@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이박사', '010-1111-2222', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [6번 유저 자동 생성] 추가 의뢰인 3 (CLIENT)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client03@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박대표', '010-3333-4444', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [7번 유저 자동 생성] 추가 전문가 3 (EXPERT - 기획자)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert03@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박기획', '010-2222-3333', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [8번 유저 자동 생성] 추가 전문가 4 (EXPERT - 퍼블리셔)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert04@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최마크', '010-6666-7777', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [9번 유저 자동 생성] 의뢰인 4 (ACTIVE)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client04@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최기업', '010-4111-0001', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [10번 유저 자동 생성] 의뢰인 5 (ACTIVE)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client05@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이쇼핑', '010-4111-0002', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [11번 유저 자동 생성] 의뢰인 6 (ACTIVE)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client06@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '정담당', '010-4111-0003', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [12번 유저 자동 생성] 의뢰인 7 (ACTIVE)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client07@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '나개인', '010-4111-0004', 'CLIENT', 'ACTIVE', false, NOW(), 0);

-- [13번 유저 자동 생성] 의뢰인 8 (PENDING - 인증 대기)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client08@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '인증대기', '010-4111-0005', 'CLIENT', 'PENDING', false, NOW(), 0);

-- [14번 유저 자동 생성] 의뢰인 9 (PENDING - 법인 가입 승인 대기)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client09@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '승인대기', '010-4111-0006', 'CLIENT', 'PENDING', false, NOW(), 0);

-- [15번 유저 자동 생성] 의뢰인 10 (SUSPENDED - 대금 분쟁 일시정지)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client10@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '분쟁유발', '010-4111-0007', 'CLIENT', 'SUSPENDED', false, NOW(), 0);

-- [16번 유저 자동 생성] 의뢰인 11 (SUSPENDED - 허위 발주 정지, 신고 4회)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client11@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '허위발주', '010-4111-0008', 'CLIENT', 'SUSPENDED', false, NOW(), 4);

-- [17번 유저 자동 생성] 의뢰인 12 (SUSPENDED - 탈퇴 신청 회원, 소프트딜리트)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client12@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이탈퇴', '010-4111-0009', 'CLIENT', 'SUSPENDED', true, NOW(), 0);

-- [18번 유저 자동 생성] 의뢰인 13 (ACTIVE - 매너 경고 1회)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('client13@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '유주의뢰', '010-4111-0010', 'CLIENT', 'ACTIVE', false, NOW(), 1);

-- [19번 유저 자동 생성] 전문가 5 (ACTIVE - 시니어 백엔드)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert05@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '최백엔', '010-5222-0001', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [20번 유저 자동 생성] 전문가 6 (ACTIVE - 프론트엔드)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert06@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '이프론', '010-5222-0002', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [21번 유저 자동 생성] 전문가 7 (ACTIVE - UI/UX 디자이너)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert07@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '박디자인', '010-5222-0003', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [22번 유저 자동 생성] 전문가 8 (ACTIVE - 마케터)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert08@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '강마켓', '010-5222-0004', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [23번 유저 자동 생성] 전문가 9 (ACTIVE - 풀스택 프리랜서)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert09@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '임풀스택', '010-5222-0005', 'EXPERT', 'ACTIVE', false, NOW(), 0);

-- [24번 유저 자동 생성] 전문가 10 (PENDING - 포트폴리오 자격 승인 대기)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert10@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '포폴대기', '010-5222-0006', 'EXPERT', 'PENDING', false, NOW(), 0);

-- [25번 유저 자동 생성] 전문가 11 (PENDING - 증빙 서류 검증 대기)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert11@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '서류대기', '010-5222-0007', 'EXPERT', 'PENDING', false, NOW(), 0);

-- [26번 유저 자동 생성] 전문가 12 (SUSPENDED - 연락 두절 잠적 정지, 신고 5회)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert12@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '잠적전문가', '010-5222-0008', 'EXPERT', 'SUSPENDED', false, NOW(), 5);

-- [27번 유저 자동 생성] 전문가 13 (SUSPENDED - 탈퇴 처리 전문가, 소프트딜리트)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert13@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '구자유', '010-5222-0009', 'EXPERT', 'SUSPENDED', true, NOW(), 0);

-- [28번 유저 자동 생성] 전문가 14 (ACTIVE - 퀄리티 분쟁 진행 중, 신고 2회)
INSERT INTO member_tb (email, password, name, phone, role, status, is_deleted, created_at, report_count)
VALUES ('expert14@sevmerge.com', '$2a$10$qr.ZacuGzwkRATQgVkeE4OsxnvzoSvln/5cXKYH3jFd33.mnKvPy2', '불만족', '010-5222-0010', 'EXPERT', 'ACTIVE', false, NOW(), 2);

-- 찜 샘플 데이터 (AI 인기 전문가 테스트를 위해 2번에게 찜 몰아준상태입니다)
-- 1번 회원이 2번 전문가 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (1, 2, NOW());
-- 3번 회원이 2번 전문가 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (3, 2, NOW());

-- 1번 회원이 3번 전문가 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (1, 3, NOW());

-- 5번 회원(client02)이 2번 전문가(홍길동) 찜 (기존 소스 연장)
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (5, 2, NOW());

-- 9번 회원(client04)이 2번 전문가(홍길동) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (9, 2, NOW());


-- [추격 전문가 만들기: 3번 김디자 전문가 누적 찜 3개로 2위 만들기]
-- 10번 회원(client05)이 3번 전문가(김디자) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (10, 3, NOW());

-- 11번 회원(client06)이 3번 전문가(김디자) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (11, 3, NOW());


-- [신규 추가된 전문가 찜하기: 다양한 데이터 분포 테스트]
-- 12번 회원(client07)이 7번 전문가(박기획) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (12, 7, NOW());

-- 12번 회원(client07)이 8번 전문가(최마크) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (12, 8, NOW());

-- 13번 회원(client08)이 19번 전문가(최백엔) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (13, 19, NOW());

-- 14번 회원(client09)이 20번 전문가(이프론) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (14, 20, NOW());


-- [한 회원이 여러 전문가를 연속 찜하는 패턴 테스트]
-- 18번 회원(유주의뢰)이 21번 전문가(박디자인) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (18, 21, NOW());

-- 18번 회원(유주의뢰)이 22번 전문가(강마켓) 찜
INSERT INTO expert_wish_tb (member_id, expert_id, created_at) VALUES (18, 22, NOW());



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

-- 4번 샘플 (자유게시판): 시니어 백엔드 전문가의 기술 스택 조언 (member_id: 19 - 최백엔)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '백엔드 프리랜서 시장에서 요즘 Java/Spring 입지 어떤가요?',
        '대기업이나 공공 프로젝트는 여전히 Spring Boot가 꽉 잡고 있네요. 하지만 최근 스타트업 외주나 가벼운 MVP 프로젝트는 확실히 Node.js나 Python(FastAPI) 비중이 많이 늘어난 게 체감됩니다.',
        65, true, 19, NOW());

-- 5번 샘플 (자유게시판): 의뢰인의 잠적 파트너 토로 (member_id: 5 - 이박사)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '개발 중간에 잠적하는 프리랜서는 어떻게 대처해야 하나요?',
        '프로젝트 착수하고 일주일 동안 연락이 잘 되다가 1차 마일스톤 데드라인 다가오니까 갑자기 카톡도 안 읽고 전화도 안 받네요.. 플랫폼 고객센터에 신고하면 계약금 반환 처리 가능할까요?',
        142, true, 5, NOW());

-- 6번 샘플 (자유게시판): UI/UX 디자이너의 피그마 협업 팁 (member_id: 21 - 박디자인)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '의뢰인이랑 피그마(Figma)로 시안 공유할 때 꿀팁 공유',
        '디자인 시안을 그냥 통째로 링크 주면 의뢰인분들이 어디를 봐야 할지 헷갈려 하십니다. 섹션별로 Dev Mode 가이드를 미리 지정해 두고, 변경된 화면은 별도 페이지로 아카이빙해서 주면 소통 비용이 확 줍니다.',
        93, true, 21, NOW());

-- 7번 샘플 (공지사항): 최고 관리자의 서비스 점검 안내 (member_id: 4 - 최관리)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('NOTICE', '[안내] 시스템 고도화 및 DB 안정화 작업을 위한 점검 안내',
        '안녕하세요. SevMerge 운영팀입니다. 더 안정적인 매칭 서비스를 제공하기 위해 서버 정기 점검이 진행될 예정입니다. 점검 시간 동안에는 프로젝트 등록 및 제안서 제출이 일시 제한되오니 양해 부탁드립니다.',
        420, true, 4, NOW());

-- 8번 샘플 (자유게시판): 분쟁 진행 중인 전문가의 억울함 호소 (member_id: 28 - 불만족)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '기획서에도 없던 무리한 추가 수정을 요구받고 있습니다.',
        '처음 합의한 기능명세서대로 구현을 완벽하게 끝냈는데, 클라이언트가 오픈 직전에 UI 레이아웃이랑 결제 프로세스를 완전히 바꿔달라고 요구하네요. 거절했더니 평점 테러하고 분쟁 신청하겠다고 협박합니다.',
        185, true, 28, NOW());

-- 9번 샘플 (자유게시판): 의뢰인의 프로젝트 마감 소회 (member_id: 6 - 박대표)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '처음 외주 맡겨봤는데 좋은 전문가분 만나서 다행이네요.',
        '외주 사기 이야기가 많아서 걱정했는데, SevMerge에서 포트폴리오 꼼꼼히 보고 매칭된 프론트엔드 개발자분이 약속된 일정보다 빠르게 완성해 주셨습니다. 다음 고도화 프로젝트도 이분께 맡기려고 합니다.',
        77, true, 6, NOW());

-- 10번 샘플 (자유게시판): 마케터 전문가의 포트폴리오 구성 팁 (member_id: 22 - 강마켓)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '마케팅 카테고리 입찰할 때 포트폴리오 수치화의 중요성',
        '단순히 "광고 집행 경험 있음"이라고 쓰면 무조건 탈락입니다. "한 달간 예산 300만 원으로 ROAS 250% 달성, 클릭당 비용(CPC) 40% 절감"처럼 전후 성과를 무조건 숫자로 증명해야 채택률이 올라갑니다.',
        54, true, 22, NOW());

-- 11번 샘플 (자유게시판): 서류 대기 중인 전문가의 질문 (member_id: 25 - 서류대기)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '전문가 등급 승인(PENDING) 보통 얼마나 걸리나요?',
        '프리랜서 등록하면서 사업자등록증이랑 경력 증빙 서류 제출하고 대기 상태인데, 보통 검토에 며칠 정도 소요되는지 궁금합니다. 빨리 프로젝트 입찰 참여해 보고 싶네요.',
        32, true, 25, NOW());

-- 12번 샘플 (공지사항): 플랫폼의 악성 유저 제재 공지 (member_id: 4 - 최관리)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('NOTICE', '[필독] 안심 거래 위반 및 불법 직거래 유도 회원 제재 안내',
        '안녕하세요. SevMerge 운영팀입니다. 최근 플랫폼 에스크로 시스템을 이용하지 않고 외부 연락처를 통한 직거래를 유도하는 사기 피해 사례가 접수되었습니다. 발견 즉시 계정이 SUSPENDED(정지) 처리됨을 알려드립니다.',
        512, true, 4, NOW());

-- 13번 샘플 (자유게시판): 풀스택 전문가의 재택 근무 루틴 공유 (member_id: 23 - 임풀스택)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, created_at)
VALUES ('FREE', '5년 차 풀스택 프리랜서의 지속 가능한 재택근무 루틴',
        '집에서 혼자 일하다 보면 밤낮이 바뀌기 쉬운데요. 저는 무조건 아침 9시에 집 앞 카페로 출근해서 6시까지만 일하는 루틴을 유지하고 있습니다. 슬럼프 방지방안이나 일정 관리 협업 툴 추천해 드립니다.',
        110, true, 23, NOW());



-- Board 1:1 게시판 데이터
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '결제 후 환불이 가능한가요?',
        '프로젝트 진행 중 전문가와 협의가 안 되어서 중단하게 됐는데, 이미 결제한 금액 환불 절차가 어떻게 되는지 궁금합니다.',
        0, true, 1, 'PAYMENT', NOW());

INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '카카오 로그인 후 비밀번호 변경이 안 됩니다',
        '카카오 소셜 로그인으로 가입했는데 비밀번호 변경 메뉴가 비활성화되어 있습니다. 일반 로그인으로 전환하는 방법이 있나요?',
        0, true, 1, 'SECURITY', NOW());

INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '전문가 등록 심사 기간이 얼마나 걸리나요?',
        '전문가 등록 신청한 지 3일이 지났는데 아직 승인이 안 됐습니다. 보통 심사 기간이 어느 정도 걸리는지 알고 싶습니다.',
        0, true, 1, 'NORMAL', NOW());

-- 4번 샘플 (1:1 문의): 전문가 정산 일정 문의 (member_id: 19 - 최백엔, inquiry_scope: PAYMENT)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '프로젝트 완료 후 대금 정산은 언제 완료되나요?',
        '클라이언트가 구매 확정(완료 승인)을 누른 지 2일이 지났습니다. 에스크로 대금이 제 등록 계좌로 입금되는 정확한 정산 요일과 시간대를 알고 싶습니다.',
        0, true, 19, 'PAYMENT', NOW());

-- 5번 샘플 (1:1 문의): 계정 도용 의심 문의 (member_id: 6 - 박대표, inquiry_scope: SECURITY)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '새로운 기기에서 로그인되었다는 알림을 받았습니다',
        '제가 접속하지 않은 시간대에 다른 지역의 IP로 로그인 성공 알림 문자가 왔습니다. 현재 임시로 비밀번호를 바꿨는데, 기존 세션 전체 로그아웃 처리가 가능한가요?',
        0, true, 6, 'SECURITY', NOW());

-- 6번 샘플 (1:1 문의): 단순 이용 방법 문의 (member_id: 5 - 이박사, inquiry_scope: NORMAL)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '프로젝트 모집 마감일 연장 방법이 궁금합니다',
        '현재 등록하여 구인 중인 프로젝트의 마감일이 내일까지인데, 마음에 드는 제안서가 없어 기간을 일주일 더 연장하고 싶습니다. 진행 중에도 수정이 가능한가요?',
        0, true, 5, 'NORMAL', NOW());

-- 7번 샘플 (1:1 문의): 세금계산서 발행 문의 (member_id: 9 - 최기업, inquiry_scope: PAYMENT)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '기업 회원 외주 비용 세금계산서 발행 프로세스',
        '플랫폼 내에서 안전결제(에스크로)로 500만 원을 결제했습니다. 회사 증빙용 전자세금계산서 발행을 위한 사업자등록증 제출 경로를 안내해 주세요.',
        0, true, 9, 'PAYMENT', NOW());

-- 8번 샘플 (1:1 문의): 포트폴리오 반려 사유 확인 (member_id: 24 - 포폴대기, inquiry_scope: NORMAL)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '전문가 포트폴리오 등록 거절 사유 확인 부탁드립니다',
        '포트폴리오 심사 결과가 반려로 변경되었는데, 알림창에 상세 사유가 표시되지 않습니다. 어떤 서류나 링크에 보완이 필요한지 확인해 주시면 감사하겠습니다.',
        0, true, 24, 'NORMAL', NOW());

-- 9번 샘플 (1:1 문의): 개인정보 변경 실패 문의 (member_id: 20 - 이프론, inquiry_scope: SECURITY)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '개인 연락처 변경 시 본인인증 오류 현상',
        '휴대폰 번호가 바뀌어 마이페이지에서 회원 정보 수정을 시도했습니다. 그런데 KCB 본인인증 단계에서 자꾸 세션 만료 에러가 발생하며 변경이 실패합니다.',
        0, true, 20, 'SECURITY', NOW());

-- 10번 샘플 (1:1 문의): 계약 불이행 신고 및 중재 요청 (member_id: 28 - 불만족, inquiry_scope: NORMAL)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '클라이언트의 일방적인 연락 두절로 인한 분쟁 중재 요청',
        '1차 결과물을 전달한 지 일주일째 클라이언트가 피드백을 주지 않고 모든 연락을 피하고 있습니다. 마감 기한이 지난 상황인데 계약 파기 절차를 밟아야 하나요?',
        0, true, 28, 'NORMAL', NOW());

-- 11번 샘플 (1:1 문의): 결제 오류 문의 (member_id: 21 - 박디자인, inquiry_scope: PAYMENT)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '안심 결제 도중 카드 승인 실패 오류 건',
        '신용카드로 프로젝트 대금 결제를 시도했으나 "ISP 인증 오류"라는 팝업과 함께 결제가 튕겼습니다. 카드사 문제는 아니라는데 크롬 브라우저 호환성 문제인가요?',
        0, true, 21, 'PAYMENT', NOW());

-- 12번 샘플 (1:1 문의): 2차 인증 설정 문의 (member_id: 25 - 서류대기, inquiry_scope: SECURITY)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '로그인 시 OTP 또는 SMS 2차 인증 설정 방법',
        '보안 강화를 위해 로그인할 때마다 지정된 휴대폰 번호로 인증번호를 받아야만 접속이 가능하도록 이중 보안 설정을 하고 싶습니다. 메뉴가 어디에 있나요?',
        0, true, 25, 'SECURITY', NOW());

-- 13번 샘플 (1:1 문의): 알림 수신 거부 설정 (member_id: 1 - 김의뢰, inquiry_scope: NORMAL)
INSERT INTO board_tb (board_type, title, content, view_count, is_active, member_id, inquiry_scope, created_at)
VALUES ('INQUIRY', '새벽 시간대 프로젝트 제안 알림톡 차단 요청',
        '전문가들의 입찰 제안서가 등록될 때마다 카카오톡 알림이 오는데, 새벽 2~3시에도 알림이 와서 수면에 방해가 됩니다. 특정 시간대 알림 무음 기능이 있나요?',
        0, true, 1, 'NORMAL', NOW());

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

-- 4번 샘플: 글로벌 이커머스 웹사이트 (member_id: 1, category: WEB, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '다국어 지원 및 해외 결제 연동 글로벌 역직구 커머스 웹 구축', 'WEB', 'K-뷰티 상품을 해외로 판매하기 위한 반응형 쇼핑몰 웹사이트 개발입니다. 영어/일어 번역 인프라 및 페이팔, 스트라이프 등 해외PG사 연동이 필수입니다.', 15000000, 25000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 5번 샘플: 소셜 데이팅 모바일 앱 (member_id: 5, category: APP, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (5, '취향 및 MBTI 기반 동네 친구 매칭 소셜 네트워킹 모바일 앱 개발', 'APP', '사용자 성향 데이터를 기반으로 매칭 알고리즘을 적용한 하이브리드(Flutter) 앱 구축 건입니다. 실시간 1:1 채팅 및 프로필 사진 본인 인증 API 연동이 필요합니다.', 18000000, 35000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 6번 샘플: 핀테크 자산관리 서비스 UI/UX (member_id: 6, category: UI_UX, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (6, 'MZ세대 겨냥 소액 투자 및 자산 관리 서비스 UI/UX 디자인 고도화', 'UI_UX', '기존 모바일 웹 앱의 복잡한 금융 대시보드를 직관적이고 트렌디하게 리뉴얼하는 디자인 프로젝트입니다. 피그마 디자인 시스템 구축 및 프로토타이핑 포함입니다.', 4000000, 8000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 7번 샘플: 이커머스 구매 데이터 분석 (member_id: 9, category: DATA, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (9, '자사 쇼핑몰 고객 행동 로그 및 매출 데이터 기반 이탈률 분석 및 시각화', 'DATA', 'GA4 데이터와 DB 내 구매 이력을 연동하여 유저들의 이탈 구간을 분석하고 태블로(Tableau) 대시보드로 시각화하는 데이터 컨설팅 작업입니다.', 3000000, 6000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 8번 샘플: 크라우드펀딩 제품 홍보 영상 (member_id: 10, category: VIDEO, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (10, '신개념 스마트 캠핑 장비 펀딩 런칭용 3D 그래픽 홍보 영상 제작', 'VIDEO', '와디즈 오픈 예정 페이지에 들어갈 60초 분량의 제품 소개 영상입니다. 제품의 내부 구조를 보여줄 3D 렌더링 및 모션 그래픽 컷이 포함되어야 합니다.', 5000000, 9000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 9번 샘플: 오프라인 매장 음향 컨설팅 (member_id: 11, category: ETC, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (11, '대형 베이커리 카페 공간별 맞춤형 BGM 큐레이션 및 음향 시스템 설계', 'ETC', '신규 오픈하는 3층 규모 대형 매장의 공간별 분위기에 맞는 음악 리스트업 및 스피커 배치 기획을 도와주실 공간 음향 전문가를 찾습니다.', 1000000, 2000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 10번 샘플: 임시저장 - AI 자소서 분석 (member_id: 12, category: DATA, project_status: DRAFT)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (12, '[임시저장] 채용 연동형 구직자 자기소개서 키워드 추출 및 역량 평가 모델링', 'DATA', '자연어 처리(NLP)를 활용하여 이력서 내 직무 연관성을 자동 채점하는 내부 시스템 초안입니다.', 0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, NOW());

-- 11번 샘플: 기업 사내 교육용 타이포그래피 영상 (member_id: 15, category: VIDEO, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (15, '임직원 정보보안 필수 교육용 인포그래픽 2D 애니메이션 영상 제작', 'VIDEO', '사내 인트라넷 게시용 3분 내외 모션 그래픽 영상입니다. 원고 기획안은 제공되며, 성우 녹음 및 배경음 포함 라이선스 해결이 필요합니다.', 2500000, 4500000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 12번 샘플: 병원 예약 키오스크 GUI 디자인 (member_id: 17, category: UI_UX, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (17, '종합병원 무인 수납 및 처방전 발급 키오스크 터치 UI/UX 디자인', 'UI_UX', '시니어 환자분들의 가독성을 최우선으로 고려한 24인치 세로형 키오스크 화면 디자인입니다. 직관적인 레이아웃과 큰 버튼 위주의 설계가 필요합니다.', 3500000, 5500000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- 13번 샘플: 기업 신년 인쇄물 디자인 및 패키징 (member_id: 19, category: ETC, project_status: OPEN)
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (19, '2027년도 기업 VIP 고객 대상 웰컴 키트 굿즈 패키지 및 캘린더 디자인', 'ETC', '다이어리, 달력, 펜 등으로 구성된 VIP 패키지 디자인입니다. 인쇄용 가이드 라인(CMYK) 작업과 최종 감리 조율까지 가능한 분을 구합니다.', 4000000, 7000000, NOW(), 'ALL', 'OPEN', 0, false, false, NOW());

-- [OPEN 1] member_id: 1, category: WEB
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '반려동물 용품 구독형 커머스 반응형 웹사이트 구축', 'WEB', '정기 구독 결제 기능이 핵심인 펫 커머스 웹 구축 건입니다. PG 정기 결제 API 연동 경험이 있으신 빌더 또는 개발팀을 모십니다.', 4000000, 8000000, NOW(), 'ALL', 'OPEN', 12, false, false, NOW());

-- [OPEN 2] member_id: 5, category: APP
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (5, '위치 기반 실시간 동네 구인구직 모바일 앱(Flutter) 개발', 'APP', '당근마켓의 알바 기능과 유사한 지역 기반 매칭 하이브리드 앱입니다. GPS 연동 및 실시간 채팅, 푸시 알림 구현이 필수입니다.', 15000000, 25000000, NOW(), 'ALL', 'OPEN', 45, false, false, NOW());

-- [OPEN 3] member_id: 6, category: UI_UX
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (6, '신규 골프 매칭 플랫폼 서비스 UI/UX 디자인 및 피그마 가이드라인', 'UI_UX', '앱과 웹 버전을 동시에 아우르는 트렌디하고 스포티한 톤앤매너의 화면 설계입니다. 와이어프레임과 컴포넌트 시스템 구축이 포함됩니다.', 3000000, 5500000, NOW(), 'ALL', 'OPEN', 28, false, false, NOW());

-- [CLOSED 1] member_id: 9, category: DATA
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (9, '사내 고객 세그먼트 분석을 위한 데이터 마이그레이션 및 파이프라인 구축', 'DATA', '지원자 모집이 마감되어 적합한 데이터 엔지니어를 선정 중입니다. 레거시 DB 데이터를 AWS Redshift로 안전하게 이전하는 작업입니다.', 8000000, 12000000, NOW(), 'ALL', 'CLOSED', 62, false, false, NOW());

-- [CLOSED 2] member_id: 10, category: VIDEO
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (10, 'IT 신제품 크라우드펀딩용 3D 그래픽 인포그래픽 홍보 영상 제작', 'VIDEO', '많은 분들이 지원해 주셔서 모집을 마감합니다. 내부 회의를 거쳐 최종 계약할 영상 편집 전문가분을 선정할 예정입니다.', 5000000, 7500000, NOW(), 'ALL', 'CLOSED', 89, false, false, NOW());

-- [CLOSED 3] member_id: 11, category: ETC
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (11, '스타트업 영문 IR 자료 교정 및 번역 컨설팅 전문가 초빙', 'ETC', '해외 투자 유치용 피치덱의 영문 번역 및 비즈니스 링구얼 검수 건입니다. 지원서 마감 후 미팅 대상자를 선별하고 있습니다.', 1500000, 2500000, NOW(), 'ALL', 'CLOSED', 34, false, false, NOW());

-- [IN_PROGRESS 1] member_id: 12, category: WEB
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (12, '학원 관리 시스템(LMS) 반응형 웹 전산망 신규 구축', 'WEB', '매칭된 전문가와 함께 1차 마일스톤(화면 퍼블리싱 및 DB 설계) 단계를 한창 진행 중인 프로젝트 레코드입니다.', 10000000, 18000000, NOW(), 'ALL', 'IN_PROGRESS', 55, false, false, NOW());

-- [IN_PROGRESS 2] member_id: 15, category: APP
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (15, '피트니스 식단 기록 및 칼로리 스캔 모바일 앱 개발', 'APP', 'Flutter 기반으로 계약이 체결되어 현재 개발이 활발히 진행 중입니다. 중간 기능 테스트 단계를 앞두고 있습니다.', 9000000, 14000000, NOW(), 'ALL', 'IN_PROGRESS', 72, false, false, NOW());

-- [IN_PROGRESS 3] member_id: 17, category: UI_UX
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (17, 'B2B 가스 충전 시스템 대시보드 UI/UX 리뉴얼 디자인', 'UI_UX', '전문가분이 피그마로 메인 대시보드 시안을 작업 중이십니다. 현재 기획서 구체화 및 UI 고도화 진행 단계입니다.', 4000000, 6000000, NOW(), 'ALL', 'IN_PROGRESS', 41, false, false, NOW());

-- [COMPLETED 1] member_id: 19, category: DATA
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (19, '쇼핑몰 고객 이탈 방지용 AI 추천 알고리즘 모델링', 'DATA', '전문가분이 소스코드를 최종 전달하셨으며, 현재 의뢰인이 정확도 검증 및 서버 연동 테스트(검수 단계)를 진행하고 있습니다.', 7000000, 11000000, NOW(), 'ALL', 'COMPLETED', 68, false, false, NOW());

-- [COMPLETED 2] member_id: 21, category: VIDEO
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (21, '유튜브 채널 런칭용 인트로 모션 그래픽 및 템플릿 제작', 'VIDEO', '최종 영상 결과물이 납품되어 화질 및 자막 오타 검수를 진행 중인 상태입니다. 검수 완료 시 대금이 최종 정산됩니다.', 1200000, 2000000, NOW(), 'ALL', 'COMPLETED', 29, false, false, NOW());

-- [COMPLETED 3] member_id: 1, category: ETC
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (1, '2027년도 스마트 오피스 신제품 전파 인증 대행 및 서류 가이드', 'ETC', '전파연구소 제출 서류 가이드 및 테스트 리포트 작성이 완료되어 클라이언트측 최종 승인 처리를 대기하고 있습니다.', 2000000, 4000000, NOW(), 'ALL', 'COMPLETED', 19, false, false, NOW());

INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (5, '사내 도서 대여 및 바코드 전산 관리 웹 사이트 구축', 'WEB', '성공적으로 모든 개발과 검수가 종료되어 전문가에게 대금 지급이 완료되었고 상호 평점 리뷰까지 마친 종료 건입니다.', 3000000, 5000000, NOW(), 'ALL', 'DONE', 104, false, false, NOW());

-- [DONE 2] member_id: 6, category: APP
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (6, '무인 스튜디오 대여실 실시간 도어락 연동 예약 앱 개발', 'APP', '앱스토어 최종 출시 및 도어락 Open API 연동까지 완벽하게 마무리되어 안전하게 정산 및 클로징된 프로젝트입니다.', 8000000, 13000000, NOW(), 'ALL', 'DONE', 132, false, false, NOW());

-- [DONE 3] member_id: 9, category: UI_UX
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (9, '사스(SaaS) 협업 툴 랜딩 페이지 UI/UX 디자인 리뉴얼', 'UI_UX', '최종 피그마 원본 파일 인계를 마치고 에스크로 대금 에이전트 정산까지 깔끔하게 완료된 완료 히스토리 데이터입니다.', 2500000, 4500000, NOW(), 'ALL', 'DONE', 95, false, false, NOW());

-- [CANCELLED 1] member_id: 10, category: WEB
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (10, '블록체인 기반 미술품 조각 투자 플랫폼 웹 사이트 구축', 'WEB', '스타트업 내부 사정 및 전면 사업 기획 수정으로 인해 모집 도중 부득이하게 철회 및 취소 처리된 프로젝트입니다.', 25000000, 50000000, NOW(), 'ALL', 'CANCELLED', 41, false, false, NOW());

-- [CANCELLED 2] member_id: 11, category: APP
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (11, '위치 기반 등산 코스 추천 및 커뮤니티 하이브리드 앱', 'APP', '개발 도중 전문가의 일방적인 잠적 및 계약 불이행으로 인해 분쟁 조정을 거쳐 계약이 파기 및 취소된 레코드입니다.', 6000000, 10000000, NOW(), 'ALL', 'CANCELLED', 59, false, false, NOW());

-- [CANCELLED 3] member_id: 12, category: VIDEO
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (12, '기업 신년 하반기 성과 보고회 타이포그래피 애니메이션 영상', 'VIDEO', '보고회 행사 일정이 취소됨에 따라 외주 발주가 불필요해져 상호 합의 하에 취소 및 환불 처리되었습니다.', 2000000, 3500000, NOW(), 'ALL', 'CANCELLED', 17, false, false, NOW());

-- [DRAFT 1] member_id: 15, category: WEB
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (15, '[임시저장] 전국 학원 버스 실시간 셔틀 노선 공유 웹 플랫폼', 'WEB', '요구사항 정의서를 작성 중입니다. 예산 및 상세 기능 스펙이 확정되면 정식 등록(OPEN)으로 전환할 예정입니다.', 0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, NOW());

-- [DRAFT 2] member_id: 17, category: APP
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (17, '[임시저장] 음성 인식 기반 독거노인 말벗 서비스 앱', 'APP', '정부 지원 사업 선정작으로 아직 세부 개발 기획을 다듬고 있는 단계의 임시 저장 더미 데이터입니다.', 0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, NOW());

-- [DRAFT 3] member_id: 19, category: UI_UX
INSERT INTO project_tb (member_id, title, category, description, budget_min, budget_max, deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
VALUES (19, '[임시저장] 해외 명품 구매 대행 쇼핑몰 모바일 UI 디자인 고도화', 'UI_UX', '아직 상세 내용을 기술하지 않은 빈 양식의 초안 작성 중 레코드입니다.', 0, 0, NOW(), 'ALL', 'DRAFT', 0, false, false, NOW());


-- Bid 더미데이터
-- expert_id는 member_tb의 id (EXPERT 역할인 2번, 3번만 사용)
INSERT INTO bid_tb (project_id, expert_id, cover_letter, approach, estimated_days, proposed_price, status, is_deleted,
                    created_at)
VALUES (1, 2, '웹 개발 5년 경력자입니다. 최선을 다하겠습니다.', 'React + Spring Boot로 개발하겠습니다.', 30, 2500000, 'PENDING', false, NOW()),
       (1, 3, '쇼핑몰 개발 다수 경험 있습니다.', 'Vue.js + Node.js로 개발하겠습니다.', 25, 2800000, 'PENDING', false, NOW()),
       (2, 2, '앱 개발 전문가입니다.', 'Flutter + Firebase로 개발하겠습니다.', 60, 8000000, 'PENDING', false, NOW()),
       (3, 3, 'UI/UX 디자인 전문입니다.', '피그마로 작업하겠습니다.', 14, 1500000, 'PENDING', false, NOW()),
       (3, 2, '풀스택으로 디자인까지 커버합니다.', 'React + Figma 병행 작업하겠습니다.', 21, 3500000, 'PENDING', false, NOW());







-- Comment 샘플데이터

-- 1. 다른 프리랜서 개발자의 공감 댓글 (member_id: 3)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 3, '오... 기능명세서 초안을 역으로 제안하는 방식은 생각 못 해봤는데 진짜 좋은 팁이네요. 다음 입찰 때 바로 써먹어 보겠습니다!', NOW(), false);

-- 2. 주니어 프리랜서의 추가 질문 댓글 (member_id: 4)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 4, '작성자님, 혹시 클라이언트가 요구사항을 아예 한 줄로만 적어둔 경우에도 이 방식이 통할까요? 기획 방향 잡기가 너무 어렵네요 ㅠㅠ', NOW(), false);

-- 3. 1번 글(제안서 팁)에 대한 시니어 백엔드 전문가의 조언 (member_id: 19 - 최백엔)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 19, '맞습니다. 특히 아키텍처나 인프라 구성을 다이어그램으로 가볍게 그려서 첨부해 주면 비개발자 출신 의뢰인분들이 엄청 좋아하십니다.', NOW(), false);

-- 4. 1번 글(제안서 팁)에 대한 프론트엔드 전문가의 팁 (member_id: 20 - 이프론)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (1, 20, '좋은 글이네요. 저는 와이어프레임 컴포넌트 몇 개만 미리 짜서 피그마 프로토타입 링크로 보여드리는데 이것도 효과 만점입니다.', NOW(), false);

-- 5. 2번 글(MVP 예산 문의)에 대한 풀스택 프리랜서의 견해 (member_id: 23 - 임풀스택)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (2, 23, '채팅이랑 결제가 필수라면 500만 원은 조금 타이트할 수 있습니다. 하이브리드 앱 기준으로 최소 800만 원 이상은 잡으셔야 퀄리티가 나옵니다.', NOW(), false);

-- 6. 2번 글(MVP 예산 문의)에 대한 마케터의 현실적 의견 (member_id: 22 - 강마켓)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (2, 22, '개발비에 예산을 다 쓰시면 안 됩니다! 런칭 후 마케팅 예산도 최소 200~300만 원은 따로 세팅해 두셔야 유저가 유입됩니다.', NOW(), false);

-- 7. 2번 글(MVP 예산 문의)에 대한 다른 의뢰인의 공감 (member_id: 5 - 이박사)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (2, 5, '저도 딱 그 정도 기능으로 SevMerge에 올렸었는데, 제안서가 10개 넘게 들어왔었어요. 금액대는 충분히 메리트 있는 것 같습니다.', NOW(), false);

-- 8. 4번 글(Spring 입지)에 대한 풀스택 개발자의 대답 (member_id: 23 - 임풀스택)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (4, 23, '동감합니다. 스타트업 초기 MVP는 빠른 기능 구현과 출시가 생명이라 NestJS나 FastAPI로 가볍게 밀고 나가는 경우가 허다하더군요.', NOW(), false);

-- 9. 4번 글(Spring 입지)에 대한 프론트엔드 개발자의 댓글 (member_id: 20 - 이프론)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (4, 20, '프론트 입장에서도 백엔드가 Node인 게 API 규격 맞추거나 타입스크립트 공유하기 편해서 선호하는 면이 있습니다.', NOW(), false);

-- 10. 5번 글(잠적 파트너 토로)에 대한 시니어 전문가의 대처법 (member_id: 19 - 최백엔)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (5, 19, '위로를 전합니다.. 계약서 작성할 때 마일스톤별로 검수 기간과 연락 두절 시 계약 해지 조항을 꼼꼼히 명시해 두는 게 유일한 예방책입니다.', NOW(), false);

-- 11. 5번 글(잠적 파트너 토로)에 대한 디자이너의 분노 (member_id: 21 - 박디자인)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (5, 21, '같은 전문가지만 중간에 잠적하는 사람들은 진짜 이해가 안 가네요. 플랫폼 차원에서 영구 정지 피널티를 강하게 때려야 합니다.', NOW(), false);

-- 12. 6번 글(피그마 협업 팁)에 대한 의뢰인의 격한 공감 (member_id: 1 - 김의뢰)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (6, 1, '제발 모든 디자이너분들이 이 글을 보셨으면 좋겠네요! 디자인 파일 그냥 던져주시면 뭐가 바뀐 건지 숨은그림찾기 하느라 하루 다 가요 ㅠㅠ', NOW(), false);

-- 13. 6번 글(피그마 협업 팁)에 대한 다른 디자이너의 칭찬 (member_id: 3)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (6, 3, '오 아카이빙 페이지 따로 파는 건 진짜 신의 한 수네요. 당장 오늘 피드백 반영할 때부터 적용해 봐야겠습니다.', NOW(), false);

-- 14. 8번 글(과도한 수정 요구)에 대한 다른 개발자의 위로 (member_id: 2)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (8, 2, '절대 그냥 해주지 마세요. 무상 수정 범위를 벗어난 영역은 단호하게 추가 비용 청구하셔야 합니다. 계약서 증빙 챙겨서 고객센터에 중재 요청하세요.', NOW(), false);

-- 15. 8번 글(과도한 수정 요구)에 대한 시니어의 조언 (member_id: 19 - 최백엔)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (8, 19, '이래서 최초 요구사항 정의서에 상호 서명이나 확답 메일을 받아두는 게 중요합니다. 분쟁 조정 들어가면 문서화된 기록이 제일 유효하거든요.', NOW(), false);

-- 16. 9번 글(외주 성공 소회)에 대한 프론트엔드 전문가의 댓글 (member_id: 20 - 이프론)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (9, 20, '훈훈한 후기네요. 클라이언트분이 소통을 명확하게 해주시고 피드백을 제때 주시면 작업자도 신나서 일정보다 빨리 끝내게 됩니다 ㅎㅎ', NOW(), false);

-- 17. 9번 글(외주 성공 소회)에 대한 의뢰인의 질문 (member_id: 9 - 최기업)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (9, 9, '혹시 매칭되신 전문가분 정보 좀 쪽지로 공유해 주실 수 있나요? 저희 팀도 다음 달에 프론트엔드 외주 기획 중이라서요.', NOW(), false);

-- 18. 10번 글(포폴 수치화)에 대한 주니어 마케터의 질문 (member_id: 25 - 서류대기)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (10, 25, '작성자님 글 보고 제 포트폴리오 보니까 너무 부끄럽네요.. 혹시 신입이라 실제 집행 수치가 낮을 때는 어떻게 강조하는 게 좋을까요?', NOW(), false);

-- 19. 10번 글(포폴 수치화)에 대한 마케터의 답댓글 (member_id: 22 - 강마켓)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (10, 22, '금액이 적다면 "전월 대비 성장률(%)"이나 "A/B 테스트를 통한 전환율 2배 개선"처럼 비율 위주로 소구점을 잡으시는 것을 추천합니다!', NOW(), false);

-- 20. 11번 글(승인 대기 기간)에 대한 마케터의 답변 (member_id: 22 - 강마켓)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (11, 22, '저는 영업일 기준 한 3일 정도 걸렸던 것 같아요. 신청자가 몰리면 조금 더 연장되기도 하니 내일까지는 기다려 보세요.', NOW(), false);

-- 21. 11번 글(승인 대기 기간)에 대한 디자이너의 경험담 (member_id: 21 - 박디자인)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (11, 21, '서류에 누락이나 해상도 문제 있으면 반려 처리되기도 하더라고요. 메일함이나 플랫폼 알림톡 한 번 다시 확인해 보세요!', NOW(), false);

-- 22. 13번 글(재택 루틴)에 대한 다른 프리랜서의 공감 (member_id: 2)
INSERT INTO comment_tb (board_id, member_id, content, created_at, is_deleted)
VALUES (13, 2, '강력 공감합니다. 집에서 일하면 침대의 유혹 때문에 능률이 반 토막 나더라고요. 공간 분리형 출근 루틴이 롱런의 핵심입니다.', NOW(), false);

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

-- 4번째 샘플 데이터 (개인정보 유출 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (4, 20, '개인정보 유출 / 권리 침해', '분쟁 중인 상대방의 실제 이름, 전화번호, 그리고 근무하는 회사명을 동의 없이 댓글에 그대로 노출하여 심각한 피해를 주고 있습니다.', NOW());

-- 5번째 샘플 데이터 (직거래 유도 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (5, 19, '플랫폼 외부 직거래 유도', '안전결제(에스크로)를 이용하지 않고 수수료를 아끼자며 자신의 개인 카카오톡 아이디와 개인 계좌번호를 남겨 외부 거래를 유도하고 있습니다.', NOW());

-- 6번째 샘플 데이터 (음란성 도배 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (6, 21, '음란성 / 청소년 유해 매체물', '프로젝트와 전혀 상관없는 성인물 사이트 링크와 청소년에게 유해한 유흥업소 관련 홍보 문구를 댓글에 작성했습니다.', NOW());

-- 7번째 샘플 데이터 (허위 사실 유포 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (7, 6, '허위 사실 유포 및 업무 방해', '해당 기업이 임금을 체불했다거나 사기를 친다는 등 확인되지 않은 악의적인 루머를 퍼뜨려 정상적인 외주 구인 업무를 방해하고 있습니다.', NOW());

-- 8번째 샘플 데이터 (정치/종교 도배 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (8, 22, '정치 / 종교 성향의 분쟁 유발', 'IT 외주 커뮤니티 성격에 전혀 맞지 않는 특정 정치인 비하 발언과 특정 종교 포교 목적의 댓글을 지속적으로 작성하고 있습니다.', NOW());

-- 9번째 샘플 데이터 (사기 및 불법 행위 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (9, 9, '불법 프로그램 / 사기 유도', '기존 상용 앱의 보안망을 뚫는 크래킹 툴이나 매크로 프로그램을 판매한다는 카카오톡 오픈채팅방 주소를 댓글로 공유하고 있습니다.', NOW());

-- 10번째 샘플 데이터 (타 서비스 유도/비방 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (10, 23, '경쟁 플랫폼 홍보 및 광고', 'SevMerge 플랫폼을 비방하면서 타사 경쟁 매칭 서비스의 가입 추천인 코드와 랜딩 페이지 주소를 댓글에 반복 도배하고 있습니다.', NOW());

-- 11번째 샘플 데이터 (도배성 무의미 댓글 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (11, 2, '동일 내용 반복 / 무의미한 도배', '내용 없이 의미 없는 단순 자음(ㅋㅋㅋ, ㅎㅎㅎ)이나 특수문자만을 수십 줄 넘게 작성하여 다른 유저들의 댓글 가독성을 심각하게 해칩니다.', NOW());

-- 12번째 샘플 데이터 (저작권 침해 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (12, 3, '저작권 및 지식재산권 침해', '다른 작가가 유료로 판매 중인 피그마 UI 키트나 디자인 소스 파일의 다운로드 구글 드라이브 링크를 불법으로 무단 공유하고 있습니다.', NOW());

-- 13번째 샘플 데이터 (거래 협박 및 공포조성 신고)
INSERT INTO comment_report_tb (comment_id, reporter_id, reason, content_detail, created_at)
VALUES (13, 28, '공포심 유발 및 협박성 발언', '계약 분쟁 과정에서 발생한 감정을 다스리지 못하고 "찾아가서 가만두지 않겠다", "업계에 매장하겠다" 등의 보복성 협박 댓글을 남겼습니다.', NOW());


-- 블랙리스트 샘플 데이터
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (1, '댓글 신고 3회 누적 자동 정지 처리 (욕설 및 비속어 유포)', '4,5,6', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 1;
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (2, '댓글 신고 3회 누적 자동 정지 처리 (홍보성 스팸 도배)', '7,8,9', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 2;
-- 3번 샘플: 플랫폼 외부 직거래 유도 (7일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (5, '댓글 및 메시지를 통한 플랫폼 외부 불법 직거래 유도', '10,11,12', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 5;

-- 4번 샘플: 분쟁 중 개인정보 유출 및 권리 침해 (14일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (6, '커뮤니티 내 타 유저의 실명 및 연락처 무단 유출 (개인정보 침해)', '13,14', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 6;

-- 5번 샘플: 유료 리소스 무단 배포 및 저작권 침해 (30일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (9, '상용 디자인 소스 및 피그마 유료 키트 무단 공유 (저작권 위반)', '15,16,17', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 9;

-- 6번 샘플: 허위 사실 유포로 인한 특정 기업 업무 방해 (14일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (20, '근거 없는 허위 사실 유포 및 기업 회원 상습 비방 댓글 작성', '18,19', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 20;

-- 7번 샘플: 경쟁 플랫폼 상습 홍보 및 도배 (7일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (21, '타사 외주 플랫폼 가입 유도 및 개인 추천인 코드 상습 도배', '20,21,22', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 21;

-- 8번 샘플: 프로젝트 진행 중 상습 잠적 및 연락 두절 (90일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (26, '외주 계약 착수 후 고의적인 연락 두절 및 잠적 (신고 5회 누적)', '23,24,25,26,27', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 26;

-- 9번 샘플: 욕설 및 협박성 발언 (30일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (28, '계약 분쟁 과정에서 상대방에 대한 보복성 협박 및 공포조성 발언', '28,29', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 28;

-- 10번 샘플: 불법 프로그램 및 매크로 판매 유도 (영구 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (22, '보안망 크래킹 툴 및 매크로 프로그램 불법 판매 유도 (영구 제한)', '30,31,32', NOW(), DATE_ADD(NOW(), INTERVAL 3650 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 22;

-- 11번 샘플: 의미 없는 자음/모음 상습 도배 (3일 정지)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (25, '게시판 가독성을 해치는 동일 내용 및 무의미한 자음 반복 도배', '33,34,35', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), true);
-- UPDATE member_tb SET status = 'SUSPENDED' WHERE id = 25;

-- 12번 샘플: 과거 정지 이력이 있으나 현재는 만료된 데이터 (비활성화 상태)
INSERT INTO blacklist_tb (member_id, reason, report_ids, banned_at, expired_at, is_active)
VALUES (19, '댓글 신고 누적 정지 (기간 만료로 인한 정상 회원 전환 건)', '1,2,3', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), false);
-- 현재는 정상 상태이므로 UPDATE 구문 생략 가능



-- ExpertProfile 샘플데이터
-- 1번 샘플: 평점 높은 7년 차 풀스택 개발자 프로필 (member_id: 2)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality,is_certified,expert_grade)
VALUES (2, 'https://sevmerge.com/storage/profiles/expert_01.png',
        '안녕하세요! 대기업 출신 프리랜서 풀스택 개발자 홍길동입니다. 대규모 트래픽 처리 및 쇼핑몰, 커뮤니티 웹/앱 개발 전문입니다. 안정적이고 확장성 있는 아키텍처를 약속드립니다.',
        '- 前 카카오 백엔드 개발자 (3년)\n- 프리랜서 외주 개발 진행 (4년)\n- SevMerge 누적 프로젝트 20건 이상 완료',
        'Spring Boot, Java, AWS, React, Next.js', true,'NORMAL');

-- 2번 샘플: 스타트업 전문 UI/UX 디자이너 프로필 (member_id: 3)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified,expert_grade)
VALUES (3, 'https://sevmerge.com/storage/profiles/expert_02.png',
        '의뢰인님의 추상적인 아이디어를 트렌디하고 직관적인 화면으로 시각화해 드립니다. 와이어프레임 기획부터 피그마 고도화 프로토타입까지 책임지고 가이드해 드립니다.',
        '- 대형 IT 에이전시 수석 디자이너 (4년)\n- 스타트업 MVP 디자인 전담 프리랜서 활동 중',
        'Figma, UI/UX Design, Adobe XD, GUI, Branding', true,'SKILLED');

-- 3번 샘플: 시니어 백엔드 개발자 프로필 (member_id: 19 - 최백엔)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (19, 'https://sevmerge.com',
        '안녕하세요. 10년 차 시니어 백엔드 엔지니어 최백엔입니다. 금융권 시스템 고도화 및 대용량 DB 아키텍처 설계 전문입니다. 안정적인 서버 인프라를 구축해 드립니다.',
        '- 대형 금융사 인프라팀 파트장 (6년)\n- 백엔드 전문 프리랜서 프리랜서 (4년)\n- MSA 아키텍처 다수 도입',
        'Java, Spring Boot, MySQL, Redis, Docker, AWS', true, 'MASTER');

-- 4번 샘플: 프론트엔드 개발자 프로필 (member_id: 20 - 이프론)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (20, 'https://sevmerge.com',
        '화면 퍼포먼스 최적화와 UX 개선에 집착하는 프론트엔드 개발자 이프론입니다. React 및 Next.js를 활용한 모던 웹 애플리케이션 구축을 전문으로 합니다.',
        '- IT 스타트업 프론트엔드 파트 리드 (3년)\n- 반응형 이커머스 웹 사이트 다수 구축',
        'JavaScript, TypeScript, React, Next.js, Tailwind CSS', true, 'SKILLED');

-- 5번 샘플: 트렌디 UI/UX 디자이너 프로필 (member_id: 21 - 박디자인)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (21, 'https://sevmerge.com',
        '클라이언트의 비즈니스 목표를 분석하여 사용자 중심의 화면을 설계하는 박디자인입니다. 모바일 앱 UI/UX 및 디자인 시스템 구축에 강점이 있습니다.',
        '- 디자인 에이전시 UI 팀장 (4년)\n- 스타트업 런칭 앱 15건 디자인 시스템 구축',
        'Figma, Adobe Illustrator, UI/UX Design, App Design', true, 'SKILLED');

-- 6번 샘플: 그로스 해킹 퍼포먼스 마케터 프로필 (member_id: 22 - 강마켓)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (22, 'https://sevmerge.com',
        '데이터를 기반으로 최소 예산 최대 효율을 뽑아내는 퍼포먼스 마케터 강마켓입니다. 검색 광고(SA) 및 디스플레이 광고(DA) 효율 극대화 솔루션을 제공합니다.',
        '- 이커머스 전문 브랜드 마케팅 팀장 (3년)\n- 누적 광고 집행 예산 10억 이상 총괄',
        'Google Analytics(GA4), Meta Ads, Google Ads, Growth Hacking', true, 'NORMAL');

-- 7번 샘플: 베테랑 풀스택 프리랜서 프로필 (member_id: 23 - 임풀스택)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (23, 'https://sevmerge.com',
        '기획부터 프론트, 백엔드 인프라 배포까지 원스톱으로 해결해 드리는 8년 차 풀스택 개발자 임풀스택입니다. 빠르고 완성도 높은 MVP 제작을 보장합니다.',
        '- 글로벌 IT 기업 풀스택 개발자 (4년)\n- 웹/앱 통합 외주 프로젝트 30건 이상 수행',
        'Flutter, Node.js, React, Express, AWS, PostgreSQL', true, 'MASTER');

-- 8번 샘플: 승인 대기 중인 신규 전문가 프로필 (member_id: 24 - 포폴대기)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (24, 'https://sevmerge.com',
        '신규 등록한 모바일 앱 개발 전문가입니다. 현재 포트폴리오 자격 심사 진행 중이며, 승인 완료 후 완성도 높은 결과물로 보답하겠습니다.',
        '- 컴퓨터공학과 졸업\n- 외주 연동형 개인 프로젝트 3건 진행 및 마켓 출시 경험',
        'Android Studio, Kotlin, Firebase, Java', false, 'NORMAL');

-- 9번 샘플: 서류 검증 대기 중인 전문가 프로필 (member_id: 25 - 서류대기)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (25, 'https://sevmerge.com',
        '서류 검증 프로세스 진행 중인 데이터 엔지니어입니다. 검증 완료 후 기업 맞춤형 데이터 파이프라인 및 가공 서비스를 안전하게 제공하겠습니다.',
        '- 데이터 분석 에이전시 주석 연구원 (2년)\n- 공공 데이터 정제 프로젝트 참여',
        'Python, SQL, Tableau, Pandas, Data Pipeline', false, 'NORMAL');

-- 10번 샘플: 잠적 정지 계정 프로필 (member_id: 26 - 잠적전문가)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (26, 'https://sevmerge.com',
        '이용 규칙 위반 및 연락 두절로 인해 계정이 임시 제한된 프로필입니다. 기존 작업 히스토리 및 포트폴리오 조회가 일시적으로 제한됩니다.',
        '- 전직 웹 퍼블리셔 활동 (2년)',
        'HTML5, CSS3, JavaScript, JQuery', false, 'NORMAL');

-- 11번 샘플: 탈퇴 처리된 전문가 프로필 (member_id: 27 - 구자유)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (27, 'https://sevmerge.com',
        '회원 요청에 의해 삭제(탈퇴) 처리가 완료된 전문가 프로필 레코드입니다. 개인 정보 및 소개 내역은 비식별 처리되었습니다.',
        '- 소프트웨어 개발 프리랜서 활동 종료',
        'No Speciality', false, 'NORMAL');

-- 12번 샘플: 분쟁 조율 중인 전문가 프로필 (member_id: 28 - 불만족)
INSERT INTO expert_profile_tb (member_id, profile_image, intro, career, speciality, is_certified, expert_grade)
VALUES (28, 'https://sevmerge.com',
        '영상 및 미디어 콘텐츠 제작 전문가 불만족입니다. 현재 클라이언트와의 퀄리티 기준 분쟁으로 인해 일부 매칭 기능이 제한 중입니다.',
        '- 유튜브 채널 영상 편집 총괄 (2년)\n- 중소기업 바이럴 마케팅 영상 20여 편 제작',
        'Adobe Premiere Pro, After Effects, Video Editing', true, 'NORMAL');


-- Payment 샘플데이터
-- 1번 샘플: 웹 구축 계약 완료 및 신용카드 결제 (총 5,000,000원 / 수수료 500,000원 / 전문가 4,500,000원)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                        paid_at)
VALUES (1, 1, 2, 5000000, 500000, 4500000, 'imp_482019482103', 'card', 'PAID', NOW());

-- 2번 샘플: AI 모델 개발 계약 완료 및 카카오페이 결제 (총 12,000,000원 / 수수료 1,200,000원 / 전문가 10,800,000원)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                        paid_at)
VALUES (2, 4, 2, 12000000, 1200000, 10800000, 'imp_910482019482', 'kakaopay', 'PAID', NOW());

-- 3번 샘플: 가상계좌 무통장 입금 대기 중인 상태 연출 (READY 상태)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status,
                        paid_at)
VALUES (3, 1, 3, 1500000, 150000, 1350000, 'imp_vbank_009182', 'vbank', 'PAID', NOW());

-- 4번 샘플: 4번 프로젝트 (배달 라이더 앱) 결제 완료 (카드)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (4, 5, 23, 16000000, 1600000, 14400000, 'imp_card_10000004', 'card', 'PAID', NOW());

-- 5번 샘플: 5번 프로젝트 (취미 소모임 웹) 결제 완료 (카카오페이)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (5, 6, 20, 8500000, 850000, 7650000, 'imp_kakao_10000005', 'kakaopay', 'PAID', NOW());

-- 6번 샘플 수정본: READY 대신 PAID로 상태 통일
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (6, 9, 21, 2000000, 200000, 1800000, 'imp_vbank_10000006', 'vbank', 'PAID', NOW());

-- 7번 샘플: 7번 프로젝트 (공정 대시보드) 토스페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (7, 10, 19, 20000000, 2000000, 18000000, 'imp_toss_10000007', 'tosspay', 'PAID', NOW());

-- 8번 샘플: 9번 프로젝트 (무인 카페 키오스크) 결제 완료 (카드)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (9, 12, 23, 10000000, 1000000, 9000000, 'imp_card_10000008', 'card', 'PAID', NOW());

-- 9번 샘플 수정본 (미리 예방)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (10, 15, 20, 6000000, 600000, 5400000, 'imp_vbank_10000009', 'vbank', 'PAID', NOW());

-- 10번 샘플: 11번 프로젝트 (도서 관리 시스템) 카카오페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (11, 17, 2, 4000000, 400000, 3600000, 'imp_kakao_10000010', 'kakaopay', 'PAID', NOW());

-- 11번 샘플: 13번 프로젝트 (여행 동선 모바일 웹) 토스페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (13, 21, 19, 13000000, 1300000, 11700000, 'imp_toss_10000011', 'tosspay', 'PAID', NOW());

-- 12번 샘플: 14번 프로젝트 (글로벌 이커머스) 결제 완료 및 에스크로 보관 (카드)
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (14, 1, 19, 22000000, 2200000, 19800000, 'imp_card_10000012', 'card', 'PAID', NOW());

-- 13번 샘플: 15번 프로젝트 (소셜 데이팅 앱) 카카오페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (15, 5, 23, 32000000, 3200000, 28800000, 'imp_kakao_10000013', 'kakaopay', 'PAID', NOW());

-- 14번 샘플: 16번 프로젝트 (핀테크 대시보드 리뉴얼) 가상계좌 입금 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (16, 6, 21, 6500000, 650000, 5850000, 'imp_vbank_10000014', 'vbank', 'PAID', NOW());

-- 15번 샘플: 17번 프로젝트 (데이터 분석 및 시각화) 토스페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (17, 9, 25, 4500000, 450000, 4050000, 'imp_toss_10000015', 'tosspay', 'PAID', NOW());


-- 17번 샘플: 21번 프로젝트 (임직원 교육 영상) 카드 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (21, 15, 22, 3500000, 350000, 3150000, 'imp_card_10000017', 'card', 'PAID', NOW());

-- 18번 샘플: 22번 프로젝트 (키오스크 GUI 디자인) 카카오페이 결제 완료
INSERT INTO payment_tb (project_id, client_id, expert_id, amount, platform_fee, net_amount, payment_key, method, status, paid_at)
VALUES (22, 17, 21, 4000000, 400000, 3600000, 'imp_kakao_10000018', 'kakaopay', 'PAID', NOW());



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

-- 5번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '증권사 대용량 거래 데이터 처리 시스템 구축',
        'Java/Spring Boot 기반 마이크로서비스 아키텍처(MSA) 도입. Redis 캐싱 레이어 설계를 통해 데이터 조회 속도 40% 개선 및 부하 분산 처리.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 6번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '기업 맞춤형 자산 정보 연동 오픈 API 구축',
        '금융결제원 및 공공 데이터 API 연동 가이드 수립. Spring Security 및 OAuth2 기반의 철저한 보안 인증 전산 인프라 설계.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 7번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '실시간 모니터링 스마트 팩토리 웹 대시보드',
        'Next.js 및 TypeScript 기반 웹 어플리케이션 구현. 웹소켓(WebSocket) 연동을 통한 실시간 데이터 차트 렌더링 및 UI 퍼포먼스 최적화.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 8번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '글로벌 역직구 뷰티 이커머스 반응형 웹 구축',
        'Tailwind CSS와 가상 스크롤 기법을 활용하여 모바일/데스크톱 디바이스 환경 최적화. 초기 로딩 속도(LCP) 1.5초 이내 단축 달성.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 9번 샘플: expert_id를 김디자(3)로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, 'MZ세대 타겟 패션 플랫폼 디자인 시스템 구축',
        'Figma 변수(Variables) 기능을 활용한 다크모드 대응 UI 가이드라인 수립. 개발자와의 협업 효율을 위한 아토믹 디자인 컴포넌트 설계.',
        'https://sevmerge.com',
        'https://figma.com', NOW());

-- 10번 샘플: expert_id를 김디자(3)로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, '종합병원 무인 수납 및 처방전 발급 키오스크 GUI 디자인',
        '24인치 세로형 디스플레이 터치 화면 설계. 고령층 사용자를 배려한 가독성 중심의 폰트 스케일 및 직관적인 뎁스 동선 구현.',
        'https://sevmerge.com',
        'https://figma.com', NOW());

-- 11번 샘플: expert_id를 김디자(3)로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, '사스(SaaS) 협업 툴 런칭 퍼포먼스 마케팅 믹스',
        '구글 및 메타 타겟팅 광고 집행을 통해 한 달 만에 가입자 5,000명 확보. GA4 이벤트 추적 설계를 통한 결제 전환율(CVR) 2.5% 개선.',
        'https://sevmerge.com',
        'https://sevmerge.com', NOW());

-- 12번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, 'MBTI 기반 소셜 데이팅 하이브리드 앱 개발',
        'Flutter를 이용해 iOS와 Android 앱 동시 빌드. 백엔드는 Node.js와 Express 기반으로 구축하고 Firebase를 연동하여 실시간 푸시 인프라 구축.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 13번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '무인 렌탈 스튜디오 실시간 예약 및 도어락 원격 제어 시스템',
        'React와 Spring Boot 기반 통합 플랫폼 구축. 도어락 Open API 연동 및 결제 완료 시 알림톡 패스워드 자동 발급 모듈 전담 개발.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 14번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '위치 기반 등산 코스 안내 및 GPS 트래킹 앱',
        'Android Studio와 Kotlin 기반 네이티브 앱 개발. 구글 맵 API를 연동하여 고도 정보 및 이동 경로 실시간 트래킹 알고리즘 구현.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 15번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '이커머스 고객 행동 로그 분석 및 코호트 시각화',
        'Python Pandas 및 SQL을 활용한 유저 리텐션 데이터 정제. Tableau 대시보드를 구축하여 마케팅 부서 전용 이탈 구간 모니터링 시스템 납품.',
        'https://sevmerge.com',
        'https://tableau.com', NOW());

-- 16번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '실시간 백그라운드 GPS 연동 배달 라이더 앱',
        'Flutter 크로스플랫폼 기반 라이더 전용 모바일 앱 개발. 도심 음영 지역에서의 GPS 위치 보정 로직 구현 및 백그라운드 트래킹 최적화.',
        'https://sevmerge.com',
        'https://github.com', NOW());

-- 17번 샘플: expert_id를 김디자(3)로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, 'IT 기술 기업 신년 VIP 고객용 웰컴 키트 패키지 디자인',
        '다이어리, 캘린더, 패키지 상자 통합 브랜딩 가이드 수립. 인쇄용 CMYK 데이터 작업 및 폰트/로고 레이아웃 감리 공정 프로세스 리드.',
        'https://sevmerge.com',
        'https://behance.net', NOW());

-- 18번 샘플: expert_id를 홍길동(2)으로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (2, '캠핑 가젯 하드웨어 제품 펀딩용 3D 모션 그래픽 홍보 영상',
        'Cinema 4D 및 After Effects를 활용한 하드웨어 내부 구조 렌더링 편집. 제품 소구점을 살린 60초 분량의 바이럴 비디오 총괄 제작.',
        'https://sevmerge.com',
        'https://vimeo.com', NOW());

-- 19번 샘플: expert_id를 김디자(3)로 매칭
INSERT INTO portfolio_tb (expert_id, title, description, image_url, project_url, created_at)
VALUES (3, '지식창업 브랜드 런칭 유튜브 콘텐츠 마케팅',
        '유튜브 쇼츠 콘텐츠 30편 기획 및 타겟 분석 광고 집행. 평균 조회수 10만 회 이상 달성을 통한 카카오톡 채널 유입 유저 300% 증대.',
        'https://sevmerge.com',
        'https://sevmerge.com', NOW());



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

-- 4번 샘플: 홍길동(2) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 5)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (5, 2, 5, '어려운 요구사항이 많았는데도 싫은 내색 없이 전부 반영해 주셨습니다. 최고의 개발자입니다.', NOW());

-- 5번 샘플: 김디자(3) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 6)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (6, 3, 5, '디자인 감각이 정말 뛰어나십니다. 저희 브랜드 아이덴티티를 완벽하게 화면으로 녹여내 주셨어요.', NOW());

-- 6번 샘플: 홍길동(2) 전문가에 대한 4점 리뷰 (reviewer_id: 9)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (9, 2, 4, '코드 퀄리티가 아주 높고 문서화도 깔끔합니다. 피드백이 살짝 늦을 때가 있었지만 아주 만족합니다.', NOW());

-- 7번 샘플: 김디자(3) 전문가에 대한 4점 리뷰 (reviewer_id: 10)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (10, 3, 4, '수정 요청이 많았는데 끝까지 책임감 있게 응대해 주셨습니다. 컴포넌트 정리도 잘 되어 있네요.', NOW());

-- 8번 샘플: 홍길동(2) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 15)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (15, 2, 5, '복잡한 API 연동 작업을 일정보다 사흘이나 빠르게 끝내주셨습니다. 실력이 대단하신 분입니다.', NOW());

-- 9번 샘플: 김디자(3) 전문가에 대한 3점 리뷰 (현실적인 피드백 예시 / reviewer_id: 1)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 3, 3, '결과물 디자인 자체는 예쁘게 잘 나왔으나, 마감 직전에 일정이 조금 밀려서 아쉬웠습니다.', NOW());

-- 10번 샘플: 홍길동(2) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 6)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (6, 2, 5, '인프라 구축부터 배포까지 완벽하게 가이드해 주셔서 비개발자 입장에서 정말 든든했습니다.', NOW());

-- 11번 샘플: 김디자(3) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 9)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (9, 3, 5, '피그마 반응형 레이아웃 설계가 예술입니다. 퍼블리싱 팀에서 가이드가 너무 잘 되어 있다고 칭찬하네요.', NOW());

-- 12번 샘플: 홍길동(2) 전문가에 대한 4점 리뷰 (reviewer_id: 10)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (10, 2, 4, '구현하기 까다로운 실시간 대시보드 기능을 안정적으로 완성해 주셨습니다. 고생 많으셨습니다.', NOW());

-- 13번 샘플: 김디자(3) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 5)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (5, 3, 5, '추상적으로 말씀드린 아이디어였는데 기획 단계부터 찰떡같이 알아들으시고 고도화해 주셨습니다.', NOW());

-- 14번 샘플: 홍길동(2) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 1)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 2, 5, '예외 처리나 보안 관련 이슈까지 먼저 제안해 주시고 막아주셔서 감동했습니다. 다음에도 무조건 이분과 합니다.', NOW());

-- 15번 샘플: 김디자(3) 전문가에 대한 4점 리뷰 (reviewer_id: 15)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (15, 3, 4, '트렌디한 모바일 앱 UI를 완성해 주셨습니다. 사용자 입장에서 동선이 아주 직관적이라 대만족입니다.', NOW());

-- 16번 샘플: 홍길동(2) 전문가에 대한 3점 리뷰 (현실적인 피드백 예시 / reviewer_id: 5)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (5, 2, 3, '개발 실력은 확실히 좋으신데, 작업 진행 상황에 대한 중간 공유가 조금 부족해서 불안했습니다.', NOW());

-- 17번 샘플: 김디자(3) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 1)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (1, 3, 5, '로고 BI 디자인부터 웹 랜딩페이지까지 통일감 있게 아주 잘 뽑아주셨습니다. 번창하세요!', NOW());

-- 18번 샘플: 홍길동(2) 전문가에 대한 5점 만점 리뷰 (reviewer_id: 9)
INSERT INTO review_tb (reviewer_id, targeter_id, count_star, content, created_at)
VALUES (9, 2, 5, '주기적으로 진행 상황 브리핑해 주시고 꼼꼼하게 검수해 주셔서 외주 맡기는 동안 발 뻗고 잤습니다.', NOW());

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

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'NEW_BID', '반려동물 용품 구독형 커머스 웹 구축 프로젝트에 제출한 제안서가 아쉽게도 선정되지 않았습니다.', '/bids/my-list', true, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '박대표님이 골프 플랫폼 로고 원본 파일 교체 요청 쪽지를 보냈습니다.', '/messages', false, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '전문가 심사가 반려되었습니다. 포트폴리오 URL 접근 권한을 확인한 후 다시 신청해 주세요.', '/experts/register', false, false, NOW());


INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '제출하신 증빙 서류 검증이 진행 중입니다. 신청자 폭주로 인해 최대 3영업일이 소요될 수 있습니다.', '/experts/dashboard', true, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '신고 5회 누적 및 연락 두절로 인해 회원님의 계정이 90일간 정지(SUSPENDED) 처리되었습니다.', '/customer/notice', false, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '클라이언트로부터 퀄리티 기준 미달 및 수정 거부로 인한 분쟁 중재가 신청되었습니다.', '/disputes/detail', false, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'NEW_BID', '이박사 클라이언트가 회원님에게 평점 5점과 함께 따뜻한 후기를 남겼습니다.', '/experts/reviews', false, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'BID_SELECTED', '축하합니다! 키오스크 GUI 디자인 프로젝트에 제출한 제안서가 낙찰되었습니다.', '/bids/my-list', false, false, NOW());

INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'BID_SELECTED', '키오스크 GUI 디자인 프로젝트의 대금 결제가 완료되어 안전하게 작업을 진행하실 수 있습니다.', '/bids/my-orders', false, false, NOW());

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

-- ── 1. FAQ 추가 샘플 데이터 (3개) ──
INSERT INTO faq_tb (question, answer)
VALUES (' SevMerge 서비스 이용 수수료는 어떻게 되나요?', 'SevMerge 플랫폼은 매칭 및 안심 대금 에스크로 시스템 이용료로 전문가 정산 시 최종 계약 금액의 10%를 플랫폼 수수료로 부과하고 있습니다. 의뢰인 등록 수수료는 무료입니다.');

INSERT INTO faq_tb (question, answer)
VALUES (' 프로젝트 진행 중 분쟁이 발생하면 어떻게 하나요?', '작업물 퀄리티나 일정 미달로 상호 합의가 안 될 경우, 1:1 문의를 통해 [분쟁 중재]를 신청하실 수 있습니다. 운영팀이 계약서와 산출물을 검토하여 대금 환불 및 정산 비율을 조율해 드립니다.');

INSERT INTO faq_tb (question, answer)
VALUES (' 소셜 가입 회원도 비밀번호를 변경할 수 있나요?', '카카오/네이버 등 소셜 로그인 회원은 해당 플랫폼의 인증을 사용하므로 SevMerge 내에서 별도 비밀번호 변경이 불가능합니다. 일반 계정 전환을 원하실 경우 고객센터로 문의 바랍니다.');


-- ── 2. 스케줄러 테스트용 알림 추가 샘플 데이터 (12개) ──

-- [삭제 대상 5] 40일 지난 알림 (30일 경과로 삭제되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'NEW_BID', '[스케줄러테스트] 40일 지난 오래된 입찰 알림 - 삭제돼야 함', '/projects/1', true, false, DATE_ADD(NOW(), INTERVAL -40 DAY));

-- [삭제 대상 6] 60일 지난 알림 (30일 경과로 삭제되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '[스케줄러테스트] 60일 지난 오래된 메시지 알림 - 삭제돼야 함', '/messages', true, false, DATE_ADD(NOW(), INTERVAL -60 DAY));

-- [삭제 대상 7] 최근이지만 이미 소프트 삭제 상태 (is_deleted=true이므로 정리되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'BID_SELECTED', '[스케줄러테스트] 2일 전 알림이나 유저가 삭제함 - 삭제돼야 함', '/bids/my-orders', true, true, DATE_ADD(NOW(), INTERVAL -2 DAY));

-- [삭제 대상 8] 31일 지난 알림 (딱 경계 넘김 - 삭제되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'NEW_BID', '[스케줄러테스트] 딱 31일 지난 경계선 알림 - 삭제돼야 함', '/projects/2', true, false, DATE_ADD(NOW(), INTERVAL -31 DAY));

-- [삭제 대상 9] 365일 지난 아주 오래된 알림 (삭제되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'MESSAGE_RECEIVED', '[스케줄러테스트] 1년 지난 정산 누락 알림 - 삭제돼야 함', '/messages', true, false, DATE_ADD(NOW(), INTERVAL -365 DAY));

-- [삭제 대상 10] 오래되었고 소프트 삭제도 된 알림 (중복 조건 - 삭제되어야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'BID_SELECTED', '[스케줄러테스트] 50일 지나고 유저가 삭제도 한 알림 - 삭제돼야 함', '/bids/my-orders', true, true, DATE_ADD(NOW(), INTERVAL -50 DAY));


-- [생존 대상 3] 28일 지난 알림 (30일 미만이므로 정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'NEW_BID', '[스케줄러테스트] 28일 지난 아슬아슬한 알림 - 남아야 함', '/projects/1', true, false, DATE_ADD(NOW(), INTERVAL -28 DAY));

-- [생존 대상 4] 15일 지난 알림 (30일 미만이므로 정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '[스케줄러테스트] 보름 전 수신된 미확인 쪽지 알림 - 남아야 함', '/messages', false, false, DATE_ADD(NOW(), INTERVAL -15 DAY));

-- [생존 대상 5] 5일 지난 알림 (30일 미만이므로 정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'BID_SELECTED', '[스케줄러테스트] 5일 전 프로젝트 최종 낙찰 안내 알림 - 남아야 함', '/bids/my-orders', false, false, DATE_ADD(NOW(), INTERVAL -5 DAY));

-- [생존 대상 6] 방금 생성된 최신 알림 (정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'NEW_BID', '[스케줄러테스트] 방금 등록된 신규 전문가 입찰 제안 알림 - 남아야 함', '/projects/1', false, false, NOW());

-- [생존 대상 7] 12시간 전 알림 (정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (2, 'MESSAGE_RECEIVED', '[스케줄러테스트] 오늘 오전 수신된 고객센터 답변 안내 - 남아야 함', '/messages', false, false, NOW());

-- [생존 대상 8] 딱 경계에 있는 29일 전 알림 (정상 생존해야 함)
INSERT INTO notification_tb (receiver_id, type, content, url, is_read, is_deleted, created_at)
VALUES (1, 'BID_SELECTED', '[스케줄러테스트] 딱 29일 지난 마지노선 생존 알림 - 남아야 함', '/bids/my-orders', true, false, DATE_ADD(NOW(), INTERVAL -29 DAY));

-- -- ════════════════════════════════════════════════════════════════
-- -- 관리자 대시보드 차트용 더미 (최근 7일 분산)
-- --  - 회원 추세   : member_tb.created_at
-- --  - 프로젝트 추세 : project_tb.created_at (전체)
-- --  - 완료 추세   : project_tb.created_at + project_status='DONE'
-- -- ════════════════════════════════════════════════════════════════
--

-- -- 프로젝트 더미 (전체 추세용, member_id=1) / 일부 DONE은 완료 추세에도 잡힘 --
-- INSERT INTO project_tb (
--     member_id, title, category, description, budget_min, budget_max,
--     deadline, bid_filter, project_status, view_count, is_deleted, is_private, created_at)
-- VALUES
--
-- -- 6일 전 데이터 (WEB, APP, UI_UX 배치)
-- (1, '[차트] 기업 홈페이지 구축 건', 'WEB', '차트용 더미', 1000000, 3000000, NOW(), 'ALL', 'OPEN', 12, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
-- (1, '[차트] 배달 서비스 앱 기획', 'APP', '차트용 더미', 2000000, 5000000, NOW(), 'ALL', 'CLOSED', 5, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
-- (1, '[차트] 랜딩페이지 UI/UX 디자인', 'UI_UX', '차트용 더미', 500000, 1000000, NOW(), 'ALL', 'DRAFT', 0, false, false, DATE_SUB(NOW(), INTERVAL 6 DAY)),
--
-- -- 5일 전 데이터 (DATA, VIDEO, ETC 배치)
-- (1, '[차트] AI 학습용 데이터 정제', 'DATA', '차트용 더미', 1500000, 4000000, NOW(), 'ALL', 'IN_PROGRESS', 25, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- (1, '[차트] 홍보 영상 편집 외주', 'VIDEO', '차트용 더미', 3000000, 6000000, NOW(), 'ALL', 'OPEN', 18, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- (1, '[차트] 사내 문서 하드웨어 정리', 'ETC', '차트용 더미', 1000000, 2000000, NOW(), 'ALL', 'CANCELLED', 2, false, false, DATE_SUB(NOW(), INTERVAL 5 DAY)),
--
-- -- 4일 전 데이터 (WEB, APP, DATA 상태 다양화)
-- (1, '[차트] 반응형 쇼핑몰 퍼블리싱', 'WEB', '차트용 더미', 800000, 1500000, NOW(), 'ALL', 'COMPLETED', 14, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
-- (1, '[차트] 커스텀 매칭 앱 개발', 'APP', '차트용 더미', 5000000, 9000000, NOW(), 'ALL', 'DONE', 30, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
-- (1, '[차트] 매장 통계 데이터 분석', 'DATA', '차트용 더미', 1200000, 2500000, NOW(), 'ALL', 'IN_PROGRESS', 9, false, false, DATE_SUB(NOW(), INTERVAL 4 DAY)),
--
-- -- 3일 전 데이터 (UI_UX, VIDEO, ETC 배치)
-- (1, '[차트] 모바일 어플 UI 개편', 'UI_UX', '차트용 더미', 300000, 700000, NOW(), 'ALL', 'OPEN', 4, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- (1, '[차트] 유튜브 인트로 영상 제작', 'VIDEO', '차트용 더미', 4000000, 8000000, NOW(), 'ALL', 'IN_PROGRESS', 42, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- (1, '[차트] 번역 및 타이핑 대행 업무', 'ETC', '차트용 더미', 1000000, 1800000, NOW(), 'ALL', 'DONE', 15, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
-- (1, '[차트] 기업 리포트 검수 요청', 'ETC', '차트용 더미', 2000000, 4000000, NOW(), 'ALL', 'CLOSED', 8, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY)),
--
-- -- 2일 전 데이터 (WEB, APP, DATA 재배치)
-- (1, '[차트] 회사 소개 웹사이트', 'WEB', '차트용 더미', 500000, 1200000, NOW(), 'ALL', 'OPEN', 11, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- (1, '[차트] 하이브리드 앱 패키징', 'APP', '차트용 더미', 3500000, 7000000, NOW(), 'ALL', 'COMPLETED', 22, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
-- (1, '[차트] 크롤링 데이터 수집 및 정렬', 'DATA', '차트용 더미', 1500000, 3000000, NOW(), 'ALL', 'CANCELLED', 3, false, false, DATE_SUB(NOW(), INTERVAL 2 DAY)),
--
-- -- 1일 전 데이터 (UI_UX, VIDEO, WEB, APP 고루 배치)
-- (1, '[차트] 대시보드 화면 디자인', 'UI_UX', '차트용 더미', 2500000, 5000000, NOW(), 'ALL', 'OPEN', 19, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- (1, '[차트] 행사 현장 스케치 영상', 'VIDEO', '차트용 더미', 3000000, 6000000, NOW(), 'ALL', 'IN_PROGRESS', 16, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- (1, '[차트] 블로그 커스텀 테마 제작', 'WEB', '차트용 더미', 1200000, 2000000, NOW(), 'ALL', 'DONE', 28, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- (1, '[차트] 태블릿 전용 앱 프로토타입', 'APP', '차트용 더미', 1000000, 2000000, NOW(), 'ALL', 'DRAFT', 0, false, false, DATE_SUB(NOW(), INTERVAL 1 DAY)),
--
-- -- 오늘 데이터 (WEB, APP, UI_UX, DATA, VIDEO, ETC 총출동)
-- (1, '[차트] 실시간 채팅 웹 애플리케이션', 'WEB', '차트용 더미', 2000000, 4500000, NOW(), 'ALL', 'OPEN', 3, false, false, NOW()),
-- (1, '[차트] 운동 기록 피트니스 앱', 'APP', '차트용 더미', 4500000, 8500000, NOW(), 'ALL', 'IN_PROGRESS', 5, false, false, NOW()),
-- (1, '[차트] 반응형 어드민 UI 디자인', 'UI_UX', '차트용 더미', 300000, 600000, NOW(), 'ALL', 'DONE', 14, false, false, NOW()),
-- (1, '[차트] 텍스트 마이닝 데이터 정제 건', 'DATA', '차트용 더미', 1000000, 2000000, NOW(), 'ALL', 'OPEN', 2, false, false, NOW()),
-- (1, '[차트] 제품 홍보 3D 모션그래픽', 'VIDEO', '차트용 더미', 5000000, 8000000, NOW(), 'ALL', 'IN_PROGRESS', 1, false, false, NOW()),
-- (1, '[차트] 단순 사무 보조 지원 프로젝트', 'ETC', '차트용 더미', 200000, 500000, NOW(), 'ALL', 'OPEN', 7, false, false, NOW());


-- 북마크 데이터
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 1);
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (1, 1);
-- 의뢰인 김의뢰(1)가 북마크한 전문가들 (2, 3번으로 고정)
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 1);  -- 김디자 전문가 북마크

-- 의뢰인 이박사(5)가 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 5);  -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 5);  -- 김디자 전문가 북마크

-- 의뢰인 박대표(6)가 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 6);  -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 6);  -- 김디자 전문가 북마크

-- 의뢰인 최기업(9)이 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 9);  -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 9);  -- 김디자 전문가 북마크

-- 의뢰인 이쇼핑(10)이 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 10); -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 10); -- 김디자 전문가... 북마크

-- 의뢰인 정담당(11)이 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 11); -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 11); -- 김디자 전문가 북마크

-- 의뢰인 나개인(12)이 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 12); -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 12); -- 김디자 전문가 북마크

-- 의뢰인 강창업(15)이 북마크한 전문가들
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (2, 15); -- 홍길동 전문가 북마크
INSERT INTO book_mark_tb (expert_id, member_id) VALUES (3, 15); -- 김디자 전문가 북마크



-- 10. AD_SLOT_TB 초기 데이터 (고정 슬롯 - 메인 배너)
-- ======================================================================================
INSERT INTO ad_slot_tb (slot_name, slot_type, min_bid_price, bid_start_at, bid_end_at, status, created_at)
VALUES ('메인 배너', 'MAIN_BANNER', 10000, NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE), 'OPEN', NOW());