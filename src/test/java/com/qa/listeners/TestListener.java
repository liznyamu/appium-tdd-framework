package com.qa.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.qa.BaseTest;
import com.qa.report.ExtentReport;
import com.qa.utils.TestUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestListener implements ITestListener {
    TestUtils utils = new TestUtils();

    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null) {
            // print stacktrace on TestNG
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);
            utils.log().error(sw.toString());
        }

        // take screenshots on failure
        BaseTest base = new BaseTest();
        File file = base.getDriver().getScreenshotAs(OutputType.FILE);

        Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

        // relative image path
        String imagePath = "screenshots" + File.separator
                + params.get("platformName") + "_" + params.get("deviceName") + File.separator
                + base.getDateTime() + File.separator
                + result.getTestClass().getRealClass().getSimpleName() + File.separator
                + result.getName() + ".png";

        String completeImagePath = System.getProperty("user.dir") + File.separator + imagePath;

        try {
            // attach screenshot to TestNG report
            FileUtils.copyFile(file, new File(imagePath));
            Reporter.log("This is a sample screenshot");
            Reporter.log("<a href='" + completeImagePath + "'> " +
                    "<img src='" + completeImagePath + "' height='100' width='100'/> </a>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Extent Reports -- attach screenshot and exception
        //      - https://extentreports.com/docs/versions/4/java/index.html
        byte[] encoded;
        try {
            encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ExtentReport.getTest().fail("Test Failed",
                MediaEntityBuilder.createScreenCaptureFromBase64String(
                        new String (encoded, StandardCharsets.US_ASCII)).build());

        ExtentReport.getTest().fail(result.getThrowable());
    }

    public void onTestStart(ITestResult result) {
        BaseTest base = new BaseTest();
        ExtentReport.startTest(result.getName(), result.getMethod().getDescription())
                .assignCategory(base.getPlatform() + "_" + base.getDeviceName())
                .assignAuthor("Agatha Harkness");
    }

    public void onTestSuccess(ITestResult result) {
        ExtentReport.getTest().log(Status.PASS, "Test Passed");
    }

    public void onTestSkipped(ITestResult result) {
        ExtentReport.getTest().log(Status.SKIP, "Test Skipped");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO method details
    }

    public void onStart(ITestContext context) {
        // TODO method details
    }

    public void onFinish(ITestContext context) {
        ExtentReport.getReporter().flush();
    }

}
