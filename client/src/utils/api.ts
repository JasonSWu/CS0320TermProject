import { getLoginCookie } from "./cookie";

/**
 * The base URL of the API.
 */
const HOST = "http://localhost:3232";

/**
 * Sends a query to the API endpoint with the specified query parameters.
 * @param {string} endpoint - The endpoint to query.
 * @param {Record<string, string>} query_params - The query parameters as key-value pairs.
 * @returns {Promise<any>} A promise that resolves to the JSON response from the API.
 */
async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
): Promise<any> {
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

/**
 * Adds a pin with the specified latitude and longitude coordinates.
 * @param {number} lat - The latitude coordinate of the pin.
 * @param {number} lon - The longitude coordinate of the pin.
 * @returns {Promise<any>} A promise that resolves to the response from the API.
 */
export async function addPin(lat: number, lon: number): Promise<any> {
  return await queryAPI("add-pin", {
    uid: getLoginCookie() || "",
    lat: lat.toString(),
    long: lon.toString(),
  });
}

/**
 * Retrieves pins from the API.
 * @returns {Promise<any>} A promise that resolves to the response from the API.
 */
export async function getPins(): Promise<any> {
  return await queryAPI("get-pins", {
    uid: getLoginCookie() || "",
  });
}

/**
 * Clears pins from the map.
 * @param {string} uid - The user ID of the pins to clear.
 * @returns {Promise<any>} A promise that resolves to the response from the API.
 */
export async function clearPins(
  uid: string = getLoginCookie() || ""
): Promise<any> {
  return await queryAPI("clear-pins", {
    uid: uid,
  });
}
