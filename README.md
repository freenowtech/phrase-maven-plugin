# Phrase Maven Plugin
[![Build Status](https://github.com/freenowtech/phrase-maven-plugin/actions/workflows/maven.yml/badge.svg?query=branch%3Amaster)](https://github.com/freenowtech/phrase-maven-plugin/actions?query=branch%3Amaster)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.free-now.apis/phrase-maven-plugin/badge.svg?x=1)](https://maven-badges.herokuapp.com/maven-central/com.free-now.apis/phrase-maven-plugin)

## What is this?
This projects contains a maven plugin to download PhraseApp translations due the 
build process from [PhraseApp API v2](http://docs.phraseapp.com/api/v2/).

## What you have to do to start it with maven?

### Create the bean PhraseAppSyncTask to run this job scheduled lately.

    Configure the maven plugin
    
    <plugin>
        <groupId>com.free-now.maven.plugins</groupId>
        <artifactId>phrase-plugin</artifactId>
        <version>2.x.x</version>
        <configuration>
            <authToken>YOUR_AUTH_TOKEN(REQUIRED)</authToken>
            <projectId>YOUR_PROJECT_ID(REQUIRED)</projectId>
            
            <generatedResourcesFolderName>YOUR_GENERATED_RESOURCE_FOLDER(default:generated-resources/)</generatedResourcesFolderName>
            <messagesFolderName>YOUR_MESSAGES_FOLDERNAME(default:messages/)</messagesFolderName>
            <messageFilePrefix>YOUR_MESSAGE_FILE_PREFIX(default:messages_)</messageFilePrefix>
            <messageFilePostfix>YOUR_MESSAGE_FILE_POSTFIX(default:.properties)</messageFilePostfix> 
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>phrase</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
