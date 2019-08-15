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

	private final String path;

	private final Boolean dotGitPresent;
	private final Boolean isPipeline;

	String WORKSPACE_NAME;

	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;
	Document doc;

	public NonPipelineXMLBuilder(String log, String JOB_NAME, String BUILD_NUMBER, String BUILD_DISPLAY_NAME, String BUILD_TAG, String BUILD_DURATION, String BUILD_DATE, String COMMIT_REVISION, String COMMIT_AUTHOR, String COMMIT_DATE, String COMMIT_MESSAGE, String path) throws ParserConfigurationException
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

		this.path = path;

		this.dotGitPresent = true;
		this.isPipeline = false;

		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.dBuilder = this.dbFactory.newDocumentBuilder();
		this.doc = this.dBuilder.newDocument();

		this.WORKSPACE_NAME = "workspace";
	}

	public NonPipelineXMLBuilder(String log, String JOB_NAME, String BUILD_NUMBER, String BUILD_DISPLAY_NAME, String BUILD_TAG, String BUILD_DURATION, String BUILD_DATE, String path) throws ParserConfigurationException
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

		this.path = path;

		this.dotGitPresent = false;
		this.isPipeline = false;

		this.dbFactory = DocumentBuilderFactory.newInstance();
		this.dBuilder = this.dbFactory.newDocumentBuilder();
		this.doc = this.dBuilder.newDocument();

		this.WORKSPACE_NAME = "workspace";
	}

	String cleanLine(String line)
	{
		String xml10pattern = "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]";
		line = line.replaceAll(xml10pattern, "");

		/*while(line.contains("[8mha:") && line.contains("[0m"))
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
		}*/

		return line;
	}

	String getWorkspaceName(String line) throws IOException
	{
		String name = WORKSPACE_NAME;
		if((line.contains("Building in") || line.contains("Building on") || line.contains("Running in") || line.contains("Running on")) && (line.contains("in workspace") || line.contains("/" + WORKSPACE_NAME)))
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
			line = cleanLine(line);
			WORKSPACE_NAME = getWorkspaceName(line);
			//Extracting and Adding Initial/Trigger Log to the XML File
			if(!initCaptured)
			{
				if(!line.contains("[" + WORKSPACE_NAME + "]"))
					initLog = initLog + line + "\n";
				else
				{
					buildDetailsEle = doc.createElement("Init-Trigger");
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

					int beg = line.indexOf("[" + WORKSPACE_NAME + "]") + WORKSPACE_NAME.length() + 2;
					line = line.substring(beg, line.length());

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

	String publishXML()
	{
		try
		{
			populateXML();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path + "/capMeta.xml"));
			transformer.transform(source, result);

			return "Metadata Captured Successfully";
		}catch(Exception e)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			return sStackTrace;
		}
	}

}
