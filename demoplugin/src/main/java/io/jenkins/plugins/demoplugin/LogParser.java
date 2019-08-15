package io.jenkins.plugins.demoplugin;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class LogParser
{
	private final String log_raw;
	private final String log;

	public LogParser(String log_raw)throws IOException
	{
		this.log_raw = log_raw;
		this.log = removeAnsi(log_raw);
	}

	public String getLogWithoutAnsi()
	{
		return log;
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

	String parsedLog()throws IOException
	{
		String newLog = "*****MetaCap in Action*****\n";
		String line = "";
		BufferedReader br = new BufferedReader(new StringReader(log));
		while((line = br.readLine()) != null)
		{
			if(line.startsWith("[workspace] ") || line.startsWith("+ ") || line.startsWith("Building in workspace") || line.contains("[Pipeline] "))
			{ }
			else
				newLog = newLog + line + "\n";
		}
		br.close();
		return newLog;
	}

	/*
	String getWorkspaceName()throws IOException
	{
		String name = "workspace";
		String line = "";
		boolean node_found = false;
		BufferedReader br = new BufferedReader(new StringReader(log));
		if(isPipeline())
		{
			while((line = br.readLine()) != null)
			{
				if(line.contains("[Pipeline]") && line.contains("node"))
					node_found = true;
				if(node_found && line.contains("Running on"))
				{
					int pos = line.indexOf("/workspace/");
					if(pos != -1)
						name = line.substring(line.lastIndexOf("/") + 1, line.length());
					node_found = false;
					break;
				}
			}
		}
		else
		{
			while((line = br.readLine()) != null)
			{
				if(line.contains("Building") && line.contains("in workspace"))
				{
					int pos = line.indexOf("/workspace/");
					if(pos != -1)
						name = line.substring(line.lastIndexOf("/") + 1, line.length());
					break;
				}
			}
		}
		return name;
	}
	*/

	boolean isPipeline()
	{
		if(log.contains("[Pipeline] "))
			return true;
		else
			return false;
	}

	void saveLog(String dest)throws FileNotFoundException, IOException
	{
		PrintWriter out = new PrintWriter(dest);
		out.println(log);
		out.close();
	}
}

/*
class LogParserMain
{
	public static void main(String args[]) throws IOException, FileNotFoundException, ParserConfigurationException
	{
		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
		File file = new File("/home/adildsw/Desktop/log.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String log = "";
		String line = "";

		while((line = br.readLine()) != null)
			log = log + line + "\n";

		LogParser p = new LogParser(log);
		log = p.removeAnsi(log);

		PipelineXMLBuilder xml = new PipelineXMLBuilder(log, "test-job", "1","test-job-1", "test-job-tag", "12ms", "21/7/18", "/home/adildsw/Desktop");
		xml.publishXML();
	}
}*/
