package org.example.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.db.ConfigLoader;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;
import org.example.repository.BaristaRepository;
import org.example.repository.CoffeeRepository;
import org.example.repository.OrderRepository;
import org.example.repository.imp.BaristaRepositoryImp;
import org.example.repository.imp.CoffeeRepositoryImp;
import org.example.repository.imp.OrderRepositoryImp;
import org.example.service.imp.BaristaService;
import org.example.service.imp.CoffeeService;
import org.example.service.imp.OrderService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

@WebListener
public class ServletInitializer implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(ServletInitializer.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConfigLoader configLoader = new ConfigLoader("/db.properties");
        ConnectionManager connectionManager = new ConnectionManagerImp(configLoader);
        Connection connection;
        try {
            connection = connectionManager.getConnection();

            BaristaRepository baristaRepository = new BaristaRepositoryImp(connection);
            CoffeeRepository coffeeRepository = new CoffeeRepositoryImp(connection);
            OrderRepository orderRepository = new OrderRepositoryImp(connection);

            BaristaService baristaService = new BaristaService(baristaRepository, orderRepository);
            CoffeeService coffeeService = new CoffeeService(orderRepository, coffeeRepository);
            OrderService orderService = new OrderService(baristaRepository, coffeeRepository, orderRepository);

            BaristaServlet baristaServlet = new BaristaServlet(baristaService);
            CoffeeServlet coffeeServlet = new CoffeeServlet(coffeeService);
            OrderServlet orderServlet = new OrderServlet(orderService);

            sce.getServletContext().addServlet("BaristaServlet", baristaServlet).addMapping("/barista/*");
            sce.getServletContext().addServlet("CoffeeServlet", coffeeServlet).addMapping("/coffee/*");
            sce.getServletContext().addServlet("OrderServlet", orderServlet).addMapping("/orders/*");

        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }


    }

}
