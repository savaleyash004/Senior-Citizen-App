package com.seniorcare.seniorCitizenAlert.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceResponse {
    private List<String> destination_addresses;
    private List<String> origin_addresses;
    private List<Row> rows;
    private String status;

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Row {
        private List<Element> elements;

        public List<Element> getElements() {
            return elements;
        }

        public void setElements(List<Element> elements) {
            this.elements = elements;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Element {
        private Distance distance;
        private Duration duration;
        private String status;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Distance {
        private String text;
        private int value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Duration {
        private String text;
        private int value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
