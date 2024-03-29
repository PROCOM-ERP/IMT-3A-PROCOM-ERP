package com.example.orderservice.service;

import com.example.orderservice.annotation.LogMessageReceived;
import com.example.orderservice.dto.EmployeeDirectoryResponseDto;
import com.example.orderservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageReceiverService {

    private final RoleService roleService;
    private final LoginProfileService loginProfileService;
    private final EmployeeService employeeService;
    private final OrderService orderService;
    private final CustomLogger logger;

    /* Public Methods */

    @RabbitListener(queues = "roles-new-queue-order")
    @LogMessageReceived(tag = CustomLogger.TAG_ROLES,
            deliveryMethod = "Broadcast", queue = "roles-new-queue-order")
    public void receiveRolesNewMessage(Message message)
    {
        String getRoleByNamePath = new String(message.getBody());
        try {
            // try to save all microservice roles
            roleService.createRole(getRoleByNamePath);
        } catch (Exception ignored) {
            String methodName = "receiveRolesNewMessage";
            logger.error("Role creation failed",
                    CustomLogger.TAG_ROLES, methodName);
        }
    }

    @RabbitListener(queues = "login-profiles-sec-queue-order")
    @LogMessageReceived(tag = CustomLogger.TAG_USERS,
            deliveryMethod = "Broadcast", queue = "login-profiles-sec-queue-order")
    public void receiveLoginProfilesSecMessage(Message message, @Header("amqp_receivedRoutingKey") String routingKey)
    {
        String body = new String(message.getBody());
        switch (routingKey) {
            case "login-profiles.new":
                receiveLoginProfilesNewMessage(body);
                break;
            case "login-profile.activation":
                receiveLoginProfileActivationMessage(body);
                break;
            case "login-profiles.jwt.disable.old":
                receiveLoginProfilesJwtDisableOldMessage();
                break;
            case "login-profile.jwt.disable.old":
                receiveLoginProfileJwtDisableOldMessage(body);
                break;
        }
    }

    @RabbitListener(queues = "employee-info-order-queue")
    @LogMessageReceived(tag = CustomLogger.TAG_ORDERS,
            deliveryMethod = "Unicast", queue = "employee-info-order-queue")
    public void receiveEmployeeInfoGet(Message message)
    {
        String getEmployeeByIdPath = new String(message.getBody());
        try {
            // try to retrieve user information and manager
            EmployeeDirectoryResponseDto employeeDto =
                    employeeService.getEmployeeFromMicroserviceById(getEmployeeByIdPath);
            // try to update the Order approver
            orderService.updateOrderApproverByOrdererId(employeeDto);
        } catch (Exception e) {
            String methodName = "receiveEmployeeInfoGet";
            //logger.error("Order approver set failed.", CustomLogger.TAG_ORDERS, methodName);
            logger.error(e, CustomLogger.TAG_ORDERS, methodName);
        }
    }

    /* Private Methods */
    private void receiveLoginProfilesNewMessage(String idLoginProfile)
    {
        loginProfileService.createLoginProfile(idLoginProfile);
    }

    private void receiveLoginProfileActivationMessage(String getLoginProfileActivationById)
    {
        try {
            loginProfileService.updateLoginProfileActivationById(getLoginProfileActivationById);
        } catch (Exception ignored) {
            String methodName = "receiveLoginProfileActivationMessage";
            logger.error("Login profile activation update failed",
                    CustomLogger.TAG_USERS, methodName);
        }
    }

    private void receiveLoginProfilesJwtDisableOldMessage()
    {
        loginProfileService.updateAllLoginProfileJwtGenMin();
    }

    private void receiveLoginProfileJwtDisableOldMessage(String idLoginProfile)
    {
        loginProfileService.updateLoginProfileJwtGenMinAtById(idLoginProfile);
    }
}
