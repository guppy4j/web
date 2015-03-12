package org.guppy4j.web.http;

import org.guppy4j.web.http.tempfiles.DefaultTempFilesFactory;
import org.guppy4j.web.http.tempfiles.TempFiles;
import org.guppy4j.web.http.tempfiles.TempFilesFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static org.guppy4j.web.http.util.ConnectionUtil.close;

/**
 * A tiny embeddable HTTP server daemon
 */
public final class ServerDaemon implements IDaemon, IPortListener {

    /**
     * Maximum time to wait on Socket.getInputStream().read() (in milliseconds)
     * This is required as the Keep-Alive HTTP connections would otherwise
     * block the socket reading thread forever (or as long the browser is open).
     */
    private static final int SOCKET_READ_TIMEOUT = 5000;

    private final InetSocketAddress address;
    private final ServerSocket serverSocket;

    private final Connections connections = new Connections();

    private final RequestExecutor executor;
    private final IServer server;
    private final Thread serverThread;

    /**
     * Pluggable strategy for creating and cleaning up temporary io.
     */
    private final TempFilesFactory tempFilesFactory;

    /**
     * Constructs an HTTP server on given port on localhost.
     */
    public ServerDaemon(int port, IServer server) {
        this(null, port, server);
    }

    /**
     * Constructs an HTTP server on given hostname and port.
     */
    public ServerDaemon(String hostname, int port,
                        IServer server) {
        this(hostname, port, server, new DefaultRequestExecutor());
    }

    public ServerDaemon(String hostname, int port,
                        IServer server, RequestExecutor executor) {
        this.tempFilesFactory = new DefaultTempFilesFactory();
        this.executor = executor;
        this.server = server;
        serverSocket = createServerSocket();
        address = getSocketAddress(hostname, port);
        serverThread = createServerThread(this::run);
    }

    /* *** Interface methods *** */
    
    /**
     * Start the server.
     *
     * @throws java.io.IOException if the socket is in use.
     */
    @Override
    public void start() throws IOException {
        serverSocket.bind(address);
        serverThread.start();
    }

    /**
     * Stop the server.
     */
    @Override
    public void stop() {
        try {
            close(serverSocket);
            connections.closeAll();
            if (serverThread != null) {
                serverThread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final int getListeningPort() {
        return serverSocket == null ? -1 : serverSocket.getLocalPort();
    }

    @Override
    public final boolean wasStarted() {
        return serverSocket != null && serverThread != null;
    }

    @Override
    public final boolean isAlive() {
        return wasStarted() && !serverSocket.isClosed() && serverThread.isAlive();
    }

    /* Execution */

    private void run() {
        do {
            try {
                final Socket socket = acceptSocket(serverSocket, connections);
                final InputStream in = socket.getInputStream();
                executor.execute(() -> {
                    try (OutputStream out = socket.getOutputStream()) {
                        handleRequest(socket, in, out);
                    } catch (IOException e) {
                        handleException(e);
                    } finally {
                        close(in, socket);
                        connections.remove(socket);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!serverSocket.isClosed());
    }

    private void handleRequest(Socket socket, InputStream in, OutputStream out)
            throws IOException {
        final TempFiles tempFiles = tempFilesFactory.create();
        final Request request = new Request(tempFiles, in, out, socket.getInetAddress());
        while (!socket.isClosed()) {
            request.handleBy(server);
        }
    }

    private static void handleException(IOException e) {
        // When the socket is closed by the client, we throw our own SocketException
        // to break the  "keep alive" loop above.
        if (!(e instanceof SocketException && "HttpServer Shutdown".equals(e.getMessage()))) {
            e.printStackTrace();
        }
    }

    private static Socket acceptSocket(ServerSocket serverSocket, Connections connections)
            throws IOException {
        final Socket socket = serverSocket.accept();
        connections.add(socket);
        socket.setSoTimeout(SOCKET_READ_TIMEOUT);
        return socket;
    }

    private static InetSocketAddress getSocketAddress(String hostname, int port) {
        return (hostname != null)
            ? new InetSocketAddress(hostname, port)
            : new InetSocketAddress(port);
    }

    private static ServerSocket createServerSocket() {
        try {
            return new ServerSocket();
        } catch (IOException e) {
            throw new IllegalStateException("Could not create ServerSocket", e);
        }
    }

    private static Thread createServerThread(Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName("HttpServer Main Listener");
        return thread;
    }
}