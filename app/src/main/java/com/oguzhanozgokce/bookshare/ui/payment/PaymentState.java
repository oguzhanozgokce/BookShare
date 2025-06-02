package com.oguzhanozgokce.bookshare.ui.payment;

public class PaymentState {

    // Form data
    private String fullName = "";
    private String phone = "";
    private String address = "";
    private String city = "";
    private String postalCode = "";
    private String startDate = "";
    private String endDate = "";

    // Book information
    private String bookTitle = "";
    private String bookId = "";

    // Validation states
    private boolean isFormValid = false;
    private boolean isDatesValid = false;
    private String errorMessage = "";

    // Loading state
    private boolean isLoading = false;
    private boolean isCompleted = false;

    public PaymentState() {
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookId() {
        return bookId;
    }

    public boolean isFormValid() {
        return isFormValid;
    }

    public boolean isDatesValid() {
        return isDatesValid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // Setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setFormValid(boolean formValid) {
        isFormValid = formValid;
    }

    public void setDatesValid(boolean datesValid) {
        isDatesValid = datesValid;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    // Helper methods
    public boolean isContactInfoComplete() {
        return !fullName.isEmpty() && !phone.isEmpty() &&
                !address.isEmpty() && !city.isEmpty() && !postalCode.isEmpty();
    }

    public boolean areDatesSelected() {
        return !startDate.isEmpty() && !endDate.isEmpty();
    }

    public boolean canCompleteBorrow() {
        return isContactInfoComplete() && areDatesSelected() && isDatesValid && !isLoading;
    }

    public void clearForm() {
        fullName = "";
        phone = "";
        address = "";
        city = "";
        postalCode = "";
        startDate = "";
        endDate = "";
        errorMessage = "";
        isFormValid = false;
        isDatesValid = false;
        isLoading = false;
        isCompleted = false;
    }
}
