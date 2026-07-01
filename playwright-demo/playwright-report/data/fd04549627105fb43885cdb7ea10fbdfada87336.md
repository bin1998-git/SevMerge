# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: 07-remaining.spec.ts >> EXPERT 05 출금+광고
- Location: tests/07-remaining.spec.ts:363:5

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
        - /url: /main
        - img "IcodeU" [ref=e5]
      - navigation [ref=e6]:
        - link "제안서 관리" [ref=e7] [cursor=pointer]:
          - /url: /bids/my-list
        - link "주문 관리" [ref=e8] [cursor=pointer]:
          - /url: /bids/my-orders
        - link "수익&정산" [ref=e9] [cursor=pointer]:
          - /url: /payment/my
        - link "평가·리뷰" [ref=e10] [cursor=pointer]:
          - /url: /reviews/my
        - link "정보 수정" [ref=e11] [cursor=pointer]:
          - /url: /experts/my/info-edit
        - link "게시판" [ref=e12] [cursor=pointer]:
          - /url: /boards
        - link "쪽지함" [ref=e13] [cursor=pointer]:
          - /url: /messages
        - link "채팅방" [ref=e14] [cursor=pointer]:
          - /url: /chat/room
        - generic [ref=e16] [cursor=pointer]: 광고
      - generic [ref=e17]:
        - link "알림" [ref=e18] [cursor=pointer]:
          - /url: /notifications
          - img [ref=e19]
        - button "내 메뉴" [ref=e24] [cursor=pointer]:
          - generic [ref=e25]: 홍
  - generic [ref=e27]:
    - generic [ref=e28]:
      - generic [ref=e29]:
        - generic [ref=e30]: 출금 가능 잔액
        - generic [ref=e31]: 4500000원
      - generic [ref=e32]: 💰
    - generic [ref=e33]:
      - heading "💸 출금 신청" [level=1] [ref=e34]
      - generic [ref=e35]:
        - text: ⚠️
        - strong [ref=e36]: 출금 안내
        - text: 출금 신청 후 영업일 1~3일 내에 등록된 계좌로 입금됩니다.
        - text: 최소 출금 금액은
        - strong [ref=e37]: 10,000원
        - text: 이며, 수수료 없이 전액 입금됩니다.
      - generic [ref=e38]:
        - generic [ref=e39]:
          - generic [ref=e40]: 은행
          - combobox [ref=e41]:
            - option "은행을 선택하세요" [disabled] [selected]
            - option "국민은행"
            - option "신한은행"
            - option "우리은행"
            - option "하나은행"
            - option "농협은행"
            - option "기업은행"
            - option "카카오뱅크"
            - option "토스뱅크"
            - option "케이뱅크"
            - option "SC제일은행"
            - option "씨티은행"
        - generic [ref=e42]:
          - generic [ref=e43]: 계좌번호
          - textbox "'-' 없이 숫자만 입력" [ref=e44]
          - paragraph [ref=e45]: "예: 3333012345678"
        - generic [ref=e46]:
          - generic [ref=e47]: 예금주명
          - textbox "예금주 이름을 입력하세요" [ref=e48]
        - generic [ref=e49]:
          - generic [ref=e50]: 출금 금액
          - spinbutton [ref=e51]
          - generic [ref=e52]:
            - button "1만원" [ref=e53] [cursor=pointer]
            - button "3만원" [ref=e54] [cursor=pointer]
            - button "5만원" [ref=e55] [cursor=pointer]
            - button "10만원" [ref=e56] [cursor=pointer]
            - button "전액" [ref=e57] [cursor=pointer]
          - paragraph [ref=e58]:
            - text: "출금 가능 최대:"
            - strong [ref=e59]: 4500000원
        - button "출금 신청하기" [ref=e60] [cursor=pointer]
    - generic [ref=e61]:
      - link "출금내역 보기 →" [ref=e62] [cursor=pointer]:
        - /url: /withdrawal/history
      - text: ·
      - link "수익 & 정산으로 돌아가기" [ref=e63] [cursor=pointer]:
        - /url: /payment/my
  - contentinfo [ref=e64]:
    - generic [ref=e65]:
      - generic [ref=e66]:
        - link "IcodeU" [ref=e67] [cursor=pointer]:
          - /url: /
        - generic [ref=e68]:
          - link "고객센터" [ref=e69] [cursor=pointer]:
            - /url: /supports
          - link "전문가센터" [ref=e70] [cursor=pointer]:
            - /url: "#"
        - navigation [ref=e71]:
          - link "회사소개" [ref=e72] [cursor=pointer]:
            - /url: /supports?tab=about
          - link "인재채용" [ref=e73] [cursor=pointer]:
            - /url: /supports?tab=careers
          - link "제휴 문의" [ref=e74] [cursor=pointer]:
            - /url: /supports?tab=partnership
          - link "공지게시판" [ref=e76] [cursor=pointer]:
            - /url: /boards?boardType=NOTICE
          - link "1:1 문의" [ref=e77] [cursor=pointer]:
            - /url: /boards/save?boardType=INQUIRY
          - link "자주 묻는 질문" [ref=e78] [cursor=pointer]:
            - /url: /policys/faq
      - generic [ref=e79]:
        - generic [ref=e80]:
          - link "이용약관" [ref=e81] [cursor=pointer]:
            - /url: /policys/terms
          - link "개인정보처리방침" [ref=e82] [cursor=pointer]:
            - /url: /policys/privacy
          - link "광고약관" [ref=e83] [cursor=pointer]:
            - /url: /policys/ad
          - link "운영정책" [ref=e84] [cursor=pointer]:
            - /url: /policys/operation
          - generic [ref=e85]: 평일 10:00~18:00 · 주말·공휴일 휴무
        - paragraph [ref=e86]: © 2025 SevMerge. All rights reserved.
  - button "AI 도우미 열기" [ref=e87] [cursor=pointer]:
    - img [ref=e88]
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