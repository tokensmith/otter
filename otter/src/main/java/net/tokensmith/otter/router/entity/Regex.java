package net.tokensmith.otter.router.entity;


public enum Regex {
    UUID ("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");

    private String regex;

    Regex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
