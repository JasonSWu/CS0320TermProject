import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

import rl_data from "../geodata/fullDownload.json";
import mock_data from "../geodata/foodLand.json";

const propertyName = "holc_grade";
export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.2,
  },
};

function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

export function mockData(): GeoJSON.FeatureCollection | undefined {
  return isFeatureCollection(mock_data) ? mock_data : undefined;
}

export async function overlayData(): Promise<
  GeoJSON.FeatureCollection | undefined
> {
  const data = await fetch("http://localhost:3232/geo-json")
    .then((result) => result.json())
    .then((json) => JSON.parse(json.result));
  return isFeatureCollection(data) ? data : undefined;
}
