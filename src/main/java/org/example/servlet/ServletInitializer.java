package org.example.servlet;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.db.ConfigLoader;
import org.example.db.ConnectionManager;
import org.example.db.ConnectionManagerImp;

@WebListener
public class ServletInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConfigLoader configLoader = new ConfigLoader("D:\\Projects\\Java\\REST\\db.properties");
        ConnectionManager connectionManager = new ConnectionManagerImp(configLoader);

        BaristaServlet baristaServlet = new BaristaServlet(connectionManager);
        CoffeeServlet coffeeServlet = new CoffeeServlet(connectionManager);
        OrderServlet orderServlet = new OrderServlet(connectionManager);

        sce.getServletContext().addServlet("BaristaServlet", baristaServlet).addMapping("/barista/*");
        sce.getServletContext().addServlet("CoffeeServlet", coffeeServlet).addMapping("/coffee/*");
        sce.getServletContext().addServlet("OrderServlet", orderServlet).addMapping("/orders/*");

    }

}
