package com.example.inframedic;

public class Issue {
    private String hospitalName;
    private String hospitalAddress;
    private String issueDescription;
    private String issueRoomNo;
    private String issueWardNo;
    private String issueDepartment;
    private String imageUrl;
    private String username;
    private String userId;

    public Issue(String hospitalName, String hospitalAddress, String issueDescription,
                 String issueRoomNo, String issueWardNo, String issueDepartment, String imageUrl, String username, String userId) {
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.issueDescription = issueDescription;
        this.issueRoomNo = issueRoomNo;
        this.issueDepartment = issueDepartment;
        this.issueWardNo = issueWardNo;
        this.imageUrl = imageUrl;
        this.username = username;
        this.userId = userId;
    }

    public String getIssueRoomNo() {
        return issueRoomNo;
    }

    public void setIssueRoomNo(String issueRoomNo) {
        this.issueRoomNo = issueRoomNo;
    }

    public String getIssueWardNo() {
        return issueWardNo;
    }

    public void setIssueWardNo(String issueWardNo) {
        this.issueWardNo = issueWardNo;
    }

    public String getIssueDepartment() {
        return issueDepartment;
    }

    public void setIssueDepartment(String issueDepartment) {
        this.issueDepartment = issueDepartment;
    }

    public Issue() {

    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
