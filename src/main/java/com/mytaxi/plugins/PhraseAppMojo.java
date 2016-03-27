package com.mytaxi.plugins;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.mytaxi.apis.phrase.tasks.PhraseAppSyncTask;
import java.io.File;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

/**
 * Goal which downloads all translations from phraseApp
 *
 * @author m.winkelmann
 * @goal phrase
 * @phase generate-sources
 * @requiresDependencyResolution
 */
public class PhraseAppMojo extends AbstractMojo
{

    private static final String GENERATED_RESOURCES = "/generated-resources/";

    /**
     * The Maven project.
     *
     * @parameter propertie="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * v2 AuthToken phrase app account. *REQUIRED
     *
     * @parameter propertie="${authToken}"
     * @required
     */
    private String authToken;

    /**
     * v2 ProjectId for the project you want to download the strings. *REQUIRED
     *
     * @parameter propertie="${projectId}"
     * @required
     */
    private String projectId;

    /**
     * Location directory of the messages folder. Default: ${project.build.directory}/generated-resources/
     *
     * @parameter propertie="${generatedResourcesFolderName}"
     */
    private String generatedResourcesFolderName;

    /**
     * Location directory of the messages files. Default: ${project.build.directory + generatedResourcesFolderName}/messages
     *
     * @parameter propertie="${messagesFolderName}"
     */
    private String messagesFolderName;

    /**
     * File prefix of the messages files. Default: messages_
     *
     * @parameter propertie="${messageFilePrefix}"
     */
    private String messageFilePrefix;

    /**
     * File postfix of the messages files. Default: .properties
     *
     * @parameter propertie="${messageFilePostfix}"
     */
    private String messageFilePostfix;

    /**
     * @component
     */
    private BuildContext buildContext;


    @Override
    public void execute() throws MojoExecutionException
    {
        checkRequiredConfigurations();

        getLog().info("Start downlaoding message resources ...");

        PhraseAppSyncTask phraseAppSyncTask = new PhraseAppSyncTask(authToken, projectId);

        configure(phraseAppSyncTask);

        try
        {
            phraseAppSyncTask.run();
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error in getting PhraseApp strings due build process", e);
        }

        addingCompileSource();

        getLog().info("... finished downloading message resources!");
    }


    private void addingCompileSource()
    {
        String generatedSourcePath = getGeneratedResourceFolder();
        File generatedSourcesDir = new File(generatedSourcePath);
        getLog().info("Adding " + generatedSourcePath + " to compile source.");
        project.addCompileSourceRoot(generatedSourcesDir.getAbsolutePath());
        Resource resource = new Resource();
        resource.setDirectory(generatedSourcesDir.getAbsolutePath());
        resource.setFiltering(false);
        project.addResource(resource);
        buildContext.refresh(generatedSourcesDir);
    }


    private void checkRequiredConfigurations()
    {
        getLog().info("Config: Check required configurations ...");
        Preconditions.checkNotNull("AuthToken is not configured but is REQUIRED", authToken);
        Preconditions.checkNotNull("ProjectId is not configured but is REQUIRED", projectId);
        getLog().info("Config: ... successfully checked required configurations.");
    }


    private void configure(PhraseAppSyncTask phraseAppSyncTask)
    {
        String generatedResourcesFoldername = getGeneratedResourceFolder();
        phraseAppSyncTask.setGeneratedResourcesFoldername(generatedResourcesFoldername);
        getLog().info("Config: GeneratedResourceFoldername is configured(else DEFAULT) - " + generatedResourcesFoldername);

        if (messagesFolderName != null)
        {
            getLog().info("Config: MessageFolderName is configured - " + messagesFolderName);
            phraseAppSyncTask.setMessagesFoldername(messagesFolderName);
        }
        if (messageFilePrefix != null)
        {
            getLog().info("Config: MessageFilePrefix is configured - " + messageFilePrefix);
            phraseAppSyncTask.setMessageFilePrefix(messageFilePrefix);
        }
        if (messageFilePostfix != null)
        {
            getLog().info("Config: MessageFilePostfix is configured - " + messageFilePostfix);
            phraseAppSyncTask.setMessageFilePostfix(messageFilePostfix);
        }
    }


    private String getGeneratedResourceFolder()
    {
        return project.getBuild().getDirectory() + MoreObjects.firstNonNull(generatedResourcesFolderName, GENERATED_RESOURCES);
    }

}
