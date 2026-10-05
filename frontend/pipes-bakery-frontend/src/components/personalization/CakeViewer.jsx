import { Suspense, useEffect, useRef, useState } from "react";
import { Canvas, useFrame } from "@react-three/fiber";
import * as THREE from "three";
import { ContactShadows, Environment, Lightformer, OrbitControls } from "@react-three/drei";
import { LuRotate3D } from "react-icons/lu";
import CakeModel from "./CakeModel";
import { STAND_HEIGHT, TIER_HEIGHT } from "./cakeGeometry";
import "./threeConsole";

// Keeps a taller cake in frame: when the tier count changes the camera eases back and re-centres,
// then hands control back to the customer (it never fights their own zoom or rotation).
function CameraRig({ tiers, reducedMotion }) {
  const settling = useRef(true);

  useEffect(() => {
    settling.current = true;
  }, [tiers]);

  useFrame((state, rawDelta) => {
    const { controls, camera } = state;
    if (!controls || !settling.current) return;
    const delta = Math.min(rawDelta, 0.1);
    const lambda = reducedMotion ? 1000 : 3.5;
    const top = STAND_HEIGHT + tiers * TIER_HEIGHT;
    const targetY = top * 0.5 + 0.15;
    const distance = 8.3 + (tiers - 1) * 1.05;

    const offset = new THREE.Vector3().subVectors(camera.position, controls.target);
    const length = THREE.MathUtils.damp(offset.length(), distance, lambda, delta);
    controls.target.y = THREE.MathUtils.damp(controls.target.y, targetY, lambda, delta);
    camera.position.copy(controls.target).add(offset.setLength(length));
    controls.update();

    if (Math.abs(length - distance) < 0.005 && Math.abs(controls.target.y - targetY) < 0.005) {
      settling.current = false;
    }
  });

  return null;
}

function supportsWebGL() {
  try {
    const canvas = document.createElement("canvas");
    return Boolean(window.WebGLRenderingContext && (canvas.getContext("webgl2") || canvas.getContext("webgl")));
  } catch {
    return false;
  }
}

function prefersReducedMotion() {
  return typeof window !== "undefined" && window.matchMedia?.("(prefers-reduced-motion: reduce)").matches;
}

// Left side of the personalization page: the live 3D cake. It stays mounted across phases.
export default function CakeViewer({ cake, caption }) {
  const [webGL] = useState(supportsWebGL);
  const [reducedMotion] = useState(prefersReducedMotion);
  const [interacted, setInteracted] = useState(false);

  return (
    <div className="cake-viewer" aria-label={`Vista 3D de tu torta: ${caption}`} role="img">
      {webGL ? (
        <Canvas
          // PCF: three removed PCFSoftShadowMap (R3F's `shadows` default) and falls back to it anyway
          shadows="percentage"
          dpr={[1, 1.75]}
          camera={{ position: [0.6, 4.6, 7.6], fov: 33 }}
          gl={{ antialias: true, preserveDrawingBuffer: false }}
        >
          <hemisphereLight args={["#fff4e6", "#b48a6a", 0.55]} />
          <directionalLight
            position={[4.5, 8, 5]}
            intensity={1.9}
            color="#fff1df"
            castShadow
            shadow-mapSize={[1024, 1024]}
            shadow-bias={-0.0004}
            shadow-normalBias={0.02}
            shadow-camera-left={-5}
            shadow-camera-right={5}
            shadow-camera-top={6}
            shadow-camera-bottom={-3}
          />
          <directionalLight position={[-6, 3, -2]} intensity={0.45} color="#ffe2c4" />

          {/* A warm studio built from soft boxes: no remote HDR file needed */}
          <Environment resolution={128}>
            <Lightformer form="rect" intensity={2.2} color="#fff3e2" position={[0, 6, 2]} rotation-x={Math.PI / 2} scale={[10, 6, 1]} />
            <Lightformer form="rect" intensity={1.1} color="#ffe0c2" position={[-6, 2, 1]} rotation-y={Math.PI / 2} scale={[8, 4, 1]} />
            <Lightformer form="rect" intensity={0.9} color="#fff8ef" position={[6, 2, 3]} rotation-y={-Math.PI / 2} scale={[8, 4, 1]} />
          </Environment>

          <Suspense fallback={null}>
            <CakeModel {...cake} reducedMotion={reducedMotion} />
          </Suspense>

          <ContactShadows position={[0, 0.001, 0]} opacity={0.42} scale={14} blur={2.6} far={4.5} color="#5a2d0c" />

          <OrbitControls
            makeDefault
            target={[0.45, 0.95, 0.3]}
            enablePan={false}
            minDistance={5.5}
            maxDistance={12}
            minPolarAngle={0.55}
            maxPolarAngle={1.42}
            autoRotate={!reducedMotion && !interacted}
            autoRotateSpeed={0.55}
            onStart={() => setInteracted(true)}
          />
          <CameraRig tiers={cake.tierDiameters.length + cake.decorativeTiers} reducedMotion={reducedMotion} />
        </Canvas>
      ) : (
        <div className="cake-viewer-fallback">
          <p>Tu navegador no puede mostrar la vista 3D, pero puedes seguir diseñando tu torta.</p>
        </div>
      )}

      <div className="cake-viewer-caption" aria-hidden="true">
        <span>{caption}</span>
        {webGL && (
          <span className="cake-viewer-hint">
            <LuRotate3D /> Arrastra para girarla
          </span>
        )}
      </div>
    </div>
  );
}
