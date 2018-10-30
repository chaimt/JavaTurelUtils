package com.turel.examples;

import java.util.Optional;

import static com.turel.examples.OptionalWooes.VerificationError.*;

public class OptionalWooes {

    public enum VerificationError { one, two, three}

    public static Optional<VerificationError> checkOne(String value){
        System.out.println("checkOne");
        return (value.equalsIgnoreCase("1")) ? Optional.of(one) : Optional.empty();
    }

    public static Optional<VerificationError> checkTwo(String value){
        System.out.println("checkTwo");
        return (value.equalsIgnoreCase("2")) ? Optional.of(two) : Optional.empty();
    }

    public static Optional<VerificationError> checkThree(String value){
        System.out.println("checkThree");
        return (value.equalsIgnoreCase("3")) ? Optional.of(three) : Optional.empty();
    }

    /**
     * this example is not composable
     * @param value
     * @return
     */
    public static Optional<VerificationError> checkBad(String value){
        return Optional.ofNullable(
                checkOne(value)
                        .orElse(checkTwo(value)
                                .orElse(checkThree(value).orElse(null))));
    }

    /**
     * code is composable but it is not lazy, all methods are evaluated and ran.
     * @param value
     * @return
     */
    public static Optional<VerificationError> checkEager(String value){
        return checkOne(value)
                .map(Optional::of).orElse(checkTwo(value))
                .map(Optional::of).orElse(checkThree(value));
    }

    public static Optional<VerificationError> checkProper(String value){
        return checkOne(value)
                .map(Optional::of).orElseGet(() -> checkTwo(value))
                .map(Optional::of).orElseGet(() -> checkThree(value));
    }

    public static void main(String[] args) {
        System.out.println("******************");
        Optional<VerificationError> verificationError = checkBad("2");
        System.out.println("checkBad: " + verificationError.get());

        System.out.println("******************");
        verificationError = checkEager("2");
        System.out.println("checkEager: " + verificationError.get());

        System.out.println("******************");
        verificationError = checkProper("2");
        System.out.println("checkNotLazy: " + verificationError.get());
    }




}
