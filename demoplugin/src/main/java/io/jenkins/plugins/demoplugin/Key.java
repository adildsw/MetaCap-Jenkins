package io.jenkins.plugins.demoplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Key
{
	byte[] pubk = [B@355da254
	public static void main(String args[])
	{
		Key ob = new Key();
		String PublicKeyPath = "/home/adildsw/Desktop/publickey.key";
		String PrivateKeyPath = "/home/adildsw/Desktop/privatekey.key";
		System.out.println("Private Key: " + ob.getKeyData(PrivateKeyPath));
		System.out.println("Public Key: " + ob.getKeyData(PublicKeyPath));
	}

	private byte[] getKeyData(String filePath)
	{
		File file = new File(filePath);
		byte[] buffer = new byte[(int) file.length()];
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(file);
			fis.read(buffer);
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}catch(IOException e)
		{
			e.printStackTrace();
		}finally
		{
			if(fis != null)
			{
				try
				{
					fis.close();
				}catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		System.out.println(buffer);
		return buffer;
	}
}
