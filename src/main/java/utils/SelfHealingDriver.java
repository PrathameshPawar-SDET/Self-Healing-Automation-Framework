package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SelfHealingDriver {

    private WebDriver driver;
    private JSONObject locatorRepo;

    // Short wait per locator attempt before trying next fallback
    private static final int FALLBACK_WAIT_SECONDS = 3;

    public SelfHealingDriver(WebDriver driver) {
        this.driver = driver;
        this.locatorRepo = loadLocators();
    }

    private JSONObject loadLocators() {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream("LocatorRepository.json");
            if (is == null) {
                throw new RuntimeException(
                        "LocatorRepository.json not found in src/test/resources"
                );
            }
            JSONObject repo = new JSONObject(new JSONTokener(is));
            System.out.println("JSON Loaded Successfully");
            return repo;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load LocatorRepository.json: " + e.getMessage(), e
            );
        }
    }

    private By resolveBy(String locator) {
        if (locator.startsWith("/") || locator.startsWith("(")) {
            return By.xpath(locator);
        }
        return By.cssSelector(locator);
    }


    public WebElement findElement(String key) {
        if (!locatorRepo.has(key)) {
            throw new NoSuchElementException(
                    "[SelfHealing] Key '" + key + "' not found in LocatorRepository.json"
            );
        }

        JSONArray locators = locatorRepo.getJSONArray(key);
        WebDriverWait shortWait = new WebDriverWait(
                driver, Duration.ofSeconds(FALLBACK_WAIT_SECONDS)
        );

        for (int i = 0; i < locators.length(); i++) {
            String locatorStr = locators.getString(i);
            By by = resolveBy(locatorStr);

            try {
                WebElement element = shortWait.until(
                        ExpectedConditions.presenceOfElementLocated(by)
                );
                if (i > 0) {
                    System.out.println(
                            "[SelfHealing] HEALED: key='" + key
                                    + "' used fallback #" + (i + 1)
                                    + " → " + locatorStr
                    );
                }
                return element;
            } catch (Exception e) {
                System.out.println(
                        "[SelfHealing] Locator #" + (i + 1)
                                + " failed for key='" + key
                                + "' → " + locatorStr
                );
            }
        }

        throw new NoSuchElementException(
                "[SelfHealing] All locators exhausted for key: '" + key + "'"
        );
    }

    public List<WebElement> findElements(String key) {
        if (!locatorRepo.has(key)) {
            System.out.println(
                    "[SelfHealing] Key '" + key + "' not found — returning empty list"
            );
            return new ArrayList<>();
        }

        JSONArray locators = locatorRepo.getJSONArray(key);

        for (int i = 0; i < locators.length(); i++) {
            String locatorStr = locators.getString(i);
            By by = resolveBy(locatorStr);

            try {
                List<WebElement> elements = driver.findElements(by);
                if (!elements.isEmpty()) {
                    if (i > 0) {
                        System.out.println(
                                "[SelfHealing] HEALED (findElements): key='" + key
                                        + "' used fallback #" + (i + 1)
                                        + " → " + locatorStr
                        );
                    }
                    return elements;
                }
            } catch (Exception e) {
                System.out.println(
                        "[SelfHealing] findElements locator #" + (i + 1)
                                + " failed for key='" + key
                                + "' → " + locatorStr
                );
            }
        }

        System.out.println(
                "[SelfHealing] All locators returned empty for key: '" + key + "'"
        );
        return new ArrayList<>();
    }
}