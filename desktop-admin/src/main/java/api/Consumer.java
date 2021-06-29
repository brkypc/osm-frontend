package api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Consumer {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://127.0.0.1:8080";

    public Consumer() {
        restTemplate = new RestTemplate();
    }

    public List<TrackingModel> getAllPoints() {
        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(
                BASE_URL + "/getAllPoints", TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TrackingModel> getPointsOfClient(int clientId) {
        URI requestUri = UriComponentsBuilder.fromUriString(BASE_URL).path("/getPointsOfClient")
                .queryParam("clientid", clientId).build().encode().toUri();

        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(requestUri, TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TrackingModel> getRoutesCloseToPoint(double latitude, double longitude) {
        URI requestUri = UriComponentsBuilder.fromUriString(BASE_URL).path("/getRoutesClosePoint")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude).build().encode().toUri();

        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(requestUri, TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TrackingModel> getRoutesCloseToPointTimeInterval(
            double latitude, double longitude, long startTime, long endTime) {

        URI requestUri = UriComponentsBuilder.fromUriString(BASE_URL).path("/getRoutesClosePointTimeInterval")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("start", startTime)
                .queryParam("end", endTime).build().encode().toUri();

        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(requestUri, TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TrackingModel> getRoutesInsideArea(
            double  latitude1, double longitude1, double latitude2, double longitude2) {
        URI requestUri = UriComponentsBuilder.fromUriString(BASE_URL).path("/getRoutesInsideArea")
                .queryParam("latitude1", latitude1)
                .queryParam("longitude1", longitude1)
                .queryParam("latitude2", latitude2)
                .queryParam("longitude2", longitude2).build().encode().toUri();

        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(requestUri, TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }

    public List<TrackingModel> getRoutesInsideAreaTimeInterval(
            double  latitude1, double longitude1, double latitude2, double longitude2, long startTime, long endTime) {
        URI requestUri = UriComponentsBuilder.fromUriString(BASE_URL).path("/getRoutesInsideAreaTimeInterval")
                .queryParam("latitude1", latitude1)
                .queryParam("longitude1", longitude1)
                .queryParam("latitude2", latitude2)
                .queryParam("longitude2", longitude2)
                .queryParam("start", startTime)
                .queryParam("end", endTime).build().encode().toUri();

        ResponseEntity<TrackingModel[]> responseEntity = restTemplate.getForEntity(requestUri, TrackingModel[].class);
        if (responseEntity.getBody() != null) {
            return Arrays.asList(responseEntity.getBody());
        } else {
            return new ArrayList<>();
        }
    }
}
