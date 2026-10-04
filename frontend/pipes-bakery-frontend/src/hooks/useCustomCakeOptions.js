import { useCallback, useEffect, useState } from "react";
import { getCustomCakeOptions } from "../services/customCakeService";

export function useCustomCakeOptions() {
  const [state, setState] = useState({ options: null, status: "loading" });
  const [attempt, setAttempt] = useState(0);

  useEffect(() => {
    let cancelled = false;

    getCustomCakeOptions()
      .then((options) => {
        if (!cancelled) setState({ options, status: "ready" });
      })
      .catch((error) => {
        if (import.meta.env.DEV) {
          console.error("Error loading cake options", error);
        }
        if (!cancelled) setState({ options: null, status: "error" });
      });

    return () => {
      cancelled = true;
    };
  }, [attempt]);

  const retry = useCallback(() => {
    setState({ options: null, status: "loading" });
    setAttempt((value) => value + 1);
  }, []);

  return { ...state, retry };
}
