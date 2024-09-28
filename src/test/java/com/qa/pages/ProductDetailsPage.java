package com.qa.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class ProductDetailsPage extends MenuPage {

    @AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Description\"]/child::android.widget.TextView)[1]")
//    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::*[1]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::XCUIElementTypeStaticText[1]")
    WebElement SLBTitle;

    @AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Description\"]/child::android.widget.TextView)[2]")
//    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::*[2]")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Description\"]/child::XCUIElementTypeStaticText[2]")
    WebElement SLBTxt;

    @AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy(id = "test-BACK TO PRODUCTS")
    WebElement backToProductsBtn;

    @iOSXCUITFindBy(id = "test-ADD TO CART") WebElement addToCartBtn;
    @iOSXCUITFindBy(id = "test-Price") WebElement SLBPrice;

    public ProductDetailsPage() {
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public String getSLBTitle() {
        String title = getText(SLBTitle);
        System.out.println("title is - " + title);
        return title;
    }

    public String getSLBTxt() {
        String txt = getText(SLBTxt);
        System.out.println("txt is - " + txt);
        return txt;
    }

    public String scrollToSLBPriceAndGetSLBPrice(){
        switch (platform){
            case "Android":
                return getText(androidScrollToElement());
            case "iOS":
                // TODO - this scroll is for tutorial purposes -- its not need as the SLB price is visible on iOS
                iOSScrollToElement();
                return getText(SLBPrice);
        }
        return  null;
    }

    public void scrollPage(){
        iOSScrollToElement();
    }

    public Boolean isAddToCartBtnDisplayed(){
        return addToCartBtn.isDisplayed();
    }

    public ProductsPage pressBackToProductsBtn(){
        System.out.println("navigate back to products page");
        click(backToProductsBtn);
        return new ProductsPage();
    }

}
