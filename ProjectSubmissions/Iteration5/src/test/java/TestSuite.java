import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({ElevatorTests.class, FloorTests.class, MessageTests.class, UDPTests.class, MailboxTests.class, ElevatorStateTests.class, IntegrationTests.class, SchedulerStateTests.class})
@SuiteDisplayName("Unit Test Suite")
public class TestSuite {
}
