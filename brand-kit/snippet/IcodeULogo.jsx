// IcodeU logo — drop-in React component.
// Font-independent: the symbol & wordmark are drawn from an integer pixel grid,
// so they render identically everywhere with no web-font dependency.
// The optional tagline uses the system monospace stack.
//
//   import { IcodeULogo } from "./IcodeULogo";
//   <IcodeULogo variant="primary" size={48} />
//   <IcodeULogo variant="symbol" size={32} mode="dark" />
//
// variant: "primary" | "horizontal" | "wordmark" | "symbol"
// mode:    "light" (default) | "dark" | "mono"
// size:    pixel height of the mark/wordmark
// color:   override accent (default #2D6BFF)

import React from "react";

const G = {
  I: ["#####", "..#..", "..#..", "..#..", "..#..", "..#..", "#####"],
  U: ["#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."],
  c: [".....", ".....", ".###.", "#....", "#....", "#....", ".###."],
  o: [".....", ".....", ".###.", "#...#", "#...#", "#...#", ".###."],
  d: ["....#", "....#", ".####", "#...#", "#...#", "#...#", ".####"],
  e: [".....", ".....", ".###.", "#...#", "####.", "#....", ".###."],
};

function Grid({ glyph, cell, color, radius = 0 }) {
  const w = glyph[0].length;
  const cells = [];
  for (let r = 0; r < glyph.length; r++)
    for (let c = 0; c < w; c++)
      cells.push(
        <div key={r + "_" + c} style={{
          width: cell, height: cell, borderRadius: radius,
          background: glyph[r][c] === "#" ? color : "transparent",
        }} />
      );
  return (
    <div style={{ display: "grid", gridTemplateColumns: `repeat(${w}, ${cell}px)`, gridAutoRows: `${cell}px` }}>
      {cells}
    </div>
  );
}

function IU({ cell, color, radius = 0 }) {
  return (
    <div style={{ display: "flex", gap: cell, alignItems: "flex-start" }}>
      <Grid glyph={G.I} cell={cell} color={color} radius={radius} />
      <Grid glyph={G.U} cell={cell} color={color} radius={radius} />
    </div>
  );
}

function Wordmark({ cell, iColor, codeColor, radius = 0 }) {
  const order = [["I", iColor], ["c", codeColor], ["o", codeColor], ["d", codeColor], ["e", codeColor], ["U", iColor]];
  return (
    <div style={{ display: "flex", gap: cell, alignItems: "flex-start" }}>
      {order.map(([g, col], i) => <Grid key={i} glyph={G[g]} cell={cell} color={col} radius={radius} />)}
    </div>
  );
}

export function IcodeULogo({ variant = "primary", size = 48, mode = "light", color = "#2D6BFF" }) {
  const ACCENT = color, INK = "#0C1424", SKY = "#6BA0FF", WHITE = "#FFFFFF";
  let iC, codeC, tagC;
  if (mode === "dark") { iC = SKY; codeC = WHITE; tagC = "rgba(255,255,255,0.6)"; }
  else if (mode === "mono") { iC = INK; codeC = INK; tagC = "#5A6B85"; }
  else { iC = ACCENT; codeC = INK; tagC = "#5A6B85"; }

  if (variant === "symbol") {
    const cell = size / 7; // glyph is 7 cells tall
    const tileBg = mode === "dark" ? INK : ACCENT;
    const markC = mode === "dark" ? SKY : WHITE;
    const pad = cell * 2.5;
    return (
      <div style={{
        display: "inline-flex", alignItems: "center", justifyContent: "center",
        background: tileBg, borderRadius: size * 0.26, padding: pad,
      }}>
        <IU cell={cell} color={markC} radius={Math.max(1, cell * 0.12)} />
      </div>
    );
  }

  const cell = size / 7;
  const wordmark = <Wordmark cell={cell} iColor={iC} codeColor={codeC} radius={Math.max(0, cell * 0.08)} />;

  if (variant === "wordmark") return wordmark;

  if (variant === "horizontal") {
    return (
      <div style={{ display: "inline-flex", alignItems: "center", gap: size * 0.4 }}>
        <IcodeULogo variant="symbol" size={size} mode={mode} color={color} />
        {wordmark}
      </div>
    );
  }

  // primary: wordmark + DEV AGENCY tagline
  return (
    <div style={{ display: "inline-flex", flexDirection: "column", alignItems: "center", gap: size * 0.32 }}>
      {wordmark}
      <div style={{
        fontFamily: "'JetBrains Mono', ui-monospace, SFMono-Regular, Menlo, monospace",
        fontSize: size * 0.26, fontWeight: 500, letterSpacing: size * 0.18,
        paddingLeft: size * 0.18, color: tagC, whiteSpace: "nowrap",
      }}>DEV AGENCY</div>
    </div>
  );
}

export default IcodeULogo;
