package cader.webapp;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomizationBean implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
  
    @Override
    public void customize(ConfigurableServletWebServerFactory container) {        
        container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error"));
        container.addErrorPages(new ErrorPage("/error"));
    }
}

