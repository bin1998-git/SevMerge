/**
 * 07-remaining.spec.ts
 * C024부터 이어서 - 미완료 CLIENT + 전체 EXPERT
 */
import { test, Page } from '@playwright/test';
import * as path from 'path';
import * as fs from 'fs';

const OUT_DIR = '/Users/park/Desktop/SevMerge shot';
fs.mkdirSync(OUT_DIR, { recursive: true });

const ACCOUNTS = {
  client:  { email: 'client01@sevmerge.com', password: '1234' },
  expert1: { email: 'expert01@sevmerge.com', password: '1234' },
  expert2: { email: 'expert02@sevmerge.com', password: '1234' },
};

let idx = 23; // start from C024

async function shot(page: Page, name: string) {
  idx++;
  const file = path.join(OUT_DIR, `C${String(idx).padStart(3,'0')}_${name}.png`);
  await page.waitForTimeout(600);
  await page.screenshot({ path: file, fullPage: false });
  console.log(`✅ ${file}`);
}

async function login(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.waitForLoadState('domcontentloaded');
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForTimeout(2000);
}

// ══════════════════════════════════════════
// CLIENT 05 나머지 - 일대일문의 작성
// ══════════════════════════════════════════
test('CLIENT 05b 일대일문의+FAQ', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 일대일문의 작성폼
  await page.goto('/boards/save-form?boardType=INQUIRY');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '일대일문의_작성폼');
  await page.locator('input[name="title"]').first().fill('낙찰 후 전문가 연락 없는 경우 처리 방법').catch(() => {});
  await page.locator('textarea[name="content"]').first()
    .fill('낙찰 처리 후 2일이 지났는데 전문가로부터 연락이 없습니다. 에스크로 결제된 상태인데 어떻게 처리해야 하나요?').catch(() => {});
  await shot(page, '일대일문의_작성중');
  await page.locator('button[type="submit"]').first().click().catch(() => {});
  await page.waitForTimeout(2000);
  await shot(page, '일대일문의_제출완료');

  // FAQ
  await page.goto('/faq');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, 'FAQ_목록');
  const faqItem = page.locator('.faq-item, .accordion-item, dt, [data-toggle], .faq-question').first();
  if (await faqItem.isVisible().catch(() => false)) {
    await faqItem.click();
    await page.waitForTimeout(800);
    await shot(page, 'FAQ_아코디언_펼침');
  }
});

// ══════════════════════════════════════════
// CLIENT 06 - 결제 충전
// ══════════════════════════════════════════
test('CLIENT 06 결제충전+에스크로', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 충전 폼
  await page.goto('/charge/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_충전폼_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '결제_충전폼_하단');

  // 금액 버튼
  const chargeBtn = page.locator('.charge-amount-btn, button:has-text("50,000"), button:has-text("10만"), button:has-text("100,000"), input[value="100000"]').first();
  if (await chargeBtn.isVisible().catch(() => false)) {
    await chargeBtn.click();
    await page.waitForTimeout(600);
    await shot(page, '결제_충전금액_선택');
  }

  // 에스크로 내역
  await page.goto('/payment/my');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_에스크로_내역');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '결제_에스크로_내역_하단');

  // 충전 이력
  await page.goto('/charge/history');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_충전이력');
});

// ══════════════════════════════════════════
// CLIENT 07 - 의뢰인 마이페이지 + 알림 + 쪽지
// ══════════════════════════════════════════
test('CLIENT 07 마이페이지+알림+쪽지', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 마이페이지
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '의뢰인_마이페이지_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '의뢰인_마이페이지_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight * 2 / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '의뢰인_마이페이지_하단');

  // 알림
  await page.goto('/notifications');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '알림목록_의뢰인');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '알림목록_의뢰인_하단');

  // 쪽지함
  await page.goto('/messages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지함_의뢰인');
  const msgLink = page.locator('a[href*="/messages/"]').first();
  if (await msgLink.isVisible().catch(() => false)) {
    await msgLink.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '쪽지_상세_의뢰인');
    await page.goBack();
    await page.waitForTimeout(500);
  }

  // 쪽지 보내기
  await page.goto('/messages/send');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지_보내기폼_의뢰인');
  const rcv = page.locator('input[name="receiverEmail"], input[name="receiver"]').first();
  if (await rcv.isVisible().catch(() => false)) {
    await rcv.fill(ACCOUNTS.expert1.email);
    await page.locator('input[name="title"]').first().fill('추가 요구사항 공유').catch(() => {});
    await page.locator('textarea[name="content"]').first()
      .fill('안녕하세요. 프로젝트 진행 중 API 연동 요구사항이 추가되었습니다. 일정과 비용 협의 부탁드립니다.').catch(() => {});
    await shot(page, '쪽지_작성완료_의뢰인');
    await page.locator('form button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(1500);
    await shot(page, '쪽지_전송후_의뢰인');
  }
});

// ══════════════════════════════════════════
// EXPERT 01 - 대시보드 + 마이페이지
// ══════════════════════════════════════════
test('EXPERT 01 대시보드+마이페이지+프로필', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 전문가 대시보드
  await page.goto('/experts/dashboard');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_대시보드_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_대시보드_하단');

  // 전문가 마이페이지
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_마이페이지_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_마이페이지_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight * 2 / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_마이페이지_하단');

  // 내 공개 프로필
  await page.goto('/experts/1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_공개프로필_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_공개프로필_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_공개프로필_리뷰섹션');
});

// ══════════════════════════════════════════
// EXPERT 02 - 프로필 편집 + 포트폴리오
// ══════════════════════════════════════════
test('EXPERT 02 프로필편집+포트폴리오', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 프로필 편집
  await page.goto('/experts/edit');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로필편집_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_프로필편집_하단');

  // 소개 수정
  const introInput = page.locator('textarea[name="intro"], #intro, textarea[name="description"]').first();
  if (await introInput.isVisible().catch(() => false)) {
    const val = await introInput.inputValue().catch(() => '');
    await introInput.fill((val + ' (포트폴리오 15건 이상, 장기 프로젝트 선호)').substring(0, 500));
    await shot(page, '전문가_프로필편집_수정중');
  }

  // 포트폴리오 목록
  await page.goto('/experts/portfolio');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_포트폴리오_목록');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_포트폴리오_목록_하단');

  // 포트폴리오 추가 폼
  const addBtn = page.locator('a:has-text("포트폴리오 추가"), a:has-text("등록"), a[href*="portfolio/save"], a[href*="portfolio/form"]').first();
  if (await addBtn.isVisible().catch(() => false)) {
    await addBtn.click();
    await page.waitForLoadState('domcontentloaded');
  } else {
    await page.goto('/experts/portfolio/save-form');
    await page.waitForLoadState('domcontentloaded');
  }
  await shot(page, '전문가_포트폴리오_등록폼');
  await page.locator('input[name="title"]').first().fill('대형 쇼핑몰 MSA 전환 프로젝트 (일 20만 주문)').catch(() => {});
  await page.locator('textarea[name="description"]').first()
    .fill('Spring Boot 기반 모놀리식 아키텍처를 MSA로 전환. 주문/상품/결제 서비스 분리. AWS EKS + Kafka 도입으로 처리량 3배 향상.').catch(() => {});
  await shot(page, '전문가_포트폴리오_작성중');
});

// ══════════════════════════════════════════
// EXPERT 03 - 프로젝트 탐색 + 제안서
// ══════════════════════════════════════════
test('EXPERT 03 프로젝트탐색+제안서', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 프로젝트 목록 탐색
  await page.goto('/projects');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로젝트목록_탐색');

  // 필터
  const catFilter = page.locator('select[name="category"], #filterCategory, select').first();
  if (await catFilter.isVisible().catch(() => false)) {
    await catFilter.selectOption({ index: 1 }).catch(() => {});
    await page.waitForTimeout(800);
    await shot(page, '전문가_프로젝트_카테고리필터');
  }

  // 오픈 프로젝트 상세
  await page.goto('/projects/5');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로젝트상세_OPEN');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로젝트상세_하단');

  // 제안서 작성
  await page.goto('/bids/save-form?projectId=5');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_제안서_작성폼');
  await page.locator('textarea[name="coverLetter"]').first()
    .fill('안녕하세요! React Native + Spring Boot 조합으로 iOS/Android 쇼핑몰 앱 3건 출시 경험 보유. 예산과 일정 준수 보장합니다.').catch(() => {});
  await page.locator('input[name="estimatedDays"]').first().fill('45').catch(() => {});
  await page.locator('input[name="proposedPrice"]').first().fill('11000000').catch(() => {});
  await shot(page, '전문가_제안서_입력완료');

  // 제안서 목록
  await page.goto('/bids/my-list');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내제안서목록');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_내제안서목록_하단');

  // 낙찰 주문 목록
  await page.goto('/bids/my-orders');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_낙찰주문목록');
});

// ══════════════════════════════════════════
// EXPERT 04 - 채팅 + 알림 + 쪽지
// ══════════════════════════════════════════
test('EXPERT 04 채팅+알림+쪽지', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 채팅방 목록
  await page.goto('/chat/room');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_채팅방목록');

  // 첫 번째 채팅방 입장
  const roomLink = page.locator('a[href*="/chat/room/"], .chat-room-item a, .room-link').first();
  if (await roomLink.isVisible().catch(() => false)) {
    await roomLink.click();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(1500);
    await shot(page, '전문가_채팅방_내부');

    // 메시지 입력
    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput, input[placeholder*="메시지"]').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.fill('API 설계 완료했습니다. 내일부터 개발 시작 예정입니다!');
      await shot(page, '전문가_채팅_메시지입력');
      await msgInput.press('Enter');
      await page.waitForTimeout(1500);
      await shot(page, '전문가_채팅_메시지전송완료');
    }
  }

  // 알림
  await page.goto('/notifications');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_알림목록');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_알림목록_하단');

  // 쪽지함
  await page.goto('/messages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_쪽지함');
  const msgLink = page.locator('a[href*="/messages/"]').first();
  if (await msgLink.isVisible().catch(() => false)) {
    await msgLink.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_쪽지_상세');
    await page.goBack();
    await page.waitForTimeout(500);
  }

  // 쪽지 보내기
  await page.goto('/messages/send');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_쪽지_보내기폼');
  const rcv = page.locator('input[name="receiverEmail"], input[name="receiver"]').first();
  if (await rcv.isVisible().catch(() => false)) {
    await rcv.fill(ACCOUNTS.client.email);
    await page.locator('input[name="title"]').first().fill('1주차 작업 완료 보고').catch(() => {});
    await page.locator('textarea[name="content"]').first()
      .fill('안녕하세요. 1주차 요구사항 분석 및 API 설계가 완료되었습니다. 다음 주부터 개발 착수합니다!').catch(() => {});
    await shot(page, '전문가_쪽지_작성완료');
    await page.locator('form button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(1500);
    await shot(page, '전문가_쪽지_전송완료');
  }
});

// ══════════════════════════════════════════
// EXPERT 05 - 출금 + 광고
// ══════════════════════════════════════════
test('EXPERT 05 출금+광고', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 잔액 확인
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_잔액확인_마이페이지');

  // 출금 신청폼
  await page.goto('/withdrawal/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_출금신청폼');
  await page.locator('input[name="bankName"]').first().fill('카카오뱅크').catch(() => {});
  await page.locator('input[name="accountNumber"]').first().fill('3333-01-7654321').catch(() => {});
  await page.locator('input[name="accountHolder"]').first().fill('홍길동').catch(() => {});
  await page.locator('input[name="amount"]').first().fill('500000').catch(() => {});
  await shot(page, '전문가_출금신청_입력완료');

  // 출금 이력
  await page.goto('/withdrawal/history');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_출금이력');

  // 광고 경매
  await page.goto('/ad-auction');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_광고경매_메인');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_광고경매_슬롯목록');

  // 광고 입찰 버튼
  const bidBtn = page.locator('button:has-text("입찰"), .btn-ad-bid, button:has-text("광고 입찰")').first();
  if (await bidBtn.isVisible().catch(() => false)) {
    await bidBtn.click();
    await page.waitForTimeout(1000);
    await shot(page, '전문가_광고입찰폼');

    const amtInput = page.locator('input[name="amount"], input[name="bidAmount"], #bidAmount').first();
    if (await amtInput.isVisible().catch(() => false)) {
      await amtInput.fill('80000');
      const adMsg = page.locator('input[name="adMessage"], textarea[name="adMessage"]').first();
      await adMsg.fill('풀스택 개발 10년, 빠른 납기 보장!').catch(() => {});
      await shot(page, '전문가_광고입찰_입력완료');
      const submitBtn = page.locator('button[type="submit"], .btn-submit-bid').first();
      await submitBtn.click().catch(() => {});
      await page.waitForTimeout(1500);
      await shot(page, '전문가_광고입찰_완료');
    }
  }

  // 내 광고 현황
  await page.goto('/ad-auction/my-bids');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내광고입찰현황');
});

// ══════════════════════════════════════════
// EXPERT 06 - 게시판 + 리뷰
// ══════════════════════════════════════════
test('EXPERT 06 게시판+리뷰', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 자유게시판 글 작성
  await page.goto('/boards/save-form?boardType=FREE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_자유게시판_글작성폼');
  const boardTitle = page.locator('input[name="title"]').first();
  if (await boardTitle.isVisible().catch(() => false)) {
    await boardTitle.fill('Spring AI + Gemini 2.5 Flash 통합 외주 후기');
    await page.locator('textarea[name="content"]').first()
      .fill('SevMerge에서 Spring AI와 Gemini 2.5 Flash를 통합한 프로젝트를 완료했습니다. AI 응답속도와 품질이 예상보다 훨씬 좋았습니다.').catch(() => {});
    await shot(page, '전문가_자유게시판_작성중');
    await page.locator('button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(2000);
    await shot(page, '전문가_자유게시판_작성완료');
  }

  // 전문가 프로필 리뷰 섹션
  await page.goto('/experts/1');
  await page.waitForLoadState('domcontentloaded');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight * 0.7, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로필_리뷰섹션');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로필_최하단');

  // 리뷰 목록 (있는 경우)
  await page.goto('/experts/reviews');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내리뷰목록');

  // 공지사항
  await page.goto('/boards?boardType=NOTICE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_공지사항목록');
  const noticeItem = page.locator('a[href*="/boards/"]').first();
  if (await noticeItem.isVisible().catch(() => false)) {
    await noticeItem.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_공지사항_상세');
  }

  // FAQ
  await page.goto('/faq');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_FAQ목록');
  const faqItem = page.locator('.faq-item, .accordion-item, dt, [data-toggle], .faq-question').first();
  if (await faqItem.isVisible().catch(() => false)) {
    await faqItem.click();
    await page.waitForTimeout(800);
    await shot(page, '전문가_FAQ_펼침');
  }
});
