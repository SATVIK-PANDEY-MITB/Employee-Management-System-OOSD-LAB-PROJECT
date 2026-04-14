package com.ems.model;

/**
 * Department Model class representing a department entity
 */
public class Department {
    private int departmentId;
    private String departmentName;
    private String head;
    private int totalEmployees;
    private String description;

    public Department(int departmentId, String departmentName, String head, int totalEmployees, String description) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.head = head;
        this.totalEmployees = totalEmployees;
        this.description = description;
    }

    // Getters and Setters
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", head='" + head + '\'' +
                ", totalEmployees=" + totalEmployees +
                ", description='" + description + '\'' +
                '}';
    }
}
