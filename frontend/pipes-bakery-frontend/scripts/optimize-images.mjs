// Builds the web-sized images in public/images from the originals in image-sources/.
// Run with `npm run images` after adding or replacing an original, and commit the output.
// With file arguments it instead re-encodes those files (e.g. product photos) next to
// themselves as <name>-web.webp, ready to upload from the admin panel.
import { mkdir, readdir, stat } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";
import sharp from "sharp";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const sourceDir = path.join(root, "image-sources");
const outputDir = path.join(root, "public", "images");

const PHOTO_QUALITY = 75;
const LOGO_QUALITY = 85;
const PRODUCT_MAX_SIZE = 1200;

async function report(file) {
  const { size } = await stat(file);
  console.log(`${path.relative(root, file).padEnd(44)} ${(size / 1024).toFixed(0).padStart(5)} KB`);
}

async function toWebp(source, name, width, quality) {
  const target = path.join(outputDir, `${name}.webp`);
  await sharp(source)
    .rotate()
    .resize({ width, withoutEnlargement: true })
    .webp({ quality, effort: 6 })
    .toFile(target);
  await report(target);
}

async function toPng(source, name, size) {
  const target = path.join(outputDir, `${name}.png`);
  await sharp(source).resize(size, size).png({ compressionLevel: 9, palette: true }).toFile(target);
  await report(target);
}

async function buildSiteImages() {
  await mkdir(outputDir, { recursive: true });
  const sources = await readdir(sourceDir);

  for (const file of sources) {
    const source = path.join(sourceDir, file);
    const carousel = file.match(/^homePageCarousel(\d+)\./);

    if (carousel) {
      await toWebp(source, `carousel-${carousel[1]}-sm`, 800, PHOTO_QUALITY);
      await toWebp(source, `carousel-${carousel[1]}-lg`, 1280, PHOTO_QUALITY);
    } else if (file.startsWith("aboutSection.")) {
      await toWebp(source, "about-sm", 480, PHOTO_QUALITY);
      await toWebp(source, "about-lg", 850, PHOTO_QUALITY);
    } else if (file.startsWith("logo.")) {
      await toWebp(source, "logo-320", 320, LOGO_QUALITY);
      await toWebp(source, "logo-640", 640, LOGO_QUALITY);
    } else if (file.startsWith("logo_2.")) {
      await toWebp(source, "logo-nav", 400, LOGO_QUALITY);
    } else if (file.startsWith("favicon.")) {
      await toPng(source, "favicon-48", 48);
      await toPng(source, "favicon-180", 180);
    } else {
      console.warn(`Skipped ${file}: no rule for it`);
    }
  }
}

async function reencodeFiles(files) {
  for (const file of files) {
    const source = path.resolve(file);
    const target = path.join(path.dirname(source), `${path.parse(source).name}-web.webp`);
    await sharp(source)
      .rotate()
      .resize({ width: PRODUCT_MAX_SIZE, height: PRODUCT_MAX_SIZE, fit: "inside", withoutEnlargement: true })
      .webp({ quality: 80, effort: 6 })
      .toFile(target);
    await report(target);
  }
}

const files = process.argv.slice(2);
await (files.length > 0 ? reencodeFiles(files) : buildSiteImages());
