// SevMerge 기능 시연 영상 자동 녹화 스크립트
// 실행: node record-videos.js
// 결과: ~/Desktop/SevMerge-영상/*.webm

const { chromium } = require('@playwright/test');
const path = require('path');
const fs = require('fs');

const OUTPUT = path.join(process.env.HOME, 'Desktop/SevMerge-영상');
const BASE   = 'http://localhost:8080';

const ACC = {
  admin:  { email: 'admin@sevmerge.com',    pw: '1234' },
  client: { email: 'client01@sevmerge.com', pw: '1234' },
  expert: { email: 'expert01@sevmerge.com', pw: '1234' },
};

// ─── 공통 헬퍼 ───────────────────────────────────────────────

async function go(page, url) {
  await page.goto(`${BASE}${url}`, { waitUntil: 'domcontentloaded', timeout: 25000 }).catch(() => {});
  await page.waitForTimeout(1800);
}

async function scroll(page, steps = 3) {
  const h = await page.evaluate(() => document.body.scrollHeight);
  const step = Math.floor(h / steps);
  for (let i = 1; i <= steps; i++) {
    await page.evaluate(y => window.scrollTo({ top: y, behavior: 'smooth' }), step * i);
    await page.waitForTimeout(900);
  }
  await page.evaluate(() => window.scrollTo({ top: 0, behavior: 'smooth' }));
  await page.waitForTimeout(600);
}

async function login(page, { email, pw }) {
  await go(page, '/login');
  await page.fill('input[name="email"]', email);
  await page.waitForTimeout(400);
  await page.fill('input[name="password"]', pw);
  await page.waitForTimeout(400);
  await page.click('button[type="submit"]');
  await page.waitForTimeout(2500);
}

async function tryClick(page, sel) {
  const el = page.locator(sel).first();
  if (await el.isVisible().catch(() => false)) {
    await el.click().catch(() => {});
    await page.waitForTimeout(1000);
  }
}

async function tryFill(page, sel, text) {
  const el = page.locator(sel).first();
  if (await el.isVisible().catch(() => false)) {
    await el.fill(text).catch(() => {});
    await page.waitForTimeout(300);
  }
}

function createCtx(browser) {
  return browser.newContext({
    recordVideo: { dir: OUTPUT, size: { width: 1440, height: 900 } },
    viewport:    { width: 1440, height: 900 },
    locale:      'ko-KR',
  });
}

async function finishVideo(page, ctx, filename) {
  const video = page.video();
  await ctx.close();
  if (video) {
    const dest = path.join(OUTPUT, `${filename}.webm`);
    await video.saveAs(dest).catch(async () => {
      // fallback: rename from path
      const p = await video.path().catch(() => null);
      if (p && fs.existsSync(p)) fs.renameSync(p, dest);
    });
    console.log(`  ✅ 저장 완료: ${filename}.webm`);
  }
}

// ─── 개별 씬 ─────────────────────────────────────────────────

async function scene01_login(browser) {
  console.log('\n[01] 로그인 기능 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  // 인트로
  await go(page, '/');
  await scroll(page, 4);

  // 로그인 폼
  await go(page, '/login');
  await page.fill('input[name="email"]', ACC.client.email);
  await page.waitForTimeout(600);
  await page.fill('input[name="password"]', ACC.client.pw);
  await page.waitForTimeout(600);
  await page.click('button[type="submit"]');
  await page.waitForTimeout(2500);

  // 로그인 후 메인
  await scroll(page, 3);
  await page.waitForTimeout(1000);

  await finishVideo(page, ctx, '01-로그인');
}

async function scene02_admin(browser) {
  console.log('\n[02] ADMIN 페이지 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.admin);

  // 대시보드
  await go(page, '/admin/main');
  await scroll(page, 4);

  // 회원 관리
  await go(page, '/admin/members');
  await scroll(page, 3);

  // 전문가 승인
  await go(page, '/admin/experts');
  await scroll(page, 3);

  // 프로젝트 관리
  await go(page, '/admin/projects');
  await scroll(page, 3);

  // 댓글 신고 관리
  await go(page, '/admin/reports');
  await scroll(page, 3);

  // 블랙리스트
  await go(page, '/admin/blacklists');
  await scroll(page, 3);

  // 에스크로
  await go(page, '/admin/escrow');
  await scroll(page, 3);

  // 광고 승인
  await go(page, '/admin/advertisements');
  await scroll(page, 3);

  // 제휴 관리
  await go(page, '/admin/partnerships');
  await scroll(page, 2);

  // 수익 현황
  await go(page, '/admin/revenue');
  await scroll(page, 3);

  await finishVideo(page, ctx, '02-ADMIN페이지');
}

async function scene03_proposal_flow(browser) {
  console.log('\n[03] 제안서 주고받기 흐름 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  // === 의뢰인: 프로젝트 등록 ===
  await login(page, ACC.client);
  await go(page, '/projects');
  await scroll(page, 2);

  // 프로젝트 등록 폼
  await go(page, '/projects/save-form');
  await tryFill(page, 'input[name="title"], #title', 'AI 추천 기반 e-커머스 플랫폼 개발');
  await tryClick(page, 'select[name="category"], #category');
  await page.locator('select[name="category"], #category').first().selectOption({ index: 1 }).catch(() => {});
  await page.waitForTimeout(500);
  await tryFill(page, 'textarea[name="description"], #description',
    '커머스 플랫폼에 AI 개인화 추천 엔진을 붙이는 작업입니다.\n- 사용자 행동 기반 상품 추천\n- 검색 자동완성\n- 관리자 대시보드\n기술스택: Spring Boot + React + Python ML');
  await tryFill(page, 'input[name="budgetMin"], #budgetMin', '5000');
  await tryFill(page, 'input[name="budgetMax"], #budgetMax', '12000');
  const dd = new Date(); dd.setDate(dd.getDate() + 30);
  await tryFill(page, 'input[name="deadline"], #deadline', dd.toISOString().split('T')[0]);
  await page.waitForTimeout(800);
  await scroll(page, 2);
  await page.waitForTimeout(800);

  // === 전문가 뷰: 프로젝트 조회 ===
  await login(page, ACC.expert);
  await go(page, '/projects');
  await scroll(page, 3);

  // 프로젝트 상세
  await go(page, '/projects/1');
  await scroll(page, 4);

  // 입찰(제안서) 폼
  await go(page, '/bids/save-form?projectId=1');
  await page.waitForTimeout(1200);
  await tryFill(page, 'input[name="title"]', '[제안] AI 추천 플랫폼 - 풀스택 구현 제안서');
  await tryFill(page, 'textarea[name="coverLetter"]',
    '안녕하세요, 풀스택 5년 경력 개발자입니다.\nSpring Boot + React + FastAPI 기반으로 AI 추천 엔진을 안정적으로 구축할 수 있습니다.\n유사 프로젝트 납기 100% 준수 실적 보유.');
  await tryFill(page, 'input[name="estimatedDays"]', '60');
  await tryFill(page, 'input[name="proposedPrice"]', '9500000');
  await scroll(page, 2);
  await page.waitForTimeout(1000);

  // === 의뢰인 뷰: 제안서 목록 조회 ===
  await login(page, ACC.client);
  await go(page, '/bids?projectId=1');
  await scroll(page, 3);

  // 제안서 상세
  const bidLink = page.locator('a[href*="/bids/"]').first();
  if (await bidLink.isVisible().catch(() => false)) {
    await bidLink.click();
    await page.waitForTimeout(1500);
    await scroll(page, 3);
  }

  // 입찰된 프로젝트 현황 (마이페이지)
  await go(page, '/my-pages');
  await scroll(page, 4);

  // 전문가 관점: 내 입찰
  await login(page, ACC.expert);
  await go(page, '/bids/my-list');
  await scroll(page, 3);

  // 낙찰 작업물 제출 (내 수주)
  await go(page, '/bids/my-orders');
  await scroll(page, 3);

  await finishVideo(page, ctx, '03-제안서주고받기');
}

async function scene04_awarded_projects(browser) {
  console.log('\n[04] 낙찰완료건 프로젝트 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  // 전문가: 낙찰된 작업 목록
  await login(page, ACC.expert);
  await go(page, '/bids/my-orders');
  await scroll(page, 3);

  // 프로젝트 상세 (낙찰된 건)
  await go(page, '/projects/1');
  await scroll(page, 4);

  // 의뢰인: 마이페이지 프로젝트 탭
  await login(page, ACC.client);
  await go(page, '/my-pages');
  await scroll(page, 4);

  // 작업 완료 / 에스크로 관련
  await go(page, '/admin/escrow');
  await scroll(page, 3);

  await finishVideo(page, ctx, '04-낙찰완료건');
}

async function scene05_filter_search(browser) {
  console.log('\n[05] 동적 다중 필터 검색 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);

  // ─ 프로젝트 검색 ─
  await go(page, '/projects');
  await scroll(page, 2);

  // 키워드 검색
  const kwInput = page.locator('input[name="keyword"], input[placeholder*="검색"]').first();
  if (await kwInput.isVisible().catch(() => false)) {
    await kwInput.fill('앱 개발');
    await page.keyboard.press('Enter');
    await page.waitForTimeout(1500);
    await scroll(page, 2);
    await kwInput.clear();
    await page.keyboard.press('Enter');
    await page.waitForTimeout(1000);
  }

  // 카테고리 필터
  const catSel = page.locator('select[name="category"], #category, select[name="type"]').first();
  if (await catSel.isVisible().catch(() => false)) {
    await catSel.selectOption({ index: 1 }).catch(() => {});
    await page.waitForTimeout(1500);
    await scroll(page, 2);
    await catSel.selectOption({ index: 0 }).catch(() => {});
    await page.waitForTimeout(1000);
  }

  // 예산 필터
  const budgetInput = page.locator('input[name="budgetMin"], input[placeholder*="예산"]').first();
  if (await budgetInput.isVisible().catch(() => false)) {
    await budgetInput.fill('1000');
    await page.keyboard.press('Enter');
    await page.waitForTimeout(1500);
    await scroll(page, 2);
  }

  // ─ 전문가 검색 ─
  await go(page, '/experts');
  await scroll(page, 3);

  // 전문가 스택 필터
  const stackCheck = page.locator('input[type="checkbox"]').first();
  if (await stackCheck.isVisible().catch(() => false)) {
    await stackCheck.check({ force: true }).catch(() => {});
    await page.waitForTimeout(1200);
    await scroll(page, 2);
    await stackCheck.uncheck({ force: true }).catch(() => {});
    await page.waitForTimeout(800);
  }

  // 경력 필터
  const careerSel = page.locator('select[name="career"], select[name="filter"]').first();
  if (await careerSel.isVisible().catch(() => false)) {
    await careerSel.selectOption({ index: 1 }).catch(() => {});
    await page.waitForTimeout(1500);
    await scroll(page, 2);
  }

  // 전문가 상세
  const firstExpert = page.locator('a[href*="/experts/"]').first();
  if (await firstExpert.isVisible().catch(() => false)) {
    await firstExpert.click();
    await page.waitForTimeout(1500);
    await scroll(page, 3);
  }

  await finishVideo(page, ctx, '05-다중필터검색');
}

async function scene06_socket_chat(browser) {
  console.log('\n[06] 소켓 채팅 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);

  // 채팅방 목록
  await go(page, '/chat/room');
  await scroll(page, 3);

  // 채팅방 입장 (있는 경우)
  const firstRoom = page.locator('a[href*="/chat/room/"]').first();
  if (await firstRoom.isVisible().catch(() => false)) {
    await firstRoom.click();
    await page.waitForTimeout(2000);
    await scroll(page, 2);

    // 메시지 입력
    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput, .message-input').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.click();
      await msgInput.fill('안녕하세요! 프로젝트 관련 문의드립니다.');
      await page.waitForTimeout(800);
      // Enter or submit
      await page.keyboard.press('Enter');
      await page.waitForTimeout(1500);
      await msgInput.fill('납기일 조율이 가능할까요?');
      await page.waitForTimeout(500);
      await page.keyboard.press('Enter');
      await page.waitForTimeout(1500);
    }
  } else {
    // 전문가와 채팅 시작
    await go(page, '/chat/start/expert/1');
    await page.waitForTimeout(2000);
    await scroll(page, 2);
    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.fill('안녕하세요! 개발 문의드려도 될까요?');
      await page.waitForTimeout(700);
      await page.keyboard.press('Enter');
      await page.waitForTimeout(1500);
    }
  }

  // 전문가 측 채팅 확인
  await login(page, ACC.expert);
  await go(page, '/chat/room');
  await scroll(page, 2);
  const expertRoom = page.locator('a[href*="/chat/room/"]').first();
  if (await expertRoom.isVisible().catch(() => false)) {
    await expertRoom.click();
    await page.waitForTimeout(2000);
    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.fill('네 안녕하세요! 어떤 부분이 궁금하신가요?');
      await page.keyboard.press('Enter');
      await page.waitForTimeout(1500);
    }
  }

  await finishVideo(page, ctx, '06-소켓채팅');
}

async function scene07_email_partnership(browser) {
  console.log('\n[07] 이메일 전송 + 제휴문의 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);

  // 고객지원 / 제휴문의 페이지
  await go(page, '/supports');
  await scroll(page, 4);

  // 제휴문의 탭 클릭
  const partnerTab = page.locator('a:has-text("제휴"), button:has-text("제휴"), [data-tab="partnership"]').first();
  if (await partnerTab.isVisible().catch(() => false)) {
    await partnerTab.click();
    await page.waitForTimeout(1200);
    await scroll(page, 2);
  }

  // 제휴문의 폼 입력
  await tryFill(page, 'input[name="companyName"]', '(주)테크솔루션');
  await tryFill(page, 'input[name="managerName"]', '박정훈');
  await tryFill(page, 'input[name="email"]', 'partner@techsolution.co.kr');
  await tryFill(page, 'input[name="phone"], input[name="contact"]', '010-3456-7890');
  await tryFill(page, 'textarea[name="content"], textarea[name="message"]',
    'SevMerge와 기업 채용 서비스 연계 제휴를 희망합니다.\n저희는 IT 기업 300곳과 협력 중이며, 프리랜서 인재풀 공동 운영을 제안드립니다.');
  await scroll(page, 2);
  await page.waitForTimeout(1000);

  // 1:1 문의 이메일
  await go(page, '/boards/save-form?boardType=INQUIRY');
  await page.waitForTimeout(1200);
  await tryFill(page, 'input[name="title"]', '세금계산서 발행 및 정산 문의');
  await tryFill(page, 'textarea[name="content"]',
    '안녕하세요. 프로젝트 완료 후 세금계산서 발행 절차와 정산 일정에 대해 안내 부탁드립니다.');
  await scroll(page, 2);
  await page.waitForTimeout(1000);
  await tryClick(page, 'button[type="submit"]');
  await page.waitForTimeout(2000);

  await finishVideo(page, ctx, '07-이메일전송+제휴문의');
}

async function scene08_notifications_messages(browser) {
  console.log('\n[08] 알림 + 쪽지 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);

  // 알림 목록
  await go(page, '/notifications');
  await scroll(page, 3);

  // 개별 알림 클릭 (있으면)
  const notif = page.locator('.notification-item a, .noti-item a, [href*="/notifications/"]').first();
  if (await notif.isVisible().catch(() => false)) {
    await notif.click();
    await page.waitForTimeout(1500);
  }

  // 쪽지함 목록
  await go(page, '/messages');
  await scroll(page, 3);

  // 쪽지 상세
  const msgLink = page.locator('a[href*="/messages/"]').first();
  if (await msgLink.isVisible().catch(() => false)) {
    await msgLink.click();
    await page.waitForTimeout(1500);
    await scroll(page, 2);
    await page.goBack();
    await page.waitForTimeout(800);
  }

  // 쪽지 작성
  await go(page, '/messages/send');
  await page.waitForTimeout(1200);
  await scroll(page, 2);
  await tryFill(page, 'input[name="receiverEmail"], input[name="to"], input[name="email"]', 'expert01@sevmerge.com');
  await tryFill(page, 'input[name="title"]', '프로젝트 진행 상황 확인 요청');
  await tryFill(page, 'textarea[name="content"]', '안녕하세요! 현재 개발 진행 상황 공유 부탁드립니다. 특히 API 연동 부분이 완료됐는지 확인하고 싶습니다.');
  await page.waitForTimeout(1000);
  await scroll(page, 2);

  await finishVideo(page, ctx, '08-알림+쪽지');
}

async function scene09_ai_chatbot(browser) {
  console.log('\n[09] AI 챗봇 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);
  await go(page, '/main');
  await scroll(page, 2);
  await page.waitForTimeout(1000);

  // 챗봇 버튼 클릭
  const chatbotBtn = page.locator('.chatbot-toggle, [class*="chatbot-toggle"], button[aria-label*="챗봇"], .floating-chat').first();
  if (await chatbotBtn.isVisible().catch(() => false)) {
    await chatbotBtn.click();
    await page.waitForTimeout(1500);

    // 메시지 입력
    const chatInput = page.locator('.chatbot-input, .chat-input input, input[placeholder*="메시지"], textarea[placeholder*="메시지"]').first();
    if (await chatInput.isVisible().catch(() => false)) {
      await chatInput.fill('SevMerge에서 프로젝트를 등록하는 방법이 궁금해요');
      await page.waitForTimeout(500);
      await page.keyboard.press('Enter');
      await page.waitForTimeout(3000); // AI 응답 대기

      await chatInput.fill('전문가 선정 기준이 있나요?');
      await page.waitForTimeout(500);
      await page.keyboard.press('Enter');
      await page.waitForTimeout(3000);
    }
    await page.waitForTimeout(1500);
  }

  // 프로젝트 목록에서도 챗봇 표시
  await go(page, '/projects');
  await page.waitForTimeout(1000);
  if (await chatbotBtn.isVisible().catch(() => false)) {
    await chatbotBtn.click();
    await page.waitForTimeout(1000);
    const chatInput = page.locator('.chatbot-input, .chat-input input, input[placeholder*="메시지"]').first();
    if (await chatInput.isVisible().catch(() => false)) {
      await chatInput.fill('에스크로 결제는 어떻게 동작하나요?');
      await page.keyboard.press('Enter');
      await page.waitForTimeout(3000);
    }
  }

  await finishVideo(page, ctx, '09-AI챗봇');
}

async function scene10_payment(browser) {
  console.log('\n[10] 결제 기능 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  await login(page, ACC.client);

  // 충전 폼
  await go(page, '/charge/form');
  await scroll(page, 3);

  // 금액 선택 버튼들
  const amtBtns = page.locator('button:has-text("만원"), button:has-text("원"), .amount-btn, [data-amount]');
  const cnt = await amtBtns.count();
  if (cnt > 0) {
    for (let i = 0; i < Math.min(3, cnt); i++) {
      await amtBtns.nth(i).click().catch(() => {});
      await page.waitForTimeout(600);
    }
  } else {
    await tryFill(page, 'input[name="amount"], input[name="chargeAmount"]', '100000');
  }
  await page.waitForTimeout(1000);
  await scroll(page, 2);

  // 결제 수단 선택
  const payMethod = page.locator('input[name="payMethod"], select[name="payMethod"], .pay-method-btn').first();
  if (await payMethod.isVisible().catch(() => false)) {
    await payMethod.click().catch(() => {});
    await page.waitForTimeout(800);
  }

  // 마이페이지 → 포인트/잔액 확인
  await go(page, '/my-pages');
  await scroll(page, 4);

  // 결제 이력
  await go(page, '/payment/my');
  await page.waitForTimeout(1500);
  await scroll(page, 3);

  // 에스크로 흐름
  await go(page, '/admin/escrow');
  await scroll(page, 3);

  await finishVideo(page, ctx, '10-결제기능');
}

async function scene11_advertisement(browser) {
  console.log('\n[11] 광고 기능 녹화 중...');
  const ctx = await createCtx(browser);
  const page = await ctx.newPage();

  // 의뢰인/전문가: 광고 등록
  await login(page, ACC.expert);

  // 광고 등록 폼
  await go(page, '/advertisements/form');
  await page.waitForTimeout(1200);
  await scroll(page, 3);
  await tryFill(page, 'input[name="title"]', '풀스택 개발 전문가 — 빠른 납기 보장');
  await tryFill(page, 'textarea[name="content"], input[name="description"]',
    'Spring Boot + React 전문, 5년 경력. 중소기업 맞춤 시스템 개발 전문입니다.');
  await tryFill(page, 'input[name="link"], input[name="url"]', 'https://sevmerge.com');
  await scroll(page, 2);
  await page.waitForTimeout(1000);

  // 내 광고 현황
  await go(page, '/advertisements/my');
  await scroll(page, 3);

  // 배너 광고 경매
  await go(page, '/ad-auction');
  await page.waitForTimeout(1500);
  await scroll(page, 3);

  const bidBtn = page.locator('button:has-text("입찰"), a:has-text("입찰"), .bid-btn').first();
  if (await bidBtn.isVisible().catch(() => false)) {
    await bidBtn.click();
    await page.waitForTimeout(1200);
    await tryFill(page, 'input[name="amount"], input[name="bidPrice"]', '50000');
    await page.waitForTimeout(800);
  }

  // 내 입찰 현황
  await go(page, '/ad-auction/my-bids');
  await scroll(page, 3);

  // 관리자: 광고 승인 관리
  await login(page, ACC.admin);
  await go(page, '/admin/advertisements');
  await scroll(page, 4);

  // 광고 슬롯 관리
  await go(page, '/admin/ad-slots');
  await scroll(page, 3);

  await finishVideo(page, ctx, '11-광고기능');
}

// ─── 메인 ─────────────────────────────────────────────────────

async function main() {
  fs.mkdirSync(OUTPUT, { recursive: true });
  console.log(`📂 출력 폴더: ${OUTPUT}`);
  console.log('🎬 SevMerge 기능 시연 영상 녹화 시작\n');

  const browser = await chromium.launch({
    headless: false,
    slowMo: 600,
    args: ['--window-size=1440,900'],
  });

  const scenes = [
    // scene01_login,  // 완료됨
    scene02_admin,
    scene03_proposal_flow,
    scene04_awarded_projects,
    scene05_filter_search,
    scene06_socket_chat,
    scene07_email_partnership,
    scene08_notifications_messages,
    scene09_ai_chatbot,
    scene10_payment,
    scene11_advertisement,
  ];

  const errors = [];
  for (const scene of scenes) {
    try {
      await scene(browser);
    } catch (e) {
      console.error(`  ⚠️  씬 실패 (계속 진행): ${e.message?.split('\n')[0]}`);
      errors.push({ scene: scene.name, error: e.message?.split('\n')[0] });
    }
  }

  await browser.close();

  if (errors.length > 0) {
    console.log('\n⚠️  실패한 씬:');
    errors.forEach(e => console.log(`  - ${e.scene}: ${e.error}`));
  }

  console.log('\n🎉 모든 영상 녹화 완료!');
  console.log(`📂 저장 위치: ${OUTPUT}`);

  const files = fs.readdirSync(OUTPUT).filter(f => f.endsWith('.webm'));
  console.log(`\n생성된 파일 (${files.length}개):`);
  files.sort().forEach(f => {
    const size = (fs.statSync(path.join(OUTPUT, f)).size / 1024 / 1024).toFixed(1);
    console.log(`  ${f} (${size} MB)`);
  });
}

main().catch(err => {
  console.error('❌ 오류 발생:', err);
  process.exit(1);
});
