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

    // Note: we are not manipulating data on this class variable - we're just reading data from it
    //          - no need to make it a local variable / thread local
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
/*       TODO remove below - no longer needed -- check why iOS fails to click the settings btn
        settingsPage = productsPage.pressSettingsBtn();
        settingsPage.pressLogoutBtn();*/
    }

    @Test
    public void validateProductOnProductsPage() {
        SoftAssert sa = new SoftAssert();


        String SLBTitle = productsPage.getSLBTitle();
        sa.assertEquals(SLBTitle, getStrings().getProperty("products_page_slb_title"));

        String SLBPrice = productsPage.getSLBPrice();
        sa.assertEquals(SLBPrice, getStrings().getProperty("products_page_slb_price"));

        sa.assertAll();
    }

    @Test
    public void validateProductOnProductsDetailsPage() {
        SoftAssert sa = new SoftAssert();

        productDetailsPage = productsPage.pressSLBTitle();

        String SLBTitle = productDetailsPage.getSLBTitle() + "fail test";
        sa.assertEquals(SLBTitle, getStrings().getProperty("product_details_page_slb_title"));

        String SLBTxt = productDetailsPage.getSLBTxt();
        sa.assertEquals(SLBTxt, getStrings().getProperty("product_details_page_slb_txt"));

        String SLBPrice = productDetailsPage.scrollToSLBPriceAndGetSLBPrice();
        sa.assertEquals(SLBPrice, getStrings().getProperty("products_details_page_slb_price"));


       /* // NOTE this check was for iOS tutorial only - the SLBPrice is already visible on iOS
        productDetailsPage.scrollPage();
        sa.assertTrue(productDetailsPage.isAddToCartBtnDisplayed(), "Add to Cart Btn is displayed");
*/
        sa.assertAll();

        productsPage = productDetailsPage.pressBackToProductsBtn();
    }


}
