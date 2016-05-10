package com.mytaxi.plugins;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
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

public class IntegrationTest
{

    private final File javaHome = new File("/Library/Java/JavaVirtualMachines/jdk1.8.0_66.jdk/Contents/Home");
    private final File mavenHome = new File("/usr/local/Cellar/maven/3.3.9/libexec");
    private final File messageOutputDir = new File("target/test-classes/target/generated-resources/messages/");


    @Ignore
    @Test
    public void invoke() throws Exception
    {
        // find test pom file
        File pomFile = findTestPomFile();

        // invoke clean
        invokeMaven(pomFile, "clean");

        // make sure clean removed stuff / does not exist in the first place
        assertFalse(messageOutputDir.exists());

        // invoke compile
        invokeMaven(pomFile, "compile");

        // find and test messages
        assertTrue(messageOutputDir.isDirectory());
        assertTrue(messageOutputDir.exists());
    }


    private File findTestPomFile() throws URISyntaxException
    {
        URL testPomUrl = getClass().getResource("/test-pom.xml");
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
