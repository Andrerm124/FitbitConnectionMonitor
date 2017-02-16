package com.ljmu.andre.fitbitconnectionmonitor.StreamWrappers;

import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class HttpURLConnectionWrapper extends HttpURLConnection {

    private HttpURLConnection wrappedConnection;

    /**
     * Constructor for the HttpURLConnection.
     *
     * @param u the URL
     */
    public HttpURLConnectionWrapper(URL u, HttpURLConnection wrappedConnection) {
        super(u);
        this.wrappedConnection = wrappedConnection;
    }

    @Override public void connect() throws IOException {
        Timber.d("Called: HttpURLConnection");
        wrappedConnection.connect();
    }

    @Override public void disconnect() {
        Timber.d("Called: HttpURLConnection");
        wrappedConnection.disconnect();
    }

    /**
     * Returns the encoding used to transmit the response body over the network.
     * This is null or "identity" if the content was not encoded, or "gzip" if
     * the body was gzip compressed. Most callers will be more interested in the
     * {@link #getContentType() content type}, which may also include the
     * content's character encoding.
     */
    @Override public String getContentEncoding() {
        Timber.d("Called: HttpURLConnection");
        return wrappedConnection.getContentEncoding(); // overridden for Javadoc only
    }

    @Override public InputStream getInputStream() throws IOException {
        InputStream stream = super.getInputStream();

        Timber.d("HttpInputStream: " + stream);
        return stream;
    }

    @Override public OutputStream getOutputStream() throws IOException {
        OutputStream stream = super.getOutputStream();

        Timber.d("HttpOutputStream: " + stream);
        return stream;
    }    /**
     * Returns an input stream from the server in the case of an error such as
     * the requested file has not been found on the remote server. This stream
     * can be used to read the data the server will send back.
     *
     * @return the error input stream returned by the server.
     */
    public InputStream getErrorStream() {
        InputStream stream = wrappedConnection.getErrorStream();
        Timber.d("ErrorInputStream: " + stream);
        return stream;
    }

    /**
     * Equivalent to {@code setFixedLengthStreamingMode((long) contentLength)},
     * but available on earlier versions of Android and limited to 2 GiB.
     */
    @RequiresApi(api = VERSION_CODES.KITKAT) public void setFixedLengthStreamingMode(int contentLength) {
        Timber.d("Called: HttpURLConnection");
        setFixedLengthStreamingMode((long) contentLength);
    }

    /**
     * Configures this connection to stream the request body with the known
     * fixed byte count of {@code contentLength}.
     *
     * @param contentLength the fixed length of the HTTP request body.
     * @throws IllegalStateException    if already connected or another mode already set.
     * @throws IllegalArgumentException if {@code contentLength} is less than zero.
     * @see #setChunkedStreamingMode
     * @since 1.7
     */
    @RequiresApi(api = VERSION_CODES.KITKAT) public void setFixedLengthStreamingMode(long contentLength) {
        Timber.d("Called: HttpURLConnection");
        wrappedConnection.setFixedLengthStreamingMode(contentLength);
    }

    /**
     * Returns the permission object (in this case {@code SocketPermission})
     * with the host and the port number as the target name and {@code
     * "resolve, connect"} as the action list. If the port number of this URL
     * instance is lower than {@code 0} the port will be set to {@code 80}.
     *
     * @return the permission object required for this connection.
     * @throws IOException if an IO exception occurs during the creation of the
     *                     permission object.
     */
    @Override
    public java.security.Permission getPermission() throws IOException {
        Timber.d("Called: HttpURLConnection");

        return wrappedConnection.getPermission();
    }

    /**
     * Stream a request body whose length is not known in advance. Old HTTP/1.0
     * only servers may not support this mode.
     * <p>
     * <p>When HTTP chunked encoding is used, the stream is divided into
     * chunks, each prefixed with a header containing the chunk's size. Setting
     * a large chunk length requires a large internal buffer, potentially
     * wasting memory. Setting a small chunk length increases the number of
     * bytes that must be transmitted because of the header on every chunk.
     * Most caller should use {@code 0} to get the system default.
     *
     * @param chunkLength the length to use, or {@code 0} for the default chunk
     *                    length.
     * @throws IllegalStateException if already connected or another mode
     *                               already set.
     * @see #setFixedLengthStreamingMode
     */
    public void setChunkedStreamingMode(int chunkLength) {
        Timber.d("Called: HttpURLConnection");
        wrappedConnection.setChunkedStreamingMode(chunkLength);
    }

    /**
     * Returns the request method which will be used to make the request to the
     * remote HTTP server. All possible methods of this HTTP implementation is
     * listed in the class definition.
     *
     * @return the request method string.
     * @see #method
     * @see #setRequestMethod
     */
    public String getRequestMethod() {
        Timber.d("Called: HttpURLConnection");
        return wrappedConnection.getRequestMethod();
    }

    /**
     * Returns the value of {@code followRedirects} which indicates if this
     * connection follows a different URL redirected by the server. It is
     * enabled by default.
     *
     * @return the value of the flag.
     * @see #setFollowRedirects
     */
    public static boolean getFollowRedirects() {
        Timber.d("Called: HttpURLConnection");
        return HttpURLConnection.getFollowRedirects();
    }    /**
     * Returns the response code returned by the remote HTTP server.
     *
     * @return the response code, -1 if no valid response code.
     * @throws IOException if there is an IO error during the retrieval.
     * @see #getResponseMessage
     */
    public int getResponseCode() throws IOException {
        Timber.d("Called: HttpURLConnection");
        // Call getInputStream() first since getHeaderField() doesn't return
        // exceptions
        getInputStream();
        String response = getHeaderField(0);
        if (response == null) {
            return -1;
        }
        response = response.trim();
        int mark = response.indexOf(" ") + 1;
        if (mark == 0) {
            return -1;
        }
        int last = mark + 3;
        if (last > response.length()) {
            last = response.length();
        }
        responseCode = Integer.parseInt(response.substring(mark, last));
        if (last + 1 <= response.length()) {
            responseMessage = response.substring(last + 1);
        }
        return responseCode;
    }

    /**
     * Sets the flag of whether this connection will follow redirects returned
     * by the remote server.
     *
     * @param auto the value to enable or disable this option.
     */
    public static void setFollowRedirects(boolean auto) {
        Timber.d("Called: HttpURLConnection");
        HttpURLConnection.setFollowRedirects(auto);
    }    /**
     * Returns the response message returned by the remote HTTP server.
     *
     * @return the response message. {@code null} if no such response exists.
     * @throws IOException if there is an error during the retrieval.
     * @see #getResponseCode()
     */
    public String getResponseMessage() throws IOException {
        Timber.d("Called: HttpURLConnection");

        if (responseMessage != null) {
            return responseMessage;
        }
        getResponseCode();
        return responseMessage;
    }



    /**
     * Sets the request command which will be sent to the remote HTTP server.
     * This method can only be called before the connection is made.
     *
     * @param method the string representing the method to be used.
     * @throws ProtocolException if this is called after connected, or the method is not
     *                           supported by this HTTP implementation.
     * @see #getRequestMethod()
     * @see #method
     */
    public void setRequestMethod(String method) throws ProtocolException {
        Timber.d("Called: HttpURLConnection");
        wrappedConnection.setRequestMethod(method);
    }

    /**
     * Returns whether this connection uses a proxy server or not.
     *
     * @return {@code true} if this connection passes a proxy server, false
     * otherwise.
     */
    public boolean usingProxy() {
        Timber.d("Called: HttpURLConnection");
        return wrappedConnection.usingProxy();
    }





    /**
     * Returns whether this connection follows redirects.
     *
     * @return {@code true} if this connection follows redirects, false
     * otherwise.
     */
    public boolean getInstanceFollowRedirects() {
        Timber.d("Called: HttpURLConnection");
        return instanceFollowRedirects;
    }

    /**
     * Sets whether this connection follows redirects.
     *
     * @param followRedirects {@code true} if this connection will follows redirects, false
     *                        otherwise.
     */
    public void setInstanceFollowRedirects(boolean followRedirects) {
        Timber.d("Called: HttpURLConnection");
        instanceFollowRedirects = followRedirects;
    }

    /**
     * Returns the date value in milliseconds since {@code 01.01.1970, 00:00h}
     * corresponding to the header field {@code field}. The {@code defaultValue}
     * will be returned if no such field can be found in the response header.
     *
     * @param field        the header field name.
     * @param defaultValue the default value to use if the specified header field wont be
     *                     found.
     * @return the header field represented in milliseconds since January 1,
     * 1970 GMT.
     */
    @Override
    public long getHeaderFieldDate(String field, long defaultValue) {
        Timber.d("Called: HttpURLConnection");
        return super.getHeaderFieldDate(field, defaultValue);
    }

}
