package it.unibz.emails;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

@Provider
public class Sanitizer implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String params = URLDecoder.decode(
                new String(containerRequestContext.getEntityStream().readAllBytes()),
                Charset.defaultCharset());

        String newParams = Arrays.stream(params.split("&"))
                .map(param -> Jsoup.clean(Jsoup.parse(param).text(), Safelist.basic()))
                .collect(Collectors.joining("&"));

        containerRequestContext.setEntityStream(new ByteArrayInputStream(newParams.getBytes()));
    }
}
