package nextstep.jwp.framework.http.common;

import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.framework.http.session.HttpCookie;
import nextstep.jwp.framework.http.session.HttpSession;
import nextstep.jwp.framework.http.session.HttpSessions;

public class HttpHeaders {

    public static final String HEADER_DELIMITER = ": ";
    private static final String SESSION_ID = "JSESSIONID";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int VALUE_KEY_INDEX = 1;
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE_KEY = "Cookie";
    public static final String CONTENT_TYPE = "Content-Type";

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public HttpHeaders(final String lines) {
        this.headers = convert(lines);
        this.cookie = new HttpCookie();
        addCookie();
    }

    private Map<String, String> convert(final String lines) {
        final Map<String, String> result = new HashMap<>();
        final String[] headerLines = lines.split(LINE_DELIMITER);

        for (final String header : headerLines) {
            final String[] split = header.split(HEADER_DELIMITER);

            putHeaders(result, split[HEADER_KEY_INDEX].trim(), split[VALUE_KEY_INDEX].trim());
        }

        return result;
    }

    private void putHeaders(final Map<String, String> headers, final String key, final String value) {
        if (key.equals(CONTENT_LENGTH)) {
            headers.put(key, value);
        }
        if (key.equals(COOKIE_KEY)) {
            headers.put(key, value);
        }
    }

    public void putContentType(final Path path) throws IOException {
        String type = FileUtils.probeContentType(path);
        headers.put(CONTENT_TYPE, type);
    }

    public void setContentLength(final int length) {
        headers.remove(CONTENT_LENGTH);
        headers.put(CONTENT_LENGTH, "" + length);
    }

    public int contentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public void addCookie() {
        final String cookies = headers.get(COOKIE_KEY);

        if (Objects.isNull(cookies)) {
            return;
        }
        cookie.addCookies(cookies);
    }

    public boolean hasCookie() {
        return Objects.nonNull(cookie.get(SESSION_ID)) && HttpSessions.isLoggedIn(cookie.get(SESSION_ID));
    }

    public HttpSession sessions() {
        String id = cookie.get(SESSION_ID);

        if (Objects.isNull(id)) {
            id = createId();
        }

        return HttpSessions.getSession(id);
    }

    private String createId() {
        final String uuid = UUID.randomUUID().toString();

        cookie.addCookie(SESSION_ID, uuid);

        return uuid;
    }

    public void setCookie(final String id) {
        headers.put("Set-Cookie", createNewCookie(id));
    }

    private String createNewCookie(final String value) {
        return SESSION_ID + "=" + value + "; ";
    }

    public String convertHeaderToResponse() {
        final StringBuilder builder = new StringBuilder();

        for (Entry<String, String> pair : headers.entrySet()) {
            createResponseHeaders(builder, pair);
        }

        return builder.toString();
    }

    private void createResponseHeaders(final StringBuilder builder, final Entry<String, String> pair) {
        if (pair.getKey().equals(COOKIE_KEY)) {
            return;
        }
        builder.append(pair.getKey())
            .append(HEADER_DELIMITER)
            .append(pair.getValue())
            .append("\r\n");
    }
}
