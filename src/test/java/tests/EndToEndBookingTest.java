package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;
import utils.JsonReader;
import utils.RetryAnalyzer;

public class EndToEndBookingTest extends BaseTest {

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void verifyCompleteBookingFlow() {

        String from = JsonReader.getTestData("search", "from");
        String to = JsonReader.getTestData("search", "to");
        String date = JsonReader.getTestData("search", "date");
        String name = JsonReader.getTestData("passenger", "name");
        String age = JsonReader.getTestData("passenger", "age");
        String gender = JsonReader.getTestData("passenger", "gender");

        HomePage homePage = new HomePage(driver);
        SearchResultPage searchPage = new SearchResultPage(driver);
        SeatSelectionPage seatPage = new SeatSelectionPage(driver);
        BookingPage bookingPage = new BookingPage(driver);
        SuccessPage successPage = new SuccessPage(driver);

        homePage.searchBus(from, to, date);

        int busCount = searchPage.getFullyLoadedBusCount();
        System.out.println("Bus Count: " + busCount);
        Assert.assertTrue(busCount > 0, "No buses found!");

        searchPage.clickFirstBus();

        seatPage.selectAnySeat();

        String price = seatPage.getTotalPrice();
        System.out.println("Selected Price: " + price);
        Assert.assertTrue(price.contains("₹"), "Price not displayed correctly");

        seatPage.clickProceed();

        bookingPage.completeBooking(name, age, gender);

        String pnr = successPage.getPNR();
        String totalAmount = successPage.getTotalAmount();

        System.out.println("PNR: " + pnr);
        System.out.println("Total Paid: " + totalAmount);

        Assert.assertNotNull(pnr, "PNR not generated!");
        Assert.assertFalse(pnr.trim().isEmpty(), "PNR is empty!");
        Assert.assertTrue(pnr.startsWith("MINT"), "Invalid PNR format");
        Assert.assertTrue(totalAmount.contains("₹"), "Invalid total amount");

        System.out.println("Booking flow completed successfully!");
    }


}