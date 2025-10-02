package com.multimedia.media_library.controller.controller_advice;

import com.multimedia.media_library.controller.RedirectAttributeProvider;
import com.multimedia.media_library.exception.UnhandledException;
import com.multimedia.media_library.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.multimedia.media_library.common.Constants.ERROR_MESSAGE_KEY;
import static com.multimedia.media_library.common.Constants.REDIRECT_PATH;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(UnhandledException.class)
    String violationExceptionHandler(UnhandledException ex,
                                     RedirectAttributes redirectAttributes,
                                     HandlerMethod handlerMethod) {
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_KEY, "An unhandled message has occurred, " + ex.getMessage());
        addRedirectAttributes(redirectAttributes, handlerMethod);

        return REDIRECT_PATH;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    String handleValidationException(ConstraintViolationException ex,
                                     RedirectAttributes redirectAttributes,
                                     HandlerMethod handlerMethod) {
        String errorMessage = ex.getConstraintViolations()
                .iterator()
                .next()
                .getMessage();
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_KEY, errorMessage);
        addRedirectAttributes(redirectAttributes, handlerMethod);

        return REDIRECT_PATH;
    }

    @ExceptionHandler(ValidationException.class)
    String violationExceptionHandler(ValidationException ex,
                                     RedirectAttributes redirectAttributes,
                                     HandlerMethod handlerMethod) {
        redirectAttributes.addFlashAttribute(ERROR_MESSAGE_KEY, "Operation resulted in the following violations: \n" + ex.formatViolations());
        addRedirectAttributes(redirectAttributes, handlerMethod);

        return REDIRECT_PATH;
    }

    private void addRedirectAttributes(RedirectAttributes redirectAttributes, HandlerMethod handlerMethod) {
        Object controller = handlerMethod.getBean();
        if (controller instanceof RedirectAttributeProvider provider) {
            provider.addRedirectAttributes(redirectAttributes);
        }
    }
}
