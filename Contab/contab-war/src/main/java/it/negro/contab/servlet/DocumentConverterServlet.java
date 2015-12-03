package it.negro.contab.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Base64;
import java.util.UUID;

/**
 * Created by gabriele on 16/11/15.
 */
public class DocumentConverterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.service(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.service(request, response);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mode = req.getParameter("mode");
        if (mode.equals("gen")) {
            String content = req.getParameter("file");
            String name = req.getParameter("name");
            String contentType = req.getParameter("type");
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            String uuid = UUID.randomUUID().toString();
            req.getSession().setAttribute(uuid, DocumentFile.newz(decodedBytes, name, contentType));
            resp.getOutputStream().print(uuid);
            resp.flushBuffer();
            return;
        }
        ByteArrayInputStream inputStream = null;
        ReadableByteChannel inputChannel = null;
        ByteBuffer byteBuffer = null;
        WritableByteChannel outputChannel = null;
        try {
            DocumentFile documentFile = (DocumentFile) req.getSession().getAttribute(mode);
            resp.setHeader("Content-Disposition", "attachment; filename=" + documentFile.getName());
            resp.setContentType(documentFile.getContentType());
            resp.setContentLength(documentFile.length());
            inputStream = new ByteArrayInputStream(documentFile.getFile());
            inputChannel = Channels.newChannel(inputStream);
            byteBuffer = ByteBuffer.allocate(16384);
            outputChannel = Channels.newChannel(resp.getOutputStream());
            int numRead = 0;
            while (numRead >= 0) {
                numRead = inputChannel.read(byteBuffer);
                byteBuffer.flip();
                outputChannel.write(byteBuffer);
                byteBuffer.rewind();
            }
            req.getSession().removeAttribute(mode);
        } finally {
            if (outputChannel != null)
                outputChannel.close();
            if (inputChannel != null)
                inputChannel.close();
            if (inputStream != null)
                inputStream.close();
            resp.flushBuffer();
        }
    }

    public static class DocumentFile {
        private byte[] file;
        private String name;
        private String contentType;

        public byte[] getFile() {
            return file;
        }

        public void setFile(byte[] file) {
            this.file = file;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public int length() {
            return file.length;
        }

        public static DocumentFile newz(byte[] file, String name, String contentType) {
            return new DocumentFile(file, name, contentType);
        }

        public DocumentFile(byte[] file, String name, String contentType) {
            this.file = file;
            this.name = name;
            this.contentType = contentType;
        }
    }
}
