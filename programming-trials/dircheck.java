import java.io.*;
class dircheck
{
	public static void main(String args[])throws FileNotFoundException
	{
		PrintWriter p = new PrintWriter("LogParser/abc.txt");
		String a = "asdasdasd";
		p.println(a);
		p.close();
	}
}
