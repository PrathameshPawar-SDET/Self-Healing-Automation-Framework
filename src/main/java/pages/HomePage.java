package pages;

import org.openqa.selenium.*;
import utils.SelfHealingDriver;
import utils.WaitUtils;

public class HomePage {

    private WebDriver driver;
    private SelfHealingDriver shDriver;
    private WaitUtils wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.shDriver = new SelfHealingDriver(driver);
        this.wait = new WaitUtils(driver);
    }

    private void selectFromSuggestion(String testIdPrefix, String value) {
        WebElement input = driver.findElement(
                By.cssSelector("input[data-testid='" + testIdPrefix + "-input']")
        );

        input.click();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
                        "    window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                input, value
        );

        try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

        String cityName = value.split(",")[0].trim();
        WebElement option = driver.findElement(
                By.cssSelector("li[data-testid='" + testIdPrefix + "-result-" + cityName + "']")
        );
        option.click();

        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("Selected " + testIdPrefix + ": " + value);
    }

    public void enterFrom(String from) { selectFromSuggestion("source", from); }
    public void enterTo(String to)     { selectFromSuggestion("dest",   to);   }

    public void selectDate(String dateValue) {
        WebElement hiddenDateInput = driver.findElement(
                By.cssSelector("input[data-testid='date-input']")
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "var nativeInputValueSetter = Object.getOwnPropertyDescriptor(" +
                        "    window.HTMLInputElement.prototype, 'value').set;" +
                        "nativeInputValueSetter.call(arguments[0], arguments[1]);" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                hiddenDateInput, dateValue
        );

        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("Date set to: " + dateValue);
    }

    public void clickSearch() {
        wait.waitForElementClickable(shDriver.findElement("search_button")).click();

        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("Alert: " + alert.getText());
            alert.accept();
            throw new RuntimeException("Search failed due to missing inputs");
        } catch (NoAlertPresentException e) {

        }
    }

    public void searchBus(String from, String to, String date) {
        enterFrom(from);
        enterTo(to);
        selectDate(date);
        clickSearch();
    }
}