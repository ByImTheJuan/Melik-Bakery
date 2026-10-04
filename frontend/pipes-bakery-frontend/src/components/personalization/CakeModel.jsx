import { useEffect, useMemo, useRef, useState } from "react";
import { useFrame } from "@react-three/fiber";
import { Text } from "@react-three/drei";
import * as THREE from "three";
import nunitoFont from "@fontsource/nunito/files/nunito-latin-800-normal.woff?url";
import {
  MAX_TIERS,
  STAND_HEIGHT,
  TIER_HEIGHT,
  arcAngles,
  buildTierSlots,
  radiusFor,
  seededRandom,
  topOfCake,
} from "./cakeGeometry";
import {
  cakeStandProfile,
  createCrumbTexture,
  createFrostingBumpTexture,
  frostedTierProfile,
  isLightColor,
} from "./cakeMaterials";

// Sponge layers and fillings of a cut face, as fractions of the tier height
const CUT_LAYERS = [
  { from: 0, to: 0.27, kind: "sponge" },
  { from: 0.27, to: 0.33, kind: "filling" },
  { from: 0.33, to: 0.6, kind: "sponge" },
  { from: 0.6, to: 0.66, kind: "filling" },
  { from: 0.66, to: 0.94, kind: "sponge" },
];
const SLICE_HALF_ANGLE = THREE.MathUtils.degToRad(19);
const BEADS_PER_RING = 64;

const PASTEL_SPRINKLES = ["#eebcb8", "#f3e2bd", "#c9d3a0", "#d6c6dd", "#d9a066", "#fbf5ea", "#7a4a2e"];
const MACARON_COLORS = ["#eebcb8", "#c9d3a0", "#d6c6dd", "#f3e2bd"];
const FLOWER_COLORS = ["#f6e7df", "#eebcb8", "#f3e2bd"];

function damp(current, target, lambda, delta) {
  return THREE.MathUtils.damp(current, target, lambda, delta);
}

// ----------------------------------------------------------------------------------------------
// Anchored decorations: placed in world units on the (animated) top tier, recomputed every frame
// so berries, candles... keep their real size while the cake under them grows or shrinks.
// ----------------------------------------------------------------------------------------------
function AnchoredItems({ anchor, items, renderItem }) {
  const refs = useRef([]);

  useFrame(() => {
    const { radius, y, height } = anchor.current;
    items.forEach((item, index) => {
      const object = refs.current[index];
      if (!object) return;

      if (item.surface === "side") {
        const distance = radius + (item.offset ?? 0.01);
        object.position.set(Math.sin(item.phi) * distance, y - item.depth * height, Math.cos(item.phi) * distance);
        object.rotation.set(0, item.phi, 0);
      } else {
        const distance = item.r * radius;
        object.position.set(Math.sin(item.phi) * distance, y + (item.lift ?? 0), Math.cos(item.phi) * distance);
        object.rotation.set(0, item.phi + (item.spin ?? 0), 0);
      }
    });
  });

  return items.map((item, index) => (
    <group key={item.key ?? index} ref={(node) => { refs.current[index] = node; }}>
      {renderItem(item, index)}
    </group>
  ));
}

function Strawberry({ seed }) {
  const random = seededRandom(seed);
  return (
    <group rotation={[random() * 0.6 - 0.3, random() * Math.PI, random() * 0.6 - 0.3]}>
      <mesh castShadow position={[0, 0.07, 0]} scale={[0.075, 0.1, 0.075]}>
        <sphereGeometry args={[1, 18, 14]} />
        <meshPhysicalMaterial color="#b8322a" roughness={0.32} clearcoat={0.6} clearcoatRoughness={0.25} />
      </mesh>
      <mesh position={[0, 0.165, 0]} rotation={[0, 0, 0]}>
        <coneGeometry args={[0.055, 0.035, 6]} />
        <meshStandardMaterial color="#5f7a3a" roughness={0.7} />
      </mesh>
    </group>
  );
}

function Blueberry() {
  return (
    <mesh castShadow position={[0, 0.04, 0]}>
      <sphereGeometry args={[0.045, 16, 12]} />
      <meshPhysicalMaterial color="#2f3557" roughness={0.45} sheen={0.6} sheenColor="#8a93b8" />
    </mesh>
  );
}

function Macaron({ color }) {
  return (
    <group rotation={[0.35, 0, 0]} position={[0, 0.09, 0]}>
      <mesh castShadow position={[0, 0.035, 0]} scale={[0.1, 0.035, 0.1]}>
        <sphereGeometry args={[1, 24, 12]} />
        <meshStandardMaterial color={color} roughness={0.65} />
      </mesh>
      <mesh position={[0, 0, 0]}>
        <cylinderGeometry args={[0.088, 0.088, 0.03, 24]} />
        <meshStandardMaterial color="#fbf3e6" roughness={0.5} />
      </mesh>
      <mesh castShadow position={[0, -0.035, 0]} scale={[0.1, 0.035, 0.1]}>
        <sphereGeometry args={[1, 24, 12]} />
        <meshStandardMaterial color={color} roughness={0.65} />
      </mesh>
    </group>
  );
}

function Flower({ color }) {
  const petals = 6;
  return (
    <group position={[0, 0.03, 0]}>
      {Array.from({ length: petals }, (_, index) => {
        const angle = (index / petals) * Math.PI * 2;
        return (
          <mesh
            key={index}
            castShadow
            position={[Math.sin(angle) * 0.06, 0.012, Math.cos(angle) * 0.06]}
            rotation={[0.35, angle, 0]}
            scale={[0.045, 0.014, 0.075]}
          >
            <sphereGeometry args={[1, 14, 10]} />
            <meshPhysicalMaterial color={color} roughness={0.55} sheen={0.5} sheenColor="#ffffff" />
          </mesh>
        );
      })}
      <mesh position={[0, 0.03, 0]}>
        <sphereGeometry args={[0.028, 14, 10]} />
        <meshStandardMaterial color="#d9a066" roughness={0.6} />
      </mesh>
    </group>
  );
}

function Candle({ seed, reducedMotion }) {
  const flameRef = useRef(null);
  const offset = seed * 1.7;

  useFrame(({ clock }) => {
    if (!flameRef.current || reducedMotion) return;
    const time = clock.elapsedTime * 9 + offset;
    const flicker = 1 + Math.sin(time) * 0.08 + Math.sin(time * 2.3) * 0.05;
    flameRef.current.scale.set(1, flicker, 1);
  });

  return (
    <group>
      <mesh castShadow position={[0, 0.17, 0]}>
        <cylinderGeometry args={[0.022, 0.022, 0.34, 14]} />
        <meshStandardMaterial color="#fbf5ea" roughness={0.45} />
      </mesh>
      <mesh position={[0, 0.355, 0]}>
        <cylinderGeometry args={[0.003, 0.003, 0.03, 6]} />
        <meshStandardMaterial color="#3a2014" />
      </mesh>
      <group ref={flameRef} position={[0, 0.37, 0]}>
        <mesh position={[0, 0.03, 0]} scale={[0.022, 0.05, 0.022]}>
          <sphereGeometry args={[1, 12, 10]} />
          <meshBasicMaterial color="#ffc46b" toneMapped={false} />
        </mesh>
      </group>
    </group>
  );
}

function GoldFlake({ seed }) {
  const random = seededRandom(seed);
  const width = 0.05 + random() * 0.06;
  const height = 0.04 + random() * 0.05;
  return (
    <mesh rotation={[0, 0, random() * Math.PI]} scale={[width, height, 1]}>
      <circleGeometry args={[1, 5]} />
      <meshStandardMaterial
        color="#d9b25a"
        metalness={0.55}
        roughness={0.3}
        emissive="#7a5418"
        emissiveIntensity={0.45}
        side={THREE.DoubleSide}
      />
    </mesh>
  );
}

function Drip({ length }) {
  return (
    <group>
      <mesh castShadow position={[0, -length / 2, 0]}>
        <capsuleGeometry args={[0.032, length, 6, 10]} />
        <meshPhysicalMaterial color="#4a2616" roughness={0.18} clearcoat={1} clearcoatRoughness={0.1} />
      </mesh>
    </group>
  );
}

// Sprinkles are many tiny pieces: one instanced mesh, matrices refreshed while the cake animates
function Sprinkles({ anchor, avoidRadius }) {
  const meshRef = useRef(null);
  const count = 140;
  const pieces = useMemo(() => {
    const random = seededRandom(97);
    return Array.from({ length: count }, () => ({
      r: 0.12 + Math.sqrt(random()) * 0.8,
      phi: random() * Math.PI * 2,
      spin: random() * Math.PI,
      color: PASTEL_SPRINKLES[Math.floor(random() * PASTEL_SPRINKLES.length)],
    }));
  }, []);
  const lastRadius = useRef(-1);
  const dummy = useMemo(() => new THREE.Object3D(), []);

  useEffect(() => {
    const mesh = meshRef.current;
    if (!mesh) return;
    const color = new THREE.Color();
    pieces.forEach((piece, index) => {
      mesh.setColorAt(index, color.set(piece.color));
    });
    mesh.instanceColor.needsUpdate = true;
  }, [pieces]);

  useEffect(() => {
    lastRadius.current = -1;
  }, [avoidRadius]);

  useFrame(() => {
    const mesh = meshRef.current;
    const { radius, y } = anchor.current;
    if (!mesh || (Math.abs(radius - lastRadius.current) < 1e-4 && Math.abs(y - mesh.userData.y) < 1e-4)) return;
    lastRadius.current = radius;
    mesh.userData.y = y;

    pieces.forEach((piece, index) => {
      // Keep the edible print clean: no sprinkles over the photo
      const hidden = piece.r < avoidRadius;
      dummy.position.set(Math.sin(piece.phi) * piece.r * radius, y + 0.012, Math.cos(piece.phi) * piece.r * radius);
      dummy.rotation.set(Math.PI / 2, 0, piece.spin);
      dummy.scale.setScalar(hidden ? 0 : 1);
      dummy.updateMatrix();
      mesh.setMatrixAt(index, dummy.matrix);
    });
    mesh.instanceMatrix.needsUpdate = true;
  });

  return (
    <instancedMesh ref={meshRef} args={[undefined, undefined, count]} castShadow>
      <capsuleGeometry args={[0.01, 0.045, 3, 6]} />
      <meshStandardMaterial roughness={0.5} />
    </instancedMesh>
  );
}

// Photo printed on edible paper: a thin disc whose texture is cropped to fill the circle
function PhotoPrint({ anchor, imageUrl, sizeFactor, centerOffset }) {
  const groupRef = useRef(null);
  // The texture is kept together with the url it was loaded from, so a stale one is never shown
  const [loaded, setLoaded] = useState({ url: null, texture: null });

  useEffect(() => {
    if (!imageUrl) return undefined;

    let cancelled = false;
    new THREE.TextureLoader().load(imageUrl, (texture) => {
      if (cancelled) {
        texture.dispose();
        return;
      }
      texture.colorSpace = THREE.SRGBColorSpace;
      const aspect = texture.image.width / texture.image.height;
      if (aspect > 1) {
        texture.repeat.set(1 / aspect, 1);
        texture.offset.set((1 - 1 / aspect) / 2, 0);
      } else {
        texture.repeat.set(1, aspect);
        texture.offset.set(0, (1 - aspect) / 2);
      }
      setLoaded({ url: imageUrl, texture });
    });

    return () => {
      cancelled = true;
    };
  }, [imageUrl]);

  useEffect(() => () => loaded.texture?.dispose(), [loaded]);

  const texture = loaded.url === imageUrl ? loaded.texture : null;

  useFrame(() => {
    if (!groupRef.current) return;
    const { radius, y } = anchor.current;
    groupRef.current.position.set(0, y + 0.008, centerOffset * radius);
    groupRef.current.scale.setScalar(radius * sizeFactor);
  });

  if (!texture) return null;

  return (
    <group ref={groupRef}>
      <mesh rotation={[-Math.PI / 2, 0, 0]} receiveShadow>
        <circleGeometry args={[1, 64]} />
        <meshStandardMaterial map={texture} roughness={0.42} />
      </mesh>
    </group>
  );
}

function CakeText({ anchor, text, color, fontSize, maxWidth, zFactor }) {
  const groupRef = useRef(null);

  useFrame(() => {
    if (!groupRef.current) return;
    const { radius, y } = anchor.current;
    groupRef.current.position.set(0, y + 0.014, zFactor * radius);
  });

  return (
    <group ref={groupRef}>
      <Text
        font={nunitoFont}
        fontSize={fontSize}
        maxWidth={maxWidth}
        lineHeight={1.05}
        textAlign="center"
        anchorX="center"
        anchorY="middle"
        rotation={[-Math.PI / 2, 0, 0]}
        color={color}
        outlineWidth={fontSize * 0.04}
        outlineColor={color}
        outlineOpacity={0.35}
      >
        {text}
      </Text>
    </group>
  );
}

// ----------------------------------------------------------------------------------------------
// Tiers, slice and stand
// ----------------------------------------------------------------------------------------------
function BeadRing({ y, radius, material }) {
  const meshRef = useRef(null);

  useEffect(() => {
    const mesh = meshRef.current;
    if (!mesh) return;
    const dummy = new THREE.Object3D();
    for (let index = 0; index < BEADS_PER_RING; index += 1) {
      const angle = (index / BEADS_PER_RING) * Math.PI * 2;
      dummy.position.set(Math.sin(angle) * radius, y, Math.cos(angle) * radius);
      dummy.rotation.set(0, angle, 0);
      dummy.updateMatrix();
      mesh.setMatrixAt(index, dummy.matrix);
    }
    mesh.instanceMatrix.needsUpdate = true;
  }, [y, radius]);

  return (
    <instancedMesh ref={meshRef} args={[undefined, undefined, BEADS_PER_RING]} material={material} castShadow>
      <sphereGeometry args={[0.05, 12, 8]} />
    </instancedMesh>
  );
}

function CutFace({ angle, materials }) {
  // Local +x runs from the centre of the cake to its edge along `angle`
  return (
    <group rotation={[0, angle - Math.PI / 2, 0]}>
      {CUT_LAYERS.map((layer) => (
        <mesh
          key={layer.from}
          position={[0.475, (layer.from + layer.to) / 2, 0]}
          scale={[0.95, layer.to - layer.from, 1]}
          material={layer.kind === "sponge" ? materials.sponge : materials.filling}
        >
          <planeGeometry args={[1, 1]} />
        </mesh>
      ))}
      {/* Buttercream skin around the cut: outer wall and top */}
      <mesh position={[0.975, 0.47, 0]} scale={[0.05, 0.94, 1]} material={materials.frosting}>
        <planeGeometry args={[1, 1]} />
      </mesh>
      <mesh position={[0.5, 0.97, 0]} scale={[1, 0.06, 1]} material={materials.frosting}>
        <planeGeometry args={[1, 1]} />
      </mesh>
    </group>
  );
}

export default function CakeModel({
  tierDiameters,
  decorativeTiers,
  frostingHex,
  spongeHex,
  fillingHex,
  text,
  imageUrl,
  extraIds,
  reducedMotion,
}) {
  const lambda = reducedMotion ? 1000 : 5;

  const textures = useMemo(() => ({ bump: createFrostingBumpTexture(), crumb: createCrumbTexture() }), []);
  const tierGeometry = useMemo(() => new THREE.LatheGeometry(frostedTierProfile(), 96), []);
  const sliceGeometry = useMemo(
    () => new THREE.LatheGeometry(frostedTierProfile(), 16, -SLICE_HALF_ANGLE, SLICE_HALF_ANGLE * 2),
    []
  );
  const standGeometry = useMemo(() => new THREE.LatheGeometry(cakeStandProfile(STAND_HEIGHT), 96), []);

  const materials = useMemo(() => {
    const frosting = new THREE.MeshPhysicalMaterial({
      color: frostingHex,
      roughness: 0.62,
      bumpMap: textures.bump,
      bumpScale: 0.6,
      sheen: 0.35,
      sheenRoughness: 0.8,
      sheenColor: new THREE.Color("#ffffff"),
      clearcoat: 0.06,
      side: THREE.DoubleSide,
    });
    const sponge = new THREE.MeshStandardMaterial({
      color: spongeHex,
      map: textures.crumb,
      roughness: 0.95,
      side: THREE.DoubleSide,
    });
    const filling = new THREE.MeshStandardMaterial({ color: fillingHex, roughness: 0.55, side: THREE.DoubleSide });
    return { frosting, sponge, filling };
    // Colours are animated in useFrame; the materials are created once
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [textures]);

  useEffect(() => () => {
    Object.values(materials).forEach((material) => material.dispose());
    Object.values(textures).forEach((texture) => texture.dispose());
    tierGeometry.dispose();
    sliceGeometry.dispose();
    standGeometry.dispose();
  }, [materials, textures, tierGeometry, sliceGeometry, standGeometry]);

  // A string key keeps the memo stable even if the diameters array is recreated
  const diametersKey = tierDiameters.join("-");
  const slots = useMemo(
    () => buildTierSlots(diametersKey.split("-").map(Number), decorativeTiers),
    [diametersKey, decorativeTiers]
  );
  const targetTop = useMemo(
    () => topOfCake(diametersKey.split("-").map(Number), decorativeTiers),
    [diametersKey, decorativeTiers]
  );
  // The stand carries the widest (bottom) tier; the served slice comes from the real cake
  const targetBaseRadius = slots[0].radius;
  const targetSliceRadius = radiusFor(tierDiameters[0]);

  const tierRefs = useRef([]);
  const standRef = useRef(null);
  const sliceRef = useRef(null);
  const anim = useRef(null);
  if (anim.current === null) {
    anim.current = {
      slots: slots.map((slot) => ({ ...slot })),
      baseRadius: targetBaseRadius,
      sliceRadius: targetSliceRadius,
    };
  }
  const anchor = useRef({ radius: targetTop.radius, y: targetTop.y, height: TIER_HEIGHT });

  const colorTargets = useMemo(
    () => ({
      frosting: new THREE.Color(frostingHex),
      sponge: new THREE.Color(spongeHex),
      filling: new THREE.Color(fillingHex),
    }),
    [frostingHex, spongeHex, fillingHex]
  );

  useFrame((_, rawDelta) => {
    const delta = Math.min(rawDelta, 0.1);
    const state = anim.current;
    const colorStep = 1 - Math.exp(-lambda * delta);

    materials.frosting.color.lerp(colorTargets.frosting, colorStep);
    materials.sponge.color.lerp(colorTargets.sponge, colorStep);
    materials.filling.color.lerp(colorTargets.filling, colorStep);

    state.baseRadius = damp(state.baseRadius, targetBaseRadius, lambda, delta);
    state.sliceRadius = damp(state.sliceRadius, targetSliceRadius, lambda, delta);

    let topIndex = 0;
    state.slots.forEach((slot, index) => {
      const target = slots[index];
      slot.radius = damp(slot.radius, target.radius, lambda, delta);
      slot.height = damp(slot.height, target.height, lambda, delta);
      slot.y = damp(slot.y, target.y, lambda, delta);
      slot.presence = damp(slot.presence, target.presence, lambda * 1.2, delta);
      if (target.presence === 1) topIndex = index;

      const group = tierRefs.current[index];
      if (group) {
        const presence = Math.max(slot.presence, 0.0001);
        group.visible = slot.presence > 0.01;
        group.position.y = slot.y;
        group.scale.set(slot.radius * presence, slot.height * presence, slot.radius * presence);
      }
    });

    const top = state.slots[topIndex];
    anchor.current.radius = top.radius;
    anchor.current.y = top.y + top.height * top.presence;
    anchor.current.height = top.height;

    if (standRef.current) {
      const plateRadius = state.baseRadius + 0.3;
      standRef.current.scale.set(plateRadius, 1, plateRadius);
    }

    if (sliceRef.current) {
      const radius = state.baseRadius;
      // Served beside the stand, front right, turned so a cut face looks at the camera
      const sliceRadius = state.sliceRadius * 0.72;
      sliceRef.current.position.set(radius + 0.72, 0.05, 0.45);
      sliceRef.current.scale.set(sliceRadius, TIER_HEIGHT, sliceRadius);
    }
  });

  const extras = useMemo(() => new Set(extraIds), [extraIds]);
  const hasPhoto = Boolean(imageUrl);
  const trimmedText = text?.trim() ?? "";
  const textColor = isLightColor(frostingHex) ? "#5a2d0c" : "#fbf5ea";

  // Decorations that sit along a crescent at the back of the top tier, sharing it evenly
  const crescentItems = useMemo(() => {
    const arrangement = ["frutos-rojos", "macarons", "flores", "velas"].filter((id) => extras.has(id));
    const start = THREE.MathUtils.degToRad(112);
    const end = THREE.MathUtils.degToRad(248);
    const span = (end - start) / Math.max(arrangement.length, 1);
    const items = [];

    arrangement.forEach((id, groupIndex) => {
      const from = start + span * groupIndex + span * 0.08;
      const to = start + span * (groupIndex + 1) - span * 0.08;
      const random = seededRandom(31 + groupIndex * 17);

      if (id === "frutos-rojos") {
        arcAngles(7, from, to).forEach((phi, index) => {
          items.push({ key: `berry-${index}`, kind: index % 2 === 0 ? "strawberry" : "blueberry", phi, r: 0.74 + random() * 0.1, seed: index + 3 });
          items.push({ key: `blue-${index}`, kind: "blueberry", phi: phi + 0.07, r: 0.6 + random() * 0.08, seed: index + 40 });
        });
      }
      if (id === "macarons") {
        arcAngles(4, from, to).forEach((phi, index) => {
          items.push({ key: `macaron-${index}`, kind: "macaron", phi, r: 0.76, color: MACARON_COLORS[index % MACARON_COLORS.length], spin: Math.PI / 2 });
        });
      }
      if (id === "flores") {
        arcAngles(5, from, to).forEach((phi, index) => {
          items.push({ key: `flower-${index}`, kind: "flower", phi, r: 0.7 + (index % 2) * 0.12, color: FLOWER_COLORS[index % FLOWER_COLORS.length] });
        });
      }
      if (id === "velas") {
        arcAngles(5, from, to).forEach((phi, index) => {
          items.push({ key: `candle-${index}`, kind: "candle", phi, r: 0.55, seed: index });
        });
      }
    });

    return items;
  }, [extras]);

  const sideItems = useMemo(() => {
    const items = [];
    if (extras.has("goteo-chocolate")) {
      const random = seededRandom(7);
      for (let index = 0; index < 26; index += 1) {
        items.push({ key: `drip-${index}`, kind: "drip", surface: "side", phi: (index / 26) * Math.PI * 2 + random() * 0.08, depth: 0.02, length: 0.1 + random() * 0.32, offset: 0.012 });
      }
    }
    if (extras.has("hoja-oro")) {
      const random = seededRandom(13);
      for (let index = 0; index < 22; index += 1) {
        items.push({ key: `gold-${index}`, kind: "gold", surface: "side", phi: random() * Math.PI * 2, depth: 0.2 + random() * 0.6, offset: 0.006, seed: index + 5 });
      }
    }
    return items;
  }, [extras]);

  const renderDecoration = (item) => {
    switch (item.kind) {
      case "strawberry":
        return <Strawberry seed={item.seed} />;
      case "blueberry":
        return <Blueberry />;
      case "macaron":
        return <Macaron color={item.color} />;
      case "flower":
        return <Flower color={item.color} />;
      case "candle":
        return <Candle seed={item.seed} reducedMotion={reducedMotion} />;
      case "drip":
        return <Drip length={item.length} />;
      case "gold":
        return <GoldFlake seed={item.seed} />;
      default:
        return null;
    }
  };

  const photoSize = trimmedText ? 0.5 : 0.62;
  const photoOffset = trimmedText ? -0.14 : 0;
  const textOnFrontEdge = hasPhoto;

  return (
    <group rotation={[0, -0.35, 0]}>
      {/* Ceramic cake stand */}
      <mesh ref={standRef} geometry={standGeometry} castShadow receiveShadow>
        <meshPhysicalMaterial color="#f4ede3" roughness={0.22} clearcoat={0.7} clearcoatRoughness={0.15} />
      </mesh>

      {/* Tiers: always mounted so adding or removing one animates instead of popping */}
      {Array.from({ length: MAX_TIERS }, (_, index) => (
        <group key={index} ref={(node) => { tierRefs.current[index] = node; }}>
          <mesh geometry={tierGeometry} material={materials.frosting} castShadow receiveShadow />
          <BeadRing y={0.035} radius={1.0} material={materials.frosting} />
          <BeadRing y={0.955} radius={0.955} material={materials.frosting} />
        </group>
      ))}

      {/* Chocolate ganache cap, under everything else on the top */}
      {extras.has("goteo-chocolate") && <GanacheCap anchor={anchor} />}

      {hasPhoto && <PhotoPrint anchor={anchor} imageUrl={imageUrl} sizeFactor={photoSize} centerOffset={photoOffset} />}

      {trimmedText && (
        <CakeText
          anchor={anchor}
          text={trimmedText}
          color={extras.has("goteo-chocolate") ? "#fbf5ea" : textColor}
          fontSize={textOnFrontEdge ? 0.11 : 0.15}
          maxWidth={targetTop.radius * (textOnFrontEdge ? 1.15 : 1.45)}
          zFactor={textOnFrontEdge ? 0.66 : 0.12}
        />
      )}

      {extras.has("chispas") && <Sprinkles anchor={anchor} avoidRadius={hasPhoto ? photoSize + Math.abs(photoOffset) + 0.04 : 0} />}

      <AnchoredItems anchor={anchor} items={crescentItems} renderItem={renderDecoration} />
      <AnchoredItems anchor={anchor} items={sideItems} renderItem={renderDecoration} />

      {/* A served slice beside the stand shows the chosen sponge and filling */}
      {/* Tip pointing at the cake, cut face towards the camera */}
      <group ref={sliceRef} rotation={[0, -0.85, 0]}>
        {/* The wedge points along +z from its tip: shift it so the slice is centred on the plate */}
        <group position={[0, 0, -0.62]}>
          <mesh geometry={sliceGeometry} material={materials.frosting} castShadow receiveShadow />
          <CutFace angle={SLICE_HALF_ANGLE} materials={materials} />
          <CutFace angle={-SLICE_HALF_ANGLE} materials={materials} />
        </group>
      </group>
      <SlicePlate anchorRef={sliceRef} />
    </group>
  );
}

function GanacheCap({ anchor }) {
  const meshRef = useRef(null);

  useFrame(() => {
    if (!meshRef.current) return;
    const { radius, y } = anchor.current;
    meshRef.current.position.set(0, y + 0.002, 0);
    meshRef.current.scale.set(radius + 0.012, 1, radius + 0.012);
  });

  return (
    <mesh ref={meshRef} castShadow receiveShadow>
      <cylinderGeometry args={[1, 1, 0.03, 96]} />
      <meshPhysicalMaterial color="#4a2616" roughness={0.18} clearcoat={1} clearcoatRoughness={0.1} />
    </mesh>
  );
}

// Small dessert plate that follows the slice
function SlicePlate({ anchorRef }) {
  const plateRef = useRef(null);

  useFrame(() => {
    if (!plateRef.current || !anchorRef.current) return;
    const slice = anchorRef.current;
    plateRef.current.position.set(slice.position.x, 0, slice.position.z);
    const radius = slice.scale.x * 0.95 + 0.12;
    plateRef.current.scale.set(radius, 1, radius);
  });

  return (
    <group ref={plateRef}>
      <mesh receiveShadow castShadow position={[0, 0.025, 0]}>
        <cylinderGeometry args={[1, 0.82, 0.05, 64]} />
        <meshPhysicalMaterial color="#f7f1e8" roughness={0.2} clearcoat={0.8} clearcoatRoughness={0.12} />
      </mesh>
    </group>
  );
}
