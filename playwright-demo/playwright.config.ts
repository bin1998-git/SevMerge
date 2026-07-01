import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  retries: 0,
  workers: 1,
  timeout: 300000,
  reporter: 'html',
  use: {
    baseURL: 'http://localhost:8080',
    headless: false,
    video: 'on',
    screenshot: 'on',
    slowMo: 600,
    viewport: { width: 1440, height: 900 },
    locale: 'ko-KR',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
});
