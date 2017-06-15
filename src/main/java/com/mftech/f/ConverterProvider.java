package com.mftech.f;

import com.google.common.io.Files;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.yandex.qatools.allure.annotations.Step;
import sun.nio.cs.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by v.vanichkov on 11.06.2017.
 */
public class ConverterProvider {
    private final int GROUP_OF_SOURCE_ELEMENTS = 2;
    private final int GROUP_OF_DEST_ELEMENTS = 3;
    private final int GROUP_OF_EXC_METHOD_ELEMENTS = 4;
    private final int GROUP_OF_SERVICE_PACKAGE_ELEMENTS = 5;
    private final int GROUP_OF_TIME_ELEMENTS = 6;
    private final List<String> MONTHS = Arrays.asList(new String[]{"январь", "февраль", "март", "апрель",
            "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь"});





    private WebDriver driver;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
    private Calendar today = Calendar.getInstance();

    public ConverterProvider(WebDriver aDriver){
        //ChromeDriverManager.getInstance().setup();
        this.driver = aDriver;
    }

    /**
     * load configuration of test from file. As default,it should be from resources.
     */
    @Step
    public List<TestProperties> loadConfiguration(String path) throws ParserConfigurationException, IOException, SAXException, ParseException {
        File pathFile = new File(path==null?"src/test/resources/testdata.xml":path);
        if(!pathFile.exists()){
            pathFile=new File("src/test/resources/testdata.xml");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(pathFile);
        doc.getDocumentElement().normalize();

        int sizeOfList = Integer.valueOf(doc.getDocumentElement().getAttribute("number"));
        dateFormat = new SimpleDateFormat(doc.getDocumentElement().getAttribute("dateformat"));
        NodeList testCases = doc.getElementsByTagName("testcase");
        if(testCases.getLength()!=sizeOfList)
            Assert.fail("Number of testcase and assigned number of it are different");
        List<TestProperties> listOfTestCases = new ArrayList<TestProperties>();
        for(int i=0;i<sizeOfList;i++){
            Node testCase = testCases.item(i);
            if(testCase.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)testCase;
                int from = Integer.valueOf(element.getElementsByTagName("from").item(0).getTextContent());
                int to = Integer.valueOf(element.getElementsByTagName("to").item(0).getTextContent());
                Double sum = Double.valueOf(element.getElementsByTagName("sum").item(0).getTextContent());
                int source = Integer.valueOf(element.getElementsByTagName("source").item(0).getTextContent());
                int destination = Integer.valueOf(element.getElementsByTagName("destination").item(0).getTextContent());
                int method = Integer.valueOf(element.getElementsByTagName("method").item(0).getTextContent());
                int servicePackage = Integer.valueOf(element.getElementsByTagName("servicepackage").item(0).getTextContent());
                String type = element.getElementsByTagName("date").item(0).getAttributes().item(0).getNodeValue();
                String date = "CURRENT";
                if(type.toLowerCase().equals("CUSTOM".toLowerCase())){
                    date = element.getElementsByTagName("date").item(0).getTextContent();
                }
                String result = element.getElementsByTagName("result").item(0).getTextContent();
                listOfTestCases.add(new TestProperties(from,to,sum,source,destination,method, servicePackage, date, result));
            }
        }
        return listOfTestCases;
    }

    /**
     * Open the web-site with converter.
     */
    @Step
    public void goToConverter(){
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        driver.get("http://www.sberbank.ru/ru/quotes/converter");
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }


    /**
     * Method for setting the value for converter
     * @param value <code>Double</code> value.
     *              <code>Assert.fail()</code> in case of negative value
     */
    @Step
    public void setValueToConvert(Double value){
        if(value<0){
            Assert.fail("Negative sign of value. Change the dataset.");
        }
        WebElement input = driver.findElement(By.cssSelector(".rates-aside__filter-block-line-right input"));
        input.click();
        input.clear();
        input.sendKeys(value.toString());
        input.clear();
        input.sendKeys(value.toString());
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }


    /**
     * Method for select the value of initial currency .
     * @param pointInList - number of currency in the list.
     */
    @Step
    public void setCurrencyFrom( int pointInList){
        WebElement openListButton = driver.findElement(By.xpath("//select[@name='converterFrom']/following::div[@class='select']/header"));
        WebDriverWait wdw = new WebDriverWait(driver, 10);
        wdw.until(ExpectedConditions.elementToBeClickable(openListButton));
        openListButton.click();
        wdw.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='select opened']/div[@class='visible']/span")));
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='select opened']/div[@class='visible']/span"));
        wdw.until(ExpectedConditions.elementToBeClickable(list.get(pointInList-1)));
        list.get(pointInList-1).click();
    }

    /**
     * * Method for select the value of end currency.
     * @param pointInList number of currency in the list of elements.
     */
    @Step
    public void setCurrencyTo(int pointInList){
        WebElement openListButton = driver.findElement(By.xpath("//select[@name='converterTo']/following::div[@class='select']/header"));
        WebDriverWait wdw = new WebDriverWait(driver, 10);
        wdw.until(ExpectedConditions.elementToBeClickable(openListButton));
        openListButton.click();
        wdw.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='select opened']/div[@class='visible']/span")));
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='select opened']/div[@class='visible']/span"));
        wdw.until(ExpectedConditions.elementToBeClickable(list.get(pointInList-1)));
        list.get(pointInList-1).click();
    }


    /**
     * Method for getting the radio button inside Source group elements.
     * @param position <code>Integer</code> should be [1,3].
     * @return code>WebElement</code> inside of group in Source groups of elements.
     * if position less or equal to zero then return first element inside group.
     * if position greater or eqaul to 4 then return the last element inside group.
     */
    @Step
    public WebElement getSourceRadioButton(int position){
        if(position<=0)
            return this.getRadioButton(GROUP_OF_SOURCE_ELEMENTS,1);
        else if(position>=4){
            return this.getRadioButton(GROUP_OF_SOURCE_ELEMENTS,3);
        }else{
            return this.getRadioButton(GROUP_OF_SOURCE_ELEMENTS,position);
        }
    }


    /**
     * Method for getting the radio button inside Destination group elements.
     * @param position <code>Integer</code> should be [1,3].
     * @return code>WebElement</code> inside of group in Destination groups of elements.
     * if position less or equal to zero then return first element inside group.
     * if position greater or eqaul to 4 then return the last element inside group.
     */
    @Step
    public WebElement getDestinationRadioButton(int position){
        if(position<=0)
            return this.getRadioButton(GROUP_OF_DEST_ELEMENTS,1);
        else if(position>=4){
            return this.getRadioButton(GROUP_OF_DEST_ELEMENTS,3);
        }else{
            return this.getRadioButton(GROUP_OF_DEST_ELEMENTS,position);
        }
    }


    /**
     * Method for getting the radio button inside Exchange Method group elements.
     * @param position <code>Integer</code> should be [1,3].
     * @return code>WebElement</code> inside of group in Exchange method groups of elements.
     * if position less or equal to zero then return first element inside group.
     * if position greater or eqaul to 4 then return the last element inside group.
     */
    @Step
    public WebElement getMethodExchangeRadioButton(int position){
        if(position<=0)
            return this.getRadioButton(GROUP_OF_EXC_METHOD_ELEMENTS,1);
        else if(position>=4){
            return this.getRadioButton(GROUP_OF_EXC_METHOD_ELEMENTS,3);
        }else{
            return this.getRadioButton(GROUP_OF_EXC_METHOD_ELEMENTS,position);
        }
    }


    /**
     * Method for getting the radio button inside Service package group of elements.
     * @param position <code>Integer</code> should be [1,3].
     * @return <code>WebElement</code> inside of group in Service Package groups of elements.
     * if position less or equal to zero then return first element inside group.
     * if position greater or eqaul to 4 then return the last element inside group.
     */
    @Step
    public WebElement getServicePackageRadioButton(int position){
        if(position<=0)
            return this.getRadioButton(GROUP_OF_SERVICE_PACKAGE_ELEMENTS,1);
        else if(position>=4){
            return this.getRadioButton(GROUP_OF_SERVICE_PACKAGE_ELEMENTS,3);
        }else{
            return this.getRadioButton(GROUP_OF_SERVICE_PACKAGE_ELEMENTS,position);
        }
    }


    /**
     * Method for setting time. If time is equal CURRENT, then method click on
     * Current time radio button, in case of date with time it will set custom time.
     * @param time <code>String</code> should be correct according to dateFormat.
     *             In common case it is <String>dd-MM-yyyy hh:mm</String>.
     */
    @Step
    public void setTime(String time){
        if(time.toLowerCase().equals("CURRENT".toLowerCase())){
            WebElement currentTime = this.getRadioButton(GROUP_OF_TIME_ELEMENTS,1);
            currentTime.click();
        }else{
            try {
                Date customDate = this.dateFormat.parse(time);
                WebElement currentTime = this.getRadioButton(GROUP_OF_TIME_ELEMENTS,2);
                currentTime.click();
                this.setCustomTime(customDate);
            } catch (ParseException e) {
                e.printStackTrace();
                Assert.fail("You wrote wrong format of date in source file.");
            }
        }
    }

    /**
     * Method sets the custom date. Date should be less than current time.
     * @param date
     */
    private void setCustomTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //checking if day is correct to DateFormat but it will be in future.
        if(calendar.after(today))
            Assert.fail("Wrong input date. You select the day from future. Please, select another date.");
        //open the datepicker
        driver.findElement(By.xpath("//div[@class='rates-aside__filter-block'][6]" +
                "/div[@class='rates-date-picker__wrapper']/button[@class='rates-date-picker__trigger']")).click();

        //open list of years
        driver.findElement(By.className("select2-chosen")).click();

        //select the year
        List<WebElement> listOfYear = driver.findElements(By.xpath("//li[@role='presentation']"));
        String yearToString = new SimpleDateFormat("yyyy").format(date);

        //find the custom year from current to previous.
        for(int i=listOfYear.size()-1;i>=0;i--){
            if(yearToString.equals(listOfYear.get(i).getText())){
                listOfYear.get(i).click();
                break;
            }
        }
        WebElement month = driver.findElement(By.xpath("//span[@class='ui-datepicker-month']"));
        //find the difference between current month and custom
        int n = MONTHS.indexOf(month.getText().toLowerCase()) - calendar.get(Calendar.MONTH);
        if(n>0){//if difference is greater zero
            for(int i=0;i<n;i++)//then we click to left exact n times
                driver.findElement(By.xpath("//a[@data-handler='prev']")).click();
        }else if(n<0){//in case of negative value of n we click to right exact n times
                for(int i=0;i<Math.abs(n);i++)
                    driver.findElement(By.xpath("//a[@data-handler='next']")).click();

        }//in case of n equals zero valoue of month will not changed.
        //get the day of month
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        List<WebElement> listOfDays = driver.findElements(By.xpath("//a[@class='ui-state-default']"));
        //check the each element and compare the text of element and selected day.
        for(WebElement i: listOfDays){
            if( Integer.valueOf(i.getText()) == day){
                i.click();
                break;
            }
        }
        //select the element DropDownList of hours and set it.
        WebElement buttonForSelect = driver.findElement(By.xpath("//select[@class='ui-timepicker-select ui-state-default ui-corner-all'][@data-unit='hour']"));
        Select hourSelect = new Select(buttonForSelect);
        hourSelect.selectByValue(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));

        //select the element DropDownList of minutes and set it.
        buttonForSelect = driver.findElement(By.xpath("//select[@class='ui-timepicker-select ui-state-default ui-corner-all'][@data-unit='minute']"));
        Select minuteSelect = new Select(buttonForSelect);
        //if minutes multiplicity is equal 5 than select custom value in other cases we fail test.
        if(calendar.get(Calendar.MINUTE)%5 == 0)
            minuteSelect.selectByValue(String.valueOf(calendar.get(Calendar.MINUTE)));
        else{
            Assert.fail("Wrong format of minutes. Select correct time with step in 5 minutes from 00 to 55.");
        }
        //end of time selection and press button 'Выбрать'
        String parameter = new String("//span[@class='rates-button rates-button_converter-datepicker-hide'][text()='\u0412\u044b\u0431\u0440\u0430\u0442\u044c']");
        driver.findElement(By.xpath(parameter)).click();
    }


    /**
     * Method clicks on button 'Показать' for converting.
     */
    @Step
    public void convert(){
        //convert the sum
        driver.findElement(By.xpath(new String("//button[@class='rates-button'][text()='\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c']"))).click();
        driver.manage().timeouts().implicitlyWait(2,TimeUnit.SECONDS);
    }


    /**
     *  Method for getting the result of conversion.
     * @return <code>String</code> value in numbers without currency.
     *          empty string in case if label with result is not displayed or enabled.
     */
    @Step
    public String getResult(){
        driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
        WebElement summary = driver.findElement(By.xpath("//span[@class='rates-converter-result__total-to']"));//find label with result
        String result = "";
        WebDriverWait wdw = new WebDriverWait(driver,3);
        wdw.until(ExpectedConditions.visibilityOf(summary));
        if (summary.isEnabled() && summary.isDisplayed()) {//if result is available
            result = summary.getText();//.substring(0,summary.getText().indexOf(" "));//then return result with value from label
            result = result.substring(0,result.lastIndexOf(" ")).replaceAll(" ","")
                    +summary.getText().substring(result.lastIndexOf(" "));
        }//else ""
        return result;
    }


    /**
     *
     * @return
     */
    @Step
    public Object getEtalonResult(){
        return null;
    }

    /**
     * Method for searching on widget of currency converter for radio-buttons.
     * @param labelPosition - position of group of elements.
     * @param positionOfButton - position inside of group of elements
     * @return <code>WebElement</code> from selected group and its position.
     */
    private WebElement getRadioButton(int labelPosition, int positionOfButton){
        return driver.findElement(
                By.xpath("//div[@class='rates-aside__filter-block'][" +
                        labelPosition +
                        "]/label["+
                        positionOfButton+
                        "]/span[@class='radio']"));
    }

    @Step
    public void quit() {
        driver.quit();
    }
}
