package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.DriverFactory;
import utils.ExtentManager;
import utils.ExcelUtils;

import java.util.List;

public class Hooks {

    static ExtentReports extent = ExtentManager.getInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // Load Excel data ONCE when project starts
    private static List<String[]> excelData;
    private static int rowIndex = 0;

    static {
        try {
            excelData = ExcelUtils.getLoginData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ThreadLocal<String[]> currentRowData = new ThreadLocal<>();

    @Before
    public void setup(Scenario scenario) {

        // Create report entry
        ExtentTest extentTest = extent.createTest(scenario.getName());
        test.set(extentTest);

        // Give current Excel row to this scenario
        synchronized (Hooks.class) {
            if (rowIndex < excelData.size()) {
                currentRowData.set(excelData.get(rowIndex));
                rowIndex++;
            }
        }

        // Open browser
        DriverFactory.initDriver();
    }

    @After
    public void tearDown(Scenario scenario) {

        // Mark pass or fail in report
        if (scenario.isFailed()) {
            test.get().fail("❌ Failed: " + scenario.getName());
        } else {
            test.get().pass("✅ Passed: " + scenario.getName());
        }

        // Close browser
        DriverFactory.quitDriver();

        // Save report
        extent.flush();
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static String[] getCurrentRowData() {
        return currentRowData.get();
    }
}