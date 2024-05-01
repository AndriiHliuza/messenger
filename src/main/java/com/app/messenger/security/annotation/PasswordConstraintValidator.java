package com.app.messenger.security.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

    private int minLength;
    private int maxLength;

    @Override
    public void initialize(Password constraintAnnotation) {
        minLength = constraintAnnotation.minLength();
        maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return containsLowerLetter(value)
                && containsUpperLetter(value)
                && containsDigit(value)
                && containsSpecialCharacter(value)
                && isLengthInBounds(minLength, maxLength, value);
    }

    private boolean containsLowerLetter(String input) {
        Pattern pattern = Pattern.compile("[a-z]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private boolean containsUpperLetter(String input) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private boolean containsDigit(String input) {
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private boolean containsSpecialCharacter(String input) {
        Pattern pattern = Pattern.compile("[!@#$%.,;]");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private boolean isLengthInBounds(int start, int end, String input) {
        int inputLength = input.length();
        return start < inputLength && inputLength < end;
    }
}
