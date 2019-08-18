package helper.fake;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


@WebServlet(value="/fake", name="fakeServlet")
public class FakeServlet extends HttpServlet {
}

