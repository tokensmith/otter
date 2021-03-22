package helper.fake;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;


@WebServlet(value="/fake", name="fakeServlet")
public class FakeServlet extends HttpServlet {
}

