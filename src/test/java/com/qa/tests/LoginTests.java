package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import com.qa.utils.TestUtils;
import io.appium.java_client.AppiumBy;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class LoginTests extends BaseTest {

    LoginPage loginPage;
    ProductsPage productsPage;

    // Note: we are not manipulating data on this class variable - we're just reading data from it
    //          - no need to make it a local variable / thread local
    JSONObject loginUsers;
    TestUtils utils = new TestUtils();


    @BeforeClass
    public void beforeClass() {
        String dataFileName = "data/loginUsers.json";
        try (InputStream datais = getClass().getClassLoader().getResourceAsStream(dataFileName)) {
            JSONTokener tokener = null;
            if (datais != null) {
                tokener = new JSONTokener(datais);
                loginUsers = new JSONObject(tokener);
            }
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public void afterClass() {

    }

    @BeforeMethod
    public void beforeMethod(Method m) {
        launchApp();
        loginPage = new LoginPage();
        utils.log().info("\n****** starting test: " + m.getName() + " ******\n");
    }

    @AfterMethod
    public void afterMethod() {
        closeApp();
    }

    @Test
    public void invalidUserName() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
        loginPage.pressLoginBtn();

        String actualTxt = loginPage.getErrTxt();
        String expectedErrTxt = getStrings().getProperty("err_invalid_username_or_password") + "failed test";
        Assert.assertEquals(actualTxt, expectedErrTxt);
    }

    @Test
    public void invalidPassword() {
        loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
        loginPage.pressLoginBtn();

        String actualTxt = loginPage.getErrTxt();
        String expectedErrTxt = getStrings().getProperty("err_invalid_username_or_password");
        Assert.assertEquals(actualTxt, expectedErrTxt);
    }

    @Test
    public void successfulLogin() {
        loginPage.enterUserName(loginUsers.getJSONObject("validUser").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
        productsPage = loginPage.pressLoginBtn();


        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = getStrings().getProperty("product_title");
        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }


}
