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

public class SuccessPage {

    private WebDriver driver;
    private SelfHealingDriver shDriver;
    private WaitUtils wait;

    public SuccessPage(WebDriver driver) {
        this.driver = driver;
        this.shDriver = new SelfHealingDriver(driver);
        this.wait = new WaitUtils(driver);
    }


    private void waitForSuccessScreen() {
        System.out.println("Waiting for success screen to render...");
        WebDriverWait explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        try {
            explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("[data-testid='success-pnr']")
            ));
        } catch (Exception e) {
            System.out.println("[SelfHealing] Primary wait failed — retrying with XPath...");
            explicitWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[@data-testid='success-pnr']")
            ));
        }

        System.out.println("Success screen is visible.");
    }


    public String getPNR() {
        waitForSuccessScreen();

        WebElement pnrEl = wait.waitForElementVisible(
                shDriver.findElement("success-pnr")
        );

        String pnr = pnrEl.getText().trim();
        System.out.println("PNR: " + pnr);

        if (pnr.isEmpty()) {
            throw new RuntimeException(
                    "PNR text is empty — success screen may not have fully rendered."
            );
        }

        return pnr;
    }

    public boolean isPNRValid() {
        String pnr = getPNR();
        boolean valid = pnr.matches("MINT-\\d+");
        System.out.println("PNR valid: " + valid + " | value: " + pnr);
        return valid;
    }


    public String getTotalAmount() {
        waitForSuccessScreen();

        WebElement totalEl = wait.waitForElementVisible(
                shDriver.findElement("success-total")
        );

        String amount = totalEl.getText().trim();
        System.out.println("Raw total amount: " + amount);
        return amount;
    }

    public int getTotalAmountAsInt() {
        String raw = getTotalAmount();
        String numeric = raw.replaceAll("[^0-9]", "");

        if (numeric.isEmpty()) {
            throw new RuntimeException(
                    "Could not extract numeric value from amount: '" + raw + "'"
            );
        }

        int amount = Integer.parseInt(numeric);
        System.out.println("Parsed total amount (int): " + amount);
        return amount;
    }

    public boolean isBookingConfirmed() {
        waitForSuccessScreen();

        try {
            WebElement heading = wait.waitForElementVisible(
                    shDriver.findElement("booking-confirmed-heading")
            );
            boolean confirmed = heading.isDisplayed();
            System.out.println("Booking confirmed heading visible: " + confirmed);
            return confirmed;
        } catch (Exception e) {
            System.out.println("[SelfHealing] Heading check failed: " + e.getMessage());
            return false;
        }
    }

    public void clickBookAnotherTicket() {
        waitForSuccessScreen();

        // SelfHealingDriver resolves "book-another-ticket" through all JSON fallbacks
        WebElement btn = wait.waitForElementClickable(
                shDriver.findElement("book-another-ticket")
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", btn);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            btn.click();
            System.out.println("Clicked Book Another Ticket (direct click).");
        } catch (Exception e) {
            System.out.println("Direct click failed — using JS click.");
            js.executeScript("arguments[0].click();", btn);
            System.out.println("Clicked Book Another Ticket (JS click).");
        }
    }


    public int getPassengerRowCount() {
        waitForSuccessScreen();

        List<WebElement> rows = driver.findElements(
                By.cssSelector(".bg-gray-50.p-2.rounded.text-sm")
        );

        System.out.println("Passenger rows visible on success screen: " + rows.size());
        return rows.size();
    }
}