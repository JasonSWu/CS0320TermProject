import { useState } from "react";
import Mapbox from "./Mapbox";

/**
 * Enum representing different sections of the Maps component.
 * @readonly
 * @enum {string}
 */
enum Section {
  FIRESTORE_DEMO = "FIRESTORE_DEMO",
  MAP_DEMO = "MAP_DEMO",
}

/**
 * Component for rendering maps and related demos.
 * @returns {JSX.Element} The rendered JSX element representing the Maps component.
 */
export default function Maps() {
  /**
   * State variable to track the current section of the component.
   * @type {[Section, function(Section): void]}
   */
  const [section, setSection] = useState<Section>(Section.FIRESTORE_DEMO);

  return (
    <div>
      {/* Title */}
      <h1 aria-label="Gearup Title">Maps</h1>

      {/* Button for Firestore Demo section */}
      <button onClick={() => setSection(Section.FIRESTORE_DEMO)}>
        Section 1: Firestore Demo
      </button>

      {/* Button for Mapbox Demo section */}
      <button
        onClick={() => setSection(Section.MAP_DEMO)}
        aria-label="maps-button"
      >
        Section 2: Mapbox Demo
      </button>

      {/* Render Mapbox component if the current section is MAP_DEMO */}
      {section === Section.MAP_DEMO ? <Mapbox /> : null}
    </div>
  );
}
