package com.example.edidemo1;

public class Users {

    String name,email,Ward,Village,Block;

    public Users() {
    }

    public Users(String name, String email, String ward, String village, String block) {
        this.name = name;
        this.email = email;
        Ward = ward;
        Village = village;
        Block = block;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getBlock() {
        return Block;
    }

    public void setBlock(String block) {
        Block = block;
    }
}
