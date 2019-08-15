using System;
using System.IO;
using System.Text;
using System.Collections;
using System.Windows.Forms;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Diagnostics;


namespace XmlGridViewSample
{
    public class FileUtilities
    {
        /// <summary>
        /// Gets the filepath the user has selected in the file-open dialog
        /// </summary>
        /// <param name="fileType"></param>
        /// <returns></returns>
        public static string GetOpenFilePath()
        {
            string sFilePath = string.Empty;

            OpenFileDialog openFile = new OpenFileDialog();
            openFile.Filter = "All files (*.*)|*.*";

            if (openFile.ShowDialog() == DialogResult.OK)
            {
                sFilePath = openFile.FileName;
            }

            return sFilePath;
        }



        /// <summary>
        /// Returns the filepath the user selected in the save-file dialog
        /// </summary>
        /// <param name="fileType"></param>
        /// <returns></returns>
        public static string GetSaveFilePath()
        {
            string sFilePath = string.Empty;

            SaveFileDialog saveFile = new SaveFileDialog();
            saveFile.Filter = "All files (*.*)|*.*";

            if (saveFile.ShowDialog() == DialogResult.OK)
            {
                sFilePath = saveFile.FileName;
            }

            return sFilePath;
        }

        /// <summary>
        /// Returns the path the user selected in the browse-for-file dialog
        /// </summary>
        /// <param name="sdescr"></param>
        /// <returns></returns>
        public static string GetBrowseForPath(string sdescr, string sInitialPath)
        {
            string sPath = string.Empty;
            
            FolderBrowserDialog fbdBrowser = new FolderBrowserDialog();
            fbdBrowser.Description = sdescr;
            fbdBrowser.ShowNewFolderButton = true;
            
            if(sInitialPath.Trim() != string.Empty) 
            {
                if ((File.GetAttributes(sInitialPath) & FileAttributes.Directory) != FileAttributes.Directory)
                {
                    FileInfo fiInfo = new FileInfo(sInitialPath);
                    sInitialPath = fiInfo.DirectoryName;
                }
            
                fbdBrowser.SelectedPath = sInitialPath;
            }

            if (fbdBrowser.ShowDialog() == DialogResult.OK)
            {
                sPath = fbdBrowser.SelectedPath;
            }

            return sPath;
        }


        public static void DeleteFile(string filepath)
        {
            if (!File.Exists(filepath))
                throw new Exception("File " + filepath + " does not exist, unable to delete.");

            File.Delete(filepath);
        }

        public static void FileExists(string filepath, string callingClass)
        {
            // Check to see that the file exists
            if (!File.Exists(filepath))
                throw new Exception(callingClass + " File " + filepath + " not found.");
        }

        /// <summary>
        /// Reads and returns the contents of a file into a string
        /// </summary>
        /// <param name="sFilePath"></param>
        /// <returns></returns>
        public static string ReadFileContents(string sFilePath)
        {
            FileStream file = null;
            StreamReader sr = null;
            string sContents = string.Empty;

            try
            {
                // make sure we're allowed to overwrite the file if it exists
                if (File.Exists(sFilePath) == false)
                {
                    throw new Exception("Cannot read the file '" + sFilePath + "' because it does not exist!");
                }

                // Specify file, instructions, and priveledges
                file = new FileStream(sFilePath, FileMode.OpenOrCreate, FileAccess.Read);

                // Create a new stream to read from a file
                sr = new StreamReader(file);

                // Read contents of file into a string
                sContents = sr.ReadToEnd();
            }
            catch (Exception ex)
            {
                throw new Exception("ReadFileContents() failed with error: " + ex.Message);
            }
            finally
            {
                // Close StreamReader
                if (sr != null)
                    sr.Close();

                // Close file
                if (file != null)
                    file.Close();
            }

            return sContents;
        }

        /// <summary>
        /// Writes a string/contents to a given filepath
        /// </summary>
        /// <param name="sFilePath"></param>
        /// <param name="sContents"></param>
        /// <param name="bOverwrite"></param>
        public static void WriteFileContents(string sFilePath, string sContents, bool bOverwrite)
        {
            FileStream file = null;
            StreamWriter sw = null;

            try
            {
                // make sure we're allowed to overwrite the file if it exists
                if (bOverwrite == false)
                {
                    if (File.Exists(sFilePath) == true)
                    {
                        throw new Exception("Cannot write the file '" + sFilePath + "' because it already exists!");
                    }
                }

                // Specify file, instructions, and privelegdes
                file = new FileStream(sFilePath, FileMode.Create, FileAccess.Write);

                // Create a new stream to write to the file
                sw = new StreamWriter(file);

                // Write a string to the file
                sw.Write(sContents);
            }
            catch (Exception ex)
            {
                throw new Exception("WriteFileContents() failed with error: " + ex.Message);
            }
            finally
            {
                // Close StreamWriter
                if (sw != null)
                    sw.Close();

                // Close file
                if (file != null)
                    file.Close();
            }
        }

        /// <summary>
        /// Gets the read-only/locked status of a file 
        /// </summary>
        /// <param name="sFilePath"></param>
        /// <returns></returns>
        public static bool GetFileLocked(string sFilePath)
        {
            bool bReadOnly = false;

            try
            {
                // if no filename, return
                if (sFilePath.Trim() == string.Empty)
                {
                    return bReadOnly;
                }

                FileInfo fiInfo = new FileInfo(sFilePath);
                if (fiInfo.Exists == true)
                {
                    bReadOnly = fiInfo.IsReadOnly;
                }
            }
            catch (Exception ex)
            {
                throw new Exception("GetFileLocked() failed with error: " + ex.Message);
            }

            return bReadOnly;
        }

        /// <summary>
        /// Sets the read-only/locked status of a file 
        /// </summary>
        /// <param name="sFilePath"></param>
        /// <param name="bLocked"></param>
        public static void SetFileLocked(string sFilePath, bool bLocked)
        {
            try
            {
                // if no filename, return
                if (sFilePath.Trim() == string.Empty)
                {
                    return;
                }

                FileInfo fiInfo = new FileInfo(sFilePath);
                if (fiInfo.Exists == true)
                {
                    fiInfo.IsReadOnly = bLocked;
                }
            }
            catch (Exception ex)
            {
                throw new Exception("SetFileLocked() failed with error: " + ex.Message);
            }
        }

        /// <summary>
        /// Launches a file in Windows in it's associated default application
        /// </summary>
        /// <param name="sFilePath"></param>
        public static void LaunchFileInWindows(string sFilePath)
        {
            if (File.Exists(sFilePath) == true)
            {
                try
                {
                    Process.Start(sFilePath);
                }
                catch (Exception ex)
                {
                    throw new Exception("LaunchFileInWindows exception: " + ex.Message);
                }
            }
        }

        /// <summary>
        /// Overload: Returns a guaranteed unique filename in the temp directory
        /// </summary>
        /// <returns></returns>
        public static string GetUniqueTempFileName()
        {
            return GetUniqueTempFileName(".tmp");
        }

        /// <summary>
        /// Returns a guaranteed unique filename in the temp directory
        /// </summary>
        /// <param name="sExtension"></param>
        /// <returns></returns>
        public static string GetUniqueTempFileName(string sExtension)
        {
            string sUniqueName = System.Guid.NewGuid().ToString() + sExtension;

            return Path.Combine(Path.GetTempPath(), sUniqueName);
        } 
    }

}
