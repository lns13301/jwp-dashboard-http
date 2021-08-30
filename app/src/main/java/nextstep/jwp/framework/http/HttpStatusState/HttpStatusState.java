package nextstep.jwp.framework.http.HttpStatusState;

import java.net.URL;
import nextstep.jwp.framework.http.HttpPath;
import nextstep.jwp.framework.http.HttpStatus;

public abstract class HttpStatusState {

    private final HttpStatus status;
    private final HttpPath path;

    public HttpStatusState(final HttpStatus status, final HttpPath path) {
        this.status = status;
        this.path = path;
    }

    abstract HttpStatusState state();

    public URL resource() {
        return path.findResourceURL();
    }

    protected HttpStatus getStatus() {
        return status;
    }

    protected HttpPath getPath() {
        return path;
    }
}
