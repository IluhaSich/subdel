package com.example.subdel.storage;

import com.example.grpc.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class GrpcClientRunner implements CommandLineRunner {

    @GrpcClient("subdel")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Override
    public void run(String... args) {
        System.out.println("--- Клиент gRPC запущен ---");

        try {
            // 1. Создаем пользователeй
            String userId;
            CreateUserResponse createResponse;
            String name;
            // a. Создаем пользователя
            name = "Студент";
            System.out.println("--> CreateUser");
            createResponse =
                    userServiceStub.createUser(
                            CreateUserRequest.newBuilder()
                                    .setName(name)
                                    .build()
                    );

            userId = createResponse.getUser().getUserId();
            System.out.println("<-- User created, id = " + userId);
            // b. Создаем пользователя
            name = "Илья";
            System.out.println("--> CreateUser");
            createResponse =
                    userServiceStub.createUser(
                            CreateUserRequest.newBuilder()
                                    .setName(name)
                                    .build()
                    );

            userId = createResponse.getUser().getUserId();
            System.out.println("<-- User created, id = " + userId);
            // c. Создаем пользователя
            name = "Сычев";
            System.out.println("--> CreateUser");
            createResponse =
                    userServiceStub.createUser(
                            CreateUserRequest.newBuilder()
                                    .setName(name)
                                    .build()
                    );

            userId = createResponse.getUser().getUserId();
            System.out.println("<-- User created, id = " + userId);

            // 2. Добавляем деликатес
            System.out.println("--> AddDelicacyToUser");
            userServiceStub.addDelicacyToUser(
                    AddDelicacyToUserRequest.newBuilder()
                            .setUserId(userId)
                            .setDelicacyId("delicacy-1")
                            .build()
            );

            // 3. Получаем деликатесы пользователя
            System.out.println("--> GetUserDelicacy");
            GetUserDelicacyResponse delicacyResponse =
                    userServiceStub.getUserDelicacy(
                            GetUserDelicacyRequest.newBuilder()
                                    .setUserId(userId)
                                    .build()
                    );

            delicacyResponse.getDelicacyList().forEach(d ->
                    System.out.println("   - delicacyId = " + d.getDelicacyId())
            );

            // 4. Удаляем деликатес
            System.out.println("--> RemoveDelicacyFromUser");
            userServiceStub.removeDelicacyFromUser(
                    RemoveDelicacyFromUserRequest.newBuilder()
                            .setUserId(userId)
                            .setDelicacyId("delicacy-1")
                            .build()
            );

            // 5. Получаем пользователей
            System.out.println("--> GetUsers");
            var usersAAA = userServiceStub.getUsers(
                    GetUsersRequest.newBuilder()
                            .build()
            );
            for (var user: usersAAA.getUserList()){
                System.out.println(user.getUserId());
                System.out.println(user.getName());
            }

        } catch (StatusRuntimeException e) {
            System.err.println("gRPC error: " + e.getStatus());
        }

        System.out.println("--- Клиент gRPC завершил работу ---");
    }
}