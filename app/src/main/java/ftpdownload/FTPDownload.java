package ftpdownload;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by 鼎鈞 on 2016/7/1.
 */

/*
www.aaa-info.club
advertising
adv1234
 */
public class FTPDownload {
    private final static String server = "www.aaa-info.club";
    private final static int port = 21;
    private final static String user = "advertising";
    private final static String pass = "adv1234";
    private static String SERVER_CHARSET = "ISO-8859-1";
    private static String LOCAL_CHARSET = "UTF-8";
    private static String LOG_TAG = "FTPDownload";

    public static Boolean downloadAndSaveFile(String remote, String local) {
        FTPClient ftpClient = new FTPClient();
        try {
            remote = new String(remote.getBytes(LOCAL_CHARSET),
                    SERVER_CHARSET);
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setControlEncoding(LOCAL_CHARSET);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            String remoteFile1 = remote;
            File downloadFile1 = new File(local);
            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
            outputStream1.close();

            if (success) {
                return true;
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}