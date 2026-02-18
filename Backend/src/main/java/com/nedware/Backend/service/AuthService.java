package com.nedware.Backend.service;


import com.nedware.Backend.domain.dto.LoginDto;
import com.nedware.Backend.domain.dto.RegisterDto;

public interface AuthService {
    String login(LoginDto loginDto);
    String register(RegisterDto registerDto);
}

