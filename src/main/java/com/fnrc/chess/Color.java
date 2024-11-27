package com.fnrc.chess;

public enum Color {
    BLACK("PRETO"),
    WHITE("BRANCO");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    public static Color getColor(String color) {
        for (Color c : Color.values()) {
            if (c.getColor().equals(color)) {
                return c;
            }
        }
        throw new IllegalArgumentException("A cor " + color + " não é válida.");
    }
}