package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.framework.http.HttpBody;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
import nextstep.jwp.framework.http.HttpStatus;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), HttpStatus.OK);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        final HttpStatus status = register(request.getBody());
        response.create(request.getRequestLine(), request.getHeaders(), request.getBody(), status);
    }

    private HttpStatus register(final HttpBody body) {
        try {
            createAccount(body);
            return HttpStatus.CREATED;
        } catch (DBNotFoundException ignored) {
            return HttpStatus.UNAUTHORIZED;
        }
    }

    private void createAccount(final HttpBody body) {
        final Map<String, String> queryParams = body.getQueryParams();
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final String email = queryParams.get("email");

        InMemoryUserRepository.save(new User(2L, account, password, email));
        logger.debug(account + "님의 새로운 계정이 생성 되었습니다.");
    }
}
