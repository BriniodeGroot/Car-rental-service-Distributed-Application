package be.ucll.da.userservice.domain;

public record User(Integer id, String firstName, String lastName, String email, Boolean isClient) {}