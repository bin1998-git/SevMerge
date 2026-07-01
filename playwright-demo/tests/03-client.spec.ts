import { test, expect } from '@playwright/test';
import { login, ACCOUNTS, scrollPage, today } from './helpers';

test('의뢰인 시연', async ({ page }) => {
  await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);

  // 01 메인
  await page.goto('/main');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);

  // 02 전문가 목록
  await page.goto('/experts');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 03 전문가 프로필 상세 + 찜
  const firstExpert = page.locator('a[href*="/experts/"]').first();
  if (await firstExpert.isVisible().catch(() => false)) {
    await firstExpert.click();
    await page.waitForTimeout(1500);
    await scrollPage(page, 3);
    const wishBtn = page.locator('button:has-text("찜"), button[data-action="wish"], .wish-btn').first();
    if (await wishBtn.isVisible().catch(() => false)) {
      await wishBtn.click();
      await page.waitForTimeout(1000);
    }
  }

  // 04 프로젝트 목록
  await page.goto('/projects');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 05 프로젝트 상세 + 입찰 목록
  await page.goto('/projects/1');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);

  // 06 입찰 목록
  await page.goto('/bids?projectId=1');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 07 프로젝트 등록
  await page.goto('/projects');
  await page.waitForTimeout(1000);
  const writeBtn = page.locator('a[href*="save-form"], a:has-text("프로젝트 등록"), a:has-text("등록")').first();
  if (await writeBtn.isVisible().catch(() => false)) {
    await writeBtn.click();
    await page.waitForTimeout(1500);
  } else {
    await page.goto('/projects/save-form');
    await page.waitForTimeout(1500);
  }
  const ptitle = page.locator('input[name="title"], #title').first();
  if (await ptitle.isVisible().catch(() => false)) {
    await scrollPage(page, 2);
    await ptitle.fill('AI 기반 고객 상담 챗봇 개발 의뢰');
    const category = page.locator('select[name="category"], #category').first();
    await category.selectOption({ value: 'APP' }).catch(async () => {
      await category.selectOption({ index: 1 }).catch(() => {});
    });
    const description = page.locator('textarea[name="description"], #description').first();
    if (await description.isVisible().catch(() => false)) {
      await description.fill('고객센터 업무 자동화를 위한 AI 챗봇 개발을 의뢰합니다.\n- 자연어 처리 기반 FAQ 자동 응답\n- 카카오톡 채널 연동\n- 관리자 대시보드');
    }
    await page.locator('input[name="budgetMin"], #budgetMin').first().fill('3000').catch(() => {});
    await page.locator('input[name="budgetMax"], #budgetMax').first().fill('8000').catch(() => {});
    await page.locator('input[name="deadline"], #deadline').first().fill(today()).catch(() => {});
    await page.locator('select[name="bidFilter"], #bidFilter').first().selectOption({ value: 'ALL' }).catch(async () => {
      await page.locator('select[name="bidFilter"], #bidFilter').first().selectOption({ index: 0 }).catch(() => {});
    });
    await page.waitForTimeout(800);
    const submitBtn = page.locator('form button[type="submit"]:not(.btn-draft), .btn-primary.btn-lg').first();
    if (await submitBtn.isVisible().catch(() => false)) {
      await submitBtn.click();
    }
    await page.waitForTimeout(2500);
  } else {
    await page.waitForTimeout(2000);
  }

  // 08 임시저장
  await page.goto('/projects/draft');
  await page.waitForTimeout(1500);
  await scrollPage(page, 2);

  // 09 프로젝트 상세
  await page.goto('/projects/2');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 10 자유게시판 목록 + 댓글
  await page.goto('/boards?boardType=FREE');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);
  const firstPost = page.locator('a[href*="/boards/"]').first();
  if (await firstPost.isVisible().catch(() => false)) {
    await firstPost.click();
    await page.waitForTimeout(1500);
    await scrollPage(page, 3);
    const commentInput = page.locator('textarea[name="content"], input[name="content"]').last();
    if (await commentInput.isVisible().catch(() => false)) {
      await commentInput.fill('좋은 정보 감사합니다!');
      const commentSubmit = page.locator('button[type="submit"]').last();
      await commentSubmit.click();
      await page.waitForTimeout(1500);
    }
  }

  // 11 자유게시판 글 작성
  await page.goto('/boards/save-form?boardType=FREE');
  await page.waitForTimeout(1000);
  const boardTitle = page.locator('input[name="title"]').first();
  if (await boardTitle.isVisible().catch(() => false)) {
    await boardTitle.fill('외주 프로젝트 기획서 작성 팁이 있을까요?');
    const boardContent = page.locator('textarea[name="content"]').first();
    await boardContent.fill('처음으로 외주를 맡기려 하는데 기획서 작성이 막막합니다. 전문가분들의 조언 부탁드립니다!');
    await page.waitForTimeout(600);
    const boardSubmit = page.locator('button[type="submit"]').first();
    await boardSubmit.click();
    await page.waitForTimeout(2000);
  }

  // 12 공지사항
  await page.goto('/boards?boardType=NOTICE');
  await page.waitForTimeout(1500);
  await scrollPage(page, 2);
  const firstNotice = page.locator('a[href*="/boards/"]').first();
  if (await firstNotice.isVisible().catch(() => false)) {
    await firstNotice.click();
    await page.waitForTimeout(1500);
    await scrollPage(page, 2);
  }

  // 13 1:1 문의
  await page.goto('/boards/save-form?boardType=INQUIRY');
  await page.waitForTimeout(1000);
  const inquiryTitle = page.locator('input[name="title"]').first();
  if (await inquiryTitle.isVisible().catch(() => false)) {
    await inquiryTitle.fill('프로젝트 마감 후 수정 요청 가능한가요?');
    const inquiryContent = page.locator('textarea[name="content"]').first();
    await inquiryContent.fill('낙찰 완료 후 추가 수정이 필요할 때 어떻게 처리하나요?');
    await page.waitForTimeout(600);
    const inquirySubmit = page.locator('button[type="submit"]').first();
    await inquirySubmit.click();
    await page.waitForTimeout(2000);
  }

  // 14 FAQ
  await page.goto('/faq');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);
  const faqItem = page.locator('.faq-item, .accordion-item, .faq-question').first();
  if (await faqItem.isVisible().catch(() => false)) {
    await faqItem.click();
    await page.waitForTimeout(800);
  }

  // 15 알림
  await page.goto('/notifications');
  await page.waitForTimeout(1500);
  await scrollPage(page, 2);

  // 16 마이페이지
  await page.goto('/my-pages');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);
});
