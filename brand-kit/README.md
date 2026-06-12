# IcodeU Brand Kit

Handoff package for the **IcodeU** brand (development outsourcing agency).
Built so another agent — e.g. **Claude cowork** — can apply the brand consistently.

## How to use it with Claude cowork / any coding agent

1. **Copy `CLAUDE.md` into your project root.** Agents read a root `CLAUDE.md`
   automatically and will follow the color/type/logo rules in every reply.
2. **Drop the `assets/` folder** into your project (e.g. `public/brand/`).
   Reference the SVGs directly — they're pure vector, no fonts required.
3. **Use the snippets** in `snippet/` to render the logo in code without any
   asset files at all.

## What's inside

| Path | What |
|---|---|
| `CLAUDE.md` | Machine-readable brand spec (tokens, fonts, logo rules). The important one. |
| `assets/*.svg` | Logo system — primary, horizontal, wordmark, symbol, app-icon, reversed/mono. Font-independent vectors. |
| `assets/*.png` | App icon 512 + favicons 64/32/16. |
| `snippet/IcodeULogo.jsx` | Drop-in React component. Props: `variant`, `size`, `mode`, `color`. |
| `snippet/logo.html` | Copy-paste HTML + inline CSS version. |
| `_preview.html` | Open in a browser to see every asset on one page. |

## Quick reference

- **Primary blue** `#2D6BFF` · **Ink** `#0C1424` · **Sky (on dark)** `#6BA0FF`
- **Fonts** — Silkscreen (logo), Space Grotesk (UI), JetBrains Mono (labels/code)
- **Logo** — `I` & `U` are blue, `code` is ink. Tagline: `DEV AGENCY`.

See `CLAUDE.md` for the full spec.
