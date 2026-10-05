import { setConsoleFunction } from "three";

// Harmless three.js warnings we can't fix at the source, so they're kept out of the customer's console:
// - React Three Fiber 9 still creates a THREE.Clock for its frame loop (deprecated since three r183).
// - On Windows, ANGLE compiles shaders through Direct3D, whose compiler reports precision notes
//   ("warning X4122") on three's own shader chunks. The program links fine; it's only a log.
// Anything else from three.js goes to the console untouched, so real shader errors still show.
function isHarmless(type, message, params) {
  if (type !== "warn") return false;
  if (message.startsWith("THREE.Clock: This module has been deprecated")) return true;
  if (message === "THREE.WebGLProgram: Program Info Log:") {
    const lines = String(params[0] ?? "").split("\n").filter((line) => line.trim());
    return lines.length > 0 && lines.every((line) => /warning X\d+:/.test(line));
  }
  return false;
}

setConsoleFunction((type, message, ...params) => {
  if (isHarmless(type, message, params)) return;
  console[type](message, ...params);
});
