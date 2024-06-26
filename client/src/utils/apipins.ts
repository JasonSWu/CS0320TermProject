import { getLoginCookie } from "./cookie";

const HOST = "http://localhost:3232";

async function queryAPI(
  endpoint: string,
  query_params: Record<string, string>
) {
  // query_params is a dictionary of key-value pairs that gets added to the URL as query parameters
  // e.g. { foo: "bar", hell: "o" } becomes "?foo=bar&hell=o"
  const paramsString = new URLSearchParams(query_params).toString();
  const url = `${HOST}/${endpoint}?${paramsString}`;
  const response = await fetch(url);
  if (!response.ok) {
    console.error(response.status, response.statusText);
  }
  return response.json();
}

export async function addPin(latitude: number, longitude: number) {
  return await queryAPI("add-pin", {
    uid: getLoginCookie() || "",
    lat: latitude.toString(),
    long: longitude.toString(),
  });
}

export async function getPins() {
  return await queryAPI("get-pins", {
    uid: getLoginCookie() || "",
  });
}

export async function clearPins(uid: string = getLoginCookie() || "") {
  return await queryAPI("clear-pin", {
    uid: uid,
  });
}
