package com.mftech.f;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class TestProviderTest {

    private ConverterProvider provider;
    private List<TestProperties> testCases;

    @Before
    public void initialization(){
        ChromeDriverManager.getInstance().setup();
        WebDriver driver = new ChromeDriver();
        provider = new ConverterProvider(driver);
    }

    @org.junit.Test
    public void GPBJPYTest() throws IOException {
        provider.goToConverter();
        try {
            this.testCases = provider.loadConfiguration(null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean result = this.testCases!=null;
        if(result){
            for(TestProperties test: this.testCases){
                provider.setCurrencyTo(test.getTo());
                provider.setValueToConvert(test.getSum());
                provider.setCurrencyFrom(test.getFrom());


                WebElement destination = provider.getDestinationRadioButton(test.getDestination());
                if(destination.isEnabled()){
                    destination.click();
                }
                WebElement source = provider.getSourceRadioButton(test.getSource());
                if(source.isEnabled()){
                    source.click();
                }
                WebElement servicePackage = provider.getServicePackageRadioButton(test.getServicePackage());
                if(servicePackage.isEnabled()){
                    servicePackage.click();
                }
                WebElement method = provider.getMethodExchangeRadioButton(test.getMethod());
                if(method.isEnabled()){
                    method.click();
                }
                provider.setTime(test.getDate());
                provider.convert();
                System.out.println(provider.getResult());
                System.out.println(test.getResult());
                System.out.println("------------");
                result = provider.getResult().toLowerCase().equals(test.getResult().toLowerCase());
            }
        }
        assertTrue(result);
        provider.quit();
    }

    @org.junit.Test
    public void multipleTest() throws IOException {
        provider.goToConverter();
        try {
            this.testCases = provider.loadConfiguration("src/test/resources/testdata2.xml");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean result = this.testCases!=null;
        if(result){
            for(TestProperties test: this.testCases){
                provider.setCurrencyTo(test.getTo());
                provider.setValueToConvert(test.getSum());
                provider.setCurrencyFrom(test.getFrom());


                WebElement destination = provider.getDestinationRadioButton(test.getDestination());
                if(destination.isEnabled()){
                    destination.click();
                }
                WebElement source = provider.getSourceRadioButton(test.getSource());
                if(source.isEnabled()){
                    source.click();
                }
                WebElement servicePackage = provider.getServicePackageRadioButton(test.getServicePackage());
                if(servicePackage.isEnabled()){
                    servicePackage.click();
                }
                WebElement method = provider.getMethodExchangeRadioButton(test.getMethod());
                if(method.isEnabled()){
                    method.click();
                }
                provider.setTime(test.getDate());
                provider.convert();
                System.out.println(provider.getResult());
                System.out.println(test.getResult());
                System.out.println("------------");
                result = provider.getResult().toLowerCase().equals(test.getResult().toLowerCase());
            }
        }
        assertTrue(result);
        provider.quit();
    }

}
