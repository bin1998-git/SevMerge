# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: 07-remaining.spec.ts >> CLIENT 05b 일대일문의+FAQ
- Location: tests/07-remaining.spec.ts:40:5

# Error details

```
Test timeout of 300000ms exceeded.
```

```
Error: page.waitForTimeout: Target page, context or browser has been closed
```

# Page snapshot

```yaml
- generic [active] [ref=e1]:
  - banner [ref=e2]:
    - generic [ref=e3]:
      - link "IcodeU" [ref=e4] [cursor=pointer]:
        - /url: /
        - img "IcodeU" [ref=e5]
      - navigation [ref=e6]:
        - link "프로젝트" [ref=e7] [cursor=pointer]:
          - /url: /projects
        - link "전문가" [ref=e8] [cursor=pointer]:
          - /url: /experts
        - link "게시판" [ref=e9] [cursor=pointer]:
          - /url: /boards
      - generic [ref=e10]:
        - link "로그인" [ref=e11] [cursor=pointer]:
          - /url: /login
        - link "회원가입" [ref=e12] [cursor=pointer]:
          - /url: /join-start
  - generic [ref=e14]:
    - generic [ref=e15]: "500"
    - generic [ref=e16]: 서버 오류가 발생했습니다
    - generic [ref=e17]: 잠시 후 다시 시도해 주세요.
    - link "홈으로 돌아가기" [ref=e18] [cursor=pointer]:
      - /url: /
  - contentinfo [ref=e19]:
    - generic [ref=e20]:
      - generic [ref=e21]:
        - link "IcodeU" [ref=e22] [cursor=pointer]:
          - /url: /
        - generic [ref=e23]:
          - link "고객센터" [ref=e24] [cursor=pointer]:
            - /url: /supports
          - link "전문가센터" [ref=e25] [cursor=pointer]:
            - /url: "#"
        - navigation [ref=e26]:
          - link "회사소개" [ref=e27] [cursor=pointer]:
            - /url: /supports?tab=about
          - link "인재채용" [ref=e28] [cursor=pointer]:
            - /url: /supports?tab=careers
          - link "제휴 문의" [ref=e29] [cursor=pointer]:
            - /url: /supports?tab=partnership
          - link "공지게시판" [ref=e31] [cursor=pointer]:
            - /url: /boards?boardType=NOTICE
          - link "1:1 문의" [ref=e32] [cursor=pointer]:
            - /url: /boards/save?boardType=INQUIRY
          - link "자주 묻는 질문" [ref=e33] [cursor=pointer]:
            - /url: /policys/faq
      - generic [ref=e34]:
        - generic [ref=e35]:
          - link "이용약관" [ref=e36] [cursor=pointer]:
            - /url: /policys/terms
          - link "개인정보처리방침" [ref=e37] [cursor=pointer]:
            - /url: /policys/privacy
          - link "광고약관" [ref=e38] [cursor=pointer]:
            - /url: /policys/ad
          - link "운영정책" [ref=e39] [cursor=pointer]:
            - /url: /policys/operation
          - generic [ref=e40]: 평일 10:00~18:00 · 주말·공휴일 휴무
        - paragraph [ref=e41]: © 2025 SevMerge. All rights reserved.
  - button "AI 도우미 열기" [ref=e42] [cursor=pointer]:
    - img [ref=e43]
  - generic:
    - generic:
      - generic:
        - img
      - generic:
        - generic: 코드요 AI 도우미
        - generic: 온라인
      - button "닫기":
        - img
    - generic:
      - generic:
        - generic: 안녕하세요! 코드요 이용 중 궁금한 점이나 프로젝트 등록을 도와드릴게요. 😊
    - generic:
      - generic: 프로젝트 등록 방법
      - generic: 입찰 참여 방법
    - generic:
      - textbox "질문을 입력하세요"
      - button "전송":
        - img
```

# Test source

```ts
  1   | /**
  2   |  * 07-remaining.spec.ts
  3   |  * C024부터 이어서 - 미완료 CLIENT + 전체 EXPERT
  4   |  */
  5   | import { test, Page } from '@playwright/test';
  6   | import * as path from 'path';
  7   | import * as fs from 'fs';
  8   | 
  9   | const OUT_DIR = '/Users/park/Desktop/SevMerge shot';
  10  | fs.mkdirSync(OUT_DIR, { recursive: true });
  11  | 
  12  | const ACCOUNTS = {
  13  |   client:  { email: 'client01@sevmerge.com', password: '1234' },
  14  |   expert1: { email: 'expert01@sevmerge.com', password: '1234' },
  15  |   expert2: { email: 'expert02@sevmerge.com', password: '1234' },
  16  | };
  17  | 
  18  | let idx = 23; // start from C024
  19  | 
  20  | async function shot(page: Page, name: string) {
  21  |   idx++;
  22  |   const file = path.join(OUT_DIR, `C${String(idx).padStart(3,'0')}_${name}.png`);
> 23  |   await page.waitForTimeout(600);
      |              ^ Error: page.waitForTimeout: Target page, context or browser has been closed
  24  |   await page.screenshot({ path: file, fullPage: false });
  25  |   console.log(`✅ ${file}`);
  26  | }
  27  | 
  28  | async function login(page: Page, email: string, password: string) {
  29  |   await page.goto('/login');
  30  |   await page.waitForLoadState('domcontentloaded');
  31  |   await page.fill('input[name="email"]', email);
  32  |   await page.fill('input[name="password"]', password);
  33  |   await page.click('button[type="submit"]');
  34  |   await page.waitForTimeout(2000);
  35  | }
  36  | 
  37  | // ══════════════════════════════════════════
  38  | // CLIENT 05 나머지 - 일대일문의 작성
  39  | // ══════════════════════════════════════════
  40  | test('CLIENT 05b 일대일문의+FAQ', async ({ page }) => {
  41  |   await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);
  42  | 
  43  |   // 일대일문의 작성폼
  44  |   await page.goto('/boards/save-form?boardType=INQUIRY');
  45  |   await page.waitForLoadState('domcontentloaded');
  46  |   await shot(page, '일대일문의_작성폼');
  47  |   await page.locator('input[name="title"]').first().fill('낙찰 후 전문가 연락 없는 경우 처리 방법').catch(() => {});
  48  |   await page.locator('textarea[name="content"]').first()
  49  |     .fill('낙찰 처리 후 2일이 지났는데 전문가로부터 연락이 없습니다. 에스크로 결제된 상태인데 어떻게 처리해야 하나요?').catch(() => {});
  50  |   await shot(page, '일대일문의_작성중');
  51  |   await page.locator('button[type="submit"]').first().click().catch(() => {});
  52  |   await page.waitForTimeout(2000);
  53  |   await shot(page, '일대일문의_제출완료');
  54  | 
  55  |   // FAQ
  56  |   await page.goto('/faq');
  57  |   await page.waitForLoadState('domcontentloaded');
  58  |   await shot(page, 'FAQ_목록');
  59  |   const faqItem = page.locator('.faq-item, .accordion-item, dt, [data-toggle], .faq-question').first();
  60  |   if (await faqItem.isVisible().catch(() => false)) {
  61  |     await faqItem.click();
  62  |     await page.waitForTimeout(800);
  63  |     await shot(page, 'FAQ_아코디언_펼침');
  64  |   }
  65  | });
  66  | 
  67  | // ══════════════════════════════════════════
  68  | // CLIENT 06 - 결제 충전
  69  | // ══════════════════════════════════════════
  70  | test('CLIENT 06 결제충전+에스크로', async ({ page }) => {
  71  |   await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);
  72  | 
  73  |   // 충전 폼
  74  |   await page.goto('/charge/form');
  75  |   await page.waitForLoadState('domcontentloaded');
  76  |   await shot(page, '결제_충전폼_상단');
  77  |   await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  78  |   await page.waitForTimeout(700);
  79  |   await shot(page, '결제_충전폼_하단');
  80  | 
  81  |   // 금액 버튼
  82  |   const chargeBtn = page.locator('.charge-amount-btn, button:has-text("50,000"), button:has-text("10만"), button:has-text("100,000"), input[value="100000"]').first();
  83  |   if (await chargeBtn.isVisible().catch(() => false)) {
  84  |     await chargeBtn.click();
  85  |     await page.waitForTimeout(600);
  86  |     await shot(page, '결제_충전금액_선택');
  87  |   }
  88  | 
  89  |   // 에스크로 내역
  90  |   await page.goto('/payment/my');
  91  |   await page.waitForLoadState('domcontentloaded');
  92  |   await shot(page, '결제_에스크로_내역');
  93  |   await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 2, behavior: 'smooth' }));
  94  |   await page.waitForTimeout(600);
  95  |   await shot(page, '결제_에스크로_내역_하단');
  96  | 
  97  |   // 충전 이력
  98  |   await page.goto('/charge/history');
  99  |   await page.waitForLoadState('domcontentloaded');
  100 |   await shot(page, '결제_충전이력');
  101 | });
  102 | 
  103 | // ══════════════════════════════════════════
  104 | // CLIENT 07 - 의뢰인 마이페이지 + 알림 + 쪽지
  105 | // ══════════════════════════════════════════
  106 | test('CLIENT 07 마이페이지+알림+쪽지', async ({ page }) => {
  107 |   await login(page, ACCOUNTS.client.email, ACCOUNTS.client.password);
  108 | 
  109 |   // 마이페이지
  110 |   await page.goto('/my-pages');
  111 |   await page.waitForLoadState('domcontentloaded');
  112 |   await shot(page, '의뢰인_마이페이지_상단');
  113 |   await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight / 3, behavior: 'smooth' }));
  114 |   await page.waitForTimeout(700);
  115 |   await shot(page, '의뢰인_마이페이지_중간');
  116 |   await page.evaluate(() => window.scrollTo({ top: document.body.scrollHeight * 2 / 3, behavior: 'smooth' }));
  117 |   await page.waitForTimeout(700);
  118 |   await shot(page, '의뢰인_마이페이지_하단');
  119 | 
  120 |   // 알림
  121 |   await page.goto('/notifications');
  122 |   await page.waitForLoadState('domcontentloaded');
  123 |   await shot(page, '알림목록_의뢰인');
```