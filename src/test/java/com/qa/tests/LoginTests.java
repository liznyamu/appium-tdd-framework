package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public class LoginTests extends BaseTest {

    LoginPage loginPage;
    ProductsPage productsPage;

    @BeforeClass
    public void beforeClass(){

    }

    @AfterClass
    public void afterClass(){

    }

    @BeforeMethod
    public void beforeMethod(Method m){
        loginPage = new LoginPage();
        System.out.println("\n****** starting test: " + m.getName() + " ******\n");

    }

    @AfterMethod
    public void afterMethod(){

    }

    @Test
    public void invalidUserName(){
        loginPage.enterUserName("invalidusername");
        loginPage.enterPassword("secret_sauce");
        loginPage.pressLoginBtn();

        String actualTxt = loginPage.getErrTxt();
              String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error txt - " + actualTxt + "\nexpected error txt - " + expectedErrTxt);

        Assert.assertEquals(actualTxt, expectedErrTxt);
    }

    @Test
    public void invalidPassword(){
        loginPage.enterUserName("standard_user");
        loginPage.enterPassword("invalidPassword");
        loginPage.pressLoginBtn();

        String actualTxt = loginPage.getErrTxt();
        String expectedErrTxt = "Username and password do not match any user in this service.";
        System.out.println("actual error txt - " + actualTxt + "\nexpected error txt - " + expectedErrTxt);
        Assert.assertEquals(actualTxt, expectedErrTxt);
    }

    @Test
    public void successfulLogin(){
        loginPage.enterUserName("standard_user");
        loginPage.enterPassword("secret_sauce");
        productsPage = loginPage.pressLoginBtn();


        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = "PRODUCTS";
        System.out.println("actual product title - " + actualProductTitle + "\nexpected product txt - " + expectedProductTitle);
        Assert.assertEquals(actualProductTitle, expectedProductTitle);
    }



}
