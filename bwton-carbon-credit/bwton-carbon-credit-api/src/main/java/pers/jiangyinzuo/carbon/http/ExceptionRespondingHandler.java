package pers.jiangyinzuo.carbon.http;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author Jiang Yinzuo
 */
@Log4j2
@ControllerAdvice
public class ExceptionRespondingHandler {

    private static final ResponseEntity<Map<String, Object>> JSON_PARSE_FAILED = new ResponseEntity<>(createBody("JSON解析错误"), HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<Map<String, Object>> UPDATE_FAILED = new ResponseEntity<>(createBody("添加失败"), HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<Map<String, Object>> BAD_REQUEST = new ResponseEntity<>(createBody("参数错误"), HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<Void> INCORRECT_METHOD = new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    private static final ResponseEntity<Map<String, Object>> UNKNOWN_ERROR = new ResponseEntity<>(createBody("未知错误"), HttpStatus.INTERNAL_SERVER_ERROR);

    @ExceptionHandler(CustomRequestException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleCustomHttpException(CustomRequestException e) {
        return new ResponseEntity<>(createBody(e.getErrMsg()), e.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(createBody(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, JsonParseException.class})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleJsonParseException(Exception e) {
        return JSON_PARSE_FAILED;
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleDuplicateKeyException(DuplicateKeyException e) {
        return UPDATE_FAILED;
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> missingParams(ServletRequestBindingException e) {
        return BAD_REQUEST;
    }
    
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseBody
    public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return INCORRECT_METHOD;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleUnexpectedException(Exception e) {
        log.error(e);
        e.printStackTrace();
        return UNKNOWN_ERROR;
    }

    /**
     * 全局捕获http request参数验证异常
     * @param e
     * @return 400 BAD_REQUEST
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        return HttpResponseUtil.badRequest(e.getMessage());
    }

    /**
     * 全局捕获http request参数类型错误异常
     * @return 400 BAD_REQUEST
     * {
     *   "errCode": -1,
     *   "errMsg": "参数类型错误"
     * }
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException() {
        return HttpResponseUtil.badRequest("参数类型错误");
    }

    private static Map<String, Object> createBody(String errMsg) {
        Map<String, Object> body = new HashMap<>(10);
        body.put("errCode", -1);
        body.put("errMsg", errMsg);
        return body;
    }
}
