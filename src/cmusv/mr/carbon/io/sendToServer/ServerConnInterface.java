package cmusv.mr.carbon.io.sendToServer;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerConnInterface {
  private static final String TAG = "ServerConnInterface";
  private static final String lineEnd = "\r\n";
  private static final String twoHyphens = "--";
  private static final String boundary = "*****";
  private static final int uploadByteLimit = 2 * 1024 * 1024;

  public static void doFileUpload(String exsistingFileName, String urlString) {
    HttpURLConnection conn = null;
    DataOutputStream dos = null;
    DataInputStream inStream = null;
    String responseFromServer = "";
    int chunkCnt = 0;

    // ------------------ CLIENT REQUEST
    try {

      Log.e(TAG, "Inside second Method");
      FileInputStream fileInputStream = new FileInputStream(new File(exsistingFileName));

      while (fileInputStream.available() > 0) {
        conn = setupConn(urlString);
        Log.e(TAG, "available:" + fileInputStream.available());
        String filename = exsistingFileName.replace(".csv", "_" + chunkCnt + ".csv");
        dos = new DataOutputStream(conn.getOutputStream());
        Log.e(TAG, filename);
        // write header
        writeHeader(filename, dos);
        writeContentTilChunkFull(dos, fileInputStream);
        chunkCnt++;
        writeEnd(dos);
        dos.flush();
        dos.close();
        try {
          inStream = new DataInputStream(conn.getInputStream());
          String str;

          while ((str = inStream.readLine()) != null) {
            Log.e(TAG, "Server Response" + str);
          }

          inStream.close();
        }

        catch (IOException ioex) {
          Log.e(TAG, "error: " + ioex.getMessage());
        }
      }
      fileInputStream.close();

    }

    catch (MalformedURLException ex)

    {

      Log.e(TAG, "error: " + ex.getMessage());

    }

    catch (IOException ioe)

    {

      Log.e(TAG, "error: " + ioe.getMessage());

    }

    // ------------------ read the SERVER RESPONSE

  }

  private static void writeContentTilChunkFull(DataOutputStream dos, FileInputStream fileInputStream)
      throws IOException {
    int bytesRead, bytesAvailable, bufferSize, bytesSum = 0;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    // create a buffer of maximum size
    bytesAvailable = fileInputStream.available();
    bufferSize = Math.min(bytesAvailable, maxBufferSize);
    buffer = new byte[bufferSize];
    // read file and write it into form...

    bytesRead = 1;
    while (bytesRead > 0 && bytesSum < uploadByteLimit) {
      bytesRead = fileInputStream.read(buffer, 0, bufferSize);
      dos.write(buffer, 0, bufferSize);
      bytesAvailable = fileInputStream.available();
      bufferSize = Math.min(bytesAvailable, maxBufferSize);
      bytesSum += bytesRead;

    }
  }

  private static HttpURLConnection setupConn(String urlString) throws IOException {
    // open a URL connection to the Servlet
    URL url = new URL(urlString);

    // Open a HTTP connection to the URL
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    // Allow Inputs
    conn.setDoInput(true);

    // Allow Outputs
    conn.setDoOutput(true);

    // Don't use a cached copy.
    conn.setUseCaches(false);

    // Use a post method.
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Connection", "Keep-Alive");
    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
    return conn;
  }

  private static void writeHeader(String filename, DataOutputStream dos) throws IOException {
    dos.writeBytes(twoHyphens + boundary + lineEnd);
    String header = "Content-Disposition: form-data; name=\"file\";filename=\"" + filename + "\""
        + lineEnd;
    Log.e(TAG, header);
    dos.writeBytes(header);
    dos.writeBytes(lineEnd);
    Log.e(TAG, "Headers are written");
  }

  private static void writeEnd(DataOutputStream dos) throws IOException {
    // send multipart form data necesssary after file data...
    dos.writeBytes(lineEnd);
    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
    // close streams
    Log.e(TAG, "File is written");

  }
}