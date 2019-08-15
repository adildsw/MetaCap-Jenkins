package io.jenkins.plugins.demoplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.AbstractProject;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class GitCommitCapturePublisher extends Recorder
{
	private final String hash_cmd, name_cmd, date_cmd, message_cmd, path;

	@DataBoundConstructor
	public GitCommitCapturePublisher(String hash, String name, String date, String message, String path)
	{
		hash_cmd = hash;
		name_cmd = name;
		date_cmd = date;
		message_cmd = message;
		this.path = path;
	}

	public String execCommand(String cmd, Build<?,?> build, Launcher launcher)throws InterruptedException, IOException
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Proc p = launcher.launch(cmd, new String[0], baos, build.getProject().getWorkspace());
			int exitCode = p.join();
			if(exitCode == 0)
				return baos.toString();
			return null;
		}catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public boolean perform(Build<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException
	{
		String hash = execCommand(hash_cmd, build, launcher);
		String name = execCommand(name_cmd, build, launcher);
		String date = execCommand(date_cmd, build, launcher);
		String message = execCommand(message_cmd, build, launcher);
		if(hash == null || name == null || date == null)
			return false;
		else
		{
			try
			{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.newDocument();

				Element rootEle = doc.createElement("Metadata");
				doc.appendChild(rootEle);

				Element vcs = doc.createElement("VersionControlSystem");
				rootEle.appendChild(vcs);

				Element hashEle = doc.createElement("Hash");
				hashEle.appendChild(doc.createTextNode(hash));
				vcs.appendChild(hashEle);

				Element authorEle = doc.createElement("Author");
				authorEle.appendChild(doc.createTextNode(name));
				vcs.appendChild(authorEle);

				Element commitdate = doc.createElement("Date");
				commitdate.appendChild(doc.createTextNode(date));
				vcs.appendChild(commitdate);

				Element commitmessage = doc.createElement("Message");
				commitmessage.appendChild(doc.createTextNode(message));
				vcs.appendChild(commitmessage);

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File(path + "vcs_results.xml"));
				transformer.transform(source, result);

				return true;
			}catch(Exception e)
			{
				listener.getLogger().println(e.toString());
				return false;
			}
		}
	}

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher>
	{

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass)
		{
			return true;
		}

		@Override
		public String getDisplayName()
		{
			return "VCS Metadata Capture";
		}
	}
}
