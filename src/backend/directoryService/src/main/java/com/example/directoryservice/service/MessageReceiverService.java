package com.example.directoryservice.service;

import com.example.directoryservice.annotation.LogMessageReceived;
import com.example.directoryservice.utils.CustomLogger;
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
    private final MessageSenderService messageSenderService;
    private final CustomLogger logger;

    /* Public Methods */

    @RabbitListener(queues = "roles-new-queue-directory")
    @LogMessageReceived(tag = CustomLogger.TAG_ROLES,
            deliveryMethod = "Broadcast", queue = "roles-new-queue")
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

    @RabbitListener(queues = "login-profiles-sec-queue-directory")
    @LogMessageReceived(tag = CustomLogger.TAG_USERS,
            deliveryMethod = "Broadcast", queue = "login-profiles-sec-queue")
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

    @RabbitListener(queues = "employee-info-get-queue")
    @LogMessageReceived(tag = CustomLogger.TAG_ORDERS,
            deliveryMethod = "Unicast", queue = "employee-info-get-queue")
    public void receiveEmployeeInfoGet(Message message)
    {
        String idEmployee = new String(message.getBody());
        messageSenderService.sendEmployeeInfoOrder(idEmployee);
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
                    CustomLogger.TAG_ORDERS, methodName);
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
