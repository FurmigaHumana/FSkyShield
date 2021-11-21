using System;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace Launcher
{
    class Program
    {
        public static int Main(string[] args)
        {

            try
            {

                var basedir = System.AppDomain.CurrentDomain.BaseDirectory;

                var dllDirectory = basedir + @"bin\server;" + basedir + @"bin";
                Environment.SetEnvironmentVariable("PATH", Environment.GetEnvironmentVariable("PATH") + ";" + dllDirectory);

                return executeJavaVM();

            } catch (Exception ex)
            {

                MessageBox.Show(ex.ToString());

                return 13816;
            }
        }

        [DllImport("wrapper.dll")]
        public unsafe static extern int executeJavaVM();
    }
}
