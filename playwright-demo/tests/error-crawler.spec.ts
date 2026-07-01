import { test, expect, Page, Request, Response } from '@playwright/test';

const BASE = 'http://localhost:8080';

const ACCOUNTS = {
  admin:  { email: 'admin@sevmerge.com',   password: '1234' },
  client: { email: 'client01@sevmerge.com', password: '1234' },
  expert: { email: 'expert01@sevmerge.com', password: '1234' },
};

interface ErrorEntry {
  url: string;
  status: number;
  type: 'navigation' | 'ajax';
  role: string;
  console?: string;
}

const errors: ErrorEntry[] = [];

async function loginAs(page: Page, role: keyof typeof ACCOUNTS) {
  await page.goto(`${BASE}/login`);
  await page.fill('input[name="email"]', ACCOUNTS[role].email);
  await page.fill('input[name="password"]', ACCOUNTS[role].password);
  await page.click('button[type="submit"]');
  await page.waitForURL(/\/(main|admin\/main|intro|dashboard)/, { timeout: 8000 }).catch(() => {});
}

async function visitPage(page: Page, path: string, role: string): Promise<{ status: number; consoleErrors: string[] }> {
  const consoleErrors: string[] = [];
  let navStatus = 200;

  page.on('console', msg => {
    if (msg.type() === 'error') consoleErrors.push(msg.text());
  });

  page.on('response', (res: Response) => {
    const s = res.status();
    const url = res.url();
    if (s >= 400 && !url.includes('/notifications/subscribe') && !url.includes('/favicon')) {
      errors.push({ url, status: s, type: 'ajax', role, console: undefined });
    }
  });

  const res = await page.goto(`${BASE}${path}`, { waitUntil: 'domcontentloaded', timeout: 15000 }).catch(() => null);
  if (res) {
    navStatus = res.status();
  }

  // body 텍스트로 500 감지 (Whitelabel Error Page 등)
  const bodyText = await page.evaluate(() => document.body?.innerText ?? '').catch(() => '');
  const has500Body = bodyText.includes('500') || bodyText.includes('Internal Server Error') || bodyText.includes('Whitelabel');
  if (has500Body && navStatus < 500) navStatus = 500;

  await page.waitForTimeout(500);
  return { status: navStatus, consoleErrors };
}

// 비로그인 접근 가능한 공개 페이지
const PUBLIC_PAGES = [
  '/',
  '/intro.mustache',  // skip – direct
  '/login',
  '/join',
  '/join-start',
  '/join-role',
  '/find-account',
  '/projects',
  '/experts',
  '/boards',
  '/policys/terms',
  '/policys/privacy',
  '/policys/operation',
  '/policys/faq',
  '/policys/ad',
  '/supports',
  '/faqs/save',
];

// 클라이언트 전용 페이지
const CLIENT_PAGES = [
  '/projects/save-form',
  '/projects/draft',
  '/my-pages',
  '/dashboard',
  '/messages',
  '/messages/send',
  '/notifications',
  '/charge',
  '/payment',
  '/history',
  '/bids/my-orders',
  '/chat/room',
];

// 전문가 전용 페이지
const EXPERT_PAGES = [
  '/expert-profile',
  '/my-pages',
  '/dashboard',
  '/bids',
  '/bids/my-list',
  '/bids/save-form',
  '/portfolios',
  '/portfolios/save',
  '/reviews/my',
  '/reviews/save',
  '/ad-auction',
  '/ad-auction/my-bids',
  '/advertisements/form',
  '/advertisements/my',
  '/withdrawal',
  '/messages',
  '/notifications',
  '/chat/room',
];

// 관리자 전용 페이지
const ADMIN_PAGES = [
  '/admin/main',
  '/admin/members',
  '/admin/experts',
  '/admin/experts/grade',
  '/admin/experts/withdraw',
  '/admin/projects',
  '/admin/boards',
  '/admin/comments',
  '/admin/reports',
  '/admin/notices',
  '/admin/notices/write',
  '/admin/inquiry',
  '/admin/partnerships',
  '/admin/ad-slots',
  '/admin/advertisements',
  '/admin/blacklists',
  '/admin/refund-applications',
  '/admin/revenue',
];

test.describe('Error Crawler', () => {
  test('PUBLIC pages - no login', async ({ page }) => {
    console.log('\n=== PUBLIC PAGES ===');
    for (const p of PUBLIC_PAGES) {
      const { status, consoleErrors } = await visitPage(page, p, 'public');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${p}`);
      if (status >= 400) {
        errors.push({ url: `${BASE}${p}`, status, type: 'navigation', role: 'public' });
      }
      if (consoleErrors.length > 0) {
        console.log(`   Console errors: ${consoleErrors.join(' | ')}`);
      }
    }
  });

  test('CLIENT pages', async ({ page }) => {
    await loginAs(page, 'client');
    console.log('\n=== CLIENT PAGES ===');
    for (const p of CLIENT_PAGES) {
      const { status, consoleErrors } = await visitPage(page, p, 'client');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${p}`);
      if (status >= 400) {
        errors.push({ url: `${BASE}${p}`, status, type: 'navigation', role: 'client' });
      }
      if (consoleErrors.length > 0) {
        console.log(`   Console errors: ${consoleErrors.join(' | ')}`);
      }
    }
  });

  test('EXPERT pages', async ({ page }) => {
    await loginAs(page, 'expert');
    console.log('\n=== EXPERT PAGES ===');
    for (const p of EXPERT_PAGES) {
      const { status, consoleErrors } = await visitPage(page, p, 'expert');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${p}`);
      if (status >= 400) {
        errors.push({ url: `${BASE}${p}`, status, type: 'navigation', role: 'expert' });
      }
      if (consoleErrors.length > 0) {
        console.log(`   Console errors: ${consoleErrors.join(' | ')}`);
      }
    }
  });

  test('ADMIN pages', async ({ page }) => {
    await loginAs(page, 'admin');
    console.log('\n=== ADMIN PAGES ===');
    for (const p of ADMIN_PAGES) {
      const { status, consoleErrors } = await visitPage(page, p, 'admin');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${p}`);
      if (status >= 400) {
        errors.push({ url: `${BASE}${p}`, status, type: 'navigation', role: 'admin' });
      }
      if (consoleErrors.length > 0) {
        console.log(`   Console errors: ${consoleErrors.join(' | ')}`);
      }
    }
  });

  test('DYNAMIC pages - with real IDs', async ({ page }) => {
    await loginAs(page, 'client');
    console.log('\n=== DYNAMIC PAGES (real IDs) ===');

    // 프로젝트 목록에서 첫 번째 ID 추출
    await page.goto(`${BASE}/projects`, { waitUntil: 'domcontentloaded' });
    const projectLink = await page.locator('a[href^="/project/"], a[href^="/projects/"]').first().getAttribute('href').catch(() => null);
    if (projectLink) {
      const { status } = await visitPage(page, projectLink, 'client');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${projectLink}`);
      if (status >= 400) errors.push({ url: `${BASE}${projectLink}`, status, type: 'navigation', role: 'client' });
    }

    // 게시판 목록에서 첫 번째 ID 추출
    await page.goto(`${BASE}/boards`, { waitUntil: 'domcontentloaded' });
    const boardLink = await page.locator('a[href^="/boards/"]').first().getAttribute('href').catch(() => null);
    if (boardLink && !boardLink.includes('save') && !boardLink.includes('edit')) {
      const { status } = await visitPage(page, boardLink, 'client');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${boardLink}`);
      if (status >= 400) errors.push({ url: `${BASE}${boardLink}`, status, type: 'navigation', role: 'client' });
    }

    // 전문가 프로필 목록
    await page.goto(`${BASE}/experts`, { waitUntil: 'domcontentloaded' });
    const expertLink = await page.locator('a[href*="/members/"], a[href^="/expert-profile/"]').first().getAttribute('href').catch(() => null);
    if (expertLink) {
      const { status } = await visitPage(page, expertLink, 'client');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${expertLink}`);
      if (status >= 400) errors.push({ url: `${BASE}${expertLink}`, status, type: 'navigation', role: 'client' });
    }

    // 전문가로 전환해서 포트폴리오 확인
    await loginAs(page, 'expert');
    await page.goto(`${BASE}/portfolios`, { waitUntil: 'domcontentloaded' });
    const portfolioLink = await page.locator('a[href^="/portfolios/"]').first().getAttribute('href').catch(() => null);
    if (portfolioLink && !portfolioLink.includes('save') && !portfolioLink.includes('edit')) {
      const { status } = await visitPage(page, portfolioLink, 'expert');
      const icon = status >= 500 ? '🔴' : status >= 400 ? '🟡' : '✅';
      console.log(`${icon} [${status}] ${portfolioLink}`);
      if (status >= 400) errors.push({ url: `${BASE}${portfolioLink}`, status, type: 'navigation', role: 'expert' });
    }
  });

  test.afterAll(async () => {
    console.log('\n\n========================================');
    console.log('        ERROR SUMMARY REPORT');
    console.log('========================================');
    if (errors.length === 0) {
      console.log('✅ No 4xx/5xx errors found!');
    } else {
      const fiveHundreds = errors.filter(e => e.status >= 500);
      const fourHundreds = errors.filter(e => e.status >= 400 && e.status < 500);

      if (fiveHundreds.length > 0) {
        console.log(`\n🔴 5xx ERRORS (${fiveHundreds.length}):`);
        fiveHundreds.forEach(e => console.log(`   [${e.status}] ${e.type.toUpperCase()} as ${e.role}: ${e.url}`));
      }
      if (fourHundreds.length > 0) {
        console.log(`\n🟡 4xx ERRORS (${fourHundreds.length}):`);
        fourHundreds.forEach(e => console.log(`   [${e.status}] ${e.type.toUpperCase()} as ${e.role}: ${e.url}`));
      }
    }
    console.log('========================================\n');
  });
});
