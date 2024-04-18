import "mapbox-gl/dist/mapbox-gl.css";
import { useEffect, useState } from "react";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Marker,
  Source,
  ViewStateChangeEvent,
} from "react-map-gl";
import { geoLayer, overlayData, mockData } from "../utils/overlay";
import { addPin, clearPins, getPins } from "../utils/api";
import {
  latLngInputs,
  nameInputs,
  holcInputs,
  searchButton,
  clearButton,
} from "./Buttons";

// Ensure that Mapbox API key is provided
const MAPBOX_API_KEY = process.env.MAPBOX_TOKEN;
if (!MAPBOX_API_KEY) {
  console.error("Mapbox API key not found. Please add it to your .env file.");
}

/**
 * Interface representing latitude and longitude coordinates.
 * @interface
 */
export interface LatLong {
  lat: number;
  long: number;
}

// Initial coordinates for Providence, Rhode Island
const ProvidenceLatLong: LatLong = {
  lat: 41.824,
  long: -71.4128,
};
const initialZoom = 10;

/**
 * Component for rendering Mapbox map and handling interactions.
 * @returns {JSX.Element} The rendered JSX element representing the Mapbox component.
 */
export default function Mapbox() {
  // State variables for markers, view state, and overlay data
  const [markers, setMarkers] = useState<LatLong[]>([]);
  const [viewState, setViewState] = useState({
    latitude: ProvidenceLatLong.lat,
    longitude: ProvidenceLatLong.long,
    zoom: initialZoom,
  });
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  // Fetch overlay data and pins from API on component mount
  useEffect(() => {
    async function fetchData() {
      if (import.meta.env.VITE_APP_NODE_ENV === "development") {
        setOverlay(mockData());
      } else {
        const result = await overlayData();
        setOverlay(result);
      }
    }
    fetchData();
    getPins().then((json) => {
      const latlons = json.pins.map((pin: string) => {
        const split = pin.split(" ");
        return { lat: parseFloat(split[0]), long: parseFloat(split[1]) };
      });
      setMarkers(latlons);
    });
  }, []);

  // Handler function for adding a pin on map click
  async function onMapClick(e: MapLayerMouseEvent) {
    const lat = e.lngLat.lat;
    const lon = e.lngLat.lng;
    setMarkers([...markers, { lat: lat, long: lon }]);
    console.log(lat + " " + lon);
    await addPin(lat, lon);
  }

  // Render Mapbox map and markers
  return (
    <>
      {/* Render input fields and buttons for adding pins */}
      {latLngInputs}
      {nameInputs}
      {holcInputs}
      {searchButton(setOverlay)}
      {clearButton(setMarkers)}
      {/* Mapbox map */}
      <div className="map">
        <Map
          mapboxAccessToken={MAPBOX_API_KEY}
          {...viewState}
          style={{ width: window.innerWidth, height: window.innerHeight }}
          mapStyle={"mapbox://styles/mapbox/streets-v12"}
          onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
          onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
        >
          {/* Render overlay data */}
          <Source id="geo_data" type="geojson" data={overlay}>
            <Layer {...geoLayer} />
          </Source>
          {/* Render markers */}
          {markers.map((marker, index) => (
            <Marker
              key={index}
              latitude={marker.lat}
              longitude={marker.long}
              anchor="bottom"
            ></Marker>
          ))}
        </Map>
      </div>
    </>
  );
}
