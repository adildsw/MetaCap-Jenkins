package io.jenkins.plugins.demoplugin;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.*;
import hudson.scm.SCM;
import hudson.scm.SCMS;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.triggers.SCMTrigger;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class LogCapturePublisher extends Recorder implements SimpleBuildStep
{
	@DataBoundConstructor
	public LogCapturePublisher()
	{
	}

	boolean isPipeline(String log)
	{
		if(log.contains("[Pipeline] "))
			return true;
		else
			return false;
	}

	public String execCommand(String cmd, FilePath filePath, Launcher launcher)throws InterruptedException, IOException
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Proc p = launcher.launch(cmd, new String[0], baos, filePath);
			int exitCode = p.join();
			if(exitCode == 0)
				return baos.toString();
			return null;
		}catch(Exception e)
		{
			return null;
		}
	}

	String removeAnsi(String log_raw)throws IOException
	{
		String line = "";
		String newLog = "";
		BufferedReader br = new BufferedReader(new StringReader(log_raw));
		while((line = br.readLine()) != null)
		{
			while(line.contains("[8mha:") && line.contains("[0m"))
			{
				int beg = -1;
				int end = -1;
				String s1 = "";
				String s2 = "";
				beg = line.indexOf("[8mha") - 1;
				end = line.indexOf("[0m") + 2;
				s1 = line.substring(0, beg);
				s2 = line.substring(end + 1, line.length());
				line = s1 + s2;
			}
			newLog = newLog + line + "\n";
		}
		return newLog;
	}

	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException
	{
		String log = run.getLog();
		File l = run.getLogFile();
		log = removeAnsi(log);
		//taskListener.getLogger().println("\ngetEnvironment: " + run.getEnvironment(taskListener));

		String JOB_NAME = run.getEnvironment(taskListener).get("JOB_NAME");
		String BUILD_NUMBER = run.getEnvironment(taskListener).get("BUILD_NUMBER");
		String BUILD_DISPLAY_NAME = run.getEnvironment(taskListener).get("BUILD_DISPLAY_NAME");
		String BUILD_TAG = run.getEnvironment(taskListener).get("BUILD_TAG");
		String BUILD_DURATION = run.getDurationString();
		String BUILD_DATE = run.getTime().toString();
		//String BUILD_RESULT = run.getResult().toString();

		BUILD_DURATION = BUILD_DURATION.substring(0, BUILD_DURATION.indexOf(" and counting"));

		//taskListener.getLogger().println(JOB_NAME + "\n" + BUILD_NUMBER + "\n" + BUILD_DISPLAY_NAME + "\n" + BUILD_TAG + "\n" + BUILD_DURATION + "\n" + BUILD_DATE + "\n" + BUILD_RESULT + "\n");

		String gitCheck = execCommand("dir -a", filePath, launcher);
		taskListener.getLogger().println("Path: " + filePath.toString());
		try
		{
			if(gitCheck.contains(".git") && System.getProperty("os.name").equalsIgnoreCase("Linux"))
			{
				String COMMIT_REVISION = execCommand("git show -s --format=%H", filePath, launcher);
				String COMMIT_AUTHOR = execCommand("git show -s --format=%cn", filePath, launcher);
				String COMMIT_DATE = execCommand("git show -s --format=%cd", filePath, launcher);
				String COMMIT_MESSAGE = execCommand("git show -s --format=%s", filePath, launcher);
				if(isPipeline(log))
				{
					PipelineXMLBuilder obj = new PipelineXMLBuilder(log, JOB_NAME, BUILD_NUMBER, BUILD_DISPLAY_NAME, BUILD_TAG, BUILD_DURATION, BUILD_DATE, COMMIT_REVISION, COMMIT_AUTHOR, COMMIT_DATE, COMMIT_MESSAGE, filePath.toString());
					taskListener.getLogger().println(obj.publishXML());
				}else
				{
					NonPipelineXMLBuilder obj = new NonPipelineXMLBuilder(log, JOB_NAME, BUILD_NUMBER, BUILD_DISPLAY_NAME, BUILD_TAG, BUILD_DURATION, BUILD_DATE, COMMIT_REVISION, COMMIT_AUTHOR, COMMIT_DATE, COMMIT_MESSAGE, filePath.toString());
					taskListener.getLogger().println(obj.publishXML());
				}
			}else
			{
				if(isPipeline(log))
				{
					PipelineXMLBuilder obj = new PipelineXMLBuilder(log, JOB_NAME, BUILD_NUMBER, BUILD_DISPLAY_NAME, BUILD_TAG, BUILD_DURATION, BUILD_DATE, filePath.toString());
					taskListener.getLogger().println(obj.publishXML());
				}else
				{
					NonPipelineXMLBuilder obj = new NonPipelineXMLBuilder(log, JOB_NAME, BUILD_NUMBER, BUILD_DISPLAY_NAME, BUILD_TAG, BUILD_DURATION, BUILD_DATE, filePath.toString());
					taskListener.getLogger().println(obj.publishXML());
				}
			}
		}catch(Exception e)
		{
			run.setResult(Result.UNSTABLE);
		}

	}

	@Extension @Symbol("capturelog")
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher>
	{

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass)
		{
			return true;
		}

		public String getDisplayName()
		{
			return "Log Capturer";
		}
	}
}
