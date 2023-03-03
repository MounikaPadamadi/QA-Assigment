package tests;


import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import org.testng.annotations.Test;

import excelUtilities.ExcelReader;

public class PlanitTests {

	WebDriver driver;
	ExcelReader obj;
	Object[][] data = null;
	Properties prop;
	String baseurl;
	String browser = "edge";

	/**
	 * Function to read configurations file which contains url of application
	 * under test
	 * 
	 * @param none
	 * @throws none
	 */
	public void readConfigFile() {

		try {
			// Read configuration file for Login
			prop = new Properties();
			FileInputStream stream = new FileInputStream("config.properties");
			prop.load(stream);
			baseurl = prop.getProperty("url");
		} catch (IOException e) {

			System.out.println("File Read exception :" + e);
		}
	}

	/**
	 * This function will execute before each Test tag in testng.xml
	 * 
	 * @param browser
	 * @throws Exception
	 */
	@BeforeTest
	public void setupTest() throws Exception {
		System.out.println("Browser = " + browser);

		readConfigFile();
		// Check if parameter passed from TestNG is 'firefox'
		if (browser.equalsIgnoreCase("firefox")) {
			// create firefox instance
			System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "//Drivers//geckodriver.exe");
			driver = new FirefoxDriver();
			System.out.println("Executing from Firefox");
		}
		// Check if parameter passed as 'chrome'
		else if (browser.equalsIgnoreCase("chrome")) {
			// set path to chromedriver.exe
			System.setProperty("webdriver.chrome.driver",
					System.getProperty("user.dir") + "\\Drivers\\chromedriver1.exe");
			// create chrome instance
			driver = new ChromeDriver();
			System.out.println("Executing from Chrome");
		}
		// Check if parameter passed as 'edge'
		else if (browser.equalsIgnoreCase("edge")) {
			// set path to IE.exe
			System.setProperty("webdriver.edge.driver",
					System.getProperty("user.dir") + "//Drivers//msedgedriver1.exe");
			// create IE instance
			driver = new EdgeDriver();
			System.out.println("Executing from Edge");
		} else {
			// If no browser passed throw exception
			throw new Exception("Browser is not correct");
		}

	}

	/**
	 * Function to invoke browser with the specified url in the configuration
	 * file
	 * 
	 * @param none
	 * @throws none
	 */
	@BeforeMethod
	public void invokeBrowser() {
		// driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		// Open URL
		driver.get(baseurl);
	}

	/**
	 * Function to read data from the excel and store in local object
	 * 
	 * @param none
	 * @throws none
	 */
	@DataProvider(name = "mandatoryFields")
	public Object[][] populateData() {
		try {
			obj = new ExcelReader();
			data = obj.readData();
			// verify Data by print
			System.out.println("***** Data *****");
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					System.out.println("data[" + i + "][" + j + "]=" + data[i][j]);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException caught :" + e);
		}
		return data;
	}

	/**
	 * Function to Test scenario 1 Verify error messages Populate mandatory
	 * fields Validate errors are gone
	 * 
	 * @param none
	 * 
	 * @throws InterruptedException
	 * @throws none
	 */
	@Test
	public void test1() throws InterruptedException {

		// Click Contacts page
		driver.findElement(By.xpath("//*[@id='nav-contact']/a")).click();

		try {
			Thread.sleep(2000);
			System.out.println("Contact page title: " + driver.getTitle());

			// Click Submit button
			driver.findElement(By.xpath("//a[normalize-space()='Submit']")).click();

			Thread.sleep(2000);
			// identify Forename Error message

			boolean display = driver.findElement(By.xpath("//span[@id='forename-err']")).isDisplayed();

			if (display) {
				// Enter Forename to remove error message
				driver.findElement(By.xpath("//input[@id='forename']")).sendKeys("Test");
				// Check if Error is gone
				Thread.sleep(1000);
				try {
					display = driver.findElement(By.xpath("//span[@id='forename-err']")).isDisplayed();

				} catch (Exception e) {
					Assert.assertEquals(display, true, "Forname error removed");
				}

			}

			// identify Email Error message

			display = driver.findElement(By.xpath("//span[@id='email-err']")).isDisplayed();

			if (display) {
				// Enter Email to remove error message
				driver.findElement(By.xpath("//input[@id='email']")).sendKeys("abc@test.com");
				// Check if Error is gone
				Thread.sleep(1000);
				try {
					display = driver.findElement(By.xpath("//span[@id='email-err']")).isDisplayed();
				} catch (Exception e) {
					Assert.assertEquals(display, true, "Email error removed");
				}
			}

			// identify Message error message

			display = driver.findElement(By.xpath("//span[@id='message-err']")).isDisplayed();

			if (display) {
				// Enter Message to remove error message
				driver.findElement(By.xpath("//textarea[@id='message']")).sendKeys("This is test message");
				// Check if Error is gone
				Thread.sleep(1000);
				try {
					display = driver.findElement(By.xpath("//span[@id='message-err']")).isDisplayed();
				} catch (Exception e) {
					Assert.assertEquals(display, true, "Message error removed");
				}

			}

		} catch (Exception e) {
			System.out.println("Exception caught :" + e);

		}

	}

	/**
	 * Function to Test scenario 2 Validate successful submission message five
	 * times
	 * 
	 * @param name,email, message
	 *            
	 * @throws none
	 */

	@Test(dataProvider = "mandatoryFields")
	public void test2(String name, String email, String message) {

		try {
			// Click Contacts page
			driver.findElement(By.xpath("//*[@id='nav-contact']/a")).click();

			// Enter Forename
			driver.findElement(By.xpath("//input[@id='forename']")).sendKeys(name);

			// Enter Email
			driver.findElement(By.xpath("//input[@id='email']")).sendKeys(email);

			// Enter Message
			driver.findElement(By.xpath("//textarea[@id='message']")).sendKeys(message);

			// Click Submit button
			driver.findElement(By.xpath("//a[normalize-space()='Submit']")).click();

			// Validate form submission
			String msg = driver.findElement(By.xpath("//div[@class='alert alert-success']")).getText();

			// Validate message
			boolean validation = false;

			if (msg.contains("we appreciate your feedback")) {
				validation = true;
			}

			Assert.assertEquals(validation, true, "Form Submission not successful !!");

		} catch (Exception e) {
			System.out.println("Exception caught :" + e);

		}
	}

	/**
	 * Function to Test scenario 3 Verify the subtotal for each product is
	 * correct Verify the price for each product Verify that total = sum(sub
	 * totals)
	 * 
	 * @param none
	 * @throws none
	 */

	@Test
	public void test3() {
		final DecimalFormat decfor = new DecimalFormat("0.00");
		int frogNo = 2;
		int bunnyNo = 5;
		int bearNo = 3;

		try {
			// Click on Shop tab
			driver.findElement(By.xpath("//a[normalize-space()='Shop']")).click();

			// Get price and Click buy item for Stuffed Frog
			String temp = driver
					.findElement(By.xpath("(//span[@class='product-price ng-binding'][normalize-space()='$10.99'])[1]"))
					.getText();
			temp = temp.replace("$", "");
			float initalFrogCost = Float.parseFloat(temp);

			driver.findElement(By.xpath("(//a[@class='btn btn-success'][normalize-space()='Buy'])[2]")).click();

			// Get price and Click buy item for Fluffy bunny
			String temp1 = driver
					.findElement(By.xpath("(//span[@class='product-price ng-binding'][normalize-space()='$9.99'])[1]"))
					.getText();
			temp1 = temp1.replace("$", "");
			float initalBunnyCost = Float.parseFloat(temp1);
			driver.findElement(By.xpath("(//a[@class='btn btn-success'][normalize-space()='Buy'])[4]")).click();

			// Get Price and Click buy item for valentine bear
			String temp2 = driver
					.findElement(By.xpath("(//span[@class='product-price ng-binding'][normalize-space()='$14.99'])[2]"))
					.getText();
			temp2 = temp2.replace("$", "");
			float initalBearCost = Float.parseFloat(temp2);
			driver.findElement(By.xpath("(//a[@class='btn btn-success'][normalize-space()='Buy'])[7]")).click();

			// Go to Carts Page
			driver.findElement(By.xpath("//a[@href='#/cart']")).click();

			// Add 2 quantity for Stuffed Frog
			driver.findElement(By.xpath("(//input[@name='quantity'])[1]")).clear();
			driver.findElement(By.xpath("(//input[@name='quantity'])[1]")).sendKeys(String.valueOf(frogNo));

			// Add 5 quantity for Fluffy Bunny
			driver.findElement(By.xpath("(//input[@name='quantity'])[2]")).clear();
			driver.findElement(By.xpath("(//input[@name='quantity'])[2]")).sendKeys(String.valueOf(bunnyNo));

			// Add 3 quantity for valentine bear
			driver.findElement(By.xpath("(//input[@name='quantity'])[3]")).clear();
			driver.findElement(By.xpath("(//input[@name='quantity'])[3]")).sendKeys(String.valueOf(bearNo));

			// Read price of each single product
			String frogPrice = driver.findElement(By.xpath("//td[normalize-space()='$10.99']")).getText();
			String bunnyPrice = driver.findElement(By.xpath("//td[normalize-space()='$9.99']")).getText();
			String bearPrice = driver.findElement(By.xpath("//td[normalize-space()='$14.99']")).getText();

			// remove $ from price and convert to Number
			Float frogPriceNum = Float.valueOf(frogPrice.replace("$", ""));
			Float bunnyPriceNum = Float.valueOf(bunnyPrice.replace("$", ""));
			Float bearPriceNum = Float.valueOf(bearPrice.replace("$", ""));

			// Read subtotal price for each product
			String frogSubtotal = driver.findElement(By.xpath("//td[normalize-space()='$21.98']")).getText();
			String bunnySubtotal = driver.findElement(By.xpath("//td[normalize-space()='$49.95']")).getText();
			String bearSubtotal = driver.findElement(By.xpath("//td[normalize-space()='$44.97']")).getText();

			// remove $ from price and convert to Number
			Float frogSubtotalNum = Float.valueOf(frogSubtotal.replace("$", ""));
			Float bunnySubtotalNum = Float.valueOf(bunnySubtotal.replace("$", ""));
			Float bearSubtotalNum = Float.valueOf(bearSubtotal.replace("$", ""));

			decfor.setRoundingMode(RoundingMode.UP);

			// 1. Verify the subtotal for each product is correct

			// Verify Subtotal of Stuffed Frog
			Assert.assertEquals(frogSubtotalNum, Float.parseFloat(decfor.format(frogPriceNum * frogNo)),
					"Stuffed Frog subtotal not matching");
			// Verify Subtotal of Fluffy Bunny
			Assert.assertEquals(bunnySubtotalNum, Float.parseFloat(decfor.format(bunnyPriceNum * bunnyNo)),
					"Fluffy Bunny subtotal not matching");
			// Verify Subtotal of Valentine bear
			Assert.assertEquals(bearSubtotalNum, bearPriceNum * bearNo, "Valentine Bear subtotal not matching");

			// 2.Verify the price for each product

			Assert.assertEquals(initalFrogCost, frogPriceNum, "Price for Stuffed Frog is not matching ");
			Assert.assertEquals(initalBunnyCost, bunnyPriceNum, "Price for Fluffy Bunny is not matching ");
			Assert.assertEquals(initalBearCost, bearPriceNum, "Price for Valentine Bear is not matching ");

			// 3. Verify that total = sum(sub totals)

			// get total from website
			String total = driver.findElement(By.xpath("//strong[@class='total ng-binding']")).getText();
			// Get numeric number from total
			String[] displaytotal = total.split(" ");
			System.out.println(displaytotal[1]);
			// Calculate price by summing all 3 items subtotals
			Float calcPrice = Float.parseFloat(
					decfor.format((frogPriceNum * frogNo) + (bunnyPriceNum * bunnyNo) + (bearPriceNum * bearNo)));
			System.out.println("Calculated price =" + calcPrice);
			Assert.assertEquals(calcPrice, Float.parseFloat(displaytotal[1]),
					"Calculated total price and website display price is not equal");

		} catch (Exception e) {
			System.out.println("Exception caught :" + e);

		}
	}

	/**
	 * Function to close browser after test
	 * 
	 * @param none
	 * @throws none
	 */

	@AfterTest
	public void TearDown() {
		driver.quit();
	}

}
