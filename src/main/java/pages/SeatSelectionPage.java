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

public class SeatSelectionPage {

    private WebDriver driver;
    private SelfHealingDriver shDriver;
    private WaitUtils wait;

    public SeatSelectionPage(WebDriver driver) {
        this.driver = driver;
        this.shDriver = new SelfHealingDriver(driver);
        this.wait = new WaitUtils(driver);
    }

    private void waitForSeatLayout() {
        System.out.println("Waiting for seat layout...");
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid^='seat-']")
        ));
        System.out.println("Seat layout ready.");
    }

    public void selectAnySeat() {
        waitForSeatLayout();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 600)");

        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        List<WebElement> allSeats = shDriver.findElements("seat-any");

        if (allSeats.isEmpty()) {
            System.out.println("[SelfHealing] seat-any empty — direct CSS fallback.");
            allSeats = driver.findElements(By.cssSelector("[data-testid^='seat-']"));
        }

        if (allSeats.isEmpty()) {
            throw new RuntimeException("No seats found — seat layout may not have rendered.");
        }

        WebElement targetSeat = null;
        for (WebElement seat : allSeats) {
            String classes = seat.getAttribute("class");
            if (classes != null
                    && !classes.contains("bg-gray-300")
                    && !classes.contains("cursor-not-allowed")
                    && classes.contains("border")) {
                targetSeat = seat;
                break;
            }
        }

        if (targetSeat == null) {
            throw new RuntimeException(
                    "No available seat found — all visible seats may be booked."
            );
        }

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", targetSeat);
        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

        String seatId = targetSeat.getAttribute("data-testid");

        try {
            targetSeat.click();
            System.out.println("Seat selected (direct click): " + seatId);
        } catch (Exception e) {
            System.out.println("Direct click failed — JS click for seat: " + seatId);
            js.executeScript("arguments[0].click();", targetSeat);
            System.out.println("Seat selected (JS click): " + seatId);
        }

        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public String getTotalPrice() {
        WebElement priceEl = wait.waitForElementVisible(
                shDriver.findElement("seat-total-price")
        );
        String raw = priceEl.getText().trim();
        System.out.println("Raw price text: " + raw);
        return raw;
    }

    public int getTotalPriceAsInt() {
        String raw = getTotalPrice();
        String numeric = raw.replaceAll("[^0-9]", "");

        if (numeric.isEmpty()) {
            throw new RuntimeException(
                    "Could not extract numeric price from: '" + raw + "'"
            );
        }

        int price = Integer.parseInt(numeric);
        System.out.println("Parsed price (int): " + price);
        return price;
    }

    public void clickProceed() {
        WebElement proceedBtn = wait.waitForElementClickable(
                shDriver.findElement("btn-proceed-booking")
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", proceedBtn);

        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

        try {
            proceedBtn.click();
            System.out.println("Clicked PROCEED (direct click).");
        } catch (Exception e) {
            System.out.println("Direct click failed on PROCEED — JS click.");
            js.executeScript("arguments[0].click();", proceedBtn);
            System.out.println("Clicked PROCEED (JS click).");
        }
    }
}