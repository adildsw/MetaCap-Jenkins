import java.io.*;

class unicoderemove
{
	public static void main(String args[])throws IOException
	{
		File file = new File("unicoderemovetest.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		String log = "";
		while((line = br.readLine()) != null)
		{
			while(line.contains("&#") && line.contains(";") && line.contains("m"))
			{
				int beg = line.indexOf("&#");
				int end = line.indexOf("m", line.indexOf("&#"));
				line = line.substring(0, beg) + line.substring(end + 1, line.length());
			}
			System.out.println(line);
		}
	}
}
