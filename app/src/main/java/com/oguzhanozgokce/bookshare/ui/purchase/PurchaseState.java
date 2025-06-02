package com.oguzhanozgokce.bookshare.ui.purchase;

public class PurchaseState {

    public enum PaymentMethod {
        CREDIT_CARD, CASH_ON_DELIVERY
    }

    // Form data
    private String fullName = "";
    private String phone = "";
    private String address = "";
    private String city = "";
    private String postalCode = "";

    // Payment data
    private PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
    private String cardNumber = "";
    private String expiry = "";
    private String cvv = "";
    private String cardHolderName = "";

    // Book information
    private String bookTitle = "";
    private String bookId = "";
    private double price = 0.0;

    // Validation states
    private boolean isFormValid = false;
    private boolean isPaymentValid = true; // Default true for cash on delivery
    private String errorMessage = "";

    // Loading state
    private boolean isLoading = false;
    private boolean isCompleted = false;

    public PurchaseState() {
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookId() {
        return bookId;
    }

    public double getPrice() {
        return price;
    }

    public boolean isFormValid() {
        return isFormValid;
    }

    public boolean isPaymentValid() {
        return isPaymentValid;
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

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setFormValid(boolean formValid) {
        isFormValid = formValid;
    }

    public void setPaymentValid(boolean paymentValid) {
        isPaymentValid = paymentValid;
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
    public boolean isDeliveryInfoComplete() {
        return !fullName.isEmpty() && !phone.isEmpty() &&
                !address.isEmpty() && !city.isEmpty() && !postalCode.isEmpty();
    }

    public boolean isCardInfoComplete() {
        return !cardNumber.isEmpty() && !expiry.isEmpty() &&
                !cvv.isEmpty() && !cardHolderName.isEmpty();
    }

    public boolean needsCardInfo() {
        return paymentMethod == PaymentMethod.CREDIT_CARD;
    }

    public boolean canCompletePurchase() {
        boolean deliveryComplete = isDeliveryInfoComplete();
        boolean paymentComplete = paymentMethod == PaymentMethod.CASH_ON_DELIVERY ||
                (paymentMethod == PaymentMethod.CREDIT_CARD && isCardInfoComplete());

        return deliveryComplete && paymentComplete && isFormValid && isPaymentValid && !isLoading;
    }

    public void clearForm() {
        fullName = "";
        phone = "";
        address = "";
        city = "";
        postalCode = "";
        paymentMethod = PaymentMethod.CASH_ON_DELIVERY;
        cardNumber = "";
        expiry = "";
        cvv = "";
        cardHolderName = "";
        errorMessage = "";
        isFormValid = false;
        isPaymentValid = true;
        isLoading = false;
        isCompleted = false;
    }
}