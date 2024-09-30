package com.qa.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.util.HashMap;
import java.util.Map;

// Refer - https://extentreports.com/docs/versions/2/java/#parallel-classes
//         https://extentreports.com/docs/versions/4/java/index.html

public class ExtentReport {
    static ExtentReports extent;
    final static String filePath = "ExtentSpark.html";
    static Map<Integer, ExtentTest> extentTestMap = new HashMap<>();

    public synchronized static ExtentReports getReporter() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter(filePath);
            spark.config().setDocumentTitle("Appium Framework");
            spark.config().setReportName("SauceLabsApp");
            spark.config().setTheme(Theme.DARK);
            extent = new ExtentReports();
            extent.attachReporter(spark);
        }

        return extent;
    }

    public static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) (Thread.currentThread().threadId()));
    }

    public static synchronized ExtentTest startTest(String testName, String desc) {
        ExtentTest test = getReporter().createTest(testName, desc);
        extentTestMap.put((int) (Thread.currentThread().threadId()), test);
        return test;
    }
}
