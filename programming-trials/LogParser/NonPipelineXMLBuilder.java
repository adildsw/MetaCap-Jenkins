package io.jenkins.plugins.demoplugin;

import jdk.nashorn.internal.scripts.JO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class NonPipelineXMLBuilder
{
	private final String log;
	private final String JOB_NAME;
	private final String BUILD_NUMBER;
	private final String BUILD_DISPLAY_NAME;
	private final String BUILD_TAG;
	private final String BUILD_DURATION;
	private final String BUILD_DATE;
	private final String COMMIT_REVISION;
	private final String COMMIT_AUTHOR;
	private final String COMMIT_DATE;
	private final String COMMIT_MESSAGE;

	private final Boolean dotGitPresent;
	private final Boolean isPipeline;

	String WORKSPACE_NAME;

	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;

	public NonPipelineXMLBuilder(String log, String JOB_NAME, String BUILD_NUMBER, String BUILD_DISPLAY_NAME, String BUILD_TAG, String BUILD_DURATION, String BUILD_DATE, String COMMIT_REVISION, String COMMIT_AUTHOR, String COMMIT_DATE, String COMMIT_MESSAGE) throws ParserConfigurationException
	{
		this.log = log;
		this.JOB_NAME = JOB_NAME;
		this.BUILD_NUMBER = BUILD_NUMBER;
		this.BUILD_DISPLAY_NAME = BUILD_DISPLAY_NAME;
		this.BUILD_TAG = BUILD_TAG;
		this.BUILD_DURATION = BUILD_DURATION;
		this.BUILD_DATE = BUILD_DATE;
		this.COMMIT_REVISION = COMMIT_REVISION;
		this.COMMIT_AUTHOR = COMMIT_AUTHOR;
		this.COMMIT_DATE = COMMIT_DATE;
		this.COMMIT_MESSAGE = COMMIT_MESSAGE;

		this.dotGitPresent = true;
		this.isPipeline = false;

		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.dBuilder = this.dbFactory.newDocumentBuilder();
		this.doc = this.dBuilder.newDocument();

		this.WORKSPACE_NAME = "workspace";
	}

	public NonPipelineXMLBuilder(String log, String JOB_NAME, String BUILD_NUMBER, String BUILD_DISPLAY_NAME, String BUILD_TAG, String BUILD_DURATION, String BUILD_DATE) throws ParserConfigurationException
	{
		this.log = log;
		this.JOB_NAME = JOB_NAME;
		this.BUILD_NUMBER = BUILD_NUMBER;
		this.BUILD_DISPLAY_NAME = BUILD_DISPLAY_NAME;
		this.BUILD_TAG = BUILD_TAG;
		this.BUILD_DURATION = BUILD_DURATION;
		this.BUILD_DATE = BUILD_DATE;
		this.COMMIT_REVISION = null;
		this.COMMIT_AUTHOR = null;
		this.COMMIT_DATE = null;
		this.COMMIT_MESSAGE = null;

		this.dotGitPresent = false;
		this.isPipeline = false;

		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.dBuilder = this.dbFactory.newDocumentBuilder();
		this.doc = this.dBuilder.newDocument();

		this.WORKSPACE_NAME = "workspace";
	}

	String getWorkspaceName(String line) throws IOException
	{
		String name = WORKSPACE_NAME;
		if((line.contains("Building") || line.contains("Running")) && (line.contains("in workspace") || line.contains("/" + WORKSPACE_NAME)))
			name = line.substring(line.lastIndexOf("/") + 1, line.length());
		return name;
	}

	void populateXML()throws IOException
	{
		String initLog = "";
		String line = "";
		Boolean initCaptured = false;
		BufferedReader br = new BufferedReader(new StringReader(log));

		Element root = doc.createElement("Metadata");
		doc.appendChild(root);

		Element build = doc.createElement("Build");
		root.appendChild(build);

		Element buildEle;

		buildEle = doc.createElement("JOB_NAME");
		buildEle.appendChild(doc.createTextNode(JOB_NAME));
		build.appendChild(buildEle);

		buildEle = doc.createElement("BUILD_NUMBER");
		buildEle.appendChild(doc.createTextNode(BUILD_NUMBER));
		build.appendChild(buildEle);

		buildEle = doc.createElement("BUILD_DISPLAY_NAME");
		buildEle.appendChild(doc.createTextNode(BUILD_DISPLAY_NAME));
		build.appendChild(buildEle);

		buildEle = doc.createElement("BUILD_TAG");
		buildEle.appendChild(doc.createTextNode(BUILD_TAG));
		build.appendChild(buildEle);

		buildEle = doc.createElement("BUILD_DURATION");
		buildEle.appendChild(doc.createTextNode(BUILD_DURATION));
		build.appendChild(buildEle);

		buildEle = doc.createElement("BUILD_DATE");
		buildEle.appendChild(doc.createTextNode(BUILD_DATE));
		build.appendChild(buildEle);

		//Adding Git Version Control System Metadata to XML File
		if(dotGitPresent)
		{
			Element vcs = doc.createElement("Version_Control_System");
			root.appendChild(vcs);

			Element vcsEle;

			vcsEle = doc.createElement("commit-revision");
			vcsEle.appendChild(doc.createTextNode(COMMIT_REVISION));
			vcs.appendChild(vcsEle);

			vcsEle = doc.createElement("commit-author");
			vcsEle.appendChild(doc.createTextNode(COMMIT_AUTHOR));
			vcs.appendChild(vcsEle);

			vcsEle = doc.createElement("commit-date");
			vcsEle.appendChild(doc.createTextNode(COMMIT_DATE));
			vcs.appendChild(vcsEle);

			vcsEle = doc.createElement("commit-message");
			vcsEle.appendChild(doc.createTextNode(COMMIT_MESSAGE));
			vcs.appendChild(vcsEle);
		}

		Element buildDetails = doc.createElement("Build_Details");
		root.appendChild(buildDetails);

		Element buildDetailsEle;
		Element workspace = null;
		Element command = null;
		Element result = null;

		String resultLog = "";

		//Parsing Build Log and Populating XML Accordingly
		while((line = br.readLine()) != null)
		{
			WORKSPACE_NAME = getWorkspaceName(line);
			//Extracting and Adding Initial/Trigger Log to the XML File
			if(!initCaptured)
			{
				if(!line.contains("[" + WORKSPACE_NAME + "]"))
					initLog = initLog + line + "\n";
				else
				{
					buildDetailsEle = doc.createElement("Init/Trigger");
					Element temp = doc.createElement("log");
					temp.appendChild(doc.createTextNode(initLog));
					buildDetailsEle.appendChild(temp);
					buildDetails.appendChild(buildDetailsEle);
					initCaptured = true;
				}
			}

			if(initCaptured)
			{
				if(line.contains("[" + WORKSPACE_NAME + "]"))
				{
					if(!resultLog.equalsIgnoreCase(""))
					{
						result = doc.createElement("log");
						result.appendChild(doc.createTextNode(resultLog));

						if(command != null)
							command.appendChild(result);
						else if(workspace != null)
							workspace.appendChild(result);
						else
							buildDetails.appendChild(result);

						resultLog = "";
						result = null;
					}

					workspace = doc.createElement("workspace");
					workspace.setAttribute("dir", WORKSPACE_NAME);

					workspace.appendChild(doc.createTextNode(line));
					buildDetails.appendChild(workspace);
				}

				else if(line.contains("+ "))
				{
					if(!resultLog.equalsIgnoreCase(""))
					{
						result = doc.createElement("log");
						result.appendChild(doc.createTextNode(resultLog));

						if(command != null)
							command.appendChild(result);
						else if(workspace != null)
							workspace.appendChild(result);
						else
							buildDetails.appendChild(result);

						resultLog = "";
						result = null;
					}

					command = doc.createElement("command");
					command.appendChild(doc.createTextNode(line));
					if(workspace != null)
						workspace.appendChild(command);
					else
						buildDetails.appendChild(command);
				}

				else
					resultLog = resultLog + line + "\n";
			}
		}

		//Adding Remaining Log to the XML File
		if(!resultLog.equalsIgnoreCase(""))
		{
			result = doc.createElement("log");
			result.appendChild(doc.createTextNode(resultLog));

			if(command != null)
				command.appendChild(result);
			else if(workspace != null)
				workspace.appendChild(result);
			else
				buildDetails.appendChild(result);

			resultLog = "";
			result = null;
		}
	}

	void publishXML()
	{
		try
		{
			populateXML();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("/home/adildsw/Desktop/" + BUILD_TAG + ".xml"));
			transformer.transform(source, result);
		}catch(Exception e)
		{ }
	}

}
