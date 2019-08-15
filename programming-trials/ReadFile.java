import java.io.*;
class ReadFile
{
	public static void main(String args[])throws IOException, FileNotFoundException
	{
		File file = new File("testfile.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while((s = br.readLine()) != null)
			System.out.println(s);
	}
}
