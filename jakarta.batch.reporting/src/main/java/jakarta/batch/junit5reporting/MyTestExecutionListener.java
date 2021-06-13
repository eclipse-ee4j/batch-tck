package jakarta.batch.junit5reporting;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.LoggingListener;

public class MyTestExecutionListener implements TestExecutionListener {
    
    private final LoggingListener loggingListener = LoggingListener.forJavaUtilLogging(Level.INFO);
    private final TestExecutionListener[] delegates = new TestExecutionListener[] {loggingListener};

    @Override
    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry) {
        Stream.of(delegates).forEach( l -> l.reportingEntryPublished(testIdentifier, entry));
        Logger logger = Logger.getLogger(LoggingListener.class.getName());
        logger.info(entry.getKeyValuePairs().get("value"));
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        Stream.of(delegates).forEach( l -> l.executionFinished(testIdentifier, testExecutionResult));
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        Logger logger = Logger.getLogger(LoggingListener.class.getName());
        Stream.of(delegates).forEach( l -> l.executionStarted(testIdentifier));
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        Stream.of(delegates).forEach( l -> l.executionSkipped(testIdentifier, reason));
    }

    @Override
    public void dynamicTestRegistered(TestIdentifier testIdentifier) {
        Stream.of(delegates).forEach( l -> l.dynamicTestRegistered(testIdentifier));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        Stream.of(delegates).forEach( l -> l.testPlanExecutionFinished(testPlan));
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        LogManager.getLogManager().getLogger(LoggingListener.class.getName()).setLevel(Level.INFO);
        
        Stream.of(delegates).forEach( l -> l.testPlanExecutionStarted(testPlan));
    }

}
