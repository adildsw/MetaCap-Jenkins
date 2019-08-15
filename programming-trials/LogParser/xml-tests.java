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

class xmltests
{
	public static void main(String args[])
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element rootEle = doc.createElement("Metadata");
			doc.appendChild(rootEle);

			Element vcs = doc.createElement("VersionControlSystem");
			vcs.appendChild(doc.createTextNode("Hello"));
			vcs.setAttribute("name", "Adil");
			rootEle.appendChild(vcs);
			
			Element abc = doc.createElement("TestChild");
			abc.appendChild(doc.createTextNode("testing"));
			vcs.appendChild(abc);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("/home/adildsw/Desktop/test_results.xml"));
			transformer.transform(source, result);
		}
		catch(Exception e)
		{
		}
	}
}
