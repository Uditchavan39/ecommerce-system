package com.ecom.store.ecommerce_store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Role() {
    }

    public Role(Long id, User user, String roleType) {
        this.id = id;
        this.user = user;
        this.roleType = roleType;
    }

    public Long getId() {
        return id;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Role [id=" + id + ", roleType=" + roleType + ", user=" + user + "]";
    }

}
