# IcodeU — Brand Spec (Agent Handoff)

> Single source of truth for the **IcodeU** brand. Any agent (Claude cowork, etc.)
> building UI, sites, decks, or assets for IcodeU must follow this file.
> IcodeU is a **development outsourcing agency** ("개발 외주 대행"). Concept: **"I code U"** —
> *I (we) code for U (you)*.

---

## 1. Logo

The logo is a **pixel wordmark** `IcodeU` with the **`I` and `U` in Electric Blue** and
`code` in Ink, plus a `DEV AGENCY` monospace tagline.

**Use the SVGs in `assets/` — never re-typeset the logo by hand.**

| File | When to use |
|---|---|
| `assets/logo-primary.svg` | Default. Wordmark + DEV AGENCY tagline, on light bg. |
| `assets/logo-primary-reversed.svg` | Same, on dark bg (Ink or photo). |
| `assets/logo-horizontal.svg` | App-tile symbol + wordmark in a row. Headers, nav bars. |
| `assets/logo-horizontal-reversed.svg` | Horizontal on dark bg. |
| `assets/wordmark.svg` | Wordmark only, no tagline. |
| `assets/symbol.svg` | `IU` mark only (blue). Compact spaces. |
| `assets/symbol-ink.svg` / `symbol-white.svg` | Mono mark for single-color contexts. |
| `assets/app-icon.svg` | Rounded blue tile + white IU. App / favicon source. |
| `assets/app-icon-dark.svg` | Ink tile + sky-blue IU. |

PNG raster (already exported): `app-icon-512.png`, `app-icon-512-dark.png`,
`app-icon-512-light.png`, `favicon-64/32/16.png`.

All logo SVGs are **pure `<rect>` vectors** (no font dependency) except the tagline,
which uses generic `monospace`. They stay crisp at any size — integer-pixel grid.

### Logo rules
- **Do** keep `I` + `U` blue and `code` ink. This is the brand's signature.
- **Do** keep clear space ≥ the height of the `I` glyph on all sides.
- **Do** use the reversed variants on dark backgrounds (never the light logo on Ink).
- **Don't** recolor `code`, add gradients/shadows/outlines, rotate, or condense.
- **Don't** rebuild the wordmark in a different font — use the provided SVG.
- Min size: wordmark height ≥ 16px; symbol/favicon ≥ 16px.

---

## 2. Color tokens

| Token | Hex | Role |
|---|---|---|
| `--brand-blue` | `#2D6BFF` | Primary. `I`/`U`, CTAs, links, accents. |
| `--brand-blue-deep` | `#1846C9` | Pressed / gradient end / emphasis. |
| `--brand-sky` | `#6BA0FF` | Blue on dark UI (logo & accents on Ink). |
| `--brand-ink` | `#0C1424` | Primary text, dark surfaces, `code` wordmark. |
| `--brand-slate` | `#33415C` | Body text on light. |
| `--brand-muted` | `#5A6B85` | Secondary text, labels. |
| `--brand-mist` | `#EBEEF4` | App background. |
| `--brand-line` | `#E2E7F0` | Borders / dividers. |
| `--brand-white` | `#FFFFFF` | Surfaces, reversed text. |

```css
:root{
  --brand-blue:#2D6BFF; --brand-blue-deep:#1846C9; --brand-sky:#6BA0FF;
  --brand-ink:#0C1424; --brand-slate:#33415C; --brand-muted:#5A6B85;
  --brand-mist:#EBEEF4; --brand-line:#E2E7F0; --brand-white:#FFFFFF;
}
```

---

## 3. Typography

| Family | Role | Google Fonts |
|---|---|---|
| **Silkscreen** | Logo wordmark only (do not use for body). | `Silkscreen:wght@400;700` |
| **Space Grotesk** | Headings & UI / body. | `Space+Grotesk:wght@400;500;600;700` |
| **JetBrains Mono** | Tagline, code, labels, metadata. | `JetBrains+Mono:wght@400;500;700` |

```html
<link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500;700&family=Silkscreen:wght@400;700&display=swap" rel="stylesheet">
```

Type rules: headings Space Grotesk 600–700, letter-spacing `-0.02em`.
Tagline / eyebrow labels = JetBrains Mono, uppercase, letter-spacing `0.2em`.

---

## 4. Voice & motifs
- Tagline: **DEV AGENCY**. Positioning line: *"We code for you."*
- Visual motif: **pixel / dot grid** (the logo's integer-pixel language). Backgrounds
  may use a subtle dotted grid: `radial-gradient(#e7ebf2 1.2px, transparent 1.3px)` at `16px`.
- Tone: trustworthy, technical, developer-native. Minimal. No emoji in formal UI.

---

## 5. Files in this kit
```
brand-kit/
├─ CLAUDE.md                 ← this spec (drop into a project root to bind the brand)
├─ README.md                 ← human-facing index
├─ snippet/IcodeULogo.jsx    ← drop-in React component (font-independent)
├─ snippet/logo.html         ← copy-paste HTML/CSS version
├─ _preview.html             ← visual contact sheet of all assets
└─ assets/                   ← SVG (vector) + PNG (raster) logo files
```
