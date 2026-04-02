package pages;

import org.openqa.selenium.WebDriver;
import utils.SelfHealingDriver;
import utils.WaitUtils;

public class BookingPage {

    private SelfHealingDriver shDriver;
    private WaitUtils wait;

    public BookingPage(WebDriver driver) {
        this.shDriver = new SelfHealingDriver(driver);
        this.wait = new WaitUtils(driver);
    }

    public void enterName(String name) {
        wait.waitForElementVisible(shDriver.findElement("passenger-name-0")).sendKeys(name);
    }

    public void enterAge(String age) {
        wait.waitForElementVisible(shDriver.findElement("passenger-age-0")).sendKeys(age);
    }

    public void selectGender(String gender) {
        wait.waitForElementVisible(shDriver.findElement("passenger-gender-0")).sendKeys(gender);
    }

    public void clickPay() {
        wait.waitForElementClickable(shDriver.findElement("btn-pay")).click();
    }

    public void completeBooking(String name, String age, String gender) {
        enterName(name);
        enterAge(age);
        selectGender(gender);
        clickPay();
    }
}