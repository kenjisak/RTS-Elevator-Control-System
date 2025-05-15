import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({ElevatorTests.class, FloorTests.class, MessageTests.class, ECSTest.class, UDPTests.class, MailboxTests.class})
@SuiteDisplayName("Unit Test Suite")
public class TestSuite {
}
