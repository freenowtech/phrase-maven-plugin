package com.mytaxi.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class IntegrationTest
{

    private final File javaHome = new File("/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home");
    private final File mavenHome = new File("/usr/local/Cellar/maven/3.3.9/libexec");
    private final File messageOutputDir = new File("target/test-classes/target/generated-resources/messages/");


    @Test
    public void testPom1() throws Exception
    {
        testPom(
            findTestPomFile("/test-pom1.xml"),
            "DRIVER_APPROACH_SMS",
            "Your driver: %1$s %2$s (%3$s) is on his way and will arrive in ca. %4$s min. \n Driver's tel.: %5$s."); // single quote
    }


    @Test
    public void testPom2() throws Exception
    {
        testPom(
            findTestPomFile("/test-pom2.xml"),
            "DRIVER_APPROACH_SMS",
            "Your driver: %1$s %2$s (%3$s) is on his way and will arrive in ca. %4$s min. \n Driver''s tel.: %5$s."); // double quote
    }


    private void testPom(File pomFile, String key, String expectedText) throws Exception
    {
        // invoke clean
        invokeMaven(pomFile, "clean");

        // make sure clean removed stuff / does not exist in the first place
        assertFalse(messageOutputDir.exists());

        // invoke compile
        invokeMaven(pomFile, "compile");

        // find and test messages
        assertTrue(messageOutputDir.isDirectory());
        assertTrue(messageOutputDir.exists());

        File messagesFile = new File(messageOutputDir, "test-messages_en.properties");
        assertTrue(messagesFile.canRead());

        try (InputStream in = new FileInputStream(messagesFile))
        {
            Properties properties = new Properties();
            properties.load(in);

            assertEquals(
                expectedText,
                properties.getProperty(key));
        }
    }


    private File findTestPomFile(String name) throws URISyntaxException
    {
        URL testPomUrl = getClass().getResource(name);
        assertNotNull(testPomUrl);

        File pomFile = new File(testPomUrl.toURI());
        assertTrue(pomFile.canRead());

        return pomFile;
    }


    private void invokeMaven(File pomFile, String goal) throws CommandLineException, MavenInvocationException
    {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList(goal));
        request.setJavaHome(javaHome);
        request.setShowErrors(true);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(mavenHome);

        InvocationResult result = invoker.execute(request);

        // check status
        CommandLineException executionException = result.getExecutionException();
        if (executionException != null)
        {
            throw executionException;
        }

        assertEquals(0, result.getExitCode());
    }
}
