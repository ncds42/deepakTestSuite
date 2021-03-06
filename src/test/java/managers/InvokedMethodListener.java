package managers;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.FileHandler;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/* This class will handle all before and after methods for each unit test. It is referenced
 * by the testng.xml and is the magic sauce to creating parallel tests without race conditions on Selenium Grid
 */


/**
 * @author nitish
 * This class will handle all before and after methods for each unit test. It is referenced
 * by the testng.xml and is the magic sauce to creating parallel tests without race conditions on Selenium Grid
 */
public class InvokedMethodListener implements IInvokedMethodListener {

	/* This will be the @before method for every unit test */
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		if (method.isTestMethod()) {
			String browserName = method.getTestMethod().getXmlTest().getLocalParameters().get("browser");
			Boolean remote = Boolean
					.parseBoolean(method.getTestMethod().getXmlTest().getLocalParameters().get("remote"));
			if (remote) {
				String url = method.getTestMethod().getXmlTest().getLocalParameters().get("gridUrl");
				DriverFactory.setDriver(browserName, url);
			} else {
				DriverFactory.setDriver(browserName);
			}
		}
	}
	
	/* This will be the @after method for every unit test */
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		if (method.isTestMethod()) {
			WebDriver driver = DriverFactory.getDriver();

			if (testResult.getStatus() == ITestResult.SUCCESS) {
				System.out.println(method.getTestMethod().getMethodName() + " was successful!");
			} else if (testResult.getStatus() == ITestResult.FAILURE) {
				System.out.println(method.getTestMethod().getMethodName() + " failed!");
				System.out.println(testResult.getThrowable().getMessage());

				/* If a test fails, a screenshot will be created and stored in testSuitePictures */
				File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				try {
					String dest = "testSuitePictures/";
					String fileName = new Timestamp(System.currentTimeMillis()).toString();
					fileName = fileName.replaceAll("-","");
					fileName = fileName.replaceAll(" ","");
					fileName = fileName.replaceAll(":","");
					fileName = fileName.replaceAll("\\.","");
					fileName = fileName + ".png";
					System.out.println("Screenshot taken: " + fileName);
					FileHandler.copy(src, new File(dest+fileName));
				} catch (IOException e) {
				}

			}
			if (driver != null) {
				driver.quit(); /* Cleans up the WebDriver object */
			}
		}
	}
}

//System.out.println("New Thread Started " + Thread.currentThread().getId()); // DEBUGGING