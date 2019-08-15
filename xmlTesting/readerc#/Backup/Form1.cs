using System;
using System.Xml;
using System.Diagnostics;
using System.IO;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace XmlGridViewSample
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            SetBehavior();            
        }

        private void SetBehavior()
        {
            bool bFileExists = ((txtFile.Text.Trim() != string.Empty) && File.Exists(txtFile.Text));                                  
            if (chkTableView.Checked == true)
            {
                xmlGridView.ViewMode = XmlGridView.VIEW_MODE.TABLE;
                cmbTableIndex.Enabled = true;
            }
            else
            {
                xmlGridView.ViewMode = XmlGridView.VIEW_MODE.XML;
                cmbTableIndex.Enabled = false;
            }                  
        }

        void cmbTableIndex_SelectedIndexChanged(object sender, System.EventArgs e)
        {
            xmlGridView.DataSetTableIndex = Convert.ToInt32(cmbTableIndex.Text.Trim());
        }

        private void chkTableView_CheckedChanged(object sender, EventArgs e)
        {
            SetBehavior();
        }

        private void btnOpenFile_Click(object sender, EventArgs e)
        {
            string sOpenPath = FileUtilities.GetOpenFilePath();
            if (sOpenPath != string.Empty)
            {
                txtFile.Text = sOpenPath;
                xmlGridView.DataFilePath = txtFile.Text;

                cmbTableIndex.Items.Clear();
                for(int i=0; i<xmlGridView.DataTableCount; i++)                
                    cmbTableIndex.Items.Add(i.ToString());

                cmbTableIndex.SelectedIndex = 0;
                xmlGridView.DataSetTableIndex = Convert.ToInt32(cmbTableIndex.Text.Trim());
                
                SetBehavior();
            }
        }

    }
}