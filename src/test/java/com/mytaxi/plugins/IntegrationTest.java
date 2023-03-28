package com.mytaxi.plugins;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Ignore
public class IntegrationTest
{

    private final File javaHome = new File(System.getenv("JAVA_HOME"));
    private final File mavenHome = new File(System.getenv("MVN_HOME"));
    private final File messageOutputDir = new File("target/test-classes/target/generated-resources/messages/");
    private static final String TESTING_KEY = "test-phrase-maven-plugin";
    private static final String TESTING_TAG_KEY = "test-tag-phrase-maven-plugin";


    @Test
    public void testPom1() throws Exception
    {
        testPom(
            findTestPomFile("/test-pom1.xml"),
            TESTING_KEY,
            "This is for testing the Maven Phrase plugin. Please, don't remove it."); // single quote
    }


    @Test
    public void testPom2() throws Exception
    {
        testPom(
            findTestPomFile("/test-pom2.xml"),
            TESTING_KEY,
            "This is for testing the Maven Phrase plugin. Please, don''t remove it."); // double quote
    }


    @Test
    public void shouldSucceedWithoutAuthTokenWhenHostIsNotDefault() throws Exception
    {
        // given
        File pomFile = findTestPomFile("/test-pom3.xml");

        // when
        int cleanGoal = invokeMaven(pomFile, "clean");
        int compileGoal = invokeMaven(pomFile, "compile");

        // then
        assertEquals(0, cleanGoal);
        assertEquals(0, compileGoal);
    }


    @Test
    public void shouldFailWhenAuthTokenIsMissing() throws Exception
    {
        // given
        File pomFile = findTestPomFile("/test-pom4.xml");

        // when
        int cleanGoal = invokeMaven(pomFile, "clean");
        int compileGoal = invokeMaven(pomFile, "compile");

        // then
        assertEquals(0, cleanGoal);
        assertEquals(1, compileGoal);
    }


    @Test
    public void shouldFetchTranslationsFilteredByTag() throws Exception
    {
        Properties properties = downloadTranslations(findTestPomFile("/test-pom5.xml"));
        assertNotNull(properties.getProperty(TESTING_TAG_KEY));
        assertNull(properties.getProperty(TESTING_KEY));
    }


    private void testPom(File pomFile, String key, String expectedText) throws Exception
    {
        Properties properties = downloadTranslations(pomFile);

        assertEquals(
            expectedText,
            properties.getProperty(key));
    }


    private Properties downloadTranslations(File pomFile) throws Exception
    {
        // invoke clean
        assertEquals(0, invokeMaven(pomFile, "clean"));

        // make sure clean removed stuff / does not exist in the first place
        assertFalse(messageOutputDir.exists());

        // invoke compile
        assertEquals(0, invokeMaven(pomFile, "compile"));

        // find and test messages
        assertTrue(messageOutputDir.isDirectory());
        assertTrue(messageOutputDir.exists());

        File messagesFile = new File(messageOutputDir, "test-messages_en.properties");
        assertTrue(messagesFile.canRead());

        try (InputStream in = Files.newInputStream(messagesFile.toPath()))
        {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
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


    private int invokeMaven(File pomFile, String goal) throws CommandLineException, MavenInvocationException
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

        return result.getExitCode();
    }
}
