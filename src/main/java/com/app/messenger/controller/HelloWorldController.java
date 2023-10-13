package com.app.messenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/hello")
    @PreAuthorize("hasAuthority('DELETE_ADMIN')")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World! User");
    }


    @GetMapping("/admin/hello")
    public ResponseEntity<String> helloWorldAdmin() {
        return ResponseEntity.ok("Hello World! Admin");
    }

    @GetMapping("/admin/delete")
    public ResponseEntity<String> deleteWorldAdmin() {
        return ResponseEntity.ok("Hello World! Admin delete");
    }

    @GetMapping("/root/hello")
    public ResponseEntity<String> helloWorldRoot() {
        return ResponseEntity.ok("Hello World! Root");
    }
}
