import { Page } from '@playwright/test';

export const BASE = 'http://localhost:8080';

export const ACCOUNTS = {
  admin:  { email: 'admin@sevmerge.com',   password: '1234', name: '최관리' },
  client: { email: 'client01@sevmerge.com', password: '1234', name: '김의뢰' },
  expert1: { email: 'expert01@sevmerge.com', password: '1234', name: '홍길동' },
  expert2: { email: 'expert02@sevmerge.com', password: '1234', name: '김디자' },
};

export async function login(page: Page, email: string, password: string) {
  await page.goto('/login');
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL(/\/(main|admin\/main|intro)/, { timeout: 8000 }).catch(() => {});
}

// 페이지 전체 천천히 스크롤 후 상단 복귀
export async function scrollPage(page: Page, steps = 3) {
  const height = await page.evaluate(() => document.body.scrollHeight);
  const step = Math.floor(height / steps);
  for (let i = 1; i <= steps; i++) {
    await page.evaluate((y) => window.scrollTo({ top: y, behavior: 'smooth' }), step * i);
    await page.waitForTimeout(800);
  }
  await page.evaluate(() => window.scrollTo({ top: 0, behavior: 'smooth' }));
  await page.waitForTimeout(500);
}

export async function logout(page: Page) {
  await page.goto('/logout', { method: 'POST' } as any).catch(() => {});
  await page.goto('/login');
}

export function today() {
  const d = new Date();
  d.setDate(d.getDate() + 30);
  return d.toISOString().split('T')[0];
}
