/**
 * 06-client-expert-full.spec.ts
 * 의뢰인 + 전문가 전체 기능 상세 캡처
 */
import { test, Page } from '@playwright/test';
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
  const num = String(idx).padStart(3, '0');
  const file = path.join(OUT_DIR, `C${num}_${name}.png`);
  await page.waitForTimeout(600);
  await page.screenshot({ path: file, fullPage: false });
  console.log(`✅ ${file}`);
}

async function shotFull(page: Page, name: string) {
  idx++;
  const num = String(idx).padStart(3, '0');
  const file = path.join(OUT_DIR, `C${num}_${name}.png`);
  await page.waitForTimeout(600);
  await page.screenshot({ path: file, fullPage: true });
  console.log(`✅ ${file}`);
}

async function login(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.waitForLoadState('domcontentloaded');
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL(/\/(main|admin\/main|intro|experts\/dashboard)/, { timeout: 10000 }).catch(() => {});
  await page.waitForTimeout(1200);
}

// ══════════════════════════════════════════
// CLIENT 전체 기능
// ══════════════════════════════════════════
test('CLIENT 01 메인+프로젝트목록+필터', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 메인
  await page.goto('/main');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '클라이언트_메인_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '클라이언트_메인_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '클라이언트_메인_하단');

  // 프로젝트 목록
  await page.goto('/projects');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_목록_기본');

  // 카테고리 필터
  const catFilter = page.locator('select[name="category"], #filterCategory, .filter-category, select').first();
  if (await catFilter.isVisible().catch(() => false)) {
    await catFilter.selectOption({ index: 1 }).catch(() => {});
    await page.waitForTimeout(800);
    await shot(page, '프로젝트_필터_카테고리');
  }

  // 키워드 검색
  const kw = page.locator('input[name="keyword"], input[type="search"], input[placeholder*="검색"]').first();
  if (await kw.isVisible().catch(() => false)) {
    await kw.fill('앱');
    await kw.press('Enter');
    await page.waitForTimeout(1000);
    await shot(page, '프로젝트_필터_키워드_앱');
    await kw.fill('');
    await kw.press('Enter');
    await page.waitForTimeout(800);
  }

  // 복합 필터
  const statusFilter = page.locator('select[name="status"], #filterStatus').first();
  if (await statusFilter.isVisible().catch(() => false)) {
    await statusFilter.selectOption({ index: 0 }).catch(() => {});
    await page.waitForTimeout(600);
    await shot(page, '프로젝트_복합_필터');
  }
});

test('CLIENT 02 프로젝트 상세+제안서목록', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // OPEN 프로젝트 상세 (project 1)
  await page.goto('/projects/1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_상세_OPEN');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '프로젝트_상세_OPEN_하단');

  // 제안서 목록
  await page.goto('/bids?projectId=1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '제안서_목록_OPEN프로젝트');

  // IN_PROGRESS 프로젝트 상세 (project 2)
  await page.goto('/projects/2');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_상세_IN_PROGRESS');

  // DONE 프로젝트 (project 3)
  await page.goto('/projects/3');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_상세_DONE');

  // COMPLETED 프로젝트 (project 13)
  await page.goto('/projects/13');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_상세_COMPLETED');

  // DRAFT 임시저장
  await page.goto('/projects/draft');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_임시저장목록');
});

test('CLIENT 03 프로젝트 등록 전체', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  await page.goto('/projects/save-form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '프로젝트_등록폼_상단');

  // AI 챗봇
  const aiBtn = page.locator('button:has-text("AI"), #aiDraftBtn, .ai-btn').first();
  if (await aiBtn.isVisible().catch(() => false)) {
    await aiBtn.click();
    await page.waitForTimeout(2000);
    await shot(page, 'AI_챗봇_프로젝트폼열림');

    const aiInput = page.locator('#aiChatInput, .ai-input, textarea[placeholder*="AI"], input[placeholder*="AI"]').first();
    if (await aiInput.isVisible().catch(() => false)) {
      await aiInput.fill('React Native 쇼핑몰 앱 개발 의뢰서 작성해줘');
      await shot(page, 'AI_챗봇_입력중');
      const sendBtn = page.locator('#aiSendBtn, .ai-send-btn, button:has-text("전송"), button:has-text("보내기")').first();
      if (await sendBtn.isVisible().catch(() => false)) {
        await sendBtn.click();
        await page.waitForTimeout(5000);
        await shot(page, 'AI_챗봇_응답완료');
      }
    }
    const closeBtn = page.locator('button:has-text("닫기"), button:has-text("X"), .modal-close, [data-dismiss]').first();
    if (await closeBtn.isVisible().catch(() => false)) {
      await closeBtn.click();
      await page.waitForTimeout(500);
    } else {
      await page.keyboard.press('Escape');
      await page.waitForTimeout(500);
    }
  }

  // 폼 작성
  await page.locator('input[name="title"], #title').first().fill('기업 내부 인사관리 시스템 개발 의뢰').catch(() => {});
  const cat = page.locator('select[name="category"], #category').first();
  await cat.selectOption({ value: 'WEB' }).catch(async () => {
    await cat.selectOption({ index: 1 }).catch(() => {});
  });
  await page.locator('textarea[name="description"], #description').first()
    .fill('직원 100명 규모 기업의 인사관리 시스템\n- 근태관리, 급여정산, 성과평가 기능\n- 관리자/직원 권한 분리\n- 모바일 앱 연동').catch(() => {});
  await page.locator('input[name="budgetMin"], #budgetMin').first().fill('8000').catch(() => {});
  await page.locator('input[name="budgetMax"], #budgetMax').first().fill('20000').catch(() => {});
  const dl = new Date(); dl.setDate(dl.getDate() + 60);
  await page.locator('input[name="deadline"], #deadline').first()
    .fill(dl.toISOString().split('T')[0]).catch(() => {});
  await page.waitForTimeout(500);
  await shot(page, '프로젝트_등록폼_입력완료');

  // 임시저장
  const draftBtn = page.locator('button.btn-draft, button:has-text("임시저장")').first();
  if (await draftBtn.isVisible().catch(() => false)) {
    await draftBtn.click();
    await page.waitForTimeout(1500);
    await shot(page, '프로젝트_임시저장_완료');
  }
});

test('CLIENT 04 전문가 목록+찜+프로필', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 전문가 목록
  await page.goto('/experts');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_목록_전체');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_목록_중간');

  // 전문가 1 프로필
  await page.goto('/experts/1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로필_상세_1');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로필_포트폴리오');

  // 찜 버튼
  const wishBtn = page.locator('button:has-text("찜"), .wish-btn, button[data-action*="wish"]').first();
  if (await wishBtn.isVisible().catch(() => false)) {
    await wishBtn.click();
    await page.waitForTimeout(800);
    await shot(page, '전문가_찜_완료');
  }

  // 전문가 2 프로필
  await page.goto('/experts/2');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로필_상세_2');

  // 찜 목록 (북마크)
  await page.goto('/my-pages/wishes');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '의뢰인_찜목록').catch(async () => {
    await page.goto('/my-pages');
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '의뢰인_마이페이지_찜확인');
  });
});

test('CLIENT 05 게시판 전체', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 자유게시판 목록
  await page.goto('/boards?boardType=FREE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '자유게시판_목록');

  // 자유게시판 상세
  const freePost = page.locator('a[href*="/boards/"]').first();
  if (await freePost.isVisible().catch(() => false)) {
    await freePost.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '자유게시판_상세');

    // 댓글 작성
    const commentInput = page.locator('textarea[name="content"], input[name="content"]').last();
    if (await commentInput.isVisible().catch(() => false)) {
      await commentInput.fill('유익한 글 감사합니다! 다음 프로젝트에 활용해볼게요.');
      await shot(page, '자유게시판_댓글_입력');
      const commentBtn = page.locator('button[type="submit"]').last();
      await commentBtn.click().catch(() => {});
      await page.waitForTimeout(1500);
      await shot(page, '자유게시판_댓글_등록완료');
    }
  }

  // 자유게시판 글 작성
  await page.goto('/boards/save-form?boardType=FREE');
  await page.waitForLoadState('domcontentloaded');
  const freeTitle = page.locator('input[name="title"]').first();
  if (await freeTitle.isVisible().catch(() => false)) {
    await freeTitle.fill('외주 개발자 선정 시 포트폴리오 어떻게 검토하시나요?');
    await page.locator('textarea[name="content"]').first()
      .fill('처음 외주를 맡겨보려 합니다. 포트폴리오 검토 시 중요하게 봐야 할 포인트가 무엇인지 선배님들의 조언 부탁드립니다!').catch(() => {});
    await shot(page, '자유게시판_글작성폼');
    await page.locator('button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(2000);
    await shot(page, '자유게시판_글작성_완료');
  }

  // 공지사항
  await page.goto('/boards?boardType=NOTICE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '공지사항_목록');
  const notice = page.locator('a[href*="/boards/"]').first();
  if (await notice.isVisible().catch(() => false)) {
    await notice.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '공지사항_상세');
  }

  // 1:1 문의
  await page.goto('/boards?boardType=INQUIRY');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '일대일문의_목록');

  await page.goto('/boards/save-form?boardType=INQUIRY');
  await page.waitForLoadState('domcontentloaded');
  const inquiryTitle = page.locator('input[name="title"]').first();
  if (await inquiryTitle.isVisible().catch(() => false)) {
    await inquiryTitle.fill('프로젝트 낙찰 후 전문가 연락이 없는 경우 어떻게 하나요?');
    await page.locator('textarea[name="content"]').first()
      .fill('낙찰 처리 후 2일이 지났는데 전문가로부터 연락이 없습니다. 에스크로는 이미 결제된 상태인데 어떻게 처리해야 하나요?').catch(() => {});
    await shot(page, '일대일문의_작성폼');
    await page.locator('button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(2000);
    await shot(page, '일대일문의_제출완료');
  }
});

test('CLIENT 06 FAQ+제휴문의+결제충전', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // FAQ
  await page.goto('/faq');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, 'FAQ_목록');
  const faqItem = page.locator('.faq-item, .accordion-item, .faq-question, dt, [data-toggle]').first();
  if (await faqItem.isVisible().catch(() => false)) {
    await faqItem.click();
    await page.waitForTimeout(800);
    await shot(page, 'FAQ_펼침');
  }

  // 결제 충전
  await page.goto('/charge/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_충전폼_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '결제_충전폼_하단');

  // 충전 금액 버튼
  const chargeBtn = page.locator('.charge-amount-btn, button:has-text("50,000"), button:has-text("10만"), button:has-text("100,000")').first();
  if (await chargeBtn.isVisible().catch(() => false)) {
    await chargeBtn.click();
    await page.waitForTimeout(600);
    await shot(page, '결제_충전금액_선택');
  }

  // 에스크로 내역
  await page.goto('/payment/my');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_에스크로_내역');

  // 충전 이력
  await page.goto('/charge/history');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '결제_충전_이력').catch(async () => {
    await page.goto('/my-pages');
    await shot(page, '결제_마이페이지_잔액확인');
  });
});

test('CLIENT 07 마이페이지 전체', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 메인 마이페이지
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '의뢰인_마이페이지_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '의뢰인_마이페이지_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight * 2 / 3, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '의뢰인_마이페이지_하단');

  // 내 프로젝트
  const myProjects = page.locator('a:has-text("내 프로젝트"), a[href*="my-projects"]').first();
  if (await myProjects.isVisible().catch(() => false)) {
    await myProjects.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '의뢰인_내프로젝트');
  } else {
    await page.goto('/projects?my=true');
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '의뢰인_내프로젝트목록');
  }

  // 프로필 편집
  await page.goto('/my-pages/edit');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '의뢰인_프로필편집').catch(async () => {
    await shot(page, '의뢰인_프로필편집_없음');
  });

  // 알림 설정
  await page.goto('/notifications');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '알림_목록_의뢰인');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '알림_목록_중간');

  // 쪽지함
  await page.goto('/messages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지함_의뢰인');
  const msgDetail = page.locator('a[href*="/messages/"]').first();
  if (await msgDetail.isVisible().catch(() => false)) {
    await msgDetail.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '쪽지_상세_의뢰인');
    await page.goBack();
  }

  // 쪽지 보내기
  await page.goto('/messages/send');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '쪽지_보내기폼_의뢰인');
  const rcv = page.locator('input[name="receiverEmail"], input[name="receiver"]').first();
  if (await rcv.isVisible().catch(() => false)) {
    await rcv.fill(ACCOUNTS.expert1.email);
    await page.locator('input[name="title"]').first().fill('추가 요구사항 공유드립니다').catch(() => {});
    await page.locator('textarea[name="content"]').first()
      .fill('진행 중인 프로젝트에 API 연동 요구사항이 추가되었습니다. 상세 내용은 첨부 문서를 확인해 주세요.').catch(() => {});
    await shot(page, '쪽지_보내기_입력완료');
    await page.locator('form button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(1500);
    await shot(page, '쪽지_전송완료_의뢰인');
  }
});

// ══════════════════════════════════════════
// EXPERT 전체 기능
// ══════════════════════════════════════════
test('EXPERT 01 로그인+대시보드+프로필', async ({ page }) => {
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
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_마이페이지_중간');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_마이페이지_하단');

  // 내 프로필 보기 (공개 프로필)
  await page.goto('/experts/1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내프로필_공개');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_내프로필_포트폴리오영역');
});

test('EXPERT 02 프로필 편집+포트폴리오', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 프로필 편집
  await page.goto('/experts/edit');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로필_편집폼_상단');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_프로필_편집폼_하단');

  // 소개 변경
  const introInput = page.locator('textarea[name="intro"], #intro').first();
  if (await introInput.isVisible().catch(() => false)) {
    const current = await introInput.inputValue().catch(() => '');
    await introInput.fill(current + ' (프로필 업데이트 중)');
    await shot(page, '전문가_프로필_편집중');
  }

  // 포트폴리오 관리
  await page.goto('/experts/portfolio');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_포트폴리오_목록');

  // 포트폴리오 등록 폼
  const addPortfolio = page.locator('a:has-text("포트폴리오 추가"), a:has-text("등록"), a[href*="portfolio/save"]').first();
  if (await addPortfolio.isVisible().catch(() => false)) {
    await addPortfolio.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_포트폴리오_등록폼');

    await page.locator('input[name="title"]').first().fill('쇼핑몰 백엔드 MSA 전환 프로젝트').catch(() => {});
    await page.locator('textarea[name="description"]').first()
      .fill('Spring Boot 기반 모놀리식 → MSA 전환. 일 평균 20만 주문 처리. AWS EKS 배포.').catch(() => {});
    await shot(page, '전문가_포트폴리오_작성중');
  } else {
    await page.goto('/experts/portfolio/save-form');
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_포트폴리오_등록폼');
  }
});

test('EXPERT 03 프로젝트탐색+제안서제출', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 프로젝트 목록 탐색
  await page.goto('/projects');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로젝트_목록탐색');

  // CERTIFIED_ONLY 필터 확인
  const certFilter = page.locator('select[name="bidFilter"], #filterBidFilter').first();
  if (await certFilter.isVisible().catch(() => false)) {
    await certFilter.selectOption({ index: 1 }).catch(() => {});
    await page.waitForTimeout(800);
    await shot(page, '전문가_필터_인증전문가전용');
  }

  // 프로젝트 상세 (오픈 프로젝트 = 1번)
  await page.goto('/projects/1');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_프로젝트_상세보기');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로젝트_상세_하단');

  // 제안서 작성 폼
  await page.goto('/bids/save-form?projectId=5');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_제안서_작성폼');

  await page.locator('textarea[name="coverLetter"]').first()
    .fill('안녕하세요! 풀스택 개발자 홍길동입니다. React Native + Spring Boot 조합으로 iOS/Android 쇼핑몰 앱을 성공적으로 출시한 경험이 있습니다. 예산과 일정 모두 준수하겠습니다.').catch(() => {});
  await page.locator('textarea[name="approach"], input[name="approach"]').first()
    .fill('1주차: 요구사항 분석 + 와이어프레임\n2-3주차: 상품/장바구니 기능\n4-5주차: 결제연동 + CMS\n6주차: QA + 배포').catch(() => {});
  await page.locator('input[name="estimatedDays"]').first().fill('45').catch(() => {});
  await page.locator('input[name="proposedPrice"]').first().fill('11000000').catch(() => {});
  await shot(page, '전문가_제안서_작성완료');

  // 내 제안서 목록
  await page.goto('/bids/my-list');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내제안서_목록');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_내제안서_목록_중간');

  // 낙찰완료 주문 (my-orders)
  await page.goto('/bids/my-orders');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_낙찰완료_주문목록');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_낙찰완료_주문_하단');
});

test('EXPERT 04 채팅+알림+쪽지', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 채팅방 목록
  await page.goto('/chat/room');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_채팅방목록');

  // 채팅방 입장
  const firstRoom = page.locator('a[href*="/chat/room/"], .chat-room-item a, .room-link').first();
  if (await firstRoom.isVisible().catch(() => false)) {
    await firstRoom.click();
    await page.waitForLoadState('domcontentloaded');
    await page.waitForTimeout(1500);
    await shot(page, '전문가_채팅방_내부');

    // 메시지 전송
    const msgInput = page.locator('input[name="message"], textarea[name="message"], #messageInput').first();
    if (await msgInput.isVisible().catch(() => false)) {
      await msgInput.fill('안녕하세요! 오늘 작업 진행 상황 공유드립니다. 현재 API 설계 완료, 다음 주부터 개발 착수 예정입니다.');
      await shot(page, '전문가_채팅_메시지입력');
      await msgInput.press('Enter');
      await page.waitForTimeout(1200);
      await shot(page, '전문가_채팅_메시지전송');
    }
  }

  // 알림
  await page.goto('/notifications');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_알림목록');

  // SSE 벨 아이콘
  const bellBtn = page.locator('.notification-bell, #notificationBell, .bell-icon, a[href*="notification"]').first();
  if (await bellBtn.isVisible().catch(() => false)) {
    await bellBtn.click();
    await page.waitForTimeout(800);
    await shot(page, '전문가_알림_헤더드롭다운');
    await page.keyboard.press('Escape');
  }

  // 쪽지함
  await page.goto('/messages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_쪽지함');
  const msgLink = page.locator('a[href*="/messages/"]').first();
  if (await msgLink.isVisible().catch(() => false)) {
    await msgLink.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_쪽지_상세');
  }

  // 쪽지 보내기
  await page.goto('/messages/send');
  await page.waitForLoadState('domcontentloaded');
  const rcv = page.locator('input[name="receiverEmail"], input[name="receiver"]').first();
  if (await rcv.isVisible().catch(() => false)) {
    await rcv.fill(ACCOUNTS.client.email);
    await page.locator('input[name="title"]').first().fill('프로젝트 중간 보고 - 1주차 완료').catch(() => {});
    await page.locator('textarea[name="content"]').first()
      .fill('안녕하세요. 1주차 요구사항 분석 및 설계가 완료되었습니다. 2주차부터 개발을 시작합니다. 진행 중 궁금하신 점 있으시면 언제든지 연락 주세요!').catch(() => {});
    await shot(page, '전문가_쪽지_보내기입력');
    await page.locator('form button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(1500);
    await shot(page, '전문가_쪽지_전송완료');
  }
});

test('EXPERT 05 결제+출금+광고', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 잔액 확인 (마이페이지)
  await page.goto('/my-pages');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_마이페이지_잔액확인');

  // 출금 신청 폼
  await page.goto('/withdrawal/form');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_출금신청_폼');
  await page.locator('input[name="bankName"]').first().fill('카카오뱅크').catch(() => {});
  await page.locator('input[name="accountNumber"]').first().fill('3333-01-7654321').catch(() => {});
  await page.locator('input[name="accountHolder"]').first().fill('홍길동').catch(() => {});
  await page.locator('input[name="amount"]').first().fill('500000').catch(() => {});
  await shot(page, '전문가_출금신청_입력완료');

  // 출금 이력
  await page.goto('/withdrawal/history');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_출금이력').catch(async () => {
    await shot(page, '전문가_출금이력_없음');
  });

  // 광고 경매 페이지
  await page.goto('/ad-auction');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_광고경매_메인');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  await page.waitForTimeout(600);
  await shot(page, '전문가_광고경매_슬롯목록');

  // 광고 입찰
  const bidBtn = page.locator('button:has-text("입찰"), button:has-text("광고 입찰"), .btn-ad-bid').first();
  if (await bidBtn.isVisible().catch(() => false)) {
    await bidBtn.click();
    await page.waitForTimeout(1000);
    await shot(page, '전문가_광고입찰_폼');

    const bidAmount = page.locator('input[name="amount"], input[name="bidAmount"], #bidAmount').first();
    if (await bidAmount.isVisible().catch(() => false)) {
      await bidAmount.fill('80000');
      const adMsg = page.locator('input[name="adMessage"], textarea[name="adMessage"]').first();
      await adMsg.fill('풀스택 개발 10년 - 웹/앱 개발 문의주세요! 빠른 납기 보장').catch(() => {});
      await shot(page, '전문가_광고입찰_입력완료');
      const submitBid = page.locator('button[type="submit"], .btn-submit-bid').first();
      await submitBid.click().catch(() => {});
      await page.waitForTimeout(1500);
      await shot(page, '전문가_광고입찰_완료');
    }
  }

  // 내 광고 입찰 현황
  await page.goto('/ad-auction/my-bids');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_광고내입찰현황');
});

test('EXPERT 06 게시판+리뷰', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 자유게시판 글 작성
  await page.goto('/boards?boardType=FREE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_자유게시판_목록');

  await page.goto('/boards/save-form?boardType=FREE');
  await page.waitForLoadState('domcontentloaded');
  const boardTitle = page.locator('input[name="title"]').first();
  if (await boardTitle.isVisible().catch(() => false)) {
    await boardTitle.fill('Spring Boot 3.x + AI 통합 외주 개발 후기');
    await page.locator('textarea[name="content"]').first()
      .fill('최근 SevMerge에서 Spring AI + Gemini 2.5 Flash를 연동한 프로젝트를 완료했습니다. AI 응답 속도와 품질이 기대 이상이었고, 의뢰인도 매우 만족하셨습니다. 비슷한 프로젝트를 고려하시는 분들께 공유드립니다.').catch(() => {});
    await shot(page, '전문가_게시글_작성중');
    await page.locator('button[type="submit"]').first().click().catch(() => {});
    await page.waitForTimeout(2000);
    await shot(page, '전문가_게시글_작성완료');
  }

  // 리뷰 목록 (내가 받은 리뷰)
  await page.goto('/experts/1');
  await page.waitForLoadState('domcontentloaded');
  await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }));
  await page.waitForTimeout(700);
  await shot(page, '전문가_프로필_리뷰섹션');

  // 리뷰 관리 페이지
  await page.goto('/experts/reviews');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_내리뷰목록').catch(async () => {
    await page.goto('/my-pages');
    await page.waitForLoadState('domcontentloaded');
    await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
    await page.waitForTimeout(700);
    await shot(page, '전문가_마이페이지_리뷰영역');
  });

  // FAQ
  await page.goto('/faq');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_FAQ_목록');

  // 공지사항
  await page.goto('/boards?boardType=NOTICE');
  await page.waitForLoadState('domcontentloaded');
  await shot(page, '전문가_공지사항_목록');
  const noticeItem = page.locator('a[href*="/boards/"]').first();
  if (await noticeItem.isVisible().catch(() => false)) {
    await noticeItem.click();
    await page.waitForLoadState('domcontentloaded');
    await shot(page, '전문가_공지사항_상세');
  }
});
