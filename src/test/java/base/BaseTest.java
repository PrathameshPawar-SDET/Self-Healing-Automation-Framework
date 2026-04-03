package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;
import utils.SelfHealingDriver;

public class BaseTest {

    protected WebDriver driver;
    protected SelfHealingDriver shDriver;

    @BeforeMethod
    public void setUp() {


        String browser = ConfigReader.getProperty("browser");
        String url = ConfigReader.getProperty("url");

        DriverManager.initDriver(browser);

        driver = DriverManager.getDriver();

        driver.get(url);

        shDriver = new SelfHealingDriver(driver);
    }

    @AfterMethod
    public void tearDown() {
        DriverManager.quitDriver();
    }
}