package com.vncsferrarini.ecommerce;

import com.vncsferrarini.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet implements Servlet {

    public static final String TOPIC_NEW_ORDER = "ECOMMERCE_NEW_ORDER";
    public static final String TOPIC_SEND_EMAIL = "ECOMMERCE_SEND_EMAIL";

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = UUID.randomUUID().toString();
            var amount = req.getParameter("amount");
            var email = req.getParameter("email");
            var order = new Order(orderId, email, new BigDecimal(amount));
            this.orderDispatcher.send(TOPIC_NEW_ORDER, new CorrelationId(GenerateAllReportsServlet.class.getSimpleName()), email, order);
            this.emailDispatcher.send(TOPIC_SEND_EMAIL, new CorrelationId(GenerateAllReportsServlet.class.getSimpleName()), email, "Thank you for your order!");

            System.out.println("New order sent successfully");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("New order sent successfully");
        } catch (ExecutionException | InterruptedException e) {
            throw new ServletException(e);
        }
    }
}