package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductTests extends BaseTest {

    LoginPage loginPage;
    ProductsPage productsPage;
    SettingsPage settingsPage;
    ProductDetailsPage productDetailsPage;

    JSONObject loginUsers;


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

//        closeApp();
//        launchApp();
    }

    @AfterClass
    public void afterClass() {

    }

    @BeforeMethod
    public void beforeMethod(Method m) {
        launchApp();

        loginPage = new LoginPage();
        System.out.println("\n****** starting test: " + m.getName() + " ******\n");

        productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),
                loginUsers.getJSONObject("validUser").getString("password"));

    }

    @AfterMethod
    public void afterMethod() {
        closeApp();
//        settingsPage = productsPage.pressSettingsBtn();
//        settingsPage.pressLogoutBtn();
    }

    @Test
    public void validateProductOnProductsPage() {
        SoftAssert sa = new SoftAssert();


        String SLBTitle = productsPage.getSLBTitle();
        sa.assertEquals(SLBTitle, strings.getProperty("products_page_slb_title"));

        String SLBPrice = productsPage.getSLBPrice();
        sa.assertEquals(SLBPrice, strings.getProperty("products_page_slb_price"));

        sa.assertAll();
    }

    @Test
    public void validateProductOnProductsDetailsPage() {
        SoftAssert sa = new SoftAssert();

        productDetailsPage = productsPage.pressSLBTitle();
        productDetailsPage.getSLBTitle();


        String SLBTitle = productDetailsPage.getSLBTitle();
        sa.assertEquals(SLBTitle, strings.getProperty("product_details_page_slb_title"));

        String SLBPrice = productDetailsPage.getSLBTxt();
        sa.assertEquals(SLBPrice, strings.getProperty("product_details_page_slb_txt"));

        sa.assertAll();

        productsPage = productDetailsPage.pressBackToProductsBtn();
    }


}
