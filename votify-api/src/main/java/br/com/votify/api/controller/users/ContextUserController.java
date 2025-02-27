package br.com.votify.api.controller.users;

import br.com.votify.api.dto.ApiResponse;
import br.com.votify.core.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@RestController
@RequestMapping("/user")
public class ContextUserController {
    @Autowired
    private UserContextService userContextService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> loginUser() {
        throw new NotImplementedException();
    }
}
