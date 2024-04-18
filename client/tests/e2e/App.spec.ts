import { expect, test } from "@playwright/test";
import { clearPins } from "../../src/utils/apipins";

/**
  The general shapes of tests in Playwright Test are:
    1. Navigate to a URL
    2. Interact with the page
    3. Assert something about the page against your expectations
  Look for this pattern in the tests below!
 */

const SPOOF_UID = "mock-user-id";

const labels: string[] = [
  "lat-center-input",
  "lon-center-input",
  "lat-length-input",
  "lon-length-input",
  "name-input",
  "state-input",
  "city-input",
  "holc-id-input",
  "holc-grade-input",
  "neighborhood-id-input",
  "area-description-input",
];

test.beforeEach(
  "add spoof uid cookie to browser",
  async ({ context, page }) => {
    // - Add "uid" cookie to the browser context
    await context.addCookies([
      {
        name: "uid",
        value: SPOOF_UID,
        url: "http://localhost:8000",
      },
    ]);

    // wipe everything for this spoofed UID in the database.
    // await clearPins(SPOOF_UID);
    await page.goto("http://localhost:8000/");
    await page.getByRole("button", { name: "Section 2: Mapbox Demo" }).click();
  }
);

/**
 * Don't worry about the "async" yet. We'll cover it in more detail
 * for the next sprint. For now, just think about "await" as something
 * you put before parts of your test that might take time to run,
 * like any interaction with the page.
 */
test("on page load, I see the gearup screen and skip auth.", async ({
  page,
}) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await expect(page.getByLabel("Gearup Title")).toBeVisible();
  // <i> with aria-label favorite-words-header should include the SPOOF_UID
  await expect(
    page.getByRole("button", { name: "Section 2: Mapbox Demo" })
  ).toBeVisible();
});

test("I can type in search boxes", async ({ page }) => {
  await expect(page.getByLabel("lat-center-input")).toBeVisible();
  await page.getByLabel("lat-center-input").fill("testing");
  await page.getByLabel("lon-center-input").fill("testing");
  await page.getByLabel("lat-length-input").fill("testing");
  await page.getByLabel("lon-length-input").fill("testing");
  await page.getByLabel("name-input").fill("testing");
  await page.getByLabel("state-input").fill("testing");
  await page.getByLabel("city-input").fill("testing");
  await page.getByLabel("holc-id-input").fill("testing");
  await page.getByLabel("holc-grade-input").fill("testing");
  await page.getByLabel("neighborhood-id-input").fill("testing");
  await page.getByLabel("area-description-input").fill("testing");
});

// Overlay Search: bound box, area-description

test("I can search by bound box", async ({ page }) => {
  await expect(page.getByLabel("lat-center-input")).toBeVisible();
  await page.getByLabel("lat-center-input").fill("41.824");
  await page.getByLabel("lon-center-input").fill("-71.4128");
  await page.getByLabel("lat-length-input").fill("0.1");
  await page.getByLabel("lon-length-input").fill("0.1");
  await expect(page.getByLabel("lat-center-input")).toHaveValue("41.824");
  await expect(page.getByLabel("lon-center-input")).toHaveValue("-71.4128");
  await expect(page.getByLabel("lat-length-input")).toHaveValue("0.1");
  await expect(page.getByLabel("lon-length-input")).toHaveValue("0.1");
  await page.getByText("Search").click();
});

test("I can search by area description", async ({ page }) => {
  await expect(page.getByLabel("lat-center-input")).toBeVisible();
  await page.getByLabel("area-description-input").fill("pool");
  await expect(page.getByLabel("area-description-input")).toHaveValue("pool");
  await page.getByText("Search").click();
});

test("I can search by all boxes", async ({ page }) => {
  await expect(page.getByLabel("lat-center-input")).toBeVisible();
  await page.getByLabel("lat-center-input").fill("testing");
  await page.getByLabel("lon-center-input").fill("testing");
  await page.getByLabel("lat-length-input").fill("testing");
  await page.getByLabel("lon-length-input").fill("testing");
  await page.getByLabel("name-input").fill("testing");
  await page.getByLabel("state-input").fill("testing");
  await page.getByLabel("city-input").fill("testing");
  await page.getByLabel("holc-id-input").fill("testing");
  await page.getByLabel("holc-grade-input").fill("testing");
  await page.getByLabel("neighborhood-id-input").fill("testing");
  await page.getByLabel("area-description-input").fill("testing");
  await page.getByText("Search").click();
});

// Pins: can add mutliple, can remove, persist on reload

test("I can add pins", async ({ page }) => {
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 831,
      y: 416,
    },
  });
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 906,
      y: 381,
    },
  });
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
  await page.getByText("Clear Pins").click();
});

test("I can add and clear pins", async ({ page }) => {
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 831,
      y: 416,
    },
  });
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 906,
      y: 381,
    },
  });
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
  await page.getByText("Clear Pins").click();
  await expect(page.locator("path").first()).not.toBeVisible();
  await expect(page.locator("path").nth(2)).not.toBeVisible();
});

test("I can add pins and they persist on reload", async ({ page }) => {
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 831,
      y: 416,
    },
  });
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 906,
      y: 381,
    },
  });
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
  await page.goto("http://localhost:8000/");
  await page.getByRole("button", { name: "Section 2: Mapbox Demo" }).click();
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
});

test("I can add pins, clear, and they are gone on load", async ({ page }) => {
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 831,
      y: 416,
    },
  });
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 906,
      y: 381,
    },
  });
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
  await page.getByText("Clear Pins").click();
  await page.goto("http://localhost:8000/");
  await page.getByRole("button", { name: "Section 2: Mapbox Demo" }).click();
  await expect(page.locator("path").first()).not.toBeVisible();
  await expect(page.locator("path").nth(2)).not.toBeVisible();
});

test("I can add pins and they stay after search", async ({ page }) => {
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 831,
      y: 416,
    },
  });
  await page.getByLabel("Map", { exact: true }).click({
    position: {
      x: 906,
      y: 381,
    },
  });
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
  await page.getByText("Search").click();
  await expect(page.locator("path").first()).toBeVisible();
  await expect(page.locator("path").nth(2)).toBeVisible();
});

// test("I can add a word to my favorites list", async ({ page }) => {
//   await page.goto("http://localhost:8000/");
//   // - get the <p> elements inside the <ul> with aria-label="favorite-words"
//   const favoriteWords = await page.getByLabel("favorite-words");
//   await expect(favoriteWords).not.toContainText("hello");

//   await page.getByLabel("word-input").fill("hello");
//   await page.getByLabel("add-word-button").click();

//   const favoriteWordsAfter = await page.getByLabel("favorite-words");
//   await expect(favoriteWordsAfter).toContainText("hello");

//   // .. and this works on refresh
//   await page.reload();
//   const favoriteWordsAfterReload = await page.getByLabel("favorite-words");
//   await expect(favoriteWordsAfterReload).toContainText("hello");
// });
