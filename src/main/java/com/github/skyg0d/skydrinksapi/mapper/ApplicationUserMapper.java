package com.github.skyg0d.skydrinksapi.mapper;

import com.github.skyg0d.skydrinksapi.domain.ApplicationUser;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPostRequestBody;
import com.github.skyg0d.skydrinksapi.requests.ApplicationUserPutRequestBody;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class ApplicationUserMapper {

    public static final ApplicationUserMapper INSTANCE = Mappers.getMapper(ApplicationUserMapper.class);

    @Mapping(target = "password", source = "applicationUserPostRequestBody.password", qualifiedByName = "encodePassword")
    public abstract ApplicationUser toApplicationUser(ApplicationUserPostRequestBody applicationUserPostRequestBody);

    @Mapping(target = "password", source = "applicationUserPutRequestBody.password", qualifiedByName = "encodePassword")
    public abstract ApplicationUser toApplicationUser(ApplicationUserPutRequestBody applicationUserPutRequestBody);

    @Named("encodePassword")
    String encodePassword(String password) {
        if (password != null && !password.isEmpty()) {
            return new BCryptPasswordEncoder().encode(password);
        }

        return password;
    }

}
