package utils;

import com.aventstack.extentreports.*;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import base.DriverManager;
import org.openqa.selenium.WebDriver;

public class ExtentTestListener implements ITestListener {

    private static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {

        WebDriver driver = DriverManager.getDriver();

        String screenshotPath = ScreenshotUtil.captureScreenshot(driver, result.getMethod().getMethodName());

        test.get().fail(result.getThrowable());

        try {
            test.get().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}