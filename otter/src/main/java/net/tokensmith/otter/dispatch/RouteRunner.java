package net.tokensmith.otter.dispatch;


import net.tokensmith.otter.router.entity.io.Answer;
import net.tokensmith.otter.router.entity.io.Ask;
import net.tokensmith.otter.router.exception.HaltException;

public interface RouteRunner {
    Answer run(Ask ask, Answer answer) throws HaltException;

}
