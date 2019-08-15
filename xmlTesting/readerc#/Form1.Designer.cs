namespace XmlGridViewSample
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {            
            this.chkTableView = new System.Windows.Forms.CheckBox();
            this.txtFile = new System.Windows.Forms.TextBox();
            this.label2 = new System.Windows.Forms.Label();
            this.btnOpenFile = new System.Windows.Forms.Button();
            this.cmbTableIndex = new System.Windows.Forms.ComboBox();
            this.xmlGridView = new XmlGridViewSample.XmlGridView();
            this.SuspendLayout();
            // 
            // chkTableView
            // 
            this.chkTableView.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.chkTableView.AutoSize = true;
            this.chkTableView.Location = new System.Drawing.Point(595, 16);
            this.chkTableView.Name = "chkTableView";
            this.chkTableView.Size = new System.Drawing.Size(79, 17);
            this.chkTableView.TabIndex = 55;
            this.chkTableView.Text = "Table View";
            this.chkTableView.UseVisualStyleBackColor = true;
            this.chkTableView.CheckedChanged += new System.EventHandler(this.chkTableView_CheckedChanged);
            // 
            // txtFile
            // 
            this.txtFile.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.txtFile.Location = new System.Drawing.Point(71, 13);
            this.txtFile.Name = "txtFile";
            this.txtFile.Size = new System.Drawing.Size(471, 20);
            this.txtFile.TabIndex = 59;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(12, 15);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(51, 13);
            this.label2.TabIndex = 56;
            this.label2.Text = "XML File:";
            // 
            // btnOpenFile
            // 
            this.btnOpenFile.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnOpenFile.Location = new System.Drawing.Point(548, 12);
            this.btnOpenFile.Name = "btnOpenFile";
            this.btnOpenFile.Size = new System.Drawing.Size(28, 23);
            this.btnOpenFile.TabIndex = 57;
            this.btnOpenFile.Text = "...";
            this.btnOpenFile.UseVisualStyleBackColor = true;
            this.btnOpenFile.Click += new System.EventHandler(this.btnOpenFile_Click);
            // 
            // txtTableIndex
            // 
            this.cmbTableIndex.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.cmbTableIndex.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cmbTableIndex.Location = new System.Drawing.Point(680, 13);
            this.cmbTableIndex.Name = "cmbTableIndex";
            this.cmbTableIndex.Size = new System.Drawing.Size(40, 20);
            this.cmbTableIndex.TabIndex = 60;
            this.cmbTableIndex.SelectedIndexChanged += new System.EventHandler(cmbTableIndex_SelectedIndexChanged);
            // 
            // xmlGridView
            // 
            this.xmlGridView.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.xmlGridView.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.xmlGridView.DataFilePath = "";
            this.xmlGridView.DataSetTableIndex = 0;
            this.xmlGridView.Location = new System.Drawing.Point(12, 41);
            this.xmlGridView.Name = "xmlGridView";
            this.xmlGridView.Size = new System.Drawing.Size(709, 524);
            this.xmlGridView.TabIndex = 0;
            this.xmlGridView.ViewMode = XmlGridViewSample.XmlGridView.VIEW_MODE.XML;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(733, 577);
            this.Controls.Add(this.cmbTableIndex);
            this.Controls.Add(this.txtFile);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.btnOpenFile);
            this.Controls.Add(this.chkTableView);
            this.Controls.Add(this.xmlGridView);
            this.Name = "Form1";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "XML Grid View Sample";
            this.Load += new System.EventHandler(this.Form1_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private XmlGridView xmlGridView;
        private System.Windows.Forms.CheckBox chkTableView;
        private System.Windows.Forms.TextBox txtFile;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button btnOpenFile;        
        private System.Windows.Forms.ComboBox cmbTableIndex;
    }
}

