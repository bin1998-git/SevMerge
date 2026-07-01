/**
 * 05-capture-all.spec.ts
 * 전체 기능 스크린샷 캡처 → /Users/park/Desktop/SevMerge shot/
 */
import { test, expect, chromium, Page, BrowserContext } from '@playwright/test';
import * as path from 'path';
import * as fs from 'fs';

const OUT_DIR = '/Users/park/Desktop/SevMerge shot';
fs.mkdirSync(OUT_DIR, { recursive: true });

const ACCOUNTS = {
  admin:   { email: 'admin@sevmerge.com',    password: '1234' },
  client:  { email: 'client01@sevmerge.com', password: '1234' },
  expert1: { email: 'expert01@sevmerge.com', password: '1234' },
  expert2: { email: 'expert02@sevmerge.com', password: '1234' },
};

let idx = 0;
async function shot(page: Page, name: string) {
  idx++;
  const num = String(idx).padStart(2, '0');
  const file = path.join(OUT_DIR, `${num}_${name}.png`);
  await page.waitForTimeout(800);
  await page.screenshot({ path: file, fullPage: false });
  console.log(`✅ ${file}`);
}

async function login(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.waitForLoadState('domcontentloaded');
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL(/\/(main|admin\/main|intro)/, { timeout: 10000 }).catch(() => {});
  await page.waitForTimeout(1000);
}

async function scrollAndShot(page: Page, name: string, scrollSteps = 2) {
  await shot(page, name + '_top');
  if (scrollSteps > 1) {
    const h = await page.evaluate(() => document.body.scrollHeight);
    await page.evaluate((y) => window.scrollTo({ top: y / 2, behavior: 'smooth' }), h);
    await page.waitForTimeout(700);
    await shot(page, name + '_mid');
    await page.evaluate(() => window.scrollTo({ top: 0, behavior: 'smooth' }));
  }
}

// ──────────────────────────────────────────
// 01  로그인
// ──────────────────────────────────────────
test('01 로그인 기능', async ({ page }) => {
  await page.goto('/login');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '로그인_화면');

  await page.fill('input[name="email"]', ACCOUNTS.client.email);
  await page.fill('input[name="password"]', ACCOUNTS.client.password);
  await shot(page, '로그인_입력완료');

  await page.click('button[type="submit"]');
  await page.waitForURL(/\/(main|intro)/, { timeout: 8000 }).catch(() => {});
  await page.waitForTimeout(1200);
  await shot(page, '로그인_완료_메인');

  // 인트로 → 메인
  const toMain = page.locator('a[href="/main"], a:has-text("시작하기"), a:has-text("바로가기")').first();
  if (await toMain.isVisible().catch(() => false)) {
    await toMain.click();
    await page.waitForTimeout(1200);
    await shot(page, '메인_홈');
  }

  await page.goto('/main');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '메인_페이지', 3);
});

// ──────────────────────────────────────────
// 02  ADMIN 페이지
// ──────────────────────────────────────────
test('02 ADMIN 페이지', async ({ page }) => {
  await login(page, ACCOUNTS.admin.email, ACCOUNTS.admin.password);

  const adminPages: [string, string][] = [
    ['/admin/main',         '관리자_대시보드'],
    ['/admin/members',      '관리자_회원목록'],
    ['/admin/experts',      '관리자_전문가승인'],
    ['/admin/projects',     '관리자_프로젝트'],
    ['/admin/escrow',       '관리자_에스크로'],
    ['/admin/revenue',      '관리자_수익'],
    ['/admin/reports',      '관리자_신고관리'],
    ['/admin/blacklist',    '관리자_블랙리스트'],
    ['/admin/partnerships', '관리자_제휴문의'],
    ['/admin/ad-slots',     '관리자_광고슬롯'],
    ['/admin/withdraw',     '관리자_출금'],
    ['/admin/notices',      '관리자_공지사항'],
    ['/admin/inquiry',      '관리자_1대1문의'],
  ];

  for (const [url, name] of adminPages) {
    await page.goto(url);
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(800);
    await shot(page, name);
  }
});

// ──────────────────────────────────────────
// 03  제안서 전체 플로우
//     프로젝트 등록 → 제안서 → 낙찰 → 작업완료 → 정산
// ──────────────────────────────────────────
test('03 제안서 플로우', async ({ browser }) => {
  // ── 의뢰인: 프로젝트 등록 ──
  const clientCtx = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const clientPage = await clientCtx.newPage();
  await login(clientPage, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await clientPage.goto('/projects/save-form');
  await clientPage.waitForLoadState('domcontentloaded');
  await shot(clientPage, '프로젝트_등록폼');

  // AI 초안 버튼 캡처 (AI 챗봇)
  const aiBtn = clientPage.locator('button:has-text("AI"), button[id*="ai"], .ai-btn, #aiDraftBtn').first();
  if (await aiBtn.isVisible().catch(() => false)) {
    await aiBtn.click();
    await clientPage.waitForTimeout(2000);
    await shot(clientPage, 'AI_챗봇_프로젝트초안');
    await clientPage.keyboard.press('Escape');
    await clientPage.waitForTimeout(500);
  }

  // 폼 입력
  await clientPage.locator('input[name="title"], #title').first().fill('모바일 쇼핑몰 앱 개발 의뢰 (React Native)').catch(() => {});
  const catSel = clientPage.locator('select[name="category"], #category').first();
  await catSel.selectOption({ index: 1 }).catch(() => {});
  await clientPage.locator('textarea[name="description"], #description').first()
    .fill('iOS/Android 크로스플랫폼 쇼핑몰 앱 개발\n- 상품 목록/상세, 장바구니, 결제 연동\n- 관리자 CMS 포함\n- 런칭 목표: 2026년 9월').catch(() => {});
  await clientPage.locator('input[name="budgetMin"], #budgetMin').first().fill('5000').catch(() => {});
  await clientPage.locator('input[name="budgetMax"], #budgetMax').first().fill('15000').catch(() => {});
  const deadline = new Date(); deadline.setDate(deadline.getDate() + 45);
  await clientPage.locator('input[name="deadline"], #deadline').first()
    .fill(deadline.toISOString().split('T')[0]).catch(() => {});
  await shot(clientPage, '프로젝트_폼_입력완료');

  await clientPage.locator('form button[type="submit"]:not(.btn-draft)').first().click().catch(() => {});
  await clientPage.waitForTimeout(2500);
  await shot(clientPage, '프로젝트_등록완료');

  // 프로젝트 ID 추출
  const projUrl = clientPage.url();
  const projIdMatch = projUrl.match(/\/projects\/(\d+)/);
  const projectId = projIdMatch ? projIdMatch[1] : '1';
  console.log('projectId:', projectId);

  // 프로젝트 목록 + 필터 검색
  await clientPage.goto('/projects');
  await clientPage.waitForLoadState('domcontentloaded');
  await shot(clientPage, '프로젝트_목록');

  // ── 동적 다중 필터 검색 ──
  const categoryFilter = clientPage.locator('select[name="category"], #filterCategory, .filter-category').first();
  if (await categoryFilter.isVisible().catch(() => false)) {
    await categoryFilter.selectOption({ index: 1 }).catch(() => {});
    await clientPage.waitForTimeout(600);
  }
  const statusFilter = clientPage.locator('select[name="status"], #filterStatus, .filter-status').first();
  if (await statusFilter.isVisible().catch(() => false)) {
    await statusFilter.selectOption({ index: 0 }).catch(() => {});
    await clientPage.waitForTimeout(600);
  }
  const keywordInput = clientPage.locator('input[name="keyword"], input[type="search"], #searchKeyword').first();
  if (await keywordInput.isVisible().catch(() => false)) {
    await keywordInput.fill('쇼핑몰');
    await keywordInput.press('Enter');
    await clientPage.waitForTimeout(1000);
  }
  await shot(clientPage, '동적_다중_필터_검색');

  // 프로젝트 상세
  await clientPage.goto(`/projects/${projectId}`);
  await clientPage.waitForLoadState('domcontentloaded');
  await scrollAndShot(clientPage, '프로젝트_상세', 2);

  await clientCtx.close();

  // ── 전문가: 제안서 제출 ──
  const expertCtx = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const expertPage = await expertCtx.newPage();
  await login(expertPage, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  await expertPage.goto(`/bids/save-form?projectId=${projectId}`);
  await expertPage.waitForLoadState('domcontentloaded');
  await shot(expertPage, '제안서_작성폼');

  await expertPage.locator('textarea[name="coverLetter"]').first()
    .fill('React Native 전문 개발자입니다. iOS/Android 동시 개발 5년 경력. 유사 쇼핑몰 앱 3건 런칭 성공. 일정 내 안정적 납품 자신합니다.').catch(() => {});
  await expertPage.locator('textarea[name="approach"], input[name="approach"]').first()
    .fill('1주차: 요구사항 분석 및 와이어프레임\n2-4주차: 핵심 기능 개발\n5-6주차: QA 및 배포').catch(() => {});
  await expertPage.locator('input[name="estimatedDays"]').first().fill('45').catch(() => {});
  await expertPage.locator('input[name="proposedPrice"]').first().fill('9000000').catch(() => {});
  await shot(expertPage, '제안서_입력완료');

  await expertPage.locator('form button[type="submit"]:not(.btn-logout)').first().click().catch(() => {});
  await expertPage.waitForTimeout(2000);
  await shot(expertPage, '제안서_제출완료');

  // 내 제안서 목록
  await expertPage.goto('/bids/my-list');
  await expertPage.waitForLoadState('domcontentloaded');
  await shot(expertPage, '전문가_내제안서목록');

  await expertCtx.close();

  // ── 의뢰인: 낙찰 처리 ──
  const clientCtx2 = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const clientPage2 = await clientCtx2.newPage();
  await login(clientPage2, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await clientPage2.goto(`/bids?projectId=${projectId}`);
  await clientPage2.waitForLoadState('domcontentloaded');
  await shot(clientPage2, '의뢰인_제안서목록');

  // 낙찰 버튼
  const selectBtn = clientPage2.locator('button:has-text("낙찰"), form[action*="select"] button, .btn-select').first();
  if (await selectBtn.isVisible().catch(() => false)) {
    await selectBtn.click();
    await clientPage2.waitForTimeout(500);
    // confirm 다이얼로그 처리
    clientPage2.on('dialog', d => d.accept());
    await clientPage2.waitForTimeout(2000);
    await shot(clientPage2, '낙찰_처리완료');
  }

  // 에스크로 확인
  await clientPage2.goto(`/payment/project/${projectId}`);
  await clientPage2.waitForLoadState('domcontentloaded');
  await shot(clientPage2, '에스크로_생성확인');

  await clientCtx2.close();

  // ── 전문가: 작업 완료 신고 ──
  const expertCtx2 = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const expertPage2 = await expertCtx2.newPage();
  await login(expertPage2, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  await expertPage2.goto('/bids/my-orders');
  await expertPage2.waitForLoadState('domcontentloaded');
  await scrollAndShot(expertPage2, '낙찰완료_주문목록', 2);

  // 작업 완료 버튼
  const completeBtn = expertPage2.locator('button:has-text("작업 완료"), button:has-text("완료 신고"), .btn-complete').first();
  if (await completeBtn.isVisible().catch(() => false)) {
    await completeBtn.click();
    await expertPage2.waitForTimeout(500);
    expertPage2.on('dialog', d => d.accept());
    await expertPage2.waitForTimeout(2000);
    await shot(expertPage2, '전문가_작업완료_신고');
  }

  await expertCtx2.close();

  // ── 의뢰인: 정산 ──
  const clientCtx3 = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const clientPage3 = await clientCtx3.newPage();
  await login(clientPage3, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await clientPage3.goto('/payment/my');
  await clientPage3.waitForLoadState('domcontentloaded');
  await shot(clientPage3, '의뢰인_결제내역');

  const settleBtn = clientPage3.locator('button:has-text("정산"), button:has-text("완료 확인"), .btn-settle').first();
  if (await settleBtn.isVisible().catch(() => false)) {
    await settleBtn.click();
    await clientPage3.waitForTimeout(500);
    clientPage3.on('dialog', d => d.accept());
    await clientPage3.waitForTimeout(2000);
    await shot(clientPage3, '정산_완료');
  }

  await clientCtx3.close();
});

// ──────────────────────────────────────────
// 04  소켓 채팅
// ──────────────────────────────────────────
test('04 소켓 채팅', async ({ browser }) => {
  const ctx = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await ctx.newPage();
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await page.goto('/chat/room');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '채팅_룸목록');

  // 첫 번째 채팅방 입장
  const firstRoom = page.locator('a[href*="/chat/room/"], .chat-room-item a, .room-link').first();
  if (await firstRoom.isVisible().catch(() => false)) {
    await firstRoom.click();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(1500);
    await shot(page, '채팅_룸_내부');

    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.fill('안녕하세요! 프로젝트 진행 상황 공유드립니다 😊');
      await shot(page, '채팅_메시지_입력');
      await msgInput.press('Enter');
      await page.waitForTimeout(1000);
      await shot(page, '채팅_메시지_전송완료');
    }
  }

  await ctx.close();
});

// ──────────────────────────────────────────
// 05  이메일 전송 + 제휴문의 이메일
// ──────────────────────────────────────────
test('05 이메일 전송 및 제휴문의', async ({ page }) => {
  // 회원가입 이메일 인증
  await page.goto('/join-start');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '이메일인증_회원가입시작');

  await page.goto('/join-role');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '이메일인증_역할선택');

  await page.goto('/join?role=CLIENT');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '이메일인증_가입폼');

  // 비밀번호 찾기 이메일
  await page.goto('/find-account');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '이메일_비밀번호찾기');

  // 제휴문의 (footer → 이메일 발송)
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);
  await page.goto('/main');
  await page.waitForLoadState('domcontentloaded');
  // 푸터 스크롤
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(1000);
  await shot(page, '제휴문의_푸터');

  // 제휴문의 링크 클릭
  const partnerLink = page.locator('a:has-text("제휴"), a[href*="partner"], .footer a:has-text("문의")').first();
  if (await partnerLink.isVisible().catch(() => false)) {
    await partnerLink.click();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(800);
    await shot(page, '제휴문의_폼');

    // 폼 입력
    await page.locator('input[name="companyName"], input[name="company"]').first().fill('테크스타트업 주식회사').catch(() => {});
    await page.locator('input[name="contactPerson"], input[name="name"]').first().fill('박대표').catch(() => {});
    await page.locator('input[name="email"]').first().fill('ceo@techstartup.kr').catch(() => {});
    await page.locator('input[name="phone"]').first().fill('010-1234-5678').catch(() => {});
    await page.locator('textarea[name="message"], textarea[name="content"]').first()
      .fill('IT 프리랜서 매칭 서비스와 제휴 협의를 원합니다. 당사는 연간 100개 이상의 개발 프로젝트를 발주하는 스타트업입니다.').catch(() => {});
    await shot(page, '제휴문의_폼_입력완료');

    const submitBtn = page.locator('form button[type="submit"]').first();
    if (await submitBtn.isVisible().catch(() => false)) {
      await submitBtn.click();
      await page.waitForTimeout(2000);
      await shot(page, '제휴문의_이메일_발송완료');
    }
  }
});

// ──────────────────────────────────────────
// 06  알림 + 쪽지(메시지)
// ──────────────────────────────────────────
test('06 알림과 쪽지', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 알림
  await page.goto('/notifications');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '알림_목록', 2);

  // SSE 실시간 알림 벨 아이콘 (헤더)
  const bellBtn = page.locator('.notification-bell, #notificationBell, .bell-icon, a[href*="notifications"]').first();
  if (await bellBtn.isVisible().catch(() => false)) {
    await bellBtn.click();
    await page.waitForTimeout(800);
    await shot(page, '알림_헤더_드롭다운');
    await page.keyboard.press('Escape');
  }

  // 쪽지
  await page.goto('/messages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지_목록');

  const firstMsg = page.locator('a[href*="/messages/"]').first();
  if (await firstMsg.isVisible().catch(() => false)) {
    await firstMsg.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '쪽지_상세');
  }

  // 쪽지 보내기
  await page.goto('/messages/send');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지_보내기폼');

  const recipientInput = page.locator('input[name="receiverEmail"], input[name="receiver"], input[name="email"]').first();
  if (await recipientInput.isVisible().catch(() => false)) {
    await recipientInput.fill(ACCOUNTS.client.email);
    await page.locator('input[name="title"]').first().fill('프로젝트 진행 관련 문의드립니다').catch(() => {});
    await page.locator('textarea[name="content"]').first()
      .fill('안녕하세요. 현재 진행 중인 프로젝트 관련하여 추가 요구사항이 있으시면 말씀해 주세요.').catch(() => {});
    await shot(page, '쪽지_보내기_입력완료');

    const sendBtn = page.locator('form button[type="submit"]').first();
    if (await sendBtn.isVisible().catch(() => false)) {
      await sendBtn.click();
      await page.waitForTimeout(2000);
      await shot(page, '쪽지_전송완료');
    }
  }
});

// ──────────────────────────────────────────
// 07  AI 챗봇 (프로젝트 작성 보조)
// ──────────────────────────────────────────
test('07 AI 챗봇', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await page.goto('/projects/save-form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, 'AI_챗봇_프로젝트폼');

  // AI 버튼 찾아 클릭
  const aiTriggers = [
    'button:has-text("AI 작성")',
    'button:has-text("AI 도움")',
    'button:has-text("AI")',
    '#aiDraftBtn',
    '.ai-helper-btn',
    '[data-ai]',
  ];

  let aiOpened = false;
  for (const selector of aiTriggers) {
    const el = page.locator(selector).first();
    if (await el.isVisible().catch(() => false)) {
      await el.click();
      await page.waitForTimeout(1500);
      await shot(page, 'AI_챗봇_열림');
      aiOpened = true;

      // AI 챗 입력
      const aiInput = page.locator('#aiChatInput, .ai-input, textarea[placeholder*="AI"], input[placeholder*="AI"]').first();
      if (await aiInput.isVisible().catch(() => false)) {
        await aiInput.fill('iOS/Android 쇼핑몰 앱 개발 프로젝트 의뢰서 초안 작성해줘');
        await shot(page, 'AI_챗봇_메시지입력');

        const aiSend = page.locator('#aiSendBtn, .ai-send-btn, button:has-text("전송")').first();
        if (await aiSend.isVisible().catch(() => false)) {
          await aiSend.click();
          await page.waitForTimeout(4000); // AI 응답 대기
          await shot(page, 'AI_챗봇_응답');
        }
      }
      break;
    }
  }

  if (!aiOpened) {
    // AI 초안 관련 섹션이 인라인인 경우
    await shot(page, 'AI_챗봇_프로젝트폼_전체');
  }
});

// ──────────────────────────────────────────
// 08  결제 기능 (충전 + 에스크로)
// ──────────────────────────────────────────
test('08 결제 기능', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 충전 폼
  await page.goto('/charge/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_충전폼');

  // 금액 선택
  const amountBtn = page.locator('.charge-amount-btn, button:has-text("50,000"), button:has-text("10만"), input[value="100000"]').first();
  if (await amountBtn.isVisible().catch(() => false)) {
    await amountBtn.click();
    await page.waitForTimeout(500);
    await shot(page, '결제_금액선택');
  }

  // 충전 버튼 (Toss 실제 호출은 안 함 - 화면만 캡처)
  await scrollAndShot(page, '결제_충전페이지', 2);

  // 결제 내역
  await page.goto('/payment/my');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_에스크로내역');

  // 출금 폼 (전문가)
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);
  await page.goto('/withdrawal/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_출금신청폼');

  await page.locator('input[name="bankName"]').first().fill('카카오뱅크').catch(() => {});
  await page.locator('input[name="accountNumber"]').first().fill('3333-01-1234567').catch(() => {});
  await page.locator('input[name="accountHolder"]').first().fill('홍길동').catch(() => {});
  await page.locator('input[name="amount"]').first().fill('50000').catch(() => {});
  await shot(page, '결제_출금신청_입력완료');
});

// ──────────────────────────────────────────
// 09  광고 기능
// ──────────────────────────────────────────
test('09 광고 기능', async ({ page }) => {
  // 전문가: 광고 입찰
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  await page.goto('/ad-auction');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '광고_경매페이지', 2);

  // 광고 입찰 버튼
  const bidBtn = page.locator('button:has-text("입찰"), button:has-text("광고 입찰"), .btn-ad-bid').first();
  if (await bidBtn.isVisible().catch(() => false)) {
    await bidBtn.click();
    await page.waitForTimeout(1000);
    await shot(page, '광고_입찰폼');

    // 금액 입력
    const bidAmount = page.locator('input[name="amount"], input[name="bidAmount"], #bidAmount').first();
    if (await bidAmount.isVisible().catch(() => false)) {
      await bidAmount.fill('100000');
      await shot(page, '광고_입찰_금액입력');

      const submitBid = page.locator('button[type="submit"], .btn-submit-bid').first();
      if (await submitBid.isVisible().catch(() => false)) {
        await submitBid.click();
        await page.waitForTimeout(1500);
        await shot(page, '광고_입찰_완료');
      }
    }
  }

  // 내 광고 입찰 현황
  await page.goto('/ad-auction/my-bids');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '광고_내입찰현황');

  // 광고 배너 등록
  await page.goto('/advertisements/my');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '광고_내광고관리');

  const adFormLink = page.locator('a[href*="advertisements/form"], a:has-text("광고 등록")').first();
  if (await adFormLink.isVisible().catch(() => false)) {
    await adFormLink.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '광고_등록폼');
  } else {
    await page.goto('/advertisements/form');
    await page.waitForLoadState('domcontentloaded').catch(() => {});
    await shot(page, '광고_등록폼');
  }

  // 관리자: 광고 슬롯 관리
  await login(page, ACCOUNTS.admin.email, ACCOUNTS.admin.password);
  await page.goto('/admin/ad-slots');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '광고_관리자_슬롯관리');

  await page.goto('/admin/advertisement');
  await page.waitForLoadState('domcontentloaded').catch(() => {});
  await shot(page, '광고_관리자_광고관리');
});

// ──────────────────────────────────────────
// 10  마이페이지 + 전문가 프로필
// ──────────────────────────────────────────
test('10 마이페이지와 전문가 프로필', async ({ page }) => {
  // 의뢰인 마이페이지
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '의뢰인_마이페이지', 3);

  // 전문가 목록
  await page.goto('/experts');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_목록');

  // 전문가 프로필 상세
  const expertLink = page.locator('a[href*="/experts/"]').first();
  if (await expertLink.isVisible().catch(() => false)) {
    await expertLink.click();
    await page.waitForLoadState('domcontentloaded');
    await scrollAndShot(page, '전문가_프로필_상세', 2);
  }

  // 전문가 마이페이지
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);
  await page.goto('/experts/dashboard');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '전문가_대시보드', 2);

  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await scrollAndShot(page, '전문가_마이페이지', 3);
});
