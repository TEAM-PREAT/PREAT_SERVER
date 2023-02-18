package com.andes.preat.dto.request.auth;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequest {
    private String nickname;
    private Integer salty;
    private Integer sweet;
    private Integer spicy;
    private List<Integer> hateFoods;
}
