package com.multimedia.media_library.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public interface RedirectAttributeProvider {
    void addRedirectAttributes(RedirectAttributes redirectAttributes);
}
