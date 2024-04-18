import "mapbox-gl/dist/mapbox-gl.css";
import { addPin, clearPins, getPins } from "../utils/api";
import { LatLong } from "./Mapbox";
import { mockData } from "../utils/overlay";

/**
 * Input fields for latitude and longitude.
 * @type {JSX.Element}
 */
export const latLngInputs = (
  <div>
    <label htmlFor="new-lat-cen">Lat Center:</label>
    <input aria-label="lat-center-input" id="centerLat" type="text" />
    <label htmlFor="new-lon-cen">Lon Center:</label>
    <input aria-label="lon-center-input" id="centerLng" type="text" />
    <label htmlFor="new-lat-len">Lat Length:</label>
    <input aria-label="lat-length-input" id="boxLat" type="text" />
    <label htmlFor="new-lon-len">Lon Length:</label>
    <input aria-label="lon-length-input" id="boxLng" type="text" />
  </div>
);

/**
 * Input fields for name, state, city, and area description.
 * @type {JSX.Element}
 */
export const nameInputs = (
  <div>
    <label htmlFor="new-name">Name:</label>
    <input aria-label="name-input" id="name" type="text" />
    <label htmlFor="new-state">State:</label>
    <input aria-label="state-input" id="state" type="text" />
    <label htmlFor="new-city">City:</label>
    <input aria-label="city-input" id="city" type="text" />
    <label htmlFor="new-area-description">Area Description:</label>
    <input
      aria-label="area-description-input"
      id="area_description_data"
      type="text"
    />
  </div>
);

/**
 * Input fields for HOLC ID, HOLC Grade, and Neighborhood ID.
 * @type {JSX.Element}
 */
export const holcInputs = (
  <div>
    <label htmlFor="new-holc-id">HOLC ID:</label>
    <input aria-label="holc-id-input" id="holc_id" type="text" />
    <label htmlFor="new-holc-grade">HOLC GRADE:</label>
    <input aria-label="holc-grade-input" id="holc_grade" type="text" />
    <label htmlFor="new-neighborhood-id">Neighborhood ID:</label>
    <input
      aria-label="neighborhood-id-input"
      id="neighborhood_id"
      type="text"
    />
  </div>
);

/**
 * Parses search parameters and constructs the query string.
 * @param {Record<string, string>} searches - The search parameters.
 * @returns {string} The constructed query string.
 */
export function parseSearch(searches: Record<string, string>): string {
  let newSearch = "";
  if (Object.keys(searches).length !== 0) {
    newSearch += "?";
    for (const [key, value] of Object.entries(searches)) {
      if (value !== "") {
        newSearch += `${key}=${value}&`;
      }
    }
    newSearch = newSearch.substring(0, newSearch.length - 1);
  }
  console.log(newSearch);
  return newSearch;
}

/**
 * Button component for initiating search with specified parameters.
 * @param {React.Dispatch<React.SetStateAction<GeoJSON.FeatureCollection | undefined>>} setOverlay - The function to set the overlay data.
 * @returns {JSX.Element} The rendered search button.
 */
export function searchButton(
  setOverlay: React.Dispatch<
    React.SetStateAction<GeoJSON.FeatureCollection | undefined>
  >
): JSX.Element {
  return (
    <div className="search">
      <button
        aria-label="search-button"
        onClick={() => {
          const ids: string[] = [
            "centerLat",
            "centerLng",
            "boxLat",
            "boxLng",
            "name",
            "state",
            "city",
            "holc_id",
            "holc_grade",
            "neighborhood_id",
            "area_description_data",
          ];
          const searches: Record<string, string> = {};
          ids.forEach((id) => {
            const htmlElement = document.getElementById(id) as HTMLInputElement;
            const idSearch = htmlElement.value;
            if (idSearch !== "") {
              searches[id] = idSearch;
            }
          });

          const newSearch = parseSearch(searches);
          if (import.meta.env.VITE_APP_NODE_ENV === "development") {
            setOverlay(mockData());
          } else {
            fetch("http://localhost:3232/geo-json" + newSearch)
              .then((response) => response.json())
              .then((jsonData) => {
                console.log(jsonData);
                setOverlay(JSON.parse(jsonData.result));
              });
          }
        }}
      >
        Search
      </button>
    </div>
  );
}

/**
 * Button component for clearing pins from the map.
 * @param {React.Dispatch<React.SetStateAction<LatLong[]>>} setMarkers - The function to set the markers.
 * @returns {JSX.Element} The rendered clear button.
 */
export function clearButton(
  setMarkers: React.Dispatch<React.SetStateAction<LatLong[]>>
): JSX.Element {
  return (
    <div className="clear">
      <button
        aria-label="clear-button"
        onClick={() => {
          clearPins();
          setMarkers([]);
        }}
      >
        Clear Pins
      </button>
    </div>
  );
}
