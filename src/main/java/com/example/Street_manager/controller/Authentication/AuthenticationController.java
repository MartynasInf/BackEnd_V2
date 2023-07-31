package com.example.Street_manager.controller.Authentication;

import com.example.Street_manager.dto.Authentication.AuthenticationRequestDto;
import com.example.Street_manager.dto.Authentication.AuthenticationResponseDto;
import com.example.Street_manager.service.Authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

//    @GetMapping("/home")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<String> sayHi(){
//        return ResponseEntity.ok("Hello from unprotected endpoint");
//    }

//    @GetMapping("/authenticated/test")
//    @CrossOrigin(origins = "http://localhost:4200")
//    public ResponseEntity<String> greeting(){
//        return ResponseEntity.ok("Hello from protected endpoint");
//    }

    @PostMapping("/authenticate")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<AuthenticationResponseDto> authenticate (@RequestBody AuthenticationRequestDto request){
        return  ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
