package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/dev")
public class DevController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/1")
    public String test() {
        return "test from security service";
    }


    @GetMapping("/encode")
    public String encodePassword(@RequestParam String password)
    {
        return bCryptPasswordEncoder.encode(password).toString();
    }
}
