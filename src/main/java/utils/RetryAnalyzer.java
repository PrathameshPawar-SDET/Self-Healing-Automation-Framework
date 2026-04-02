package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int count = 0;
    private int maxTry = Integer.parseInt(ConfigReader.getProperty("retry.count"));

    @Override
    public boolean retry(ITestResult result) {
        if (count < maxTry) {
            count++;
            System.out.println("Retrying test: " + result.getName());
            return true;
        }
        return false;
    }
}