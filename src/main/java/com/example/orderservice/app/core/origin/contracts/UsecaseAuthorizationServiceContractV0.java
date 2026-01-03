package com.example.orderservice.app.core.origin.contracts;

import org.springframework.stereotype.Service;

import com.example.orderservice.app.core.origin.interfaces.UsecaseAuthorizationService;
import com.example.orderservice.app.core.origin.schemas.User;

@Service
public class UsecaseAuthorizationServiceContractV0 implements UsecaseAuthorizationService {

    @Override
    public boolean isAuthorized(User user, String usecaseName) {
        // Custom authorization: allow all authenticated users
        // Or implement role-based check: return "ADMIN".equals(user.role());
        System.out.println("Inside UsecaseAuthorizationServiceContractV0");
        System.out.println("Authorizing user " + user.email() + " for usecase " + usecaseName);
        return user != null && user.id() != null;
    }

}
