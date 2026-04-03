package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.SelfHealingDriver;
import utils.WaitUtils;

import java.time.Duration;
import java.util.List;

public class SearchResultPage {

    private WebDriver driver;
    private SelfHealingDriver shDriver;
    private WaitUtils wait;

    public SearchResultPage(WebDriver driver) {
        this.driver = driver;
        this.shDriver = new SelfHealingDriver(driver);
        this.wait = new WaitUtils(driver);
    }


    private void waitForBusCards() {
        System.out.println("Waiting for bus cards...");
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid^='bus-card-']")
        ));
        System.out.println("Bus cards ready.");
    }


    public int getBusCount() {
        waitForBusCards();
        List<WebElement> cards = shDriver.findElements("bus-card");
        System.out.println("Currently visible bus cards: " + cards.size());
        return cards.size();
    }

    public int getTotalBusCount() {
        waitForBusCards();
        WebElement countEl = wait.waitForElementVisible(
                shDriver.findElement("bus-count")
        );
        String text = countEl.getText().trim();
        System.out.println("Total bus count text: " + text);

        String numeric = text.replaceAll("[^0-9]", "");
        if (numeric.isEmpty()) {
            throw new RuntimeException(
                    "Could not parse bus count from: '" + text + "'"
            );
        }
        return Integer.parseInt(numeric);
    }

    public void loadAllBuses() {
        waitForBusCards();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        int maxScrolls = 20;
        int previousCount = 0;
        int noChangeCount = 0;

        System.out.println("Starting lazy-load scroll loop...");

        for (int i = 0; i < maxScrolls; i++) {

            js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int currentCount = shDriver.findElements("bus-card").size();
            System.out.println("Scroll #" + (i + 1) + " - Visible bus cards: " + currentCount);

            List<WebElement> endMsgs = shDriver.findElements("end-message");
            if (!endMsgs.isEmpty() && endMsgs.get(0).isDisplayed()) {
                System.out.println("End message detected - all buses loaded.");
                break;
            }

            if (currentCount == previousCount) {
                noChangeCount++;
                if (noChangeCount >= 3) {
                    System.out.println("Count unchanged for 3 scrolls - stopping.");
                    break;
                }
            } else {
                noChangeCount = 0;
            }

            previousCount = currentCount;
        }

        System.out.println("Final loaded bus card count: " + shDriver.findElements("bus-card").size());
    }

    public int getFullyLoadedBusCount() {
        loadAllBuses();
        return getBusCount();
    }

    public void clickFirstBus() {
        waitForBusCards();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement viewBtn = shDriver.findElement("first-bus-view-seat");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", viewBtn);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            viewBtn.click();
            System.out.println("Clicked View on first bus (direct click).");
        } catch (Exception e) {
            System.out.println("Direct click failed - using JS click.");
            js.executeScript("arguments[0].click();", viewBtn);
            System.out.println("Clicked View on first bus (JS click).");
        }
    }


    public void clickBusByIndex(int index) {
        waitForBusCards();

        List<WebElement> viewButtons = shDriver.findElements("first-bus-view-seat");

        if (viewButtons.isEmpty()) {
            throw new RuntimeException(
                    "No View buttons found. Bus cards may not have rendered."
            );
        }

        if (index >= viewButtons.size()) {
            throw new RuntimeException(
                    "Index " + index + " out of range. "
                            + viewButtons.size() + " buses visible. "
                            + "Call loadAllBuses() first to load more."
            );
        }

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement btn = viewButtons.get(index);
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            btn.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", btn);
        }

        System.out.println("Clicked View button at index: " + index);
    }

    public void applyACFilter() {
        wait.waitForElementClickable(shDriver.findElement("filter-ac")).click();
        System.out.println("Applied AC filter.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void applyNonACFilter() {
        wait.waitForElementClickable(shDriver.findElement("filter-nonac")).click();
        System.out.println("Applied Non-AC filter.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isLazyLoadingVisible() {
        try {
            List<WebElement> loaders = shDriver.findElements("lazy-loader");
            boolean visible = !loaders.isEmpty() && loaders.get(0).isDisplayed();
            System.out.println("Lazy loader visible: " + visible);
            return visible;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isEndMessageVisible() {
        try {
            List<WebElement> msgs = shDriver.findElements("end-message");
            boolean visible = !msgs.isEmpty() && msgs.get(0).isDisplayed();
            System.out.println("End message visible: " + visible);
            return visible;
        } catch (Exception e) {
            return false;
        }
    }
}