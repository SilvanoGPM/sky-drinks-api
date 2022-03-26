package com.github.skyg0d.skydrinksapi.util.user;

import com.github.skyg0d.skydrinksapi.domain.TotalUsers;

public class TotalUsersCreator {

    public static TotalUsers createTotalUsers() {
        return TotalUsers
                .builder()
                .total(2)
                .locked(1)
                .unlocked(1)
                .build();
    }

}
