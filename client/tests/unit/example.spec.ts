import { expect, test } from "vitest";
import { parseSearch } from "../../src/components/Buttons.tsx";

test("1 + 2 should be 3", () => {
  expect(1 + 2).toBe(3);
});

test("parse search on no properties", () => {
  var searches: Record<string, string> = {};
  const result = parseSearch(searches)
  expect(result).toBe("");
});

test("parse search on one property", () => {
  var searches: Record<string, string> = {};
  searches["a"] = "1";
  const result = parseSearch(searches)
  expect(result).toBe("?a=1");
});

test("parse search on two properties", () => {
  var searches: Record<string, string> = {};
  searches["a"] = "1";
  searches["b"] = "2";
  const result = parseSearch(searches)
  expect(result).toBe("?a=1&b=2");
});

test("parse search on multiple properties, all empty", () => {
  var searches: Record<string, string> = {};
  searches["a"] = "";
  searches["b"] = "";
  searches["c"] = "";
  searches["d"] = "";
  const result = parseSearch(searches)
  expect(result).toBe("");
});

test("parse search on multiple properties, some empty", () => {
  var searches: Record<string, string> = {};
  searches["a"] = "1";
  searches["b"] = "";
  searches["c"] = "3";
  searches["d"] = "";
  const result = parseSearch(searches)
  expect(result).toBe("?a=1&c=3");
});

