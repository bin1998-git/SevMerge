import { test, expect } from '@playwright/test';
import { login, ACCOUNTS, scrollPage } from './helpers';

test('관리자 시연', async ({ page }) => {
  // 로그인 1회
  await login(page, ACCOUNTS.admin.email, ACCOUNTS.admin.password);

  // 01 메인 대시보드
  await page.goto('/admin/main');
  await expect(page).toHaveURL(/admin\/main/);
  await page.waitForTimeout(1500);
  await scrollPage(page, 4);

  // 02 회원 목록
  await page.goto('/admin/members');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 03 전문가 승인 관리
  await page.goto('/admin/experts');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 04 프로젝트 관리
  await page.goto('/admin/projects');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 05 게시판 관리
  await page.goto('/boards?boardType=FREE');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 06 공지사항
  await page.goto('/admin/notices');
  await page.waitForTimeout(1200);
  await scrollPage(page, 2);
  const writeBtn = page.locator('a[href*="write"], button:has-text("작성"), a:has-text("공지 작성"), a:has-text("등록")').first();
  if (await writeBtn.isVisible().catch(() => false)) {
    await writeBtn.click();
    await page.waitForTimeout(800);
    const titleInput = page.locator('input[name="title"]').first();
    if (await titleInput.isVisible().catch(() => false)) {
      await titleInput.fill('[공지] 서비스 점검 안내 (2026.07.01)');
      const contentInput = page.locator('textarea[name="content"]').first();
      await contentInput.fill('안녕하세요. SevMerge 운영팀입니다.\n서비스 안정화 작업으로 인해 잠시 점검이 예정되어 있습니다.\n이용에 불편을 드려 죄송합니다.');
      await page.waitForTimeout(800);
      const submitBtn = page.locator('form button[type="submit"]:not(.pd-logout), .btn-submit').first();
      if (await submitBtn.isVisible().catch(() => false)) {
        await submitBtn.click();
      }
      await page.waitForTimeout(1500);
    }
  } else {
    await page.waitForTimeout(1000);
  }

  // 07 블랙리스트
  await page.goto('/admin/blacklists');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 08 에스크로
  await page.goto('/admin/escrow');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 09 광고 관리
  await page.goto('/admin/advertisements');
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);

  // 10 제휴 관리
  await page.goto('/admin/partnership').catch(() => {});
  await page.waitForTimeout(1500);
  await scrollPage(page, 3);
});
