import org.junit.platform.suite.api.*;

// test suite to run all tests simultaneously
@Suite
@SuiteDisplayName("Test suite")
@SelectClasses({JsonFileReaderTest.class})
public class TestSuite {
}
