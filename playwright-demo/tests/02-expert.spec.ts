import { test, expect } from '@playwright/test';
import { login, ACCOUNTS, scrollPage } from './helpers';

test('전문가 시연', async ({ page }) => {
  await login(page, ACCOUNTS.expert1.email, ACCOUNTS.expert1.password);

  // 01 메인
  await page.goto('/main');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);

  // 02 전문가 프로필 작성
  await page.goto('/experts/my/form');
  await page.waitForTimeout(1000);
  await scrollPage(page, 3);
  const career = page.locator('select[name="career"], #careerSelect').first();
  if (await career.isVisible().catch(() => false)) {
    await career.selectOption({ index: 1 });
  }
  const intro = page.locator('textarea[name="intro"], #introTextarea').first();
  if (await intro.isVisible().catch(() => false)) {
    await intro.fill('풀스택 개발자로 5년 경력. Spring Boot, React, AWS 전문. 스타트업 MVP부터 엔터프라이즈 시스템까지 다양한 경험 보유.');
  }
  const github = page.locator('input[name="githubUrl"], #githubInput').first();
  if (await github.isVisible().catch(() => false)) {
    await github.fill('https://github.com/example-dev');
  }
  const stackChips = page.locator('form input[type="checkbox"], .stack-chip input[type="checkbox"]');
  const chipCount = await stackChips.count();
  for (let i = 0; i < Math.min(3, chipCount); i++) {
    if (page.isClosed()) break;
    await stackChips.nth(i).check({ force: true }).catch(() => {});
  }
  if (!page.isClosed()) {
    await page.waitForTimeout(800);
    const submitBtn = page.locator('form button[type="submit"]:not(.btn-logout), .btn-submit').first();
    if (await submitBtn.isVisible().catch(() => false)) {
      await submitBtn.click().catch(() => {});
    }
    await page.waitForTimeout(1500);
  }

  // 03 전문가 대시보드
  await page.goto('/experts/dashboard');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);

  // 04 포트폴리오 목록
  await page.goto('/portfolios');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 05 포트폴리오 등록
  const addBtn = page.locator('a[href*="save-form"], a:has-text("등록"), button:has-text("등록")').first();
  if (await addBtn.isVisible().catch(() => false)) {
    await addBtn.click();
    await page.waitForTimeout(1500);
  } else {
    await page.goto('/portfolios/save-form');
    await page.waitForTimeout(1500);
  }
  const ptitle = page.locator('input[name="title"]').first();
  if (await ptitle.isVisible().catch(() => false)) {
    await ptitle.fill('쇼핑몰 풀스택 개발 프로젝트');
    const pdesc = page.locator('textarea[name="description"], textarea[name="content"]').first();
    if (await pdesc.isVisible().catch(() => false)) {
      await pdesc.fill('React + Spring Boot로 구축한 이커머스 플랫폼. 월 MAU 5천명 운영 중.');
    }
    await page.waitForTimeout(800);
    const psubmit = page.locator('form button[type="submit"]').first();
    if (await psubmit.isVisible().catch(() => false)) {
      await psubmit.click();
      await page.waitForTimeout(1500);
    }
  }

  // 06 프로젝트 목록
  await page.goto('/projects');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);
  const firstProject = page.locator('a[href*="/projects/"]').first();
  if (await firstProject.isVisible().catch(() => false)) {
    await firstProject.click();
    await page.waitForTimeout(1500);
    await scrollPage(page, 3);
  }

  // 07 입찰 제안서
  await page.goto('/bids/save-form?projectId=1');
  await page.waitForTimeout(1000);
  await scrollPage(page, 2);
  const btitle = page.locator('input[name="title"]').first();
  if (await btitle.isVisible().catch(() => false)) {
    await btitle.fill('중고 부품 거래 플랫폼 - 풀스택 개발 제안서');
    const coverLetter = page.locator('textarea[name="coverLetter"]').first();
    await coverLetter.fill('5년 경력 풀스택 개발자입니다. Spring Boot + React로 안정적인 플랫폼 구축 가능합니다.');
    await page.locator('input[name="estimatedDays"]').first().fill('45');
    await page.locator('input[name="proposedPrice"]').first().fill('7500000');
    await page.waitForTimeout(800);
    const bsubmit = page.locator('form button[type="submit"]:not(.btn-logout), .btn-submit').first();
    if (await bsubmit.isVisible().catch(() => false)) {
      await bsubmit.click();
    }
    await page.waitForTimeout(2000);
  } else {
    await page.waitForTimeout(1500);
  }

  // 08 내 입찰 목록
  await page.goto('/bids/my-list');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 09 전문가 목록
  await page.goto('/experts');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);
  const firstExpert = page.locator('a[href*="/experts/"]').first();
  if (await firstExpert.isVisible().catch(() => false)) {
    await firstExpert.click();
    await page.waitForTimeout(1500);
    await scrollPage(page, 3);
  }

  // 10 자유게시판 글 작성
  await page.goto('/boards/save-form?boardType=FREE');
  await page.waitForTimeout(1000);
  const boardTitle = page.locator('input[name="title"]').first();
  if (await boardTitle.isVisible().catch(() => false)) {
    await boardTitle.fill('스타트업 MVP 프로젝트 협업 후기 공유합니다');
    const content = page.locator('textarea[name="content"]').first();
    await content.fill('SevMerge에서 의뢰받은 스타트업 MVP 프로젝트 후기입니다. 플랫폼 덕분에 안전하게 대금 수령도 완료했습니다.');
    await page.waitForTimeout(800);
    const boardSubmit = page.locator('button[type="submit"]').first();
    await boardSubmit.click();
    await page.waitForTimeout(2000);
  }

  // 11 알림
  await page.goto('/notifications');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 12 메시지함
  await page.goto('/messages');
  await page.waitForTimeout(1500);
  await scrollPage(page, 2);

  // 13 마이페이지
  await page.goto('/my-pages');
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);
});
