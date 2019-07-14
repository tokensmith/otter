package org.rootservices.otter.dispatch;


import org.rootservices.otter.router.entity.io.Answer;
import org.rootservices.otter.router.entity.io.Ask;
import org.rootservices.otter.router.exception.HaltException;

public interface RouteRunner {
    Answer run(Ask ask, Answer answer) throws HaltException;

}
