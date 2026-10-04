import * as THREE from "three";

// Procedural textures and geometry shared by the 3D cake. Generated once per page load.

function makeCanvas(size) {
  const canvas = document.createElement("canvas");
  canvas.width = size;
  canvas.height = size;
  return canvas;
}

// Soft, blotchy noise: reads as the gentle unevenness of spread buttercream when used as a bump map
export function createFrostingBumpTexture() {
  const size = 256;
  const canvas = makeCanvas(size);
  const context = canvas.getContext("2d");
  context.fillStyle = "rgb(128,128,128)";
  context.fillRect(0, 0, size, size);

  for (let index = 0; index < 900; index += 1) {
    const x = Math.random() * size;
    const y = Math.random() * size;
    const radius = 4 + Math.random() * 18;
    const shade = 110 + Math.random() * 40;
    const gradient = context.createRadialGradient(x, y, 0, x, y, radius);
    gradient.addColorStop(0, `rgba(${shade},${shade},${shade},0.35)`);
    gradient.addColorStop(1, "rgba(128,128,128,0)");
    context.fillStyle = gradient;
    context.fillRect(x - radius, y - radius, radius * 2, radius * 2);
  }

  const texture = new THREE.CanvasTexture(canvas);
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  texture.repeat.set(4, 1.5);
  return texture;
}

// Speckled crumb: tiny air pockets of a sponge cake, multiplied over the flavour colour
export function createCrumbTexture() {
  const size = 256;
  const canvas = makeCanvas(size);
  const context = canvas.getContext("2d");
  context.fillStyle = "#ffffff";
  context.fillRect(0, 0, size, size);

  for (let index = 0; index < 2600; index += 1) {
    const x = Math.random() * size;
    const y = Math.random() * size;
    const radius = 0.6 + Math.random() * 2.2;
    const light = 165 + Math.random() * 70;
    context.fillStyle = `rgba(${light},${light * 0.96},${light * 0.9},0.55)`;
    context.beginPath();
    context.arc(x, y, radius, 0, Math.PI * 2);
    context.fill();
  }

  const texture = new THREE.CanvasTexture(canvas);
  texture.colorSpace = THREE.SRGBColorSpace;
  texture.wrapS = THREE.RepeatWrapping;
  texture.wrapT = THREE.RepeatWrapping;
  return texture;
}

// Profile of a frosted tier for a unit radius and unit height, with a softly rounded top edge.
// Points go bottom to top so the lathe's normals face outwards.
export function frostedTierProfile() {
  const bevel = 0.07;
  const points = [new THREE.Vector2(0, 0), new THREE.Vector2(0.992, 0), new THREE.Vector2(1, 0.015)];
  points.push(new THREE.Vector2(1, 1 - bevel));

  const steps = 8;
  for (let step = 1; step <= steps; step += 1) {
    const angle = (step / steps) * (Math.PI / 2);
    points.push(new THREE.Vector2(1 - bevel + Math.cos(angle) * bevel, 1 - bevel + Math.sin(angle) * bevel));
  }

  points.push(new THREE.Vector2(0, 1));
  return points;
}

// Profile of a glazed ceramic cake stand: wide foot, slim stem, flat plate of radius 1 on top.
export function cakeStandProfile(height) {
  const plateThickness = 0.06;
  return [
    new THREE.Vector2(0, 0),
    new THREE.Vector2(0.42, 0),
    new THREE.Vector2(0.44, 0.03),
    new THREE.Vector2(0.36, 0.07),
    new THREE.Vector2(0.17, height * 0.32),
    new THREE.Vector2(0.13, height * 0.62),
    new THREE.Vector2(0.2, height - plateThickness - 0.03),
    new THREE.Vector2(0.97, height - plateThickness),
    new THREE.Vector2(1, height - plateThickness * 0.5),
    new THREE.Vector2(0.99, height),
    new THREE.Vector2(0, height),
  ];
}

export function isLightColor(hex) {
  const color = new THREE.Color(hex);
  const luminance = 0.2126 * color.r + 0.7152 * color.g + 0.0722 * color.b;
  return luminance > 0.45;
}
