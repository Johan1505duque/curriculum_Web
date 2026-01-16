package com.hse.Curriculum.Exception.Profile;
import com.hse.Curriculum.Exception.Login.BusinessException;

public class ProfileAlreadyExistsException extends BusinessException {
    public ProfileAlreadyExistsException(Integer userId) {
        super("El usuario con ID " + userId + " ya tiene un perfil creado");
    }
}
