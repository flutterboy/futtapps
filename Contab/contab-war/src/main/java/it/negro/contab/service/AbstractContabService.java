package it.negro.contab.service;

import it.negro.contab.entity.ContabException;
import it.negro.contab.entity.ErrorInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by gabriele on 16/11/15.
 */
public class AbstractContabService {

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public
    @ResponseBody
    ErrorInfo handleException(HttpServletRequest request, Exception e) {
        Logger logger = Logger.getLogger(AbstractContabService.class);
        logger.error(e.getMessage(), e);
        ErrorInfo result = null;
        try {
            result = new ErrorInfo();
            if (e instanceof ContabException) {
                result.setMessage(e.getMessage());
                result.setDeveloperMessage(((ContabException) e).getDeveloperMessage());
            } else {
                result.setMessage("Errore non previsto!");
                result.setDeveloperMessage(e.getMessage());
            }
            result.setExceptionName(e.getClass().getName());
            String stacktrace = stacktraceString(e);
            stacktrace = stacktrace.replaceAll(" ", "&nbsp;");
            stacktrace = stacktrace.replaceAll("\n", "<br>");
            stacktrace = stacktrace.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
            result.setStackTrace(stacktrace);
            result.setException(e);
        } catch (Exception e1) {
            result = ErrorInfo.defaultErrorInfo(e);
        }
        return result;
    }

    private String stacktraceString(Throwable t) {
        Writer result = null;
        PrintWriter printWriter = null;
        String stacktrace = "Non disponibile!";
        try {
            result = new StringWriter();
            printWriter = new PrintWriter(result);
            t.printStackTrace(printWriter);
            stacktrace = result.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (printWriter != null) printWriter.close();
            } catch (Exception e1) {
            }
            try {
                if (result != null) result.close();
            } catch (Exception e1) {
            }
        }
        return stacktrace;
    }

}
